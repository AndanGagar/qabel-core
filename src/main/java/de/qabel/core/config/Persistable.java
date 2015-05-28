package de.qabel.core.config;

import java.io.Serializable;
import java.util.UUID;

public abstract class Persistable implements Serializable {

	private UUID persistenceID;

	public Persistable() {
		this.persistenceID = genPersistenceID();
	}

	public String getPersistenceID() { return persistenceID.toString(); }

	/**
	 * Generated a random UUID for persistent storage.
	 * @return Unique UUID
	 */
	private UUID genPersistenceID(){
		return UUID.randomUUID();
	}
}
