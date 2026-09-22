package com.example.tournament.controller.web;

import com.example.tournament.dto.request.CreatePlayerRequest;
import com.example.tournament.dto.request.CreateTeamRequest;
import com.example.tournament.entity.Player;
import com.example.tournament.entity.Team;
import com.example.tournament.entity.TournamentRegistration;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.mapper.TeamMapper;
import com.example.tournament.repository.TournamentRegistrationRepository;
import com.example.tournament.service.PlayerService;
import com.example.tournament.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamWebController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final PlayerService playerService;
    private final TournamentRegistrationRepository registrationRepository;

    @GetMapping
    public String listTeams(
            @RequestParam(required = false) String search,
            Pageable pageable,
            Model model) {

        Page<Team> page = (search != null && !search.isBlank())
                ? teamService.searchTeams(search, pageable)
                : teamService.getAllTeams(pageable);

        model.addAttribute("teams", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("search", search);

        return "teams/list";
    }

    @GetMapping("/{id}")
    public String teamDetail(@PathVariable Long id, Model model) {
        Team team = teamService.getTeamById(id);
        List<Player> players = playerService.getPlayersByTeam(id);
        List<TournamentRegistration> registrations = registrationRepository.findByTeamId(id);

        model.addAttribute("team", team);
        model.addAttribute("players", players);
        model.addAttribute("registrations", registrations);
        if (!model.containsAttribute("playerRequest")) {
            model.addAttribute("playerRequest", new CreatePlayerRequest());
        }

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
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "teams/form";
        }

        try {
            Team team = teamMapper.toEntity(request);
            Team created = teamService.createTeam(team);
            redirectAttributes.addFlashAttribute("successMessage", "Équipe créée avec succès.");
            return "redirect:/teams/" + created.getId();
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "teams/form";
        }
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
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editId", id);
            return "teams/form";
        }

        try {
            Team updatedData = teamMapper.toEntity(request);
            teamService.updateTeam(id, updatedData);
            redirectAttributes.addFlashAttribute("successMessage", "Équipe mise à jour avec succès.");
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
            redirectAttributes.addFlashAttribute("successMessage", "Équipe supprimée avec succès.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseBody
    public ResponseEntity<Void> deleteTeamViaHtmx(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok()
                    .header("HX-Trigger", "toast")
                    .build();
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("HX-Trigger", "toast")
                    .build();
        }
    }

    @PostMapping("/{teamId}/players")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String addPlayer(
            @PathVariable Long teamId,
            @Valid @ModelAttribute("playerRequest") CreatePlayerRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().isEmpty()
                    ? "Données du joueur invalides."
                    : bindingResult.getFieldErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:/teams/" + teamId;
        }

        try {
            Player player = new Player();
            player.setFirstName(request.getFirstName());
            player.setLastName(request.getLastName());
            player.setJerseyNumber(request.getJerseyNumber());
            player.setPosition(request.getPosition());
            playerService.addPlayerToTeam(teamId, player);
            redirectAttributes.addFlashAttribute("successMessage", "Joueur ajouté avec succès.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/teams/" + teamId;
    }

    @PostMapping("/{teamId}/players/{playerId}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deletePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            RedirectAttributes redirectAttributes) {

        try {
            playerService.deletePlayer(playerId);
            redirectAttributes.addFlashAttribute("successMessage", "Joueur retiré de l'équipe.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/teams/" + teamId;
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseBody
    public ResponseEntity<Void> deletePlayerViaHtmx(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        try {
            playerService.deletePlayer(playerId);
            return ResponseEntity.ok()
                    .header("HX-Trigger", "toast")
                    .build();
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("HX-Trigger", "toast")
                    .build();
        }
    }
}