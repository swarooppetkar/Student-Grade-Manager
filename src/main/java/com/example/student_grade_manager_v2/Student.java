package com.example.student_grade_manager_v2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Stores a student's name and subject marks (each out of 100).
 *
 * Grade boundaries (percentage):
 *   F  : 0  - 34.99   (Fail)
 *   D  : 35 - 49.99   (Pass)
 *   C  : 50 - 59.99
 *   B  : 60 - 74.99
 *   A  : 75 - 89.99
 *   A+ : 90 - 100
 */
public class Student {

    public static final int MAX_MARK = 100;
    public static final int PASS_PERCENT = 35;

    private final String name;
    private final Map<String, Integer> marks;

    public Student(String name, Map<String, Integer> marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() { return name; }
    public Map<String, Integer> getMarks() { return marks; }

    public int getTotal() {
        return marks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getMaxTotal() { return marks.size() * MAX_MARK; }

    public int getSubjectCount() { return marks.size(); }

    public double getPercentage() {
        if (marks.isEmpty()) return 0.0;
        return getTotal() * 100.0 / getMaxTotal();
    }

    /** Percentage with one decimal and a dot, safe to use inside CSS. */
    public String getPercentageText() {
        return String.format(Locale.ROOT, "%.1f", getPercentage());
    }

    public String getGrade() { return gradeFor(getPercentage()); }

    public String getGradeClass() { return cssClass(getGrade()); }

    public String getStatus() {
        return getPercentage() >= PASS_PERCENT ? "PASS" : "FAIL";
    }

    public static String gradeFor(double percent) {
        if (percent >= 90) return "A+";
        if (percent >= 75) return "A";
        if (percent >= 60) return "B";
        if (percent >= 50) return "C";
        if (percent >= PASS_PERCENT) return "D";
        return "F";
    }

    public static String cssClass(String grade) {
        return grade.equals("A+") ? "aplus" : grade.toLowerCase();
    }

    /** One subject with its grade, used by the report page. */
    public static class SubjectResult {
        private final String name;
        private final int mark;

        SubjectResult(String name, int mark) {
            this.name = name;
            this.mark = mark;
        }

        public String getName() { return name; }
        public int getMark() { return mark; }
        public String getGrade() { return gradeFor(mark); }
        public String getGradeClass() { return cssClass(getGrade()); }
    }

    public List<SubjectResult> getResults() {
        List<SubjectResult> list = new ArrayList<>();
        marks.forEach((s, m) -> list.add(new SubjectResult(s, m)));
        return list;
    }

    public SubjectResult getBest() {
        return getResults().stream().max(Comparator.comparingInt(SubjectResult::getMark)).orElse(null);
    }

    public SubjectResult getLowest() {
        return getResults().stream().min(Comparator.comparingInt(SubjectResult::getMark)).orElse(null);
    }
}