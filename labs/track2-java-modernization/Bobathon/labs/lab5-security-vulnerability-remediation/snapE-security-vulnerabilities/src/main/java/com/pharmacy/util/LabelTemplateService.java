package com.pharmacy.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates medicine dispense labels by substituting template variables.
 *
 * VULNERABILITY: Uses commons-text 1.9 which is affected by Text4Shell
 * (CVE-2022-42889, GHSA-599f-7c49-w659). StringSubstitutor supports
 * script/dns/url interpolation prefixes that allow arbitrary code execution
 * when user-controlled strings are substituted.
 * Fix: upgrade commons-text to 1.10.0
 */
public class LabelTemplateService {

    private static final LabelTemplateService INSTANCE = new LabelTemplateService();

    private static final String LABEL_TEMPLATE =
            "Patient: ${patientName}\n" +
            "Medicine: ${medicineName}\n" +
            "Quantity: ${quantity}\n" +
            "Prescriber: ${prescriber}\n" +
            "Date: ${date}\n" +
            "Instructions: ${instructions}";

    private LabelTemplateService() {}

    public static LabelTemplateService getInstance() {
        return INSTANCE;
    }

    /**
     * Render a dispense label for the given prescription details.
     */
    public String renderLabel(String patientName, String medicineName,
                              int quantity, String prescriber,
                              String date, String instructions) {
        Map<String, String> values = new HashMap<>();
        values.put("patientName", patientName);
        values.put("medicineName", medicineName);
        values.put("quantity", String.valueOf(quantity));
        values.put("prescriber", prescriber);
        values.put("date", date);
        values.put("instructions", instructions != null ? instructions : "As directed");

        StringSubstitutor substitutor = new StringSubstitutor(values);
        return substitutor.replace(LABEL_TEMPLATE);
    }
}
