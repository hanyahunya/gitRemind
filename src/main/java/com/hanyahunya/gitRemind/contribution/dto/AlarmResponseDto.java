package com.hanyahunya.gitRemind.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor(staticName = "set")
public class AlarmResponseDto {
    private Set<Integer> alarmHours;
}
