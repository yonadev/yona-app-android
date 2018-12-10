/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.security;

import java.security.Provider;

/**
 * Implementation of Provider for SecureRandom. The implementation     supports the
 * "SHA1PRNG" algorithm described in JavaTM Cryptography Architecture, API
 * Specification & Reference
 */
public final class CryptoProvider extends Provider
{
	/**
	 * Creates a Provider and puts parameters
	 */
	public CryptoProvider()
	{
		super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
		put("SecureRandom.SHA1PRNG",
				"org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
		put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
	}
}