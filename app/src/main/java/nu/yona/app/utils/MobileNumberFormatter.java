package nu.yona.app.utils;

public class MobileNumberFormatter {

    private static final String DUTCH_COUNTRY_CODE = "+31";
    private static final int ACCEPTED_PHONE_NUMBER_LENGTH = 9;
    private static final String UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX = "[^123456789+]";
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
        return DUTCH_COUNTRY_CODE.concat(number.substring(number.length() - ACCEPTED_PHONE_NUMBER_LENGTH));
    }

}
