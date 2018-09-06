package nu.yona.app.utils;

public class MobileNumberFormatter {

    private static final String INTERNATIONAL_ACCESS_PLUS = "+";
    private static final String DUTCH_COUNTRY_CODE = "+31";
    private static final int MIN_ACCEPTED_PHONE_NUMBER_LENGTH = 9;
    private static final String UNACCEPTED_PHONE_NUMBER_CHARACTERS_REGEX = "[^0123456789+]";
    private static final String NON_DUTCH_INTERNATIONAL_NUMBER_PREFIX_REGEX = "\\+(?!31).*";
    private static final String VALID_NUMBER_REGEX = "^\\+[0-9]{6,20}$";


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

/*
        Below function will do following on mobile number:
            - Remove white spaces, new lines any other unwanted characters our of character set "[^0123456789+]"
            - Concate country code prefix if it is not present.
            - Add the prefix from the prefix input field if the number doesn't already start with +
*/

    public static String formatMobileNumber(String countryCode, String mobileNumber){
        countryCode = removeUnwantedCharacters(countryCode);
        mobileNumber = removeUnwantedCharacters(mobileNumber);
        String mobileNumberWithCountryCode = mobileNumber;
        if(!mobileNumber.startsWith("+")){
            if(!countryCode.startsWith("+")){
                countryCode = "+"+countryCode;
            }
            mobileNumberWithCountryCode = countryCode+mobileNumber;
        }
        return formatMobileNumberWithCountryCode(mobileNumberWithCountryCode);
    }

    /*
        Below function will do following on mobile number:
             - Remove white spaces, new lines any other unwanted characters our of character set "[^0123456789+]"
            - Remove the construct (0) if that occurs in the number
            - Replace +310 with +31, as it's a common Dutch mistake to convert a mobile number like 06 1819... to +31061819...
            - Validate the regex "^\\+[0-9]{6,20}$"
*/

    public static String formatMobileNumberWithCountryCode(String mobileNumberWithCountryCode){
        mobileNumberWithCountryCode = removeUnwantedCharacters(mobileNumberWithCountryCode);
        mobileNumberWithCountryCode = mobileNumberWithCountryCode.replace("(0)","");
        if(mobileNumberWithCountryCode.startsWith("+310")){
            mobileNumberWithCountryCode = mobileNumberWithCountryCode.replace("+310","+31");
        }
        return mobileNumberWithCountryCode;
    }

    // To validate after formatting the number.
    public static boolean validateMobileNumber(String mobileNumber){
        return mobileNumber.matches(VALID_NUMBER_REGEX);
    }

}
