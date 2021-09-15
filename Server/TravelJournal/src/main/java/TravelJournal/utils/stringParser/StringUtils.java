package TravelJournal.utils.stringParser;

/**
 * String utils. It allows to capitalise words or lowercase.
 * It also allows to change connector between word.
 */
public class StringUtils {

    /**
     * Method to capitalise word or words connected with separator e.g:
     * north-america -> North-America
     * @param word - word to capitalise
     * @param separator - separator between words e.g. "-", "_" etc.
     * @return capitalised word or words
     */
    public String toCapitalFirstLetter(String word, String separator) {
        if (!separator.isEmpty() && word.contains(separator)) {
            String[] splitWord = word.split(separator);

            StringBuilder newWord = new StringBuilder();

            for (int i = 0; i < splitWord.length; ++i) {
                newWord.append(splitWord[i].substring(0, 1).toUpperCase());
                newWord.append(splitWord[i].substring(1).toLowerCase());

                if (i != (splitWord.length - 1)) {
                    newWord.append(separator);
                }
            }
            return newWord.toString();
        } else {
            return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
        }
    }

    /**
     * Method to change separator between words, e.g.
     * north-america -> north america
     * @param word to change separator
     * @param separator - old separator e.g. "-"
     * @param newSeparator - separator to be changed to e.g. " "
     * @return word with changed separator
     */
    public String separatorChanger(String word, String separator, String newSeparator) {
        if (word.contains(separator)) {
            return word.replace(separator, newSeparator);
        } else {
            return word;
        }
    }

    /**
     * Method to lowercase word or words separated by separator
     * @param word - word to be lowercased
     * @param separator - separator between words
     * @return lowered word
     */
    public String toLowerCaseConverter(String word, String separator) {
        if (!separator.isEmpty() && word.contains(separator)) {
            String[] splitWord = word.split(separator);

            StringBuilder newWord = new StringBuilder();

            for (int i = 0; i < splitWord.length; ++i) {
                newWord.append(splitWord[i].toLowerCase());

                if (i != (splitWord.length - 1)) {
                    newWord.append(separator);
                }
            }
            return newWord.toString();

        } else {
            word = word.toLowerCase();
        }
        return word;
    }
}
