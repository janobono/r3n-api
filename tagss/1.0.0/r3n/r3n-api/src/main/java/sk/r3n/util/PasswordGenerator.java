package sk.r3n.util;

import java.security.SecureRandom;

public class PasswordGenerator {

	public static final short NUMERIC = 10;
	public static final short ALPHA = 20;
	public static final short ALPHA_NUMERIC = 30;

	private char[] numeric = new char[] { '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0' };

	private char[] alpha = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z' };

	private char[] alphaNumberic = new char[] { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0' };

	private SecureRandom secureRandom;

	public PasswordGenerator() throws Exception {
		this("SHA1PRNG");
	}

	public PasswordGenerator(String algorithm) throws Exception {
		super();
		secureRandom = SecureRandom.getInstance(algorithm);
	}

	public String generatePassword(short type, int length) {
		if (length <= 0)
			return "";
		switch (type) {
		case NUMERIC:
		case ALPHA:
		case ALPHA_NUMERIC:
			break;
		default:
			throw new IllegalArgumentException();
		}
		StringBuffer stringBuffer = new StringBuffer();
		switch (type) {
		case NUMERIC:
			for (int i = 0; i < length; i++) {
				stringBuffer.append(numeric[secureRandom
						.nextInt(numeric.length)]);
			}
			break;
		case ALPHA:
			for (int i = 0; i < length; i++) {
				stringBuffer.append(alpha[secureRandom.nextInt(alpha.length)]);
			}
			break;
		default:
			for (int i = 0; i < length; i++) {
				stringBuffer.append(alphaNumberic[secureRandom
						.nextInt(alphaNumberic.length)]);
			}
			break;
		}
		return stringBuffer.toString();
	}

}
