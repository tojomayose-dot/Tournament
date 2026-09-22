package com.example.tournament.controller.web;

import com.example.tournament.dto.request.CreateTournamentRequest;
import com.example.tournament.dto.response.TeamRankingResponse;
import com.example.tournament.entity.Team;
import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentStatus;
import com.example.tournament.entity.TournamentType;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.mapper.MatchMapper;
import com.example.tournament.mapper.TournamentMapper;
import com.example.tournament.service.MatchService;
import com.example.tournament.service.TeamService;
import com.example.tournament.service.TournamentService;
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

import java.util.List;

@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentWebController {

    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final MatchService matchService;
    private final TournamentMapper tournamentMapper;
    private final MatchMapper matchMapper;

   @GetMapping
    public String listTournaments(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) TournamentStatus status,
            Pageable pageable, Model model) {

        Page<Tournament> page = tournamentService.searchTournaments(sport, status, pageable);

        model.addAttribute("tournaments", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("statuses", TournamentStatus.values());
        model.addAttribute("selectedSport", sport);
        model.addAttribute("selectedStatus", status);

        return "tournaments/list";
    }

    @GetMapping("/{id}")
    public String tournamentDetail(@PathVariable Long id, Model model) {

        Tournament tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);

        // Toutes les équipes existantes (pour le formulaire d'inscription)
        List<Team> allTeams = teamService.getAllTeams(Pageable.unpaged()).getContent();
        model.addAttribute("allTeams", allTeams);

        // Les matchs déjà générés pour ce tournoi
        List<?> matches = matchService.getMatchesByTournament(id).stream()
                .map(matchMapper::toResponse)
                .toList();
        model.addAttribute("matches", matches);
        model.addAttribute("hasMatches", !matches.isEmpty());

        // Le classement, uniquement pertinent pour un championnat
        if (tournament.getType().name().equals("CHAMPIONNAT")) {
            List<TeamRankingResponse> ranking = matchService.getRanking(id);
            model.addAttribute("ranking", ranking);
        }

        return "tournaments/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String showCreateForm(Model model) {

        if (!model.containsAttribute("tournamentRequest")) {
            model.addAttribute("tournamentRequest", new CreateTournamentRequest());
        }
        model.addAttribute("types", TournamentType.values());

        return "tournaments/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String createTournament(
            @Valid @ModelAttribute("tournamentRequest") CreateTournamentRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", TournamentType.values());
            return "tournaments/form";
        }

        try {
            Tournament tournament = tournamentMapper.toEntity(request);
            tournamentService.createTournament(tournament);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("types", TournamentType.values());
            return "tournaments/form";
        }

        return "redirect:/tournaments";
    }

    @PostMapping("/{id}/teams")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String registerTeam(
            @PathVariable Long id,
            @RequestParam Long teamId,
            RedirectAttributes redirectAttributes) {

        try {
            teamService.registerTeamToTournament(id, teamId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/generate-matches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String generateMatches(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            matchService.generateMatches(id);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{tournamentId}/matches/{matchId}/result")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String recordResult(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestParam Integer homeScore,
            @RequestParam Integer awayScore,
            RedirectAttributes redirectAttributes) {

        try {
            matchService.recordResult(matchId, homeScore, awayScore);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/tournaments/" + tournamentId;
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {

        Tournament tournament = tournamentService.getTournamentById(id);

        CreateTournamentRequest request = new CreateTournamentRequest();
        request.setName(tournament.getName());
        request.setSport(tournament.getSport());
        request.setDescription(tournament.getDescription());
        request.setStartDate(tournament.getStartDate());
        request.setEndDate(tournament.getEndDate());
        request.setLocation(tournament.getLocation());
        request.setMaxTeams(tournament.getMaxTeams());
        request.setType(tournament.getType());

        model.addAttribute("tournamentRequest", request);
        model.addAttribute("types", TournamentType.values());
        model.addAttribute("editId", id);

        return "tournaments/form";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateTournament(
            @PathVariable Long id,
            @Valid @ModelAttribute("tournamentRequest") CreateTournamentRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", TournamentType.values());
            model.addAttribute("editId", id);
            return "tournaments/form";
        }

        try {
            Tournament updatedData = tournamentMapper.toEntity(request);
            tournamentService.updateTournament(id, updatedData);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("types", TournamentType.values());
            model.addAttribute("editId", id);
            return "tournaments/form";
        }

        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deleteTournament(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tournamentService.deleteTournament(id);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tournaments";
    }
}