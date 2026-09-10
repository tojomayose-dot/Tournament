package com.example.tournament.dto.response;

import com.example.tournament.entity.TournamentStatus;
import com.example.tournament.entity.TournamentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TournamentResponse {

    private Long id;
    private String name;
    private String sport;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Integer maxTeams;
    private TournamentType type;
    private TournamentStatus status;
}