package sk.r3n.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.close(bos);
            FileUtil.close(os);
            FileUtil.close(bis);
            FileUtil.close(is);
        }
    }
}
