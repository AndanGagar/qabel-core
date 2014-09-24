package de.qabel.core.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;

public class DropServerTest {
    DropServer server = new DropServer();
    URL validUrl = null;
    URL invalidUrl = null;

    @Test
    public void validURLTest() {
        try {
            validUrl = new URL("http://www.foo.org/1234567890123456789012345678901234567890123");
            server.setUrl(validUrl);
        } catch (MalformedURLException e) {

        }
    }

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void tooShortURLTest() {
        try {
            invalidUrl = new URL("http://www.bar.org/not43base64chars");
            exception.expect(IllegalArgumentException.class);
            server.setUrl(invalidUrl);
        } catch (MalformedURLException e) {

        }
    }

    @Test
    public void tooLongURLTest() {
        try {
            invalidUrl = new URL("http://www.foo.org/01234567890123456789012345678901234567890123");
            exception.expect(IllegalArgumentException.class);
            server.setUrl(invalidUrl);
        } catch (MalformedURLException e) {

        }
    }

    @Test
    public void nonBase64URLTest() {
        try {
            exception.expect(IllegalArgumentException.class);
            validUrl = new URL("http://www.baz.org/2@34567890123456789012345678901234567890123");
            server.setUrl(validUrl);
        } catch (MalformedURLException e) {

        }
    }

    @Test
    public void URLisNullTest() {
        exception.expect(IllegalArgumentException.class);
        server.setUrl(null);
    }
}
