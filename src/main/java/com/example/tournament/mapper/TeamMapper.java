package com.example.tournament.mapper;

import com.example.tournament.dto.request.CreateTeamRequest;
import com.example.tournament.dto.response.TeamResponse;
import com.example.tournament.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(CreateTeamRequest request) {
        Team team = new Team();
        team.setName(request.getName());
        team.setLogo(request.getLogo());
        team.setCity(request.getCity());
        team.setCoachName(request.getCoachName());
        return team;
    }

    public TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .logo(team.getLogo())
                .city(team.getCity())
                .coachName(team.getCoachName())
                .status(team.getStatus())
                .build();
    }
}