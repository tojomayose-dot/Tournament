package com.example.tournament.mapper;

import com.example.tournament.dto.request.CreateTournamentRequest;
import com.example.tournament.dto.request.UpdateTournamentRequest;
import com.example.tournament.dto.response.TournamentResponse;
import com.example.tournament.entity.Tournament;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper {

    public Tournament toEntity(CreateTournamentRequest request) {
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setSport(request.getSport());
        tournament.setDescription(request.getDescription());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(request.getLocation());
        tournament.setMaxTeams(request.getMaxTeams());
        tournament.setType(request.getType());
        return tournament;
    }

    public Tournament toEntity(UpdateTournamentRequest request) {
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setSport(request.getSport());
        tournament.setDescription(request.getDescription());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(request.getLocation());
        tournament.setMaxTeams(request.getMaxTeams());
        tournament.setType(request.getType());
        return tournament;
    }

    public TournamentResponse toResponse(Tournament tournament) {
        return TournamentResponse.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .sport(tournament.getSport())
                .description(tournament.getDescription())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .location(tournament.getLocation())
                .maxTeams(tournament.getMaxTeams())
                .type(tournament.getType())
                .status(tournament.getStatus())
                .build();
    }
}