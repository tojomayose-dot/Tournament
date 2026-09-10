package com.example.tournament.dto.response;

import com.example.tournament.entity.TeamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String logo;
    private String city;
    private String coachName;
    private TeamStatus status;
}