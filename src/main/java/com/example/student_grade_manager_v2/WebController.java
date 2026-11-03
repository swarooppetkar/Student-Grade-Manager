package com.example.student_grade_manager_v2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    static final int MAX_SUBJECTS = 50;
    static final int DEFAULT_SUBJECTS = 5;

    /** One subject row on the form (raw text so wrong input can be shown back). */
    public static class FormRow {
        private final String subject;
        private final String mark;

        public FormRow(String subject, String mark) {
            this.subject = subject;
            this.mark = mark;
        }

        public String getSubject() { return subject; }
        public String getMark() { return mark; }
    }

    @GetMapping("/")
    public String form(Model model) {
        fillForm(model, "", blankRows(DEFAULT_SUBJECTS), null);
        return "form";
    }

    @PostMapping("/report")
    public String report(HttpServletRequest request, Model model) {
        String name = request.getParameter("name") == null ? "" : request.getParameter("name").trim();
        String[] subjects = request.getParameterValues("subject");
        String[] marks = request.getParameterValues("mark");
        if (subjects == null) subjects = new String[0];
        if (marks == null) marks = new String[0];

        List<String> errors = new ArrayList<>();
        List<FormRow> rows = new ArrayList<>();
        Map<String, Integer> valid = new LinkedHashMap<>();

        if (name.isEmpty()) {
            errors.add("Student name must not be empty.");
        }

        int n = Math.max(subjects.length, marks.length);
        if (n == 0) {
            errors.add("Choose the number of subjects and enter the marks.");
        } else if (n > MAX_SUBJECTS) {
            errors.add("A maximum of " + MAX_SUBJECTS + " subjects is allowed.");
            n = MAX_SUBJECTS;
        }

        for (int i = 0; i < n; i++) {
            String subject = i < subjects.length ? subjects[i].trim() : "";
            String markText = i < marks.length ? marks[i].trim() : "";
            rows.add(new FormRow(subject, markText));
            int row = i + 1;

            if (subject.isEmpty() || markText.isEmpty()) {
                errors.add("Subject " + row + ": enter both the subject name and the mark.");
                continue;
            }
            if (valid.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(subject))) {
                errors.add("Subject " + row + ": \"" + subject + "\" is entered twice.");
                continue;
            }
            try {
                int mark = Integer.parseInt(markText);
                if (mark < 0 || mark > Student.MAX_MARK) {
                    errors.add(subject + ": mark must be between 0 and 100.");
                } else {
                    valid.put(subject, mark);
                }
            } catch (NumberFormatException ex) {
                errors.add(subject + ": mark must be a whole number.");
            }
        }

        if (!errors.isEmpty()) {
            fillForm(model, name, rows.isEmpty() ? blankRows(DEFAULT_SUBJECTS) : rows, errors);
            return "form";
        }

        model.addAttribute("student", new Student(name, valid));
        return "report";
    }

    /** Puts everything the form page needs into the model. */
    private void fillForm(Model model, String name, List<FormRow> rows, List<String> errors) {
        int count = rows.size();
        model.addAttribute("name", name);
        model.addAttribute("rows", rows);
        model.addAttribute("selected", count <= 10 ? count : 10);
        model.addAttribute("custom", count > 10 ? String.valueOf(count) : "");
        if (errors != null) {
            model.addAttribute("errors", errors);
        }
    }

    private List<FormRow> blankRows(int count) {
        List<FormRow> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(new FormRow("", ""));
        }
        return rows;
    }
}