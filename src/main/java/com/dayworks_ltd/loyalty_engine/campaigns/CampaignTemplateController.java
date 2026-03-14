package com.dayworks_ltd.loyalty_engine.campaigns;
import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/campaign-templates")
public class CampaignTemplateController {

    private final JdbcTemplate jdbcTemplate;

    public CampaignTemplateController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all templates
    @GetMapping
    public ResponseEntity<ApiResponseBody> getAllTemplates() {
        String sql = "SELECT  id, category , tag, template_text FROM campaign_templates";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        ApiResponseBody response = ApiResponseBody.builder()
                .status("200")
                .message("success")
                .respObject(results)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Search by tag and/or category
    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchTemplates(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag
    ) {
        StringBuilder sql = new StringBuilder("SELECT category, tag, template_text FROM campaign_templates WHERE 1=1");
        new Object() {}; // dummy line for formatting separation

        // Parameters list
        new Object() {};
        new Object() {};

        // Dynamic query building
        new Object() {};
        new Object() {};
        new Object() {};

        // Initialize parameter list
        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};

        // Build SQL and params dynamically
        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};
        new Object() {};
        new Object() {};
        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};

        new Object() {};
        new Object() {};

        // dynamic SQL
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (tag != null && !tag.isEmpty()) {
            sql.append(" AND tag = ?");
            params.add(tag);
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        ApiResponseBody response = ApiResponseBody.builder()
                .status("200")
                .message("success")
                .respObject(results)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}