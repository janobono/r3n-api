/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.io.Serializable;
import java.util.Arrays;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class ByteArray implements Serializable {

    public static ByteArray parseByteArray(String string) {
        ByteArray result = new ByteArray();
        result.append(new HexBinaryAdapter().unmarshal(string));
        return result;
    }
    private byte[] byteArray;

    public ByteArray() {
        byteArray = new byte[0];
    }

    public byte[] append(byte b) {
        int i = this.byteArray.length;
        byte result[] = new byte[i + 1];
        System.arraycopy(this.byteArray, 0, result, 0, i);
        result[i] = b;
        this.byteArray = result;
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

    public byte[] append(String s) {
        return append(s.getBytes());
    }

    public void clear() {
        byteArray = new byte[]{};
    }

    public boolean endsWith(byte b) {
        if (this.byteArray.length == 0) {
            return false;
        }
        return this.byteArray[length() - 1] == b;
    }

    public boolean endsWith(byte[] b) {
        if (this.byteArray.length < b.length) {
            return false;
        }
        for (int i = 0; i < b.length; i++) {
            if (this.byteArray[length() - 1 - i] != b[b.length - 1 - i]) {
                return false;
            }
        }
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Arrays.hashCode(this.byteArray);
        return hash;
    }

    public byte[] getBytes() {
        return byteArray;
    }

    public int indexOf(byte b) {
        return indexOf(b, 0);
    }

    public int indexOf(byte b, int startPosition) {
        int result = -1;
        if (startPosition >= this.byteArray.length) {
            return result;
        }
        for (int l = startPosition; l < this.byteArray.length; l++) {
            if (this.byteArray[l] != b) {
                continue;
            }
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
        if (b.length == 0) {
            return result;
        }
        if ((startPosition + b.length) - 1 >= this.byteArray.length) {
            return result;
        }
        for (int l = startPosition; l < this.byteArray.length - (b.length - 1); l++) {
            if (this.byteArray[l] != b[0]) {
                continue;
            }
            boolean flag = true;
            for (int i1 = l; i1 < b.length + l; i1++) {
                if (this.byteArray[i1] == b[i1 - l]) {
                    continue;
                }
                flag = false;
                break;
            }
            if (!flag) {
                continue;
            }
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
        if (this.byteArray.length == 0) {
            return false;
        }
        return this.byteArray[0] == b;
    }

    public boolean startsWith(byte[] b) {
        if (this.byteArray.length < b.length) {
            return false;
        }
        for (int i = 0; i < b.length; i++) {
            if (this.byteArray[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public ByteArray subArray(int to) {
        if (this.byteArray.length < to) {
            return null;
        }
        byte result[] = new byte[to];
        System.arraycopy(this.byteArray, 0, result, 0, to);
        ByteArray array = new ByteArray();
        array.append(result);
        return array;
    }

    public ByteArray subArray(int from, int to) {
        if (from > to) {
            return null;
        }
        if (this.byteArray.length < to) {
            return null;
        }
        byte result[] = new byte[to - from];
        for (int k = from; k < to; k++) {
            result[k - from] = this.byteArray[k];
        }
        ByteArray array = new ByteArray();
        array.append(result);
        return array;
    }

    @Override
    public String toString() {
        return new HexBinaryAdapter().marshal(getBytes());
    }

    public byte valueAt(int p) {
        return this.byteArray[p];
    }
}
