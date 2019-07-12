package ar.org.example.web.rest.vm;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View Model object to return as body in JWT Authentication.
 */
public class UserAuthenticationVM implements Serializable {

    private String token;

    private String username;

    private List<String> roles = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setRoles(Collection<GrantedAuthority> authorities) {
        authorities.forEach(grantedAuthority -> this.roles.add(grantedAuthority.getAuthority()));
    }

    @Override
    public String toString() {
        return "UserAuthenticationVM{" +
                "token='" + getToken() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }

}
