package org.n52.oss.ui;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class userDetailsImp implements UserDetails {
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	public userDetailsImp(String user, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.username = user;
		this.password = password;
		this.authorities = authorities;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
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
