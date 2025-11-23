package com.example.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimetableRow {
    private String day;
    private String slot1; // 09:00
    private String slot2; // 10:00
    private String slot3; // 11:00
    private String slot4; // 12:00
    private String slot5; // 01:00
    private String slot6; // 02:00
}