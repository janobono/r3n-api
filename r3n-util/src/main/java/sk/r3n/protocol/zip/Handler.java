/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.protocol.zip;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * Zip file handler.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class Handler extends URLStreamHandler {

    public static void register() {
        final String packageName = Handler.class.getPackage().getName();
        final String pkg = packageName.substring(0, packageName.lastIndexOf('.'));
        final String protocolPathProp = "java.protocol.handler.pkgs";

        String uriHandlers = System.getProperty(protocolPathProp, "");
        if (uriHandlers.indexOf(pkg) == -1) {
            if (uriHandlers.length() != 0)
                uriHandlers += "|";
            uriHandlers += pkg;
            System.setProperty(protocolPathProp, uriHandlers);
        }
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new URLConnection(u);
    }

}
