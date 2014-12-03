package de.qabel.core.config;

import com.google.gson.annotations.SerializedName;

/**
 * https://github.com/Qabel/qabel-doc/wiki/Qabel-Client-Configuration#storage-volume
 */
public class StorageVolume extends SyncSettingItem {
	/**
	 * ID of the storage server
	 * Field name in serialized json: "storage_server_id"
	 */
	@SerializedName("storage_server_id")
	private int storageServerId;
	/**
	 * identifier of the storage volume on the server
	 * Field name in serialized json: "public_identifier"
	 */
	@SerializedName("public_identifier")
	private String publicIdentifier;
	/**
	 * Credential granting write permission to the storage volume
	 * Field name in serialized json: "token"
	 */
	private String token;
	/**
	 * Credential granting the permission to delete the whole storage volume
	 * Field name in serialized json: "revoke_token"
	 */
	@SerializedName("revoke_token")
	private String revokeToken;
	
	/**
	 * Creates an instance of StorageVolume
	 * @param publicIdentifier
	 * @param token
	 * @param revokeToken
	 */
	public StorageVolume(String publicIdentifier, String token, String revokeToken) {
		this.setPublicIdentifier(publicIdentifier);
		this.setToken(token);
		this.setRevokeToken(revokeToken);
	}

	/**
	 * Returns ID of the storage server
	 * @return storageServerId
	 */
	public int getStorageServerId() {
		return storageServerId;
	}

	/**
	 * Sets the ID of the storage server
	 * @param storageServerId
	 */
	public void setStorageServerId(int storageServerId) {
		this.storageServerId = storageServerId;
	}

	/**
	 * Returns the public identifier of the storage volume
	 * @return publicIdentifier
	 */
	public String getPublicIdentifier() {
		return publicIdentifier;
	}

	/**
	 * Sets the public identifier of the storage volume
	 * @param publicIdentifier
	 */
	public void setPublicIdentifier(String publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	/**
	 * Returns the token of the storage volume
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token of the storage volume
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Returns the revoke token of the storage volume
	 * @return revokeToken
	 */
	public String getRevokeToken() {
		return revokeToken;
	}

	/**
	 * Sets the revoke token of the storage volume
	 * @param revokeToken
	 */
	public void setRevokeToken(String revokeToken) {
		this.revokeToken = revokeToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = super.hashCode();
		
		result = prime
				* result
				+ ((publicIdentifier == null) ? 0 : publicIdentifier.hashCode());
		result = prime * result
				+ ((revokeToken == null) ? 0 : revokeToken.hashCode());
		result = prime * result + storageServerId;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		StorageVolume other = (StorageVolume) obj;
		if (publicIdentifier == null) {
			if (other.publicIdentifier != null)
				return false;
		} else if (!publicIdentifier.equals(other.publicIdentifier))
			return false;
		if (revokeToken == null) {
			if (other.revokeToken != null)
				return false;
		} else if (!revokeToken.equals(other.revokeToken))
			return false;
		if (storageServerId != other.storageServerId)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;

		return true;
	}

}
