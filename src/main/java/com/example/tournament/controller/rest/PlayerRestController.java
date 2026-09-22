package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.CreatePlayerRequest;
import com.example.tournament.dto.response.PlayerResponse;
import com.example.tournament.entity.Player;
import com.example.tournament.mapper.PlayerMapper;
import com.example.tournament.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/players")
@RequiredArgsConstructor
public class PlayerRestController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PlayerResponse> addPlayer(
            @PathVariable Long teamId, @Valid @RequestBody CreatePlayerRequest request) {

        Player player = playerMapper.toEntity(request);
        Player created = playerService.addPlayerToTeam(teamId, player);
        return ResponseEntity.status(HttpStatus.CREATED).body(playerMapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getPlayers(@PathVariable Long teamId) {
        List<PlayerResponse> players = playerService.getPlayersByTeam(teamId).stream()
                .map(playerMapper::toResponse)
                .toList();
        return ResponseEntity.ok(players);
    }

    @DeleteMapping("/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }
}