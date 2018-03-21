package nu.yona.app.utils;

public class MobileNumberFormatter {

    private static final String INTERNATIONAL_ACCESS_PLUS = "+";
    private static final String DUTCH_COUNTRY_CODE = "+31";
    private static final int MIN_ACCEPTED_PHONE_NUMBER_LENGTH = 9;
    private static final String UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX = "[^0123456789+]";
    private static final String NON_DUTCH_INTERNATIONAL_NUMBER_PREFIX_REGEX = "\\+(?!31).*";

    /**
     * @param number user's mobile number
     * @return formatted mobile number in the form +316XXXXXXXX or +CCXXXXXXXXX
     */
    public static String formatDutchAndInternationalNumber(String number) {
        String cleanNumber = removeUnwantedCharacters(number);
        if (!cleanNumber.matches(NON_DUTCH_INTERNATIONAL_NUMBER_PREFIX_REGEX)) {
            cleanNumber = formatDutchNumber(cleanNumber);
        }
        return cleanNumber;
    }

    /**
     * Removes any char but [123456789+]
     * @param number input number
     * @return number cleaned of unwanted characters
     */
    private static String removeUnwantedCharacters(String number) {
        return number.replaceAll(UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX, "");
    }

    private static String formatDutchNumber(String number) {
        String result = number;
        if (result.length() >= MIN_ACCEPTED_PHONE_NUMBER_LENGTH) {
            result = number.substring(number.length() - MIN_ACCEPTED_PHONE_NUMBER_LENGTH);
            if (!result.startsWith(DUTCH_COUNTRY_CODE)) {
                return DUTCH_COUNTRY_CODE.concat(result.substring(result.length() - MIN_ACCEPTED_PHONE_NUMBER_LENGTH));
            }
        }
        return result;
    }

}
