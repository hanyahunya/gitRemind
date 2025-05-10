package com.hanyahunya.gitRemind.contribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue(message = "設定可能時刻は1時～23時です。")
    public boolean isValidAlarmHours() {
        if (alarmHours == null) {
            return false;
        }
        return alarmHours.stream().allMatch(hour -> hour >= 1 && hour <= 23);
    }
}
