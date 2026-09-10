package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.CreateTeamRequest;
import com.example.tournament.dto.response.TeamResponse;
import com.example.tournament.entity.Team;
import com.example.tournament.mapper.TeamMapper;
import com.example.tournament.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamRestController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Team team = teamMapper.toEntity(request);
        Team created = teamService.createTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        return ResponseEntity.ok(teamMapper.toResponse(team));
    }

    @GetMapping
    public ResponseEntity<Page<TeamResponse>> getAllTeams(Pageable pageable) {
        Page<TeamResponse> page = teamService.getAllTeams(pageable).map(teamMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id, @Valid @RequestBody CreateTeamRequest request) {
        Team updatedData = teamMapper.toEntity(request);
        Team updated = teamService.updateTeam(id, updatedData);
        return ResponseEntity.ok(teamMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}