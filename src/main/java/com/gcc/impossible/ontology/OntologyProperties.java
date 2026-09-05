package com.gcc.impossible.ontology;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 업종별 단골 기준을 코드가 아니라 데이터(ontology.yml)로 정의한다.
 * 기준이 바뀌어도 로직을 안 건드리기 위함 — FE src/lib/ontology.ts 와 동일한 설계 원칙.
 *
 * (업종명을 YAML 맵 키로 쓰면 한글 키 바인딩이 깨져서 리스트로 둔다)
 */
@ConfigurationProperties(prefix = "ontology")
public class OntologyProperties {

    private List<Entry> industries = List.of();

    public List<Entry> getIndustries() {
        return industries;
    }

    public void setIndustries(List<Entry> industries) {
        this.industries = industries;
    }

    public static class Entry {
        private String industry;
        private Category category;
        private int threshold;
        private boolean substitutable;

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public boolean isSubstitutable() {
            return substitutable;
        }

        public void setSubstitutable(boolean substitutable) {
            this.substitutable = substitutable;
        }
    }
}
