package com.example.tournament.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlayerRequest {

    @NotBlank(message = "Le prénom est obligatoire.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    private String lastName;

    private Integer jerseyNumber;

    private String position;
}