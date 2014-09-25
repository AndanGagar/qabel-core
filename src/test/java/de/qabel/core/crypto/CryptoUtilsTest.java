package de.qabel.core.crypto;

import static org.junit.Assert.*;

import org.junit.Test;

import de.qabel.core.crypto.CryptoUtils;

public class CryptoUtilsTest {

	CryptoUtils cu = CryptoUtils.getInstance();

	@Test
	public void sha512Test() {

		String str = "The quick brown fox jumps over the lazy dog";
		String digest = cu.getSHA512sumHumanReadable(str);
		String expectedDigest = "07:e5:47:d9:58:6f:6a:73:f7:3f:ba:c0:43:5e:d7:69:51:21:8f:b7:d0:c8:d7:88:a3:09:d7:85:43:6b:bb:64:2e:93:a2:52:a9:54:f2:39:12:54:7d:1e:8a:3b:5e:d6:e1:bf:d7:09:78:21:23:3f:a0:53:8f:3d:b8:54:fe:e6";

		assertEquals(digest, expectedDigest);
	}

	@Test
	public void rsaEncryptForRecipientTest() {
		QblPrimaryKeyPair qpkp = new QblPrimaryKeyPair();

		String secretMessage = "SecretMessage";

		byte[] ciphertext = cu.rsaEncryptForRecipient(secretMessage.getBytes(),
				qpkp.getQblEncPublicKey());

		String decryptedMessage = new String(cu.rsaDecrypt(ciphertext,
				qpkp.getQblEncPrivateKey()));

		assertTrue(secretMessage.equals(decryptedMessage));
	}
}
