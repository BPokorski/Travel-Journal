package TravelJournal.utils.randomGenerator;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Class to generate random number in given range or to choose one out of two given.
 */
@Component
public class RandomIntGenerator {

    private final Random randomGenerator = new Random();

    /**
     * Generate random int in given range.
     * @param randomNumberOrigin - Beginning of the range.
     * @param randomNumberBound - Ending of the range.
     * @return Randomly generated int.
     */
    public int generateRandomIntWithinRange(int randomNumberOrigin, int randomNumberBound) {
        return randomGenerator.ints(1, randomNumberOrigin, randomNumberBound).findFirst().getAsInt();
    }

    /**
     * Generate random int between two given number.
     * @param firstNumber - First int to be considered.
     * @param secondNumber - Second int to be considered.
     * @return - Random int between two given number.
     */
    public int generateRandomOfTwoInts(int firstNumber, int secondNumber) {
        return randomGenerator.nextBoolean() ? firstNumber: secondNumber;
    }
}
