package nu.yona.app.utils;


public class MobileNumberFormatter {

    private static final String DUTCH_COUNTRY_CODE = "+31";
    private static final int ACCEPTED_PHONE_NUMBER_LENGTH = 9;
    private static final String UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX = "[^123456789+]";

    /**
     * @param number input mobile number
     * @return formatted mobile number
     */
    public static String formatMobileNumber(String number) {
        String cleanNumber = removeUnwantedCharacters(number);
        return DUTCH_COUNTRY_CODE.concat(cleanNumber.substring(cleanNumber.length() - ACCEPTED_PHONE_NUMBER_LENGTH));
    }

    /**
     * Removes any char but [123456789+]
     * @param number input number
     * @return number cleaned of unwanted characters
     */
    private static String removeUnwantedCharacters(String number) {
        return number.replaceAll(UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX, "");
    }
}
