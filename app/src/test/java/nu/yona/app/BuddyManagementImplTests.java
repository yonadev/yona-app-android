package nu.yona.app;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import nu.yona.app.api.manager.impl.AuthenticateManagerImpl;
import nu.yona.app.api.manager.impl.BuddyManagerImpl;

import static org.junit.Assert.assertEquals;

/**
 * Created by mkologlu on 22/02/2018.
 */

public class BuddyManagementImplTests {


    private final String password = "12423423234234324234";
    private BuddyManagerImpl manager;

    @Before
    public void setUp() throws Exception {
        manager = new BuddyManagerImpl(YonaApplication.getAppContext());
    }


    @Test
    public void formatPhoneNumber_keepsCorrectNumber() {
        String input = "+31612345678";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_fixesLeadingZeroInDutchNumber() {
        String input = "+310612345678";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_removesSpaces() {
        String input = "+316123 456 78";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_addsCountryCodeToLeadingSix() {
        String input = "612345678";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_addsCountryCodeToLeadingZeroSix() {
        String input = "0612345678";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_removesSpacesAndAddsCountryCode() {
        String input = "6123 4567 8";
        String expected = "+31612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }

    @Test
    public void formatPhoneNumber_formatsNonDutchNumber() {
        String input = "+45 612345678";
        String expected = "+45612345678";
        assertEquals(expected, manager.formatMobileNumber(input));
    }
}
