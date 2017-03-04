/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {

    private Cipher cipher;

    private Key key;

    public Encrypter() throws Exception {
        this("Blowfish");
    }

    public Encrypter(String algorithm) throws Exception {
        super();
        cipher = Cipher.getInstance(algorithm);
    }

    public void generateKey(int length) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(cipher.getAlgorithm());
        kgen.init(length);
        createKey(kgen.generateKey().getEncoded());
    }

    public void createKey(byte[] key) {
        this.key = new SecretKeySpec(key, cipher.getAlgorithm());
    }

    public byte[] decrypt(byte[] data) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
        return cipher.doFinal(data);
    }

    public void decrypt(InputStream in, OutputStream out) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
        byte[] buf = new byte[1024];
        in = new CipherInputStream(in, cipher);
        int numRead;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        in.close();
    }

    public byte[] encrypt(byte[] data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
        return cipher.doFinal(data);
    }

    public void encrypt(InputStream in, OutputStream out) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
        byte[] buf = new byte[1024];
        out = new CipherOutputStream(out, cipher);
        int numRead;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }

    public Key getKey() {
        return key;
    }

    private Key getSecretKeySpec() {
        if (key == null) {
            createKey("Blowfish".getBytes());
        }
        return key;
    }

    public void loadKey(InputStream inputStream) throws Exception {
        byte[] encoded;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = inputStream.read(buffer);
        while (read >= 0) {
            for (int i = 0; i < read; i++) {
                out.write(buffer[i]);
            }
            read = inputStream.read(buffer);
        }
        encoded = out.toByteArray();
        createKey(encoded);
    }

    public void saveKey(OutputStream outputStream) throws Exception {
        outputStream.write(key.getEncoded());
        outputStream.flush();
    }

}
