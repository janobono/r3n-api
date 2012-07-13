package sk.r3n.protocol.zip;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new URLConnection(u);
	}

}
