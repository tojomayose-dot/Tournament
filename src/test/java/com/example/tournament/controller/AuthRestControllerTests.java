package com.example.tournament.controller;

import com.example.tournament.controller.rest.AuthRestController;
import com.example.tournament.entity.Role;
import com.example.tournament.entity.User;
import com.example.tournament.repository.UserRepository;
import com.example.tournament.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthRestControllerTests {
    private final AuthenticationManager manager = mock(AuthenticationManager.class);
    private final UserRepository users = mock(UserRepository.class);
    private final AuthRestController controller = new AuthRestController(mock(UserService.class), manager, users);

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void loginRotatesExistingSessionAndPersistsAuthentication() {
        var request = new MockHttpServletRequest();
        var previousId = request.getSession().getId();
        var authentication = UsernamePasswordAuthenticationToken.authenticated("alice", null, List.of());
        when(manager.authenticate(any())).thenReturn(authentication);
        var user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRole(Role.MANAGER);
        when(users.findByUsername("alice")).thenReturn(Optional.of(user));

        var result = controller.login(new AuthRestController.LoginRequest("alice", "secret"),
                request, new MockHttpServletResponse());

        assertEquals(200, result.getStatusCode().value());
        assertNotEquals(previousId, request.getSession().getId());
        var context = new HttpSessionSecurityContextRepository().loadDeferredContext(request).get();
        assertEquals("alice", context.getAuthentication().getName());
    }

    @Test
    void incorrectPasswordDoesNotCreateAuthenticatedSession() {
        when(manager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));
        var request = new MockHttpServletRequest();
        var result = controller.login(new AuthRestController.LoginRequest("alice", "wrong"),
                request, new MockHttpServletResponse());
        assertEquals(401, result.getStatusCode().value());
        assertNull(request.getSession(false));
    }

    @Test
    void logoutInvalidatesSessionAndClearsAuthentication() {
        var request = new MockHttpServletRequest();
        request.getSession();
        var authentication = UsernamePasswordAuthenticationToken.authenticated("alice", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertEquals(204, controller.logout(request, new MockHttpServletResponse(), authentication)
                .getStatusCode().value());
        assertNull(request.getSession(false));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
