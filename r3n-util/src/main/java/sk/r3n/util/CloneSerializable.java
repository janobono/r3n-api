/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.io.*;

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

            return is.readObject();
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
