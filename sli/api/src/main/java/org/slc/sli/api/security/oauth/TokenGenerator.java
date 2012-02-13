package org.slc.sli.api.security.oauth;

import java.security.SecureRandom;

public class TokenGenerator {

	private static char[] validChars = null;

	static {
		validChars = new char[62];

		// upper case
		for (int i = 0; i < 26; i++) {
			validChars[i] = (char) (97 + i);
		}

		// lower case
		for (int i = 0; i < 26; i++) {
			validChars[i + 26] = (char) (65 + i);
		}

		// digits
		for (int i = 0; i < 10; i++) {
			validChars[i + 52] = (char) (48 + i);
		}

	}

	public static String generateToken(int len) {
		SecureRandom rand = new SecureRandom();
		StringBuffer id = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			id.append(validChars[rand.nextInt(validChars.length)]);
		}
		return id.toString();
	}

}
