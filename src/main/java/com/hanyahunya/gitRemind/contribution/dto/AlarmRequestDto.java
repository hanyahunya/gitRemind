package com.hanyahunya.gitRemind.contribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AlarmRequestDto {
    @JsonIgnore
    private String mid;
    @NotNull
    private List<Integer> alarmHours;
}
