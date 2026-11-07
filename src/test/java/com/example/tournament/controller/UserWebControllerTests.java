package com.example.tournament.controller;

import com.example.tournament.controller.web.UserWebController;
import com.example.tournament.dto.request.RegisterRequest;
import com.example.tournament.entity.Role;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserWebControllerTests {
    private final UserService users = mock(UserService.class);
    private final UserWebController controller = new UserWebController(users);

    private RegisterRequest request(Role role) {
        var request = new RegisterRequest();
        request.setUsername("alice");
        request.setPassword("secret");
        request.setRole(role);
        return request;
    }

    @Test
    void forgedAdminRoleIsRejected() {
        var request = request(Role.ADMIN);
        var errors = new BeanPropertyBindingResult(request, "registerRequest");
        assertEquals("admin/users/form", controller.createUser(request, errors,
                new ExtendedModelMap(), new RedirectAttributesModelMap()));
        assertTrue(errors.hasFieldErrors("role"));
        verifyNoInteractions(users);
    }

    @Test
    void managerCreationRedirectsWithConfirmation() {
        var request = request(Role.MANAGER);
        var flash = new RedirectAttributesModelMap();
        assertEquals("redirect:/dashboard", controller.createUser(request,
                new BeanPropertyBindingResult(request, "registerRequest"), new ExtendedModelMap(), flash));
        verify(users).register("alice", "secret", Role.MANAGER);
        assertTrue(flash.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void duplicateUsernameReturnsFormWithError() {
        when(users.register("alice", "secret", Role.USER)).thenThrow(new BusinessException("Nom déjà pris"));
        var request = request(Role.USER);
        var model = new ExtendedModelMap();
        assertEquals("admin/users/form", controller.createUser(request,
                new BeanPropertyBindingResult(request, "registerRequest"), model, new RedirectAttributesModelMap()));
        assertEquals("Nom déjà pris", model.getAttribute("errorMessage"));
        assertNotNull(model.getAttribute("roles"));
    }
}
