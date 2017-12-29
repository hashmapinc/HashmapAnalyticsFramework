package com.hashmap.haf.mappers;

import com.hashmap.haf.models.SecurityUser;
import com.hashmap.haf.models.UserInformation;
import com.hashmap.haf.models.UserPrincipal;

public class DefaultUserDetailsMapper implements UserDetailsMapper{
    @Override
    public SecurityUser map(UserInformation details, UserPrincipal userPrincipal) {
        return new SecurityUser(details, true, userPrincipal);
    }
}
