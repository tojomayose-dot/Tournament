package com.example.tournament.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeamRequest {

    @NotBlank(message = "Le nom de l'équipe est obligatoire.")
    private String name;

    private String logo;

    private String city;

    private String coachName;
}