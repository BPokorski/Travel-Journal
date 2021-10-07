package TravelJournal.utils.randomGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomIntGeneratorTest {

    private RandomIntGenerator randomIntGenerator;

    @BeforeEach
    void setUp() { randomIntGenerator = new RandomIntGenerator();}

    @Test
    @DisplayName("Generated number should be within range 1-10")
    public void testGenerateRandomIntWithinRange() {
        int numberOrigin = 1;
        int numberBound = 10;

        int actualGeneratedInt = randomIntGenerator.generateRandomIntWithinRange(numberOrigin, numberBound);

        assertTrue(actualGeneratedInt >= numberOrigin && actualGeneratedInt <= numberBound,
                "Generated number should be within range 1-10");
    }

    @Test
    @DisplayName("Generated number should be 7 or 8743")
    public void testGenerateRandomOfTwoInts() {
        int firstNumber = 7;
        int secondNumber = 8743;

        int actualGeneratedNumber = randomIntGenerator.generateRandomOfTwoInts(firstNumber, secondNumber);

        assertTrue(actualGeneratedNumber == firstNumber ||
                        actualGeneratedNumber == secondNumber,
                "Generated number should be 7 or 8743");
    }
}
