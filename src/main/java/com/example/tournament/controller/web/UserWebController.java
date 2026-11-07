package com.example.tournament.controller.web;

import com.example.tournament.dto.request.RegisterRequest;
import com.example.tournament.entity.Role;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserWebController {

    private static final List<Role> ASSIGNABLE_ROLES =
            List.of(Role.MANAGER, Role.OPERATOR, Role.USER);

    private final UserService userService;

    @GetMapping({"", "/", "/new"})
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        addAssignableRoles(model);
        return "admin/users/form";
    }

    @PostMapping({"", "/", "/new"})
    public String createUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (request.getRole() != null && !ASSIGNABLE_ROLES.contains(request.getRole())) {
            bindingResult.rejectValue("role", "role.notAssignable",
                    "Le rôle Administrateur ne peut pas être attribué depuis ce formulaire.");
        }

        if (bindingResult.hasErrors()) {
            addAssignableRoles(model);
            return "admin/users/form";
        }

        try {
            userService.register(request.getUsername(), request.getPassword(), request.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès.");
            return "redirect:/dashboard";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addAssignableRoles(model);
            return "admin/users/form";
        }
    }

    private void addAssignableRoles(Model model) {
        model.addAttribute("roles", ASSIGNABLE_ROLES);
    }
}
