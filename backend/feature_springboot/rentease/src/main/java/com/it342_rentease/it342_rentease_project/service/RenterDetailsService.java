package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.Renter;
import com.it342_rentease.it342_rentease_project.repository.RenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class RenterDetailsService implements UserDetailsService {

    @Autowired
    private RenterRepository renterRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Renter renter = renterRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Renter not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                renter.getEmail(),
                renter.getPassword(),
                getAuthorities());
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_RENTER"));
    }
}
