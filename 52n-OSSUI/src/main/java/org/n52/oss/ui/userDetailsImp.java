/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/** @author Yakoub
 */

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
