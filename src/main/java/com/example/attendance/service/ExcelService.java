package com.example.attendance.service;

import com.example.attendance.model.Course;
import com.example.attendance.model.TimetableRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public List<Course> parseStudentFile(MultipartFile file) {
        List<Course> courses = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip Header

                // Read the specific columns requested
                // Order: Code | Desc | Ltps | Section | Year | Sem | FrDate | Conducted | Attended | Tcbr
                String code = getVal(row, 0);
                String desc = getVal(row, 1);
                String ltps = getVal(row, 2);
                String section = getVal(row, 3);
                String year = getVal(row, 4);
                String sem = getVal(row, 5);
                String frDate = getVal(row, 6);
                
                int conducted = (int) Double.parseDouble(getVal(row, 7));
                int attended = (int) Double.parseDouble(getVal(row, 8));
                int tcbr = (int) Double.parseDouble(getVal(row, 9));

                courses.add(new Course(code, desc, ltps, section, year, sem, frDate, conducted, attended, tcbr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Keep the Timetable parser same as before
    public List<TimetableRow> parseTimetableFile(MultipartFile file) {
        List<TimetableRow> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                rows.add(new TimetableRow(getVal(row,0), getVal(row,1), getVal(row,2), getVal(row,3), getVal(row,4), getVal(row,5), getVal(row,6)));
            }
        } catch (Exception e) {}
        return rows;
    }

    // Helper to avoid crashes on empty cells
    private String getVal(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "0";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        return "0";
    }
    
    // Dummy Data matching new columns
    public List<Course> getDummyData() {
        List<Course> list = new ArrayList<>();
        list.add(new Course("25SC1105E", "PROBLEM SOLVING JAVA", "L", "S-11", "2025", "Odd", "N", 14, 12, 0));
        list.add(new Course("25MT1002E", "DISCRETE MATHS", "T", "S-11", "2025", "Odd", "N", 14, 14, 0));
        return list;
    }
}