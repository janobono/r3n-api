package sk.r3n.util;

import java.io.Serializable;
import java.util.Arrays;

public class ByteArray implements Comparable<ByteArray>, Serializable {

	private static final long serialVersionUID = 6829059423449932899L;

	public static final String BIN_PREFIX = "0x";

	public static byte charToHexa(char c) {
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':
		case 'A':
			return 10;
		case 'b':
		case 'B':
			return 11;
		case 'c':
		case 'C':
			return 12;
		case 'd':
		case 'D':
			return 13;
		case 'e':
		case 'E':
			return 14;
		case 'f':
		case 'F':
			return 15;
		}
		throw new IllegalArgumentException("Only hexa chars!");
	}

	public static byte parseByte(String string) {
		if (string.length() == 0)
			return 0;
		if (string.startsWith(BIN_PREFIX)) {
			string = string.substring(BIN_PREFIX.length());
			if (string.length() == 2) {
				byte[] hexaNumber = string.getBytes();
				byte result = 0;
				result = (byte) (charToHexa((char) hexaNumber[0]) * 16 + charToHexa((char) hexaNumber[1]));
				return result;
			}
			if (string.length() == 8) {
				byte[] binNumber = string.getBytes();
				byte result = 0;
				for (int i = 0; i < binNumber.length; i++) {
					if (binNumber[i] == '0') {
						binNumber[i] = 0;
						continue;
					}
					if (binNumber[i] == '1') {
						binNumber[i] = 1;
						continue;
					}
					throw new IllegalArgumentException();
				}
				result = (byte) (binNumber[0] * 128 + binNumber[1] * 64
						+ binNumber[2] * 32 + binNumber[3] * 16 + binNumber[4]
						* 8 + binNumber[5] * 4 + binNumber[6] * 2 + binNumber[7] * 1);
				return result;
			}
			throw new IllegalArgumentException();
		}
		int number = Integer.parseInt(string);
		if (number < 0 || number > 255)
			throw new IllegalArgumentException();
		return (byte) number;
	}

	public static byte[] parseBytes(String string) {
		String[] dataArray = string.split(" ");
		byte[] result = new byte[dataArray.length];
		for (int i = 0; i < dataArray.length; i++) {
			result[i] = parseByte(dataArray[i]);
		}
		return result;
	}

	public static ByteArray parseByteArray(String s) {
		ByteArray result = new ByteArray();
		result.append(parseBytes(s));
		return result;
	}

	public static String toString(byte value) {
		int intValue = value;
		if (value < 0)
			intValue = 256 + value;
		String result = Integer.toHexString(intValue);
		if (result.length() == 1)
			result = "0" + result;
		return BIN_PREFIX + result;
	}

	public static String toString(byte[] value) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			result.append(toString(value[i]));
			if (i < value.length - 1)
				result.append(" ");
		}
		return result.toString();
	}

	public static String toString(ByteArray value) {
		return toString(value.getBytes());
	}

	private byte[] byteArray;

	public ByteArray() {
		byteArray = new byte[0];
	}

	public ByteArray(byte b) {
		this();
		append(b);
	}

	public ByteArray(byte[] byteArray) {
		this();
		append(byteArray);
	}

	public ByteArray(ByteArray byteArray) {
		this();
		append(byteArray);
	}

	public ByteArray(char c) {
		this();
		append(c);
	}

	public ByteArray(int i) {
		this();
		append(i);
	}

	public ByteArray(String s) {
		this();
		append(s);
	}

	public byte[] append(byte b) {
		int i = this.byteArray.length;
		byte byteArray[] = new byte[i + 1];
		System.arraycopy(this.byteArray, 0, byteArray, 0, i);
		byteArray[i] = b;
		this.byteArray = byteArray;
		return this.byteArray;
	}

	public byte[] append(byte[] b) {
		int i = this.byteArray.length;
		int j = b.length;
		byte[] result = new byte[i + j];
		System.arraycopy(this.byteArray, 0, result, 0, i);
		System.arraycopy(b, 0, result, i, j);
		this.byteArray = result;
		return this.byteArray;
	}

	public byte[] append(ByteArray byteArray) {
		byte result[] = byteArray.getBytes();
		return append(result);
	}

	public byte[] append(char c) {
		char ac[] = new char[1];
		ac[0] = c;
		String s = new String(ac);
		int i = s.hashCode();
		if (i < 256)
			return append(i);
		byte result[] = new byte[0];
		result = s.getBytes();
		return append(result);
	}

	public byte[] append(int i) {
		if (i > 255)
			return this.byteArray;
		return append((byte) i);
	}

	public byte[] append(String s) {
		byte result[] = new byte[0];
		result = s.getBytes();
		return append(result);
	}

	public void clear() {
		byteArray = new byte[] {};
	}

	public int compareTo(ByteArray byteArray) {
		if (Arrays.equals(this.byteArray, byteArray.getBytes())) {
			return 0;
		}
		if (length() == byteArray.length()) {
			int value1 = 0;
			for (int i = 0; i < this.byteArray.length; i++) {
				value1 += this.byteArray[i];
			}
			int value2 = 0;
			for (int i = 0; i < byteArray.getBytes().length; i++) {
				value2 += byteArray.getBytes()[i];
			}
			return value1 - value2;
		}
		return length() - byteArray.length();
	}

	public boolean endsWith(byte b) {
		if (this.byteArray.length == 0)
			return false;
		return this.byteArray[length() - 1] == b;
	}

	public boolean endsWith(byte[] b) {
		if (this.byteArray.length < b.length)
			return false;
		for (int i = 0; i < b.length; i++)
			if (this.byteArray[length() - 1 - i] != b[b.length - 1 - i])
				return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ByteArray) {
			ByteArray objByteArray = (ByteArray) obj;
			return Arrays.equals(objByteArray.getBytes(), getBytes());
		}
		return false;
	}

	public byte[] getBytes() {
		return byteArray;
	}

	public int indexOf(byte b) {
		return indexOf(b, 0);
	}

	public int indexOf(byte b, int startPosition) {
		int result = -1;
		if (startPosition >= this.byteArray.length)
			return result;
		for (int l = startPosition; l < this.byteArray.length; l++) {
			if (this.byteArray[l] != b)
				continue;
			result = l;
			break;
		}
		return result;
	}

	public int indexOf(byte[] b) {
		return indexOf(b, 0);
	}

	public int indexOf(byte[] b, int startPosition) {
		int result = -1;
		if (b.length == 0)
			return result;
		if ((startPosition + b.length) - 1 >= this.byteArray.length)
			return result;
		for (int l = startPosition; l < this.byteArray.length - (b.length - 1); l++) {
			if (this.byteArray[l] != b[0])
				continue;
			boolean flag = true;
			for (int i1 = l; i1 < b.length + l; i1++) {
				if (this.byteArray[i1] == b[i1 - l])
					continue;
				flag = false;
				break;
			}
			if (!flag)
				continue;
			result = l;
			break;
		}
		return result;
	}

	public int indexOf(ByteArray byteArray) {
		return indexOf(byteArray.getBytes(), 0);
	}

	public int indexOf(ByteArray byteArray, int startPosition) {
		return indexOf(byteArray.getBytes(), startPosition);
	}

	public byte[] insert(int position, byte b) {
		int j = this.byteArray.length;
		byte result[] = new byte[j + 1];
		System.arraycopy(this.byteArray, 0, result, 0, position);
		result[position] = b;
		System.arraycopy(this.byteArray, position, result, position + 1, j
				- position);
		this.byteArray = result;
		return this.byteArray;
	}

	public byte[] insert(int position, byte[] b) {
		int j = this.byteArray.length;
		int k = b.length;
		byte result[] = new byte[j + k];
		System.arraycopy(this.byteArray, 0, result, 0, position);
		System.arraycopy(b, 0, result, position, k);
		System.arraycopy(this.byteArray, position, result, position + k, j
				- position);
		this.byteArray = result;
		return this.byteArray;
	}

	public byte[] insert(int position, ByteArray byteArray) {
		byte result[] = byteArray.getBytes();
		return insert(position, result);
	}

	public int length() {
		return byteArray.length;
	}

	public byte[] setAt(int position, byte b) {
		this.byteArray[position] = b;
		return this.byteArray;
	}

	public boolean startsWith(byte b) {
		if (this.byteArray.length == 0)
			return false;
		return this.byteArray[0] == b;
	}

	public boolean startsWith(byte[] b) {
		if (this.byteArray.length < b.length)
			return false;
		for (int i = 0; i < b.length; i++)
			if (this.byteArray[i] != b[i])
				return false;
		return true;
	}

	public ByteArray subArray(int to) {
		if (this.byteArray.length < to)
			return null;
		byte result[] = new byte[to];
		for (int j = 0; j < to; j++)
			result[j] = this.byteArray[j];
		return new ByteArray(result);
	}

	public ByteArray subArray(int from, int to) {
		if (from > to)
			return null;
		if (this.byteArray.length < to)
			return null;
		byte result[] = new byte[to - from];
		for (int k = from; k < to; k++)
			result[k - from] = this.byteArray[k];
		return new ByteArray(result);
	}

	@Override
	public String toString() {
		return toString(this);
	}

	public byte valueAt(int p) {
		return this.byteArray[p];
	}
}
