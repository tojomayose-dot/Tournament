package com.example.tournament.service;

import com.example.tournament.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {

    Team createTeam(Team team);

    Team getTeamById(Long id);

    Page<Team> getAllTeams(Pageable pageable);
    
    Page<Team> searchTeams(String keyword, Pageable pageable);

    Team updateTeam(Long id, Team updatedData);

    void deleteTeam(Long id);

    void registerTeamToTournament(Long tournamentId, Long teamId);

    void withdrawTeamFromTournament(Long tournamentId, Long teamId);
}