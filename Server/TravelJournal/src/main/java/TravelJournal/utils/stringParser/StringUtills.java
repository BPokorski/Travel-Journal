package TravelJournal.utils.stringParser;

public class StringUtills {

    public String toCapitalFirstLetter(String word) {
        if (word.contains("-")) {
            String[] words = word.split("-");

            return words[0].substring(0,1).toUpperCase() +
                    words[0].substring(1).toLowerCase() + "-" +
                    words[1].substring(0,1).toUpperCase() +
                    words[1].substring(1).toLowerCase();
        } else {
            return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
        }

    }
    public String connectorChanger(String word, String connector, String newConnector) {
        if (word.contains(connector)) {
            return word.replace(connector, newConnector);
        } else {
            return word;
        }

    }

    public String toLowerCaseConverter(String word) {
        if (word.contains(" ")) {
            String[] words = word.split(" ");
            word = words[0].toLowerCase() + " " +
                    words[1].toLowerCase();

        } else {
            word = word.toLowerCase();
        }
        return word;
    }
}
