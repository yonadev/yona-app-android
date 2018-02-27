package nu.yona.app.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MobileNumberFormatterTests {


    @Test
    public void formatPhoneNumber_keepsCorrectNumber() {
        String input = "+31612345678";
        String expected = "+31612345678";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_fixesLeadingZeroInDutchNumber() {
        String input = "+310612345678";
        String expected = "+31612345678";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_removesSpaces() {
        String input = "+316123 456 78";
        String expected = "+31612345678";
        assertEquals(expected, MobileNumberFormatter.formatMobileNumber(input));
    }
}
