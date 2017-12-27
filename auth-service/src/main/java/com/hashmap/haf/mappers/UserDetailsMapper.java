package com.hashmap.haf.mappers;

import com.hashmap.haf.models.SecurityUser;
import com.hashmap.haf.models.UserInformation;
import com.hashmap.haf.models.UserPrincipal;

public interface UserDetailsMapper {

    SecurityUser map(UserInformation details, UserPrincipal userPrincipal);
}
