package de.qabel.core.config;

import java.util.*;

/**
 * EntityMaps provide functionality to lookup an Entity based
 * on its key identifier.
 *
 * @see Entity
 */
abstract class EntityMap<T extends Entity> extends Persistable implements EntityObservable {
	private static final long serialVersionUID = -4541440187172822588L;
	private final Map<String, T> entities = Collections.synchronizedMap(new HashMap<String, T>());
	private transient List<EntityObserver> observerList = new LinkedList<>();

	public EntityMap() {
		super();
	}

	/**
	 * Returns unmodifiable set of contained contacts
	 *
	 * @return Set<Contact>
	 */
	public synchronized Set<T> getEntities() {
		return Collections.unmodifiableSet(new HashSet<>(entities.values()));
	}

	/**
	 * Inserts new Entity into the map associated to its key identifier.
	 *
	 * @param entity Entity to insert
	 * @return old Entity associated to the same key identifier or null if no such old Entity was present.
	 */
	public synchronized T put(T entity) {
		T result = this.entities.put(entity.getKeyIdentifier(), entity);
		notifiedObserver();
		return result;
	}

	/**
	 * Removes given Entity from the map.
	 *
	 * @param entity Entity to remove
	 * @return old Entity associated to the key identifier of the given Entity, or null if there was no such Entity
	 */
	public synchronized T remove(T entity) {
		T result = this.remove(entity.getKeyIdentifier());
		notifiedObserver();
		return result;
	}

	/**
	 * Removes Entity with the given key identifier from the map.
	 *
	 * @param keyIdentifier key identifier of the Entity that is to be removed
	 * @return old Entity associated to the given key identifier, or null if there was no such Entity
	 */
	public synchronized T remove(String keyIdentifier) {
		T result = this.entities.remove(keyIdentifier);
		notifiedObserver();
		return result;
	}

	/**
	 * Get entity by key identifier (right most 64 bit of the identity's public fingerprint)
	 *
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
		result = prime * result + entities.hashCode();
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
		if (!entities.equals(other.entities))
			return false;
		return true;
	}

	@Override
	public void addObserver(EntityObserver observer) {
		getObserverList().add(observer);
	}

	@Override
	public void notifiedObserver() {
		List<EntityObserver> lst = getObserverList();

		for (EntityObserver e : lst) {
			e.update();
		}
	}

	private List<EntityObserver> getObserverList() {
		if (observerList == null) {
			observerList = new LinkedList<>();
			return observerList;
		}
		return observerList;
	}

}
