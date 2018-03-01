package com.hashmapinc.haf.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

public class SecurityUser implements Serializable{

    private static final long serialVersionUID = 4093587665196741372L;

    private Collection<GrantedAuthority> authorities;
    private boolean enabled;
    private UserInformation user;

    public SecurityUser(UserInformation user, boolean enabled) {
        this.user = user;
        this.enabled = enabled;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority))
                    .collect(Collectors.toList());
        }
        return authorities;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UserInformation getUser() {
        return user;
    }
}
