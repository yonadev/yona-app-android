package nu.yona.app.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MobileNumberFormatterTest {

    @Test
    public void format_withSpecialCharacters_removedSpecialCharacters(){
        String input = "+316444$/*45";
        String expected = "+31644445";
        assertEquals(expected, MobileNumberFormatter.format(input));
    }

    @Test
    public void format_withZeroConstruct_removeZeroConstruct(){
        String input = "+31(0)67842546";
        String expected = "+3167842546";
        assertEquals(expected, MobileNumberFormatter.format(input));
    }

    @Test
    public void format_withDutchMobileNumber_correctsCountryCode(){
        String input = "+310777889900";
        String expected = "+31777889900";
        assertEquals(expected, MobileNumberFormatter.format(input));
    }

    @Test
    public void format_invalidWellFormattedNumber_removesZero(){
        String input = "+3109686270640";
        String expected = "+319686270640";
        assertEquals(expected, MobileNumberFormatter.format(input));
    }

    @Test
    public void format_validWellFormattedNumber_returnsInput(){
        String input = "+319686270640";
        String expected = input;
        assertEquals(expected, MobileNumberFormatter.format(input));
    }

    /*

    COUNTRY CODE + MOBILE NUMBER
     */
    @Test
    public void format_withoutPlus_addsPlus(){
        String input_country = "91";
        String input_number = "9700830218";
        String expected = "+919700830218";
        assertEquals(expected, MobileNumberFormatter.format(input_country, input_number));
    }

    @Test
    public void format_validNumber_returnsInternationalMobileNumber(){
        String input_country = "+91";
        String input_number = "9700830218";
        String expected = "+919700830218";
        assertEquals(expected, MobileNumberFormatter.format(input_country, input_number));
    }

    @Test
    public void format_mobileNumberWithPlus_igonresCountryCode(){
        String input_country = "+91";
        String input_number = "+9700830218";
        String expected = "+9700830218";
        assertEquals(expected, MobileNumberFormatter.format(input_country, input_number));
    }

    /*
    validation
     */
    @Test
    public void validate_withDigitsExceed_returnsFalse(){
        String input = "+91465654464431545456645";
        boolean expected = false;
        assertEquals(expected, MobileNumberFormatter.isValid(input));
    }

    @Test
    public void validate_digitsShortage_returnsFalse(){
        String input = "+3232";
        boolean expected = false;
        assertEquals(expected, MobileNumberFormatter.isValid(input));
    }

    @Test
    public void validate_alphaNumericString_returnsFalse(){
        String input = "+xyz3232";
        boolean expected = false;
        assertEquals(expected, MobileNumberFormatter.isValid(input));
    }

    @Test
    public void validate_validNumber_returnsTrue(){
        String input = "+919686270640";
        boolean expected = true;
        assertEquals(expected, MobileNumberFormatter.isValid(input));
    }

    @Test
    public void validate_numberWithoutPlus_returnsFalse(){
        String input = "919686270640";
        boolean expected = false;
        assertEquals(expected, MobileNumberFormatter.isValid(input));
    }

}
