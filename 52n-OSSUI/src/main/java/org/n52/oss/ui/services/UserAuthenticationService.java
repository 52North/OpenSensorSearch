package org.n52.oss.ui.services;

import org.n52.oss.ui.userDetailsImp;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("userDetailsService")
public class UserAuthenticationService implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException {
		// TODO Yakoub : implement The DAO HAR32
		
		userDetailsImp impl = new userDetailsImp(arg0, "password", null);
		if(!arg0.equals("new"))
			throw new UsernameNotFoundException("Wrong username");
		return impl;
	}
}
