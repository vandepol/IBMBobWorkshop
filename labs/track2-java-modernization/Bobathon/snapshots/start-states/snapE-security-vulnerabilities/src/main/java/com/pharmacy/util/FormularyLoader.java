package com.pharmacy.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Loads the medicine formulary configuration from a YAML resource file.
 *
 * VULNERABILITY: Uses snakeyaml 1.30 which is affected by
 * GHSA-mjmj-j48q-9wg2 (Constructor Deserialization RCE). Loading untrusted
 * YAML with the default Yaml constructor allows arbitrary Java class
 * instantiation.
 * Fix: upgrade snakeyaml to 2.0
 */
public class FormularyLoader {

    private static final FormularyLoader INSTANCE = new FormularyLoader();
    private static final String FORMULARY_RESOURCE = "/formulary.yaml";

    private FormularyLoader() {}

    public static FormularyLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Load and return the list of formulary categories from the bundled YAML config.
     */
    @SuppressWarnings("unchecked")
    public List<String> loadCategories() {
        Yaml yaml = new Yaml();
        InputStream in = getClass().getResourceAsStream(FORMULARY_RESOURCE);
        if (in == null) {
            return Collections.emptyList();
        }
        Map<String, Object> data = yaml.load(in);
        Object categories = data.get("categories");
        if (categories instanceof List) {
            return (List<String>) categories;
        }
        return Collections.emptyList();
    }

    /**
     * Check whether a given medicine category is on the formulary.
     */
    public boolean isCategoryApproved(String category) {
        return loadCategories().stream()
                .anyMatch(c -> c.equalsIgnoreCase(category));
    }
}
