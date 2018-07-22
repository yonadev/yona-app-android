/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.signup;

import org.junit.Before;
import org.junit.Test;
import nu.yona.app.YonaTestCase;
import nu.yona.app.api.manager.APIManager;


/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepOneTest extends YonaTestCase {

    private StepOne stepOneFragment;
    private String name;


    @Before
    public void setup() {
           stepOneFragment = new StepOne();
    }

    @Test
    public void validateFirstName() {
        assertNotNull(stepOneFragment);
        name = "madhu";
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateText(name));
    }

    @Test
    public void validateLastName() {
        name = "vardan";
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateText(name));
    }

}
