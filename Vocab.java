public abstract class Vocab {
    public static void main(String[] args) throws SpellingException, ConjugationException {
        Verb verb = new Verb("たべる", "eat", "eats", "ate", "eating", false, null);

        for (int i = 0; i < 8; i++) {
            boolean isPositive = (i & 1) != 0;
            boolean isFuture = (i & 2) != 0;
            boolean isProgressive = (i & 4) != 0;

            System.out.println(verb.conjugate(!isPositive, !isFuture, isProgressive));
        }

        Adjective adj = new Adjective("たかい", "tall");
        for (int i = 0; i < 4; i++) {
            boolean isPositive = (i & 1) != 0;
            boolean isFuture = (i & 2) != 0;
            System.out.println(adj.conjugate(isPositive, isFuture) + "です");
        }
    }

    protected final String hiragana;
    protected final String english;

    public Vocab(String hiragana, String english) {
        this.hiragana = hiragana;
        this.english = english;
    }

    @Override
    public String toString() {
        return hiragana;
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
    protected final String englishPresent;
    protected final String englishPast;
    protected final String englishProgressive;
    protected final String[] particles;

    protected final String stem;
    protected final String teForm;

    public Verb(String hiragana, String englishInf, String englishPresent, String englishPast, String englishProgressive, 
                boolean iruEruOverride, String[] particles) throws SpellingException, ConjugationException {
        super(hiragana, englishInf);
        this.englishPresent = englishPresent;
        this.englishPast = englishPast;
        this.englishProgressive = englishProgressive;
        this.particles = particles;

        // Precalculate conjugations
        String romanji = Conjugate.toRomanji(hiragana);
        Conjugate.assertVerb(romanji);
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

    public String getParticleEnglish(char hiraganaParticle) {
        for (String particle : particles) {
            if (particle.charAt(0) == hiraganaParticle) {
                return particle.substring(1);
            }
        }
        return null;
    }
}

class Adjective extends Vocab {
    private final String stem;

    public Adjective(String hiragana, String english) throws ConjugationException {
        super(hiragana, english);
        Conjugate.assertAdj(hiragana);
        stem = hiragana.substring(0, hiragana.length() - 1);
    }
    
    public String conjugate(boolean isPositive, boolean isFuture) {
        return stem + (isPositive ? (isFuture ? "い" : "かった") : (isFuture ? "くない" : "くなかった"));
    }
}