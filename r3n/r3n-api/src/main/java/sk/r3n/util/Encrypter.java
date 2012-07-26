package sk.r3n.util;

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
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (Exception e) {
            throw e;
        }
    }

    public void generateKey(int length) throws Exception {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(cipher.getAlgorithm());
            kgen.init(length);
            createKey(kgen.generateKey().getEncoded());
        } catch (Exception e) {
            throw e;
        }
    }

    public void createKey(byte[] key) {
        this.key = new SecretKeySpec(key, cipher.getAlgorithm());
    }

    public byte[] decrypt(byte[] data) throws Exception {
        try {
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw e;
        }
    }

    public void decrypt(InputStream in, OutputStream out) throws Exception {
        try {
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
            byte[] buf = new byte[1024];
            in = new CipherInputStream(in, cipher);
            int numRead;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            in.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] encrypt(byte[] data) throws Exception {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw e;
        }
    }

    public void encrypt(InputStream in, OutputStream out) throws Exception {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
            byte[] buf = new byte[1024];
            out = new CipherOutputStream(out, cipher);
            int numRead;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (Exception e) {
            throw e;
        }
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
        try {
            byte[] buffer = new byte[1024];
            ByteArray byteArray = new ByteArray();
            int read = inputStream.read(buffer);
            while (read >= 0) {
                for (int i = 0; i < read; i++) {
                    byteArray.append(buffer[i]);
                }
                read = inputStream.read(buffer);
            }
            encoded = byteArray.getBytes();
        } catch (Exception e) {
            throw e;
        }
        createKey(encoded);
    }

    public void saveKey(OutputStream outputStream) throws Exception {
        try {
            outputStream.write(key.getEncoded());
            outputStream.flush();
        } catch (Exception e) {
            throw e;
        }
    }
}
