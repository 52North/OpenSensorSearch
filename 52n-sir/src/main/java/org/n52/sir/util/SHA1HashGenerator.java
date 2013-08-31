package org.n52.sir.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1HashGenerator implements HashGenerator {

	@Override
	public String generate(String s) {
		try {
			// create an identifier based on SHA-1 for a string
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			digest.update(s.getBytes());
			byte[] mdbytes = digest.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException exception) {
			return null;
		}

	}

}
