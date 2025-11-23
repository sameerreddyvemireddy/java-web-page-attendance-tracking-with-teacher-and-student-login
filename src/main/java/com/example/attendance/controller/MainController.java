package com.example.attendance.controller;

import com.example.attendance.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/")
    public String loginPage() { return "login"; }

    @PostMapping("/student/login")
    public String studentLogin(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "student-dashboard";
    }

    @PostMapping("/student/search")
    public String searchAttendance(Model model) {
        model.addAttribute("courses", excelService.getDummyData());
        model.addAttribute("searched", true);
        return "student-dashboard";
    }

    @PostMapping("/teacher/login")
    public String teacherLogin() { return "teacher-dashboard"; }

    @PostMapping("/teacher/upload")
    public String uploadFiles(@RequestParam("timetable") MultipartFile timeTable,
                              @RequestParam("students") MultipartFile students,
                              Model model) {
        
        // 1. Process Student Data
        model.addAttribute("courses", excelService.parseStudentFile(students));
        
        // 2. Process Timetable Data
        model.addAttribute("timetable", excelService.parseTimetableFile(timeTable));
        
        model.addAttribute("uploaded", true);
        return "teacher-dashboard";
    }
}