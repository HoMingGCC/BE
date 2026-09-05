package com.gcc.impossible.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoKeywordResponse(List<Document> documents) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(String place_name, String x, String y) {
    }

    public List<Document> documentsOrEmpty() {
        return documents == null ? List.of() : documents;
    }
}
