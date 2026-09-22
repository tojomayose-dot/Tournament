package com.example.tournament.service;

import com.example.tournament.entity.Player;

import java.util.List;

public interface PlayerService {
    Player addPlayerToTeam(Long teamId, Player player);
    List<Player> getPlayersByTeam(Long teamId);
    void deletePlayer(Long playerId);
}