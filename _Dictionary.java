import java.util.ArrayList;
import java.util.Arrays;

public class _Dictionary {
    private static ArrayList<Vocab> words = new ArrayList<>();

    public static void populate() throws SpellingException, ConjugationException {
        Vocab[] w = {
            new Noun("ひと", "person", true),
            new Noun("こども", "kid", true),
            new Noun("くるま", "car", false),
            new Verb("はなす", "speak", "spoke", false, null)
        };

        words.addAll(Arrays.asList(w));
    }

    public static void add(Vocab word) {
        words.add(word);
    }

    public static Vocab getJapaneseWord(String hiragana) {
        for (Vocab word : words) {
            if (word.hiragana.equals(hiragana)) {
                return word;
            }
        }
        return null;
    }
}
