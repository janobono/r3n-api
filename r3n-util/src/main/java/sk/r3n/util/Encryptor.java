/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

/**
 * {@link Cipher} utility methods.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class Encryptor {

    private final Cipher cipher;

    private Key key;

    /**
     * Default constructor.
     *
     * @throws Exception
     */
    public Encryptor() throws Exception {
        this("Blowfish");
    }

    /**
     * Constructor with transformation param.
     *
     * @param transformation the name of the transformation, e.g., <i>AES/CBC/PKCS5Padding</i>.
     *                       See the Cipher section in the
     *                       <a href="{@docRoot}/../technotes/guides/security/StandardNames.html#Cipher">Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     *                       for information about standard transformation names.
     * @throws Exception
     */
    public Encryptor(String transformation) throws Exception {
        super();
        cipher = Cipher.getInstance(transformation);
    }

    /**
     * Generate key.
     *
     * @param length key length
     * @throws Exception
     */
    public void generateKey(int length) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(cipher.getAlgorithm());
        kgen.init(length);
        createKey(kgen.generateKey().getEncoded());
    }

    /**
     * Create key.
     *
     * @param key byte array key representation
     */
    public void createKey(byte[] key) {
        this.key = new SecretKeySpec(key, cipher.getAlgorithm());
    }

    /**
     * Decrypt data in byte array format.
     *
     * @param data source data
     * @return decrypted data
     * @throws Exception
     */
    public byte[] decrypt(byte[] data) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
        return cipher.doFinal(data);
    }

    /**
     * Decrypt stream to stream.
     *
     * @param in  source stream
     * @param out target stream
     * @throws Exception
     */
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

    /**
     * Encrypt data in byte array format.
     *
     * @param data source data
     * @return encrypted data
     * @throws Exception
     */
    public byte[] encrypt(byte[] data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
        return cipher.doFinal(data);
    }

    /**
     * @param in
     * @param out
     * @throws Exception
     */
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

    /**
     * Return key.
     *
     * @return key
     */
    public Key getKey() {
        return key;
    }

    private Key getSecretKeySpec() {
        if (key == null) {
            createKey("Blowfish".getBytes());
        }
        return key;
    }

    /**
     * Load key from stream.
     *
     * @param inputStream stream
     * @throws Exception If the first byte cannot be read for any reason other than the end of the file,
     *                   if the input stream has been closed, or if some other I/O error occurs.
     */
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

    /**
     * Save key to stream.
     *
     * @param outputStream stream
     * @throws Exception if an I/O error occurs.
     */
    public void saveKey(OutputStream outputStream) throws Exception {
        outputStream.write(key.getEncoded());
        outputStream.flush();
    }
}
