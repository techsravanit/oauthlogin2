package com.purpletalk.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.purpletalk.entities.AuthUser;

public interface AuthUserRepositary extends JpaRepository<AuthUser, Long>{
	AuthUser findByUsername(String username);
}
