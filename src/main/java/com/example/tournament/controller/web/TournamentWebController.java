package com.example.tournament.controller.web;

import com.example.tournament.dto.request.CreateTournamentRequest;
import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentType;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.mapper.TournamentMapper;
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

@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentWebController {

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;

    @GetMapping
    public String listTournaments(Pageable pageable, Model model) {

        Page<Tournament> page = tournamentService.getAllTournaments(pageable);

        model.addAttribute("tournaments", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());

        return "tournaments/list";
    }

    @GetMapping("/{id}")
    public String tournamentDetail(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);
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
}