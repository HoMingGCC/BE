package com.gcc.impossible.ontology;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/** FE lib/ontology.ts 의 ruleOf() 포팅 — 정의되지 않은 업종은 기본값(기준 10회, 대체 불가)으로 처리 */
@Service
@EnableConfigurationProperties(OntologyProperties.class)
public class OntologyService {

    private static final int DEFAULT_THRESHOLD = 10;

    private final Map<String, IndustryRule> rules;

    public OntologyService(OntologyProperties properties) {
        this.rules = properties.getIndustries().stream()
                .map(e -> new IndustryRule(e.getIndustry(), e.getCategory(), e.getThreshold(), e.isSubstitutable()))
                .collect(Collectors.toMap(IndustryRule::industry, Function.identity()));
    }

    public IndustryRule ruleOf(String industry) {
        IndustryRule rule = rules.get(industry);
        if (rule == null) {
            return new IndustryRule(industry, Category.LIFE, DEFAULT_THRESHOLD, false);
        }
        return rule;
    }
}
