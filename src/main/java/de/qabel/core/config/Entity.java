package de.qabel.core.config;

import java.security.InvalidKeyException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.qabel.core.crypto.QblEncPublicKey;
import de.qabel.core.crypto.QblPrimaryPublicKey;
import de.qabel.core.crypto.QblSignPublicKey;
import de.qabel.core.drop.DropURL;

public abstract class Entity extends SyncSettingItem {
	private final Set<DropURL> dropUrls;
	private final Set<AbstractModuleSettings> moduleSettings = new HashSet<AbstractModuleSettings>(); // TODO: Will

	public Entity(Collection<DropURL> drops) {
		if (drops != null) {
			this.dropUrls = new HashSet<>(drops);
		} else {
			this.dropUrls = new HashSet<>();
		}
	}
	
	abstract public QblPrimaryPublicKey getPrimaryPublicKey();

	public List<QblEncPublicKey> getEncryptionPublicKeys() {
		return this.getPrimaryPublicKey().getEncPublicKeys();
	}

	public void addEncryptionPublicKey(QblEncPublicKey key) throws InvalidKeyException {
		this.getPrimaryPublicKey().attachEncPublicKey(key);
	}

	public List<QblSignPublicKey> getSignPublicKeys() {
		return this.getPrimaryPublicKey().getSignPublicKeys();
	}

	public void addSignaturePublicKey(QblSignPublicKey key) throws InvalidKeyException {
		this.getPrimaryPublicKey().attachSignPublicKey(key);
	}

	/**
	 * Returns the key identifier. The key identifier consists of the right-most 64 bit of the public fingerprint
	 * 
	 * @return key identifier
	 */
	public String getKeyIdentifier() {
		return this.getPrimaryPublicKey().getReadableKeyIdentifier();
	}

	public Set<DropURL> getDropUrls() {
		return Collections.unmodifiableSet(dropUrls);
	}

	public void addDrop(DropURL drop) {
		this.dropUrls.add(drop);
	}

	public Set<AbstractModuleSettings> getModuleSettings() {
		return moduleSettings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dropUrls == null) ? 0 : dropUrls.hashCode());
		result = prime * result + ((moduleSettings == null) ? 0 : moduleSettings.hashCode());
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
		if (dropUrls == null) {
			if (other.dropUrls != null)
				return false;
		} else if (!dropUrls.equals(other.dropUrls))
			return false;
		if (moduleSettings == null) {
			if (other.moduleSettings != null)
				return false;
		} else if (!moduleSettings.equals(other.moduleSettings))
			return false;
		return true;
	}
}
