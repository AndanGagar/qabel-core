package de.qabel.core.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

	private final static CryptoUtils INSTANCE = new CryptoUtils();

	private final static String ASYM_KEY_ALGORITHM = "RSA";
	private final static int ASYM_KEY_SIZE_BIT = 2048;
	private final static String SYMM_KEY_ALGORITHM = "AES";
	private final static String SYMM_TRANSFORMATION = "AES/CTR/NoPadding";
	private final static int SYMM_NONCE_SIZE_BIT = 128;
	private final static int AES_KEY_SIZE_BYTE = 32;
	private final static int ENCRYPTED_AES_KEY_SIZE_BYTE = 256;

	private KeyPairGenerator keyGen;
	private SecureRandom secRandom;
	private MessageDigest messageDigest;
	private Cipher symmetricCipher;

	private CryptoUtils() {

		try {
			secRandom = new SecureRandom();

			keyGen = KeyPairGenerator.getInstance(ASYM_KEY_ALGORITHM);
			keyGen.initialize(ASYM_KEY_SIZE_BIT);

			messageDigest = MessageDigest.getInstance("SHA-512");
			symmetricCipher = Cipher.getInstance(SYMM_TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static CryptoUtils getInstance() {
		return CryptoUtils.INSTANCE;
	}

	/**
	 * Returns a new KeyPair
	 * 
	 * @return KeyPair
	 */
	public KeyPair generateKeyPair() {
		return keyGen.generateKeyPair();
	}

	/**
	 * Returns a random byte array with an arbitrary size
	 * 
	 * @param numBytes
	 *            Number of random bytes
	 * @return byte[ ] with random bytes
	 */
	public byte[] getRandomBytes(int numBytes) {
		byte[] ranBytes = new byte[numBytes];
		secRandom.nextBytes(ranBytes);
		return ranBytes;
	}

	/**
	 * Returns the SHA512 digest for a byte array
	 * 
	 * @param bytes
	 *            byte[ ] to get the digest from
	 * @return SHA512 digest as as String in the following format:
	 *         "00:11:aa:bb:..."
	 */
	public byte[] getSHA512sum(byte[] bytes) {
		byte[] digest = messageDigest.digest(bytes);
		return digest;
	}

	public String getSHA512sumHumanReadable(byte[] bytes) {
		byte[] digest = getSHA512sum(bytes);

		StringBuilder sb = new StringBuilder(191);

		for (int i = 0; i < digest.length - 1; i++) {
			sb.append(String.format("%02x", digest[i] & 0xff));
			sb.append(":");
		}
		sb.append(String.format("%02x", digest[digest.length - 1] & 0xff));
		return sb.toString();
	}

	/**
	 * Returns the SHA512 digest for a String
	 * 
	 * @param plain
	 *            Input String
	 * @return SHA512 digest as as String in the following format:
	 *         "00:11:aa:bb:..."
	 */
	public byte[] getSHA512sum(String plain) {
		return getSHA512sum(plain.getBytes());
	}

	public String getSHA512sumHumanReadable(String plain) {
		return getSHA512sumHumanReadable(plain.getBytes());
	}

	public byte[] rsaSignKeyPair(QblKeyPair qkp, QblPrimaryKeyPair qpkp) {
		// TODO: null check
		byte[] sign = null;
		try {
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(qpkp.getRSAPrivateKey());
			signer.update(qkp.getPublicKeyFingerprint());
			sign = signer.sign();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

	public boolean rsaValidateKeySignature(QblPrimaryPublicKey primaryKey,
			QblSubPublicKey subKey) {
		// TODO: null check
		boolean isValid = false;
		try {
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initVerify(primaryKey.getRSAPublicKey());
			signer.update(subKey.getPublicKeyFingerprint());
			isValid = signer.verify(subKey.getPrimaryKeySignature());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isValid;
	}

	public byte[] rsaEncryptForRecipient(byte[] message,
			QblEncPublicKey reciPubKey) {
		byte[] cipherText = null;
		try {
			Cipher cipher = Cipher
					.getInstance("RSA/ECB/OAEPWITHSHA1ANDMGF1PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, reciPubKey.getRSAPublicKey(),
					secRandom);
			cipherText = cipher.doFinal(message);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cipherText;
	}

	public byte[] rsaDecrypt(byte[] cipherText, RSAPrivateKey privKey) {
		byte[] plaintext = null;
		try {
			Cipher cipher = Cipher
					.getInstance("RSA/ECB/OAEPWITHSHA1ANDMGF1PADDING");
			cipher.init(Cipher.DECRYPT_MODE, privKey, secRandom);
			plaintext = cipher.doFinal(cipherText);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plaintext;
	}

	/**
	 * Returns the encrypted byte[] of the given plain text, i.e.
	 * ciphertext=enc(plaintext,key) The algorithm, mode and padding is set in
	 * constant SYMM_TRANSFORMATION
	 * 
	 * @param plainText
	 *            message which will be encrypted
	 * @param key
	 *            symmetric key which is used for en- and decryption
	 * @return cipher text which is the result of the encryption
	 */
	public byte[] symmEncrypt(byte[] plainText, byte[] key) {
		byte[] rand;
		ByteArrayOutputStream cipherText = new ByteArrayOutputStream();
		IvParameterSpec nonce;

		rand = getRandomBytes(SYMM_NONCE_SIZE_BIT / 8);
		nonce = new IvParameterSpec(rand);

		SecretKeySpec symmetricKey = new SecretKeySpec(key, SYMM_KEY_ALGORITHM);

		try {
			cipherText.write(rand);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			symmetricCipher.init(Cipher.ENCRYPT_MODE, symmetricKey, nonce);
			cipherText.write(symmetricCipher.doFinal(plainText));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cipherText.toByteArray();
	}

	/**
	 * Returns the plain text of the encrypted input
	 * plaintext=enc⁻¹(ciphertext,key) The algorithm, mode and padding is set in
	 * constant SYMM_TRANSFORMATION
	 * 
	 * @param cipherText
	 *            encrypted message which will be decrypted
	 * @param key
	 *            symmetric key which is used for en- and decryption
	 * @return plain text which is the result of the decryption
	 */
	public byte[] symmDecrypt(byte[] cipherText, byte[] key) {
		ByteArrayInputStream bi = new ByteArrayInputStream(cipherText);
		byte[] rand = new byte[SYMM_NONCE_SIZE_BIT / 8];
		byte[] encryptedPlainText = new byte[cipherText.length
				- SYMM_NONCE_SIZE_BIT / 8];
		byte[] plainText = null;
		IvParameterSpec nonce;
		SecretKeySpec symmetricKey;

		try {
			bi.read(rand);
			bi.read(encryptedPlainText);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		nonce = new IvParameterSpec(rand);
		symmetricKey = new SecretKeySpec(key, SYMM_KEY_ALGORITHM);

		try {
			symmetricCipher.init(Cipher.DECRYPT_MODE, symmetricKey, nonce);
			plainText = symmetricCipher.doFinal(encryptedPlainText);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plainText;
	}

	/**
	 * Hybrid encrypts a String message for a recipient. The String message is
	 * encrypted with a random AES key. The AES key gets RSA encrypted with the
	 * recipients public key.
	 * 
	 * @param message
	 *            String message to encrypt
	 * @param recipient
	 *            Recipient to encrypt message for
	 * @return hybrid encrypted String message
	 */
	public byte[] encryptMessage(String message, QblEncPublicKey recipient) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		byte[] aesKey = getRandomBytes(AES_KEY_SIZE_BYTE);

		try {
			bs.write(rsaEncryptForRecipient(aesKey, recipient));
			bs.write(symmEncrypt(message.getBytes(), aesKey));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toByteArray();
	}

	/**
	 * Decrypts a hybrid encrypted String message. The AES key is decrypted
	 * using the own private key. The decrypted AES key is used to decrypt the
	 * String message
	 * 
	 * @param cipherText
	 *            hybrid encrypted String message
	 * @param privKey
	 *            private key to encrypt String message with
	 * @return decrypted String message
	 */
	public String decryptMessage(byte[] cipherText, RSAPrivateKey privKey) {
		ByteArrayInputStream bs = new ByteArrayInputStream(cipherText);
		byte[] encryptedAesKey = new byte[ENCRYPTED_AES_KEY_SIZE_BYTE];
		byte[] aesCipherText = new byte[bs.available()
				- ENCRYPTED_AES_KEY_SIZE_BYTE];

		try {
			bs.read(encryptedAesKey);
			bs.read(aesCipherText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] aesKey = rsaDecrypt(encryptedAesKey, privKey);
		return new String(symmDecrypt(aesCipherText, aesKey));
	}
}
