package de.qabel.core.config;

import java.io.Serializable;
import java.util.*;

/**
 * EntityMaps provide functionality to lookup an Entity based
 * on its key identifier.
 * 
 * @see Entity
 */
abstract class EntityMap<T extends Entity> implements Serializable {
	private static final long serialVersionUID = -8004819460313825206L;
	private final Map<String, T> entities = Collections.synchronizedMap(new HashMap<String, T>());

	/**
	 * Returns unmodifiable set of contained contacts
	 * 
	 * @return Set<Contact>
	 */
	public synchronized Set<T> getEntities() {
		return Collections.unmodifiableSet(new HashSet<>(entities.values()));
	}

	public synchronized boolean put(T entity) {
		if (this.entities.put(entity.getKeyIdentifier(), entity) == null) {
			return false;
		}
		return true;
	}

	public synchronized boolean remove(T entity) {
		return (entity != null && this.entities.remove(entity.getKeyIdentifier()) != null);
	}

	public synchronized boolean remove(String keyIdentifier) {
		return (keyIdentifier != null && this.entities.remove(keyIdentifier) != null);
	}

	/**
	 * Get entity by key identifier (right most 64 bit of the identity's public fingerprint)
	 * @param keyIdentifier
	 * @return entity to which the key identifier is mapped or null if there is no mapping for this key identifier
	 */
	public synchronized T getByKeyIdentifier(String keyIdentifier) {
		return this.entities.get(keyIdentifier);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entities == null) ? 0 : entities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityMap<T> other = (EntityMap<T>) obj;
		if (entities == null) {
			if (other.entities != null)
				return false;
		} else if (!entities.equals(other.entities))
			return false;
		return true;
	}
}
