package coordints;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
final class Utils {
    static final Random RANDOM = new Random();

    // redact the number with a random amount of Xs between 1 and 5
    private static final Function<String, String> DEFAULT_COORD_REDACTOR = s -> "X".repeat(RANDOM.nextInt(5) + 1);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(-?\\d+[.,]?\\d+)");

    static String redactCoords(final String message) {
        return redactCoords(message, null);
    }

    static String redactCoords(String message, @Nullable Function<String, String> coordRedactor) {
        if (coordRedactor == null) coordRedactor = DEFAULT_COORD_REDACTOR;

        // first, get all parts of the string that contain a number
        final List<String> numberParts = getNumberParts(message);

        // convert the number parts to doubles
        final double[] numbers = numberParts.stream().mapToDouble(Double::parseDouble).toArray();

        // then, check if any of those parts are close to a blacklisted coordinate
        for (int i = 0; i < numbers.length; i++) {
            final double number = numbers[i];
            final double sensitivity = getSensitivity(number);
            for (final double blacklistedCoord : Config.BLACKLISTED_COORDS) {
                if (relativeDifference(number, blacklistedCoord) < sensitivity) {
                    // if so, redact the coordinate number from the message
                    message = message.replace(numberParts.get(i), coordRedactor.apply(numberParts.get(i)));
                }
            }
        }

        return message;
    }

    @SuppressWarnings("SameReturnValue")
    static boolean messageContainsCoords(final String message) {
        // first, get all parts of the string that contain a number
        final List<String> numberParts = getNumberParts(message);

        // convert the number parts to doubles
        final double[] numbers = numberParts.stream().mapToDouble(Double::parseDouble).toArray();

        // then, check if any of those parts are close to a blacklisted coordinate
        for (final double number : numbers) {
            // use a higher sensitivity for coords with 3 digits
            final double sensitivity = getSensitivity(number);
            for (final double blacklistedCoord : Optional.ofNullable(Config.BLACKLISTED_COORDS).orElse(new double[0])) {
                if (relativeDifference(number, blacklistedCoord) <= sensitivity)
                    return true;
            }
        }

        return false;
    }

    /**
     * Calculates the relative difference between two doubles.
     * @param a The first double to compare
     * @param b The second double to compare
     * @return 0 if the doubles are equal, 1 if they are completely different
     */
    private static double relativeDifference(final double a, final double b) {
        return Math.abs(a - b) / ((a + b) / 2.0);
    }

    private static double getSensitivity(final double number) {
        // use a higher sensitivity for coords with 3 digits
        return number < 999 && number > -999 ? 0.2 : 0.1;
    }

    private static List<String> getNumberParts(final String message) {
        final var matcher = NUMBER_PATTERN.matcher(message);
        final List<String> numberParts = new ObjectArrayList<>();
        while (matcher.find()) {
            numberParts.add(matcher.group());
        }
        return numberParts;
    }
}