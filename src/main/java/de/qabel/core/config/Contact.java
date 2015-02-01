package de.qabel.core.config;

import de.qabel.core.crypto.*;
import de.qabel.core.drop.DropURL;

import java.util.*;

/** 
 * https://github.com/Qabel/qabel-doc/wiki/Qabel-Client-Contact-Drop-Messages#contact
 */
public class Contact extends Entity {
	/**
	 * Primary public key of the contact
	 * Field name in serialized json: "keys"
	 */
	private QblPrimaryPublicKey primaryPublicKey;
	/**
	 * The owner identity which owns this contact.
	 * Note: This is not the identity which is represented by this contact!
	 */
	private Identity contactOwner;
	/**
	 * The key identifier of the identity the contact belongs to.
	 * A key identifier is defined as the right-most 64 bit of the identity's public fingerprint
	 * Field name in serialized json: "my_identity"
	 */
	private String contactOwnerKeyId;
	
	/**
	 * Returns the primary public key of the contact
	 * @return QblPrimaryPublicKey
	 */
	@Override
	public QblPrimaryPublicKey getPrimaryPublicKey() {
		return primaryPublicKey;
	}
	
	/**
	 * Sets the primary public key of the contacts
	 * @param key
	 */
	public void setPrimaryPublicKey(QblPrimaryPublicKey key)
	{
		primaryPublicKey = key;
	}
	
	/**
	 * Returns the identity which owns the contact
	 * @return contactOwner
	 */
	public Identity getContactOwner()
	{
		return contactOwner;
	}
	
	/**
	 * Sets the contact owning identity
	 * @param identity
	 */
	public void setContactOwner (Identity identity) {
		this.contactOwner = identity;
		this.contactOwnerKeyId = identity.getKeyIdentifier();
	}
	
	/**
	 * Returns the key identifier of the contact owning identity
	 * @return contactOwnerKeyId
	 */
	public String getContactOwnerKeyId() {
		return this.contactOwnerKeyId;
	}
	
	/**
	 * Returns the public encryption key of the contact
	 * @return QblEncPublicKey
	 */
	@Deprecated
	public QblEncPublicKey getEncryptionPublicKey()
	{
		return primaryPublicKey.getEncPublicKey();
	}
	
	/**
	 * Returns a list of the public encryption keys of the contact
	 * @return List<QblEncPublicKey>
	 */
	public List<QblEncPublicKey> getEncryptionPublicKeys() {
		return primaryPublicKey.getEncPublicKeys();
	}

	/**
	 * Adds a public encryption key to the contact
	 * @param key
	 * @throws InvalidKeyException
	 */
	public void addEncryptionPublicKey(QblEncPublicKey key) throws InvalidKeyException
	{
		primaryPublicKey.attachEncPublicKey(key);
	}
	
	/**
	 * Returns the public signing key of the contact
	 * @return QblSignPublicKey
	 */
	@Deprecated
	public QblSignPublicKey getSignaturePublicKey()
	{
		return primaryPublicKey.getSignPublicKey();
	}
	
	/**
	 * Returns a list of the public sign keys of the contact
	 * @return List<QblSignPublicKey>
	 */
	public List<QblSignPublicKey> getSignPublicKeys() {
		return primaryPublicKey.getSignPublicKeys();
	}

	/**
	 * Adds a public signing key to the contact
	 * @param key
	 * @throws InvalidKeyException
	 */
	public void addSignaturePublicKey(QblSignPublicKey key) throws InvalidKeyException
	{
		primaryPublicKey.attachSignPublicKey(key);
	}
	
	
	/**
	 * Creates an instance of Contact and sets the contactOwner and contactOwnerKeyId
	 * @param owner
	 */
	public Contact(Identity owner, Collection<DropURL> dropUrls, QblPrimaryPublicKey pubKey) {
		super(dropUrls);
		this.contactOwner = owner;
		this.contactOwnerKeyId = owner.getKeyIdentifier();
		this.setPrimaryPublicKey(pubKey);
	}
	
	/**
	 * Creates an instance of Contact and sets the contactOwnerId.
	 * Attention: This constructor is intended for deserialization purposes. The contactOwner needs to be set afterwards
	 * @param ownerKeyId
	 */
	protected Contact(String ownerKeyId, Collection<DropURL> dropUrls, QblPrimaryPublicKey pubKey) {
		super(dropUrls);
		this.contactOwnerKeyId = ownerKeyId;
		this.setPrimaryPublicKey(pubKey);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contactOwner == null) ? 0 : contactOwner.hashCode());
		result = prime * result + ((contactOwnerKeyId == null) ? 0 : contactOwnerKeyId.hashCode());
		result = prime * result + ((primaryPublicKey == null) ? 0 : primaryPublicKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (contactOwner == null) {
			if (other.contactOwner != null)
				return false;
		} else if (!contactOwner.equals(other.contactOwner))
			return false;
		if (contactOwnerKeyId == null) {
			if (other.contactOwnerKeyId != null)
				return false;
		} else if (!contactOwnerKeyId.equals(other.contactOwnerKeyId))
			return false;
		if (primaryPublicKey == null) {
			if (other.primaryPublicKey != null)
				return false;
		} else if (!primaryPublicKey.equals(other.primaryPublicKey))
			return false;
		return true;
	}
}
