import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Dictionary {
    public static void main(String[] args) throws ConjugationException, SpellingException {
        Dictionary d = new Dictionary(List.of(
            new Noun("わたし", "I", true),
            new Noun("ひと", "person", true),
            new Noun("こども", "kid", true),
            new Noun("まち", "town", false),
            new Noun("だいじょうぶ", "okay", false),
            new Noun("ことば", "word", false),
            new Adjective("たかい", "high"),
            new Adjective("かっこよい", "cool"),
            new Adjective("よい", "good"),
            new Verb("はなす", "speak", "speaks", "spoke", "speaking", false, null),
            new Verb("たべる", "eat", "eats", "ate", "eating", false, null),
            new Verb("かえる", "return", "returns", "returned", "returning", true, null)
        ));

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("Enter something: ");
            String hiragana = input.nextLine();
            System.out.println(d.phraseToEnglish(hiragana));
        }
    }

    private final Class<?>[] wordClasses = {Noun.class, Verb.class, Adjective.class};
    @SuppressWarnings("unchecked") // this is type safe
    protected final ArrayList<ArrayList<Vocab>>[] wordListsByClass = (ArrayList<ArrayList<Vocab>>[]) new ArrayList<?>[wordClasses.length];

    private void init() {
        for (int i = 0; i < wordListsByClass.length; i++) {
            wordListsByClass[i] = new ArrayList<>();
        }
    }

    public Dictionary(Collection<? extends Vocab> words) {
        init();
        add(words);
    }

    public Dictionary() {
        init();
    }

    public final void add(Collection<? extends Vocab> words) {
        for (Vocab word : words) {
            ArrayList<Vocab> subList = getClassSubList(word.getClass(), word.hiragana.length());
            subList.add(word);
        }
    }

    private String phraseToEnglish(String hiragana) {
        System.out.println("Translating: " + hiragana);
        // First, check to see if phrase is all one translatable noun
        Vocab word = get(hiragana, Noun.class);
        if (word != null) {
            return word.english;
        }

        // Get number of 'ha's
        int haCount = 0;
        for (char c : hiragana.toCharArray()) {
            if (c == 'は') {
                haCount++;
            }
        }

        // If 'ha's exist, try to find a phrase on both sides of each 'ha'
        if (haCount != 0) {
            int lastHaIndex = 0;
            for (int i = 0; i < haCount; i++) {
                int haIndex = hiragana.substring(lastHaIndex + 1).indexOf('は') + lastHaIndex + 1;

                String phrase1 = phraseToEnglish(hiragana.substring(0, haIndex));

                if (phrase1 != null) {
                    String phrase2 = phraseToEnglish(hiragana.substring(haIndex + 1));

                    if (phrase2 != null) {
                        // If found, combine the two
                        return phrase1 + " " + phrase2;
                    }
                }

                lastHaIndex = haIndex;
            }
        }

        // Check for verbs
        Conjugation<Verb> verbConj = findVerbFollowingPhrase(hiragana);
        if (verbConj != null) {
            return verbConj.toEnglish();
        }

        // Check for statements like "です"
        String[] statement = getEnd(hiragana, STATEMENT_ENDS);
        Conjugation<Adjective> adjConj = null;

        if (statement != null) {
            // Cut off statement
            String toTranslate = removeEnd(hiragana, statement);

            // If statement is "です", check for an adjective before it
            if (statement[0] == STATEMENT_ENDS[1]) {
                adjConj = findAdjFollowingPhrase(toTranslate);
            }

            if (adjConj == null) {
                // Return statement phrase then the translation of the statement
                // ex. "がっこうです" -> {"school", "is"} -> "is school"
                return statement[1] + " " + phraseToEnglish(toTranslate);
            } else {
                return adjConj.toEnglish();
            }
        }
        
        return "?";
    }

    public Vocab get(String hiragana, Class<? extends Vocab> c) {
        ArrayList<ArrayList<Vocab>> classList = getClassList(c);
        int index = getClassSubListIndex(classList, hiragana.length());

        // No words of the same length exist
        if (index < 0) {
            return null;
        }

        ArrayList<Vocab> subList = classList.get(index);
        for (Vocab word : subList) {
            if (word.hiragana.equals(hiragana)) {
                return word;
            }
        }
        return null;
    }

    /**
     * @param hiraganaPhrase A phrase ending with an infinitive word
     * @param c The type of word to look for
     * @return An instance of class c as a Vocab object, or null on failure
     */
    public Vocab findFollowingPhrase(String hiraganaPhrase, Class<? extends Vocab> c) {
        ArrayList<ArrayList<Vocab>> classList = getClassList(c);

        // Iterate from longest to shortest, returning first found
        for (int i = classList.size() - 1; i >= 0; i--) {
            ArrayList<Vocab> subList = classList.get(i);

            // Only check if words aren't longer than the phrase
            if (subList.get(0).hiragana.length() > hiraganaPhrase.length()) {
                continue;
            }

            for (Vocab word : subList) {
                if (hiraganaPhrase.endsWith(word.hiragana)) {
                    return word;
                }
            }
        }
        return null;
    }

    private ArrayList<ArrayList<Vocab>> getClassList(Class<? extends Vocab> c) {
        for (int i = 0; i < wordClasses.length; i++) {
            if (wordClasses[i].equals(c)) {
                return wordListsByClass[i];
            }
        }
        return null;
    }
    /**
     * @param c The class of word to find
     * @param wordLength Word length of the sub-list
     * @return the index of the sub-list if it exists, otherwise the ONE LESS THAN the
     negative index of where a new list for wordLength length would be inserted
     */
    private int getClassSubListIndex(ArrayList<ArrayList<Vocab>> classList, int wordLength) {
        // TODO: make binary search
        boolean found = false;
        int i;
        for (i = 0; i < classList.size(); i++) {
            int thisWordLength = classList.get(i).get(0).hiragana.length();
            if (thisWordLength == wordLength) {
                found = true;
                break;
            } else if (thisWordLength > wordLength) {
                break;
            }
        }
        return found ? i : (i * -1) - 1;
    }

    /**
     * @param c The class of word to find
     * @param wordLength Word length of the sub-list
     * @return The corresponding sub-list if it exists, otherwise the new one put in its place
     */
    private ArrayList<Vocab> getClassSubList(Class<? extends Vocab> c, int wordLength) {
        ArrayList<ArrayList<Vocab>> classList = getClassList(c);
        int index = getClassSubListIndex(classList, wordLength);

        // If already exists, return
        if (index >= 0) {
            return classList.get(index);
        }

        // Otherwise, create a list, insert, and return
        ArrayList<Vocab> subList = new ArrayList<>();
        classList.add((index * -1) - 1, subList);

        return subList;
    }

    private Conjugation<Adjective> findAdjFollowingPhrase(String hiraganaPhrase) {
        // Get ending indicating conjugation
        String[] endSet = getEnd(hiraganaPhrase, ADJ_ENDS);

        if (endSet != null) {
            // Get infitive of adjective and corresponding object
            String inf = removeEnd(hiraganaPhrase, endSet) + "い";
            Vocab word = findFollowingPhrase(inf, Adjective.class);

            if (word instanceof Adjective adj) { // effectively asserts non-null
                return new Conjugation<>(adj, endSet[1]);
            }
        }
        return null;
    }

    private Conjugation<Verb> findVerbFollowingPhrase(String hiraganaPhrase) {
        String[] endSet = getEnd(hiraganaPhrase, VERB_ENDS);
        if (endSet == null) {
            return null;
        }
        String stem = removeEnd(hiraganaPhrase, endSet);

        boolean isProgressive = stem.endsWith("てい") || stem.endsWith("でい");
        // For non-progressives, check an extra word length for non-iru and eru exceptions
        // ex. the stem of たべる would be たべ, so words of length 3 would need to be checked
        int lastLengthCheck = hiraganaPhrase.length() + 1;
        if (isProgressive) {
            // Extra length check not necessary for progressives
            lastLengthCheck--;
            // Change from ending with "てい" to "て" for checking te-forms
            stem = stem.substring(0, stem.length() - 1);
        }

        // Check each sub-list until we exceed the maximum possible length of verb,
        // returning the longest match
        ArrayList<ArrayList<Vocab>> verbList = getClassList(Verb.class);
        int length = 0;
        Verb found = null;
        for (int i = 0; i < verbList.size() && length <= lastLengthCheck; i++) {
            ArrayList<Vocab> subList = verbList.get(i);
            length = subList.get(0).hiragana.length();
            for (Vocab word : subList) {
                Verb v = (Verb) word;
                if ((isProgressive && stem.endsWith(v.teForm)) ||
                    (!isProgressive && stem.endsWith(v.stem))) {
                        found = v;
                    }
            }
        }
        if (found != null) {
            return new Conjugation<>(found, endSet[1], isProgressive);
        }
        return null;
    }

    /**
     * @param hiragana The hiragana string with the ending
     * @param ends An array of endings for each combination of tense and affirmation as
     * ordered in ENG_ENDS
     * @return An array of two strings: the ending found and its english counterpart, or null
     * on failure
     */
    private static String[] getEnd(String hiragana, String[] ends) {
        for (int i = 0; i < ends.length; i++) {
            if (hiragana.endsWith(ends[i])) {
                String[] endSet = new String[2];
                endSet[0] = ends[i];
                endSet[1] = ENG_ENDS[i];
                return endSet;
            }
        }
        return null;
    }

    private static String removeEnd(String hiragana, String[] ends) {
        return hiragana.substring(0, hiragana.length() - ends[0].length());
    }

    protected static final String[] ENG_ENDS = {
        "is not",
        "is",
        "was not",
        "was"
    };

    private static final String[] STATEMENT_ENDS = {
        "じゃありません",
        "です",
        "じゃありませんでした",
        "でした"
    };

    private static final String[] ADJ_ENDS = {
        "くない",
        "い",
        "くなかった",
        "かった"
    };

    private static final String[] VERB_ENDS = {
        "ません",
        "ます",
        "ませんでした",
        "ました"
    };
}

class Conjugation<T extends Vocab> {
    protected final T word;
    protected boolean isPositive;
    protected boolean isPresent;
    protected final boolean isProgressive; // only applies to verbs

    public Conjugation(T word, boolean isPositive, boolean isPresent, boolean isProgressive) {
        this.word = word;
        this.isPositive = isPositive;
        this.isPresent = isPresent;
        this.isProgressive = isProgressive;
    }

    public Conjugation(T word, boolean isPositive, boolean isPresent) {
        this.word = word;
        this.isPositive = isPositive;
        this.isPresent = isPresent;
        this.isProgressive = false;
    }

    private void statementInit(String engStatement) {
        this.isPositive = (engStatement == Dictionary.ENG_ENDS[1] || engStatement == Dictionary.ENG_ENDS[3]);
        this.isPresent =  (engStatement == Dictionary.ENG_ENDS[0] || engStatement == Dictionary.ENG_ENDS[1]);
    }

    public Conjugation(T word, String engStatement) {
        this.word = word;
        statementInit(engStatement);
        this.isProgressive = false;
    }

    public Conjugation(T word, String engStatement, boolean isProgressive) {
        this.word = word;
        statementInit(engStatement);
        this.isProgressive = isProgressive;
    }

    @Override
    public String toString() {
        String rep = "(" + word + ": ";
        rep += (isPositive ? "positive" : "negative") + ", ";
        rep += (isPresent ? "present" : "past") + ", ";
        if (word instanceof Verb) {
            rep += (isProgressive ? "" : "non-") + "progressive";
        }
        return rep + ")";
    }

    public String toEnglish() {
        if (word instanceof Verb v) {
            if (isProgressive) {
                return (isPresent ? "is" : "was") + " " + (isPositive ? "" : "not ") + v.englishProgressive;
            }
            if (isPresent) {
                if (isPositive) {
                    return v.englishPresent;
                }
                return "does not " + v.english;
            }

            if (isPositive) {
                return v.englishPast;
            }

            return "did not " + v.english;
        }
        return Dictionary.ENG_ENDS[(isPresent ? 1 : 3) - (isPositive ? 0 : 1)] + " " + word.english;
    }
}