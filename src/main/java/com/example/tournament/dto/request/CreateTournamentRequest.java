package com.example.tournament.dto.request;

import com.example.tournament.entity.TournamentType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTournamentRequest {

    @NotBlank(message = "Le nom du tournoi est obligatoire.")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères.")
    private String name;

    @NotBlank(message = "Le sport est obligatoire.")
    private String sport;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères.")
    private String description;

    @NotNull(message = "La date de début est obligatoire.")
    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur.")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire.")
    private LocalDate endDate;

    private String location;

    @NotNull(message = "Le nombre maximum d'équipes est obligatoire.")
    @Positive(message = "Le nombre maximum d'équipes doit être positif.")
    private Integer maxTeams;

    @NotNull(message = "Le type de tournoi est obligatoire.")
    private TournamentType type;
}