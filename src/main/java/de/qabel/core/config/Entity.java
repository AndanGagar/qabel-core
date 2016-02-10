package de.qabel.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.qabel.core.crypto.QblECPublicKey;
import de.qabel.core.drop.DropURL;

/**
 * Entity is an abstract class for a participant in a Qabel Drop
 * communication.
 */
public abstract class Entity extends SyncSettingItem {
	private static final long serialVersionUID = 570661846263644501L;

	private final Set<DropURL> dropUrls;

	public Entity(Collection<DropURL> drops) {
		if (drops != null) {
			this.dropUrls = new HashSet<>(drops);
		} else {
			this.dropUrls = new HashSet<>();
		}
	}
	
	abstract public QblECPublicKey getEcPublicKey();

	/**
	 * Returns the key identifier. The key identifier consists of the right-most 64 bit of the public fingerprint
	 * 
	 * @return key identifier
	 */
	public String getKeyIdentifier() {
		return this.getEcPublicKey().getReadableKeyIdentifier();
	}

	public Set<DropURL> getDropUrls() {
		return Collections.unmodifiableSet(dropUrls);
	}

	public void addDrop(DropURL drop) {
		this.dropUrls.add(drop);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dropUrls == null) ? 0 : dropUrls.hashCode());
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
		Entity other = (Entity) obj;
		if (!dropUrls.equals(other.dropUrls))
			return false;
		return true;
	}
}
