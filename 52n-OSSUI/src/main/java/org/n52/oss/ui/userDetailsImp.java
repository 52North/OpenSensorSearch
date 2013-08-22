package org.n52.oss.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.ui.services.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class userDetailsImp implements UserDetails {
	private String username;
	private String password;
	private Collection<org.n52.oss.ui.services.GrantedAuthorityImpl> authorities;

	public userDetailsImp(String user, String password,
			Collection<GrantedAuthorityImpl> authorities) {
		this.username = user;
		this.password = password;
		if (authorities == null) {
			this.authorities = new ArrayList<GrantedAuthorityImpl>();
			this.authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		} else
			this.authorities = authorities;
	}

	@Override
	public Collection<GrantedAuthorityImpl> getAuthorities() {
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
