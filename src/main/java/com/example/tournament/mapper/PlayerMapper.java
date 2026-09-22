package com.example.tournament.mapper;

import com.example.tournament.dto.request.CreatePlayerRequest;
import com.example.tournament.dto.response.PlayerResponse;
import com.example.tournament.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(CreatePlayerRequest request) {
        Player player = new Player();
        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setJerseyNumber(request.getJerseyNumber());
        player.setPosition(request.getPosition());
        return player;
    }

    public PlayerResponse toResponse(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .firstName(player.getFirstName())
                .lastName(player.getLastName())
                .jerseyNumber(player.getJerseyNumber())
                .position(player.getPosition())
                .build();
    }
}