import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Dictionary {
    public static void main(String[] args) throws ConjugationException {
        Dictionary d = new Dictionary(List.of(
            new Noun("わたし", "", true),
            new Noun("ひと", "", true),
            new Noun("こども", "", true),
            new Adjective("たかい", ""),
            new Adjective("かっこよい", ""),
            new Adjective("よい", "")
        ));

        System.out.println(Arrays.toString(d.wordListsByClass));
        System.out.println(d.findFollowingPhrase("わたしはかっこよい", Adjective.class));
    }

    private final Class<?>[] wordClasses = {Noun.class, Verb.class, Adjective.class};
    @SuppressWarnings("unchecked") // this is type safe
    protected final ArrayList<ArrayList<Vocab>>[] wordListsByClass = (ArrayList<ArrayList<Vocab>>[]) new ArrayList<?>[wordClasses.length];

    private void init() {
        // Initialize word lists
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

    private ArrayList<Vocab> getClassSubList(Class<? extends Vocab> c, int wordLength) {
        // TODO: make binary search
        ArrayList<ArrayList<Vocab>> classList = getClassList(c);
        int insertIndex = -1;
        for (int i = 0; i < classList.size(); i++) {
            int thisWordLength = classList.get(i).get(0).hiragana.length();
            if (thisWordLength == wordLength) {
                return classList.get(i);
            } else if (thisWordLength > wordLength) {
                insertIndex = i;
                break;
            }
        }

        // List for this word length not found, create one and insert in place
        ArrayList<Vocab> subList = new ArrayList<>();
        if (insertIndex == -1) {
            classList.addLast(subList);
        } else {
            classList.add(insertIndex, subList);
        }

        return subList;
    }
}