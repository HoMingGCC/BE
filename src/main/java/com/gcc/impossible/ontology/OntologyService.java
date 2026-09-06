package com.gcc.impossible.ontology;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * 데이터팀(주희) 노션 "업종별 단골 판정 기준" — ontology.json을 그대로 읽어서 판정 엔진에 제공한다.
 * 숫자를 Java 코드에 하드코딩하지 않고 JSON에서 불러오므로, 기준이 바뀌어도 이 클래스는 안 건드려도 된다.
 */
@Service
public class OntologyService {

    private static final String ONTOLOGY_FILE = "ontology.json";
    private static final int DEFAULT_MIN_VISITS = 10;
    private static final int DEFAULT_PERIOD_MONTHS = 12;

    private final Map<String, IndustryRule> rules;

    public OntologyService() {
        this.rules = loadRules();
    }

    public IndustryRule ruleOf(String industry) {
        IndustryRule rule = rules.get(industry);
        if (rule == null) {
            return new IndustryRule(industry, "count", DEFAULT_MIN_VISITS, DEFAULT_PERIOD_MONTHS);
        }
        return rule;
    }

    private Map<String, IndustryRule> loadRules() {
        try (InputStream in = new ClassPathResource(ONTOLOGY_FILE).getInputStream()) {
            OntologyFile file = new ObjectMapper().readValue(in, OntologyFile.class);
            return file.categories().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new IndustryRule(
                                    e.getKey(), e.getValue().type(), e.getValue().minVisits(), e.getValue().periodMonths())));
        } catch (IOException e) {
            throw new IllegalStateException(ONTOLOGY_FILE + " 을 읽는 데 실패했습니다", e);
        }
    }
}
