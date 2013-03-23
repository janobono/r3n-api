package sk.r3n.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CloneSerializable {

    public static Object clone(Object object) {
        ObjectOutputStream os = null;
        ByteArrayOutputStream bos = null;
        ObjectInputStream is = null;
        ByteArrayInputStream bis = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(object);

            bis = new ByteArrayInputStream(bos.toByteArray());
            is = new ObjectInputStream(bis);
            Object clone = is.readObject();

            return clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(bos);
            close(os);
            close(bis);
            close(is);
        }
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }
}
