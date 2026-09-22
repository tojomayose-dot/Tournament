package com.example.tournament.service.impl;

import com.example.tournament.entity.Player;
import com.example.tournament.entity.Team;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.repository.PlayerRepository;
import com.example.tournament.repository.TeamRepository;
import com.example.tournament.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public Player addPlayerToTeam(Long teamId, Player player) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe introuvable avec l'id : " + teamId));

        player.setTeam(team);
        return playerRepository.save(player);
    }

    @Override
    public List<Player> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    @Override
    @Transactional
    public void deletePlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Joueur introuvable avec l'id : " + playerId));
        playerRepository.delete(player);
    }
}