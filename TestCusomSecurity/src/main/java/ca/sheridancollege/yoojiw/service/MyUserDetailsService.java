package ca.sheridancollege.yoojiw.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.sheridancollege.yoojiw.database.DatabaseAccess;

@Service
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	private DatabaseAccess da; 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		ca.sheridancollege.yoojiw.bean.User user =    da.getOneUserByUsername(username); 
		
		List<String> roles =  da.getAllRolesByUserId(user.getUserId()); 
		
		List<GrantedAuthority> grantList = new ArrayList<>(); 
		
		for(String i : roles) {
			
			grantList.add(new SimpleGrantedAuthority(i)); 
		}
		
		
		UserDetails userDetails = new User(user.getUsername(), user.getPassword(), grantList);  
		
				
		return userDetails; 
	}

}
