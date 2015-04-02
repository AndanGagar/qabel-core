package de.qabel.core.config;

import de.qabel.core.crypto.*;
import de.qabel.core.drop.DropURL;

import java.util.*;

/**
 * https://github.com/Qabel/qabel-doc/wiki/Qabel-Client-Contact-Drop-Messages#contact
 */
public class Contact extends Entity {
	/**
	 *
	 */
	private static final long serialVersionUID = 3971315594579958553L;
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
	 * Creates an instance of Contact and sets the contactOwner and contactOwnerKeyId
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
	 */
	protected Contact(String ownerKeyId, Collection<DropURL> dropUrls, QblPrimaryPublicKey pubKey) {
		super(dropUrls);
		this.contactOwnerKeyId = ownerKeyId;
		this.setPrimaryPublicKey(pubKey);
	}

	/**
	 * Creates an instance of Contact without any attributes set
	 * Attention: This constructor is intended for deserialization purposes when getting copied by ContactsActor
	 */
	protected Contact() {
		super(null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((contactOwner == null) ? 0 : contactOwner.hashCode());
		result = prime * result
				+ ((contactOwnerKeyId == null) ? 0 : contactOwnerKeyId.hashCode());
		result = prime * result
				+ ((primaryPublicKey == null) ? 0 : primaryPublicKey.hashCode());
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
