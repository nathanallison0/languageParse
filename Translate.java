
import java.util.List;
import java.util.Scanner;


public class Translate {
    public static void main(String[] args) throws SpellingException, ConjugationException {
        Scanner input = new Scanner(System.in);
        Dictionary d = new Dictionary(List.of(
            new Noun("わたし", "", true),
            new Noun("ひと", "", true),
            new Noun("こども", "", true),
            new Adjective("たかい", ""),
            new Adjective("かっこよい", ""),
            new Adjective("よい", "")
        ));

        while (true) {
            System.out.print("Enter a thing: ");
            System.out.println(phraseToEnglish(input.nextLine()));
        }
    }

    public static String toEnglish(String hiragana) {
        return null;
    }


    private static String phraseToEnglish(String hiragana) {
        // First, check to see if phrase is all one translatable word
        Vocab word = Dictionary.getJapaneseWord(hiragana);
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

        // Check for statements like "です"
        String[] statement = findStatementInPhrase(hiragana);
        Adjective adj = null;

        if (statement != null) {
            // Cut off statement
            String toTranslate = hiragana.substring(0, hiragana.length() - statement[0].length());

            // If statement is "です", check for an adjective before it
            if (statement == STATEMENTS[0]) {
                adj = Adjective.getInfinitive(toTranslate);
            }

            // Return statement phrase along with translation of what the statement
            // refers to
            // ex. "がっこうです" -> {"school", "is"} -> "is school"
            return statement[1] + " " + phraseToEnglish(toTranslate);
        }

        // Check for adjective at end of phrase
        
        return null;
    }

    private static final String[][] STATEMENTS = {
        {"です", "is"},
        {"じゃありません", "is not"},
        {"じゃありませんでした", "was not"},
        {"でした", "was"}
    };

    private static String[] findStatementInPhrase(String hiragana) {
        for (String[] set : STATEMENTS) {
            if (hiragana.endsWith(set[0])) {
                return set;
            }
        }
        return null;
    }
}
