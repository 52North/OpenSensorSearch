package org.n52.oss.ui.services;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

	private String rolename;
	@Override
	public String getAuthority() {
		return this.rolename;
	}
	public GrantedAuthorityImpl(String rolename){
		this.rolename = rolename;
	}
	

}
