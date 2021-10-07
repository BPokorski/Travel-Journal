package TravelJournal.utils.stringParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {

    private StringUtils stringUtils;

    @BeforeEach
    void setUp() {
        stringUtils = new StringUtils();
    }

    @Test
    @DisplayName("Capitalising single word")
    public void testToCapitalFirstLetterSingleWord() {
        String expectedWord = "Poland";

        String actualWord = stringUtils.toCapitalFirstLetter(
                "poland",
                "");

        assertEquals(expectedWord,
                actualWord,
                "Only first letter should be capitalised");
    }

    @Test
    @DisplayName("Capitalising multiple words with given separator")
    public void testToCapitalFirstLetterMultipleWords() {
        String expectedWord = "Democratic Republic Of The Congo";

        String actualWord = stringUtils.toCapitalFirstLetter(
                "democratic republic of the congo",
                " ");

        assertEquals(expectedWord,
                actualWord,
                "Every first letter should be capitalised");
    }

    @Test
    @DisplayName("Replacing separator multiple words")
    public void testSeparatorChangerMultipleWords() {
        String expectedWord = "united-states-of-america";

        String actualWord = stringUtils.separatorChanger(
                "united states of america",
                " ",
                "-");

        assertEquals(expectedWord,
                actualWord,
                "Words are not separated with dash");
    }

    @Test
    @DisplayName("Replacing separator single word")
    public void testSeparatorChangerSingleWord() {
        String expectedWord = "Germany";

        String actualWord = stringUtils.separatorChanger("Germany",
                "-",
                " ");

        assertEquals(expectedWord,
                actualWord,
                "Changed separator of single word");
    }

    @Test
    @DisplayName("Lowercase single word")
    public void testToLowerCaseConverter() {
        String expectedWord = "georgia";

        String actualWord = stringUtils.toLowerCaseConverter(
                "Georgia",
                " ");

        assertEquals(expectedWord,
                actualWord,
                "Every letter should be lowercase");
    }

    @Test
    @DisplayName("Lowercase multiple words")
    public void testToLowerCaseConverterMultipleWords() {
        String expectedWord = "south korea";

        String actualWord = stringUtils.toLowerCaseConverter(
                "South Korea",
                "");

        assertEquals(expectedWord,
                actualWord,
                "Every letter should be lowercase");
    }
}
