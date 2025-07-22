public abstract class Vocab {
    public static void main(String[] args) throws SpellingException, ConjugationException {
        Verb verb = new Verb("たべる", "eat", "ate", false, null);

        for (int i = 0; i <= 7; i++) {
            boolean isPositive = (i & 1) != 0;
            boolean isFuture = (i & 2) != 0;
            boolean isProgressive = (i & 4) != 0;

            System.out.println(verb.conjugate(!isPositive, !isFuture, isProgressive));
        }
    }

    protected final String hiragana;
    protected final String english;

    public Vocab(String hiragana, String english) {
        this.hiragana = hiragana;
        this.english = english;
    }
}

class Noun extends Vocab {
    private final boolean living;
    public boolean isLiving() {
        return living;
    }
    
    public Noun(String hiragana, String english, boolean living) {
        super(hiragana, english);
        this.living = living;
    }
}

class Verb extends Vocab {
    private final String englishPast;
    private final String[] particleRules;

    protected final String stem;

    protected final String teForm;

    public Verb(String hiragana, String englishPresent, String englishPast, boolean iruEruOverride, String[] particleRules) throws SpellingException, ConjugationException {
        super(hiragana, englishPresent);
        this.englishPast = englishPast;
        this.particleRules = particleRules;

        // Precalculate conjugations
        String romanji = Conjugate.toRomanji(hiragana);
        stem = Conjugate.toHiragana(Conjugate.getVerbStem(romanji, iruEruOverride));
        teForm = Conjugate.toHiragana(Conjugate.getVerbTeForm(romanji, iruEruOverride));
    }

    public String conjugate(boolean isPositive, boolean isFuture, boolean isProgressive) {
        String s = isProgressive ? teForm + 'い' : stem;
        if (isPositive) {
            return s + (isFuture ? "ます" : "ました");
        }

        return s + "ません" + (isFuture ? "" : "でした");
    }
}