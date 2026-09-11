package com.example.tournament.controller.rest;

import com.example.tournament.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/teams")
@RequiredArgsConstructor
public class TournamentRegistrationRestController {

    private final TeamService teamService;

    @PostMapping("/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<Void> registerTeam(
            @PathVariable Long tournamentId, @PathVariable Long teamId) {
        teamService.registerTeamToTournament(tournamentId, teamId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<Void> withdrawTeam(
            @PathVariable Long tournamentId, @PathVariable Long teamId) {
        teamService.withdrawTeamFromTournament(tournamentId, teamId);
        return ResponseEntity.noContent().build();
    }
}