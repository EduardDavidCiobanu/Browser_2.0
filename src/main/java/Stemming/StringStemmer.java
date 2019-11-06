package Stemming;

/**
 * This is a small extension of Porter {@link Stemmer}
 * which adds String stemming functionality.
 *
 */
public class StringStemmer extends Stemmer {

    /**
     * Filters and brings the word to lowercase.
     * @param word the string to process
     * @return processed word
     */
    private String processWord(String word){

        String result = "";
        for (char c : word.toCharArray()){
            if(c == '\'')
                break;

            if(!isSpecialCh(c))
                result += c;
        }

        return result.toLowerCase();
    }

    /**
     * Check if the char is a special character. <br>
     * ex: ' " , . [ ] { }
     * @param c
     * @return true if c is a special character.
     */
    private boolean isSpecialCh(char c){
        switch (c){
            case '.':
            case ',':
            case '?':
            case '!':
            case '$':
            case '\"':
            case ';':
            case ':':
            case '{':
            case '}':
            case '(':
            case ')':
            case '[':
            case ']': return true;
            default: return false;
        }

    }

    /**
     * Brings a string word to the grammatical root form.
     *
     * @param something to stem
     * @return the stemmed word
     */
    public String stem(String something) {
        String local = processWord(something);

        this.add(local.toCharArray(), local.length());
        this.stem();

        return this.toString();
    }
}
