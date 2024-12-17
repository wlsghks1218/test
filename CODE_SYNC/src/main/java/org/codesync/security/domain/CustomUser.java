package org.codesync.security.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.codesync.domain.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomUser extends User implements Serializable{

	private UserDTO user;

    public CustomUser(UserDTO user) {
        super(
            user.getUserId(),
            user.getUserPw(),
            List.of(new SimpleGrantedAuthority(user.getAuthAdmin() == 1 ? "ROLE_USER" : "ROLE_ADMIN"))
        );
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }
}
