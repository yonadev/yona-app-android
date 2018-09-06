package nu.yona.app.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MobileNumberTests {

    @Test
    public void formatPhoneNumber_withSymbols(){
        String input_country = "+31";
        String input_number = "6444$/*45";
        String expected = "+31644445";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void formatPhoneNumber_withBrackets(){
        String input_country = "+31";
        String input_number = "(0)67842546";
        String expected = "+3167842546";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void formatPhoneNumber_withoutPlusSymbol(){
        String input_country = "91";
        String input_number = "9700830218";
        String expected = "+919700830218";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void formatPhoneNumber_withCorrectNumber(){
        String input_country = "+91";
        String input_number = "9700830218";
        String expected = "+919700830218";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void formatPhoneNumber_withMultipleCharactersInCountryCode(){
        String input_country = "+$.91";
        String input_number = "54646 5434";
        String expected = "+91546465434";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void formatPhoneNumber_withDutchMobileNumber(){
        String input_country = "+310";
        String input_number = "777889900";
        String expected = "+31777889900";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input_country, input_number));
    }

    @Test
    public void validateMobileNumber_digitsExceed(){
        String input = "+91465654464431545456645";
        assertEquals(false, MobileNumberFormatter.validateMobileNumber(input));
    }

    @Test
    public void validateMobileNumber_digitsShortage(){
        String input = "+3232";
        assertEquals(false, MobileNumberFormatter.validateMobileNumber(input));
    }


    @Test
    public void validateMobileNumber_alphaNumericString(){
        String input = "+xyz3232";
        assertEquals(false, MobileNumberFormatter.validateMobileNumber(input));
    }

    @Test
    public void validateMobileNumber_validString(){
        String input = "+919686270640";
        assertEquals(true, MobileNumberFormatter.validateMobileNumber(input));
        String input1 = MobileNumberFormatter.formatMobileNumberWithCountryCode("+3109686270640");
        assertEquals(true, MobileNumberFormatter.validateMobileNumber(input1));
    }


}
