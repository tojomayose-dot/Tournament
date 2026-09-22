package com.example.tournament.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlayerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer jerseyNumber;
    private String position;
}