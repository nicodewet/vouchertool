package com.mayloom.vouchserv.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import com.mayloom.vouchserv.dbo.User;
import com.mayloom.vouchserv.dbo.enums.FetchRoles;
import com.mayloom.vouchserv.dbo.enums.FetchVBOs;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optUser = databaseHelper.getUser(username, FetchRoles.TRUE, FetchVBOs.FALSE);
		if (optUser.isPresent()) {
			return optUser.get();
		} else {
			logger.error("There is no user with username {}",username);
			throw new UsernameNotFoundException(username + " not found");
		}
	}

}
