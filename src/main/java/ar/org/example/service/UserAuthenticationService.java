package ar.org.example.service;

import ar.org.example.repository.UserRepository;
import ar.org.example.service.dto.RoleDTO;
import ar.org.example.service.dto.UserDTO;
import ar.org.example.service.mapper.UserMapper;
import ar.org.example.web.rest.errors.UserNotActivatedException;
import ar.org.example.web.rest.vm.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

/**
 * Service Implementation for authenticate a user from the database.
 */
@Service
@Transactional
public class UserAuthenticationService {

    private final Logger log = LoggerFactory.getLogger(UserAuthenticationService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserAuthenticationService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Get security user from the database.
     *
     * @param loginVM View Model object
     * @return the entity
     */
    @Transactional(readOnly = true)
    public UsernamePasswordAuthenticationToken getSecurityUser(LoginVM loginVM) throws AuthenticationException {
        String lowercaseLogin = loginVM.getUsername().toLowerCase(Locale.ENGLISH);
        log.debug("Authenticating {}", lowercaseLogin);
        String password = loginVM.getPassword();

        return userRepository.findByLogin(lowercaseLogin)
                .map(userMapper::toDto)
                .map(userDTO -> createSecurityUser(lowercaseLogin, password, userDTO))
                .orElseThrow(() -> new BadCredentialsException("User " + lowercaseLogin + " was not found in the database"));
    }

    private UsernamePasswordAuthenticationToken createSecurityUser(String lowercaseLogin, String password, UserDTO user) {
        if (user.getEnabled() != 1) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        return new UsernamePasswordAuthenticationToken(lowercaseLogin,
                password,
                getGrantedAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Set<RoleDTO> roles) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RoleDTO role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
        }
        return grantedAuthorities;
    }

}
