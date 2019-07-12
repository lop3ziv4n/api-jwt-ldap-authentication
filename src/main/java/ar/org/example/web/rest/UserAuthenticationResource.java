package ar.org.example.web.rest;

import ar.org.example.security.jwt.TokenProvider;
import ar.org.example.service.UserAuthenticationService;
import ar.org.example.web.rest.util.HeaderUtil;
import ar.org.example.web.rest.vm.LoginVM;
import ar.org.example.web.rest.vm.UserAuthenticationVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST controller for managing to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserAuthenticationResource {

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationResource(TokenProvider tokenProvider, AuthenticationManager authenticationManager, UserAuthenticationService userAuthenticationService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationVM> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = userAuthenticationService.getSecurityUser(loginVM);
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String token = tokenProvider.createToken(authentication, rememberMe);
        return new ResponseEntity<>(getUserAuthenticationVM(authenticationToken, token), HeaderUtil.createAuthorizationToken(token), HttpStatus.OK);
    }

    private UserAuthenticationVM getUserAuthenticationVM(UsernamePasswordAuthenticationToken authenticationToken, String token) {
        UserAuthenticationVM userAuthenticationVM = new UserAuthenticationVM();
        userAuthenticationVM.setToken(token);
        userAuthenticationVM.setUsername(authenticationToken.getName());
        userAuthenticationVM.setRoles(authenticationToken.getAuthorities());
        return userAuthenticationVM;
    }
}
