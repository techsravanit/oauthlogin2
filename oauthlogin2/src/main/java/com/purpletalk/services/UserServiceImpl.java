package com.purpletalk.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import com.purpletalk.entities.AuthUser;
import com.purpletalk.repositary.AuthUserRepositary;

@Service(value = "userService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	AuthUserRepositary repo;

	@Autowired
	private ClientDetailsService parent;
	
	@Override
	public List findAll() {
		List list = new ArrayList<>();
		repo.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	/*@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthUser user = repo.findByUsername(username);
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		ClientDetails client = parent.loadClientByClientId(username);
		return new org.springframework.security.core.userdetails.User(client.getClientId(),
				new BCryptPasswordEncoder().encode(client.getClientSecret()), client.getAuthorities());
		//return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}*/

}
