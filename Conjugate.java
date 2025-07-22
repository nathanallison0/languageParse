/*
Supported:
あいうえおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろわをん

'ぢ' = "di"
'づ' = "du"
'ふ' = "hu"
'を' = "wo"

All hiragana ascii:
ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんゔゕゖ
Starting index 12353
86 chars long
Sections (inclusive):

0-9 Vowels
ぁあぃいぅうぇえぉお

10-19 Kk
かがきぎくぐけげこご

20-29 Ss
さざしじすずせぜそぞ

30-40 Tt
ただちぢっつづてでとど

41-45 Nn
なにぬねの

46-60 Hh
はばぱひびぴふぶぷへべぺほぼぽ

61-65 Mm
まみむめも

66-71 Yy
ゃやゅゆょよ

72-76 Rr
らりるれろ

77-85 Misc
ゎわゐゑをんゔゕゖ

わ = 78
を = 81
ん = 82
*/

class SpellingException extends Exception {
    public SpellingException(String m) {
        super(m);
    }
}

class ConjugationException extends Exception {
    public ConjugationException(String m) {
        super(m);
    }
}

public class Conjugate {
    public static void main(String[] args) throws SpellingException, ConjugationException {
        //String h = "あいうえおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゆよらりるれろわをん";

        String[] verbs = {"たべる", "いく", "はこぶ", "まつ", "べんきょうする", "もらう", "はなす"};
        for (String verb : verbs) {
            String r = toRomanji(verb);
            String stem = getVerbTeForm(r, false);
            System.out.println(toHiragana(stem) + "います");
        }
        /* 
        String h = "きゃにゃしゅ";
        String r = toRomanji(h);
        String n = toHiragana(r);

        System.out.println(h);
        System.out.println(n);

        int end = Math.min(h.length(), n.length());

        for (int i = 0; i < end; i++) {
            if (h.charAt(i) == n.charAt(i)) {
                System.out.print(" ");
            } else {
                System.out.print("X");
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.println(r);
        */
        /* 
        String r = "aiueokagakigikugukegekogosazashijisuzusezesozotadachidittsudutedetodonaninunenohabapahibipihubupuhebepehobopomamimumemoyayuyorarirurerowawon";
        String t = toRomanji(toHiragana(r));
        System.out.println(t);
        System.out.println(r);
        for (int i = 0; i < r.length(); i++) {
            if (r.charAt(i) == t.charAt(i)) {
                System.out.print(" ");
            } else {
                System.out.print("X");
            }
        }
         */
    }

    private static void assertVerb(String verb) throws ConjugationException {
        if (verb.charAt(verb.length() - 1) != 'u') {
            throw new ConjugationException("Verb '" + verb + "' does not end in 'u'");
        }
    }

    private static final String IRU = "iru";
    private static final String ERU = "eru";
    private static final String SHITE = "shite";
    private static final String TTE = "tte";
    private static final String NDE = "nde";
    private static final String[][] TEFORMS = {
        {"suru", SHITE},
        {"tsu", TTE},
        {"su", SHITE},
        {"ru", TTE},
        {"mu", NDE},
        {"bu", NDE},
        {"nu", NDE},
        {"ku", "ite"},
        {"gu", "ide"},
        {"u", TTE}
    };
    public static String getVerbTeForm(String verb, boolean iruEruOverride) throws ConjugationException {
        assertVerb(verb);

        String teStem = null;
        if (!iruEruOverride && (verb.endsWith(IRU) || verb.endsWith(ERU))) {
            teStem = verb.substring(0, verb.length() - 2);
        } else if (verb.equals("iku")) {
            teStem = "it"; // becomes itte
        } else if (verb.equals("kuru")) {
            teStem = "ki";
        }

        if (teStem != null) {
            return teStem + "te";
        }

        for (String[] form : TEFORMS) {
            if (verb.endsWith(form[0])) {
                return verb.substring(0, verb.length() - form[0].length()) + form[1];
            }
        }

        throw new ConjugationException("Wrong verb ending with verb '" + verb + "'");
    }

    private static String putVerbStemException(String romanji, String end, String stemEnd) {
        if (romanji.endsWith(end)) {
            return romanji.substring(0, romanji.length() - end.length()) + stemEnd;
        }
        return null;
    }

    private static final String[][] STEMEXCEPTIONS = {
        {"suru", "shi"},
        {"tsu", "chi"},
        {"su", "shi"}
    };

    public static String getVerbStem(String romanji, boolean iruEruOverride) throws ConjugationException {
        assertVerb(romanji);

        // Kuru exception
        if (romanji.equals("kuru")) {
            return "ki";
        }

        if (!iruEruOverride && (romanji.endsWith(IRU) || romanji.endsWith(ERU))) {
            return romanji.substring(0, romanji.length() - 2);
        }

        // All stems that aren't just changing 'u' to 'i'
        String stem;
        for (String[] exc : STEMEXCEPTIONS) {
            stem = putVerbStemException(romanji, exc[0], exc[1]);
            if (stem != null) {
                return stem;
            }
        }

        // u to i
        return romanji.substring(0, romanji.length() - 1) + 'i';
    }



    private static final int H_BASE = 12353;
    private static final String VOWELS = "aiueo";
    private static final String HBP = "hbp";
    private static final String AUO = "auo";
    private static final int[] H_SECTIONS = {0, 10, 20, 30, 41, 46, 61, 66, 72, 77};

    public static String toHiragana(String romanji) throws SpellingException {
        String hiragana = "";

        int charLen;
        boolean isSmall = false;
        for (int i = 0; i < romanji.length(); i += charLen) {
            char first = romanji.charAt(i);
            String firstS = Character.toString(first);

            SpellingException spellEx = new SpellingException("Error parsing romanji -> '" + romanji.substring(i) + "'");

            charLen = 1;
            if (VOWELS.contains(firstS)) {
                // If the first character is a vowel, get its counterpart
                hiragana += (char) (H_BASE + (VOWELS.indexOf(firstS) * 2) + 1);

            } else if (i != romanji.length() - 1) {
                // Handle hiragana that take at least two romanji characters and 'ん'
                char second = romanji.charAt(i + 1);
                String secondS = Character.toString(second);
                int vowelIndex = VOWELS.indexOf(secondS);
                boolean hasThird = i != romanji.length() - 2;
                charLen = 2;

                // Check for combinations
                // Set the romanji so that the small character becomes large
                // ex. kya becomes kiya
                // Then, next iteration, make the second char small
                boolean isShort3 = (hasThird && second == 'y');
                boolean isShort4 = (!isShort3 && i < romanji.length() - 3 && romanji.substring(i, i + 3).equals("shy"));
                if (isShort3 || isShort4) {
                    int split = i + (isShort3 ? 1 : 2);
                    romanji = romanji.substring(0, split) + 'i' + romanji.substring(split);
                    charLen = 0;
                    isSmall = true;
                    continue;

                } else if (first == second) {
                    // Check for hiraganaeating character, meaning 'っ'
                    charLen = 1;
                    hiragana += 'っ';
                    continue;
                }

                switch (first) {
                    case 'k':
                    case 'g':
                        // かがきぎくぐけげこご
                        hiragana += (char) (H_BASE + H_SECTIONS[1] + ((vowelIndex * 2) + (first == 'k' ? 0 : 1)));
                        break;

                    case 's':
                    case 'z':
                    case 'j':
                        // さざしじすずせぜそぞ
                        if (first == 'j' && second == 'i') {
                            hiragana += 'じ';
                        } else if (hasThird && romanji.substring(i, i + 3).equals("shi")) {
                            charLen = 3;
                            hiragana += 'し';
                        } else {
                            hiragana += (char) (H_BASE + H_SECTIONS[2] + ((vowelIndex * 2) + (first == 's' ? 0 : 1)));
                        }
                        break;

                    case 't':
                    case 'd':
                    case 'c':
                        // ただちぢっつづてでとど
                        if (hasThird && vowelIndex == -1) {
                            charLen = 3;
                            String three = romanji.substring(i, i + 3);
                            if (three.equals("chi")) {
                                hiragana += 'ち';
                            } else if (three.equals("tsu")) {
                                hiragana += 'つ';
                            }
                        } else {
                            char c = (char) (H_BASE + H_SECTIONS[3] + ((vowelIndex * 2) + (first == 't' ? 0 : 1)));
                            if (c >= 'っ') {
                                c++;
                            }
                            hiragana += c;
                        }
                        break;

                    case 'n':
                        // なにぬねのん
                        if (vowelIndex == -1) {
                            charLen = 1;
                            hiragana += 'ん';
                        } else {
                            hiragana += (char) (H_BASE + H_SECTIONS[4] + vowelIndex);
                        }
                        break;

                    case 'h':
                    case 'b':
                    case 'p':
                        // はばぱひびぴふぶぷへべぺほぼぽ
                        hiragana += (char) (H_BASE + H_SECTIONS[5] + ((vowelIndex * 3) + HBP.indexOf(first)));
                        break;

                    case 'm':
                        // まみむめも
                        hiragana += (char) (H_BASE + H_SECTIONS[6] + vowelIndex);
                        break;

                    case 'y':
                        // ゃやゅゆょよ
                        if (vowelIndex % 2 == 1) {
                            throw spellEx;
                        }

                        char c = (char) (H_BASE + H_SECTIONS[7] + (AUO.indexOf(second) * 2));
                        if (isSmall) {
                            isSmall = false;
                        } else {
                            c++;
                        }
                        hiragana += c;
                        break;

                    case 'r':
                        // らりるれろ
                        hiragana += (char) (H_BASE + H_SECTIONS[8] + vowelIndex);
                        break;

                    case 'w':
                        // わ, を
                        switch (second) {
                            case 'a' -> hiragana += 'わ';
                            case 'o' -> hiragana += 'を';
                            default -> throw spellEx;
                        }
                        break;

                    default:
                        throw spellEx;
                }
            } else if (first == 'n') {
                hiragana += 'ん';
            }
        }

        return hiragana;
    }

    public static String toRomanji(String hiragana) throws SpellingException {
        String romanji = "";
        boolean paused = false;

        for (int i = 0; i < hiragana.length(); i++) {
            char c = hiragana.charAt(i);
            int relIndex = (int) (c - H_BASE);
            String rep = "";

            // Operate differntly depending on which section the character
            // falls under
            int s = 1;
            if (relIndex < H_SECTIONS[s++]) {
                // ぁあぃいぅうぇえぉお
                rep += VOWELS.charAt((relIndex - 1) / 2);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // かがきぎくぐけげこご

                if (relIndex % 2 == 0) {
                    rep += 'k';
                } else {
                    // Dakuten
                    rep += 'g';
                }

                rep += VOWELS.charAt(relIndex / 2);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // さざしじすずせぜそぞ

                if (relIndex % 2 == 0) {
                    if (c == 'し') {
                        rep += "sh";
                    } else {
                        rep += 's';
                    }
                } else {
                    // Dakuten
                    if (c == 'じ') {
                        rep += 'j';
                    } else {
                        rep += 'z';
                    }
                }

                rep += VOWELS.charAt(relIndex / 2);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // ただちぢっつづてでとど

                if (c == 'っ') {
                    paused = true;
                    continue;
                } else if (c > 'っ') {
                    // Remove っ from consideration
                    relIndex -= 1;
                }

                // Set now is ただちぢつづてでとど
                if (relIndex % 2 == 0) {
                    switch (c) {
                        case 'ち' -> rep += "ch";
                        case 'つ' -> rep += "ts";
                        default -> rep += 't';
                    }
                } else {
                    // Dakuten
                    rep += 'd';
                }

                rep += VOWELS.charAt(relIndex / 2);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // なにぬねの

                rep += "n" + VOWELS.charAt(relIndex);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // はばぱひびぴふぶぷへべぺほぼぽ

                int dakutenIndex = relIndex % 3;
                rep += HBP.charAt(dakutenIndex);
                rep += VOWELS.charAt(relIndex / 3);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // まみむめも

                rep += "m" + VOWELS.charAt(relIndex);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // ゃやゅゆょよ
            
                if (relIndex % 2 == 0) {
                    // ゃゅょ
                    if (romanji.charAt(romanji.length() - 1) != 'i') {
                        throw new SpellingException("Error parsing hiragana -> " + hiragana.substring(i - 1));
                    }
                    romanji = romanji.substring(0, romanji.length() - 1);
                }

                rep += 'y';
                rep += AUO.charAt(relIndex / 2);

            } else if (relIndex < H_SECTIONS[s++]) {
                relIndex -= H_SECTIONS[s - 2];
                // らりるれろ

                rep += 'r';
                rep += VOWELS.charAt(relIndex);

            } else {
                // Misc
                rep = switch (c) {
                    case 'わ' -> "wa";
                    case 'を' -> "wo";
                    case 'ん' -> "n";
                    default -> "";
                };
            }

            if (paused) {
                // If last char was っ, add extra copy of first character to beginning
                romanji += rep.charAt(0);
                paused = false;
            }

            romanji += rep;
        }

        return romanji;
    }
}
