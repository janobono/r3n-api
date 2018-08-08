/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.protocol.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class URLConnection extends java.net.URLConnection {

    private final String file;

    private final String zipEntryName;

    private ZipFile zipFile;

    private ZipEntry zipEntry;

    URLConnection(URL url) throws MalformedURLException {
        super(url);
        String spec = url.getFile();
        int separator = spec.indexOf('!');
        if (separator == -1) {
            throw new MalformedURLException("no ! found in url spec:" + spec);
        }
        file = spec.substring(0, separator);
        zipEntryName = spec.substring(separator + 1);
    }

    @Override
    public void connect() throws IOException {
        this.zipFile = new ZipFile(file);
        this.zipEntry = zipFile.getEntry(zipEntryName);
        if (zipEntry == null) {
            throw new IOException("Entry " + zipEntryName + " not found in file " + file);
        }
        this.connected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }
        return zipFile.getInputStream(zipEntry);
    }

    @Override
    public int getContentLength() {
        if (!connected) {
            return -1;
        }
        return (int) zipEntry.getSize();
    }

}
