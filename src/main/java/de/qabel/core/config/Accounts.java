package de.qabel.core.config;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class Accounts {
	
	/**
	 * <pre>
	 *           1     0..*
	 * Accounts ------------------------- Account
	 *           accounts        &gt;       account
	 * </pre>
	 */
	private final Set<Account> account = new HashSet<Account>();

	public Set<Account> getAccount() {
		return Collections.unmodifiableSet(this.account);
	}
	
	public boolean add(Account account) {
		return this.account.add(account);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
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
		Accounts other = (Accounts) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		return true;
	}
	
	

}
