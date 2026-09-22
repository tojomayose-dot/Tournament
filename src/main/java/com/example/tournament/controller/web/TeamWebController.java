package com.example.tournament.controller.web;

import com.example.tournament.dto.request.CreateTeamRequest;
import com.example.tournament.entity.Team;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.mapper.TeamMapper;
import com.example.tournament.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamWebController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @GetMapping
    public String listTeams(Pageable pageable, Model model) {

        Page<Team> page = teamService.getAllTeams(pageable);

        model.addAttribute("teams", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());

        return "teams/list";
    }

    @GetMapping("/{id}")
    public String teamDetail(@PathVariable Long id, Model model) {
        Team team = teamService.getTeamById(id);
        model.addAttribute("team", team);
        return "teams/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String showCreateForm(Model model) {

        if (!model.containsAttribute("teamRequest")) {
            model.addAttribute("teamRequest", new CreateTeamRequest());
        }

        return "teams/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String createTeam(
            @Valid @ModelAttribute("teamRequest") CreateTeamRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "teams/form";
        }

        try {
            Team team = teamMapper.toEntity(request);
            teamService.createTeam(team);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "teams/form";
        }

        return "redirect:/teams";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {

        Team team = teamService.getTeamById(id);

        CreateTeamRequest request = new CreateTeamRequest();
        request.setName(team.getName());
        request.setCity(team.getCity());
        request.setCoachName(team.getCoachName());
        request.setLogo(team.getLogo());

        model.addAttribute("teamRequest", request);
        model.addAttribute("editId", id);

        return "teams/form";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateTeam(
            @PathVariable Long id,
            @Valid @ModelAttribute("teamRequest") CreateTeamRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editId", id);
            return "teams/form";
        }

        try {
            Team updatedData = teamMapper.toEntity(request);
            teamService.updateTeam(id, updatedData);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("editId", id);
            return "teams/form";
        }

        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teamService.deleteTeam(id);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams";
    }
}