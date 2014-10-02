package de.qabel.core.config;

import de.qabel.core.crypto.*;
import java.net.URL;

public class Identity {
	private final int id;
	private int updated;
	private int created;
	private int deleted;
	private String alias;
	private QblPrimaryKeyPair primaryKeyPair;
	private URL inbox;
	/**
	 * <pre>
	 *           0..*     0..1
	 * Identity ------------------------- Identities
	 *           identity        &lt;       identities
	 * </pre>
	 */
	private Identities identities;
	
	public Identity(String alias, URL inbox) {
		this.id = 0; //just to set it somehow
		this.setAlias(alias);
		this.setPrimaryKeyPair(primaryKeyPair);
		this.setInbox(inbox);
	}

	public int getId() {
		return this.id;
	}

	public void setUpdated(int value) {
		this.updated = value;
	}

	public int getUpdated() {
		return this.updated;
	}

	public void setCreated(int value) {
		this.created = value;
	}

	public int getCreated() {
		return this.created;
	}

	public void setDeleted(int value) {
		this.deleted = value;
	}

	public int getDeleted() {
		return this.deleted;
	}

	public void setAlias(String value) {
		this.alias = value;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setPrimaryKeyPair(QblPrimaryKeyPair key)
	{
		this.primaryKeyPair = key;
	}
	
	public QblPrimaryKeyPair getPrimaryKeyPair()
	{
		return this.primaryKeyPair;
	}

	public void setInbox(URL inbox) {
		this.inbox = inbox;
	}

	public URL getInbox() {
		return this.inbox;
	}


	public void setIdentities(Identities value) {
		this.identities = value;
	}

	public Identities getIdentities() {
		return this.identities;
	}
	
	
	public Identity(int id)
	{
		this.id = id;
	}

}
