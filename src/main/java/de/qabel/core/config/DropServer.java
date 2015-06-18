package de.qabel.core.config;

import java.net.URI;

/** 
 * https://github.com/Qabel/qabel-doc/wiki/Qabel-Client-Configuration#drop-server
 */
public class DropServer extends SyncSettingItem {
	private static final long serialVersionUID = 6784516352213179983L;
	/**
	 * URI to the DropServer without the drop id.
	 * Field name in serialized json: "uri"
	 */
	private URI uri;
	/**
	 * Authentication for the DropServer (Credential for optional, additional access regulation).
	 * Field name in serialized json: "auth"
	 */	
	private String auth;
	/**
	 * Status flag of the DropServer.
	 * Field name in serialized json: "active"
	 */
	private boolean active;
	/**
	 * Creates an instance of DropServer.
	 * @param uri URI of the DropServer.
	 * @param auth Authentication for the DropServer.
	 * @param active Status flag of the DropServer.
	 */
	public DropServer(URI uri, String auth, boolean active) {
		this.setUri(uri);
		this.setAuth(auth);
		this.setActive(active);
	}
	/**
	 * Creates an instance of DropServer.
	 */
	public DropServer() {
		
	}

	/**
	 * Sets the uri of the DropServer.
	 * @param uri URI of the DropServer.
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * Returns the uri of the DropServer.
	 * @return URI
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * Sets the authentication of the DropServer.
	 * @param value Authentication for the DropServer.
	 */
	public void setAuth(String value) {
		this.auth = value;
	}

	/**
	 * Returns the authentication of the DropServer.
	 * @return authentication
	 */
	public String getAuth() {
		return this.auth;
	}

	/**
	 * Sets the status flag of the DropServer.
	 * @param value Status flag to set the DropServer to.
	 */
	public void setActive(boolean value) {
		this.active = value;
	}

	/**
	 * Returns the status flag of the DropServer.
	 * @return boolean
	 */
	public boolean isActive() {
		return this.active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = super.hashCode();
		
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((auth == null) ? 0 : auth.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) == false) {
		    return (false);
		}

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		DropServer other = (DropServer) obj;
		if (active != other.active)
			return false;
		if (auth == null) {
			if (other.auth != null)
				return false;
		} else if (!auth.equals(other.auth))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
