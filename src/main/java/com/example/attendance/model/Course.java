package com.example.attendance.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class Course {
    // Excel Data Fields (Unchanged)
    private String courseCode;
    private String courseDesc;
    private String ltps;      
    private String section;
    private String year;
    private String semester;
    private String frDate;
    private int totalConducted;
    private int totalAttended;
    private int totalAbsent;
    private int tcbr;
    private double percentage;

    // PREDICTION LOGIC DATES
    private final LocalDate TODAY = LocalDate.of(2025, 11, 22);
    private final LocalDate END_SEM = LocalDate.of(2025, 12, 30);

    public Course(String code, String desc, String ltps, String section, String year, 
                  String sem, String frDate, int conducted, int attended, int tcbr) {
        this.courseCode = code;
        this.courseDesc = desc;
        this.ltps = ltps;
        this.section = section;
        this.year = year;
        this.semester = sem;
        this.frDate = frDate;
        this.totalConducted = conducted;
        this.totalAttended = attended;
        this.tcbr = tcbr;
        
        this.totalAbsent = conducted - attended;
        this.percentage = conducted == 0 ? 0.0 : ((double)attended / conducted) * 100;
    }

    // --- LOGIC ---

    // 1. Calculate remaining working days (Mon-Fri)
    public int getRemainingClassesEst() {
        long days = Stream.iterate(TODAY.plusDays(1), date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(TODAY, END_SEM))
            .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
            .count();
        return (int) days; 
    }

    // 2. Total classes by Dec 30
    public int getTotalProjectedClasses() {
        return totalConducted + getRemainingClassesEst();
    }

    // 3. Classes needed for 85%
    public int getClassesNeededForTarget() {
        int totalProjected = getTotalProjectedClasses();
        int requiredAttendance = (int) Math.ceil(0.85 * totalProjected);
        int needed = requiredAttendance - totalAttended;
        return Math.max(needed, 0);
    }

    // 4. GAINING PERCENTAGE (Max Possible if I attend ALL remaining) - Used for New Column
    public double getMaxPossiblePercentage() {
        int totalProjected = getTotalProjectedClasses();
        if (totalProjected == 0) return 0.0;
        int potentialAttended = totalAttended + getRemainingClassesEst();
        return ((double) potentialAttended / totalProjected) * 100;
    }

    // 5. LOSING PERCENTAGE (Min Possible if I miss ALL remaining) - Used for New Column
    public double getMinPossiblePercentage() {
        int totalProjected = getTotalProjectedClasses();
        if (totalProjected == 0) return 0.0;
        // Assume student attends 0 more classes, so attended remains same
        return ((double) totalAttended / totalProjected) * 100;
    }

    // 6. The Detailed Message for the Dashboard (Action Plan)
    public String getRiskMessage() {
        int needed = getClassesNeededForTarget();
        int min = (int) getMinPossiblePercentage();
        int max = (int) getMaxPossiblePercentage();

        // Base Logic for 85%
        String action;
        if (percentage >= 85 && needed == 0) {
            action = "Safe. Maintain streak.";
        } else if (max < 85) {
            action = "CRITICAL: Cannot reach 85%.";
        } else {
            action = "Action: Attend next " + needed + " classes.";
        }

        return action; // Returning just the action, as percentages are now in columns
    }
}