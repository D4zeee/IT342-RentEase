package com.it342_rentease.it342_rentease_project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.it342_rentease.it342_rentease_project.repository.RenterRepository;

@Service
public class RenterDetailsService implements UserDetailsService {

    @Autowired
    private RenterRepository renterRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return renterRepository.findByEmail(email)
            .map(renter -> new org.springframework.security.core.userdetails.User(
                    renter.getEmail(),
                    renter.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_RENTER"))
            )).orElseThrow(() -> new UsernameNotFoundException("Renter not found"));
    }
}
