package com.gcc.impossible.bizstatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NtsStatusResponse(String status_code, int match_cnt, int request_cnt, List<NtsStatusItem> data) {

    public List<NtsStatusItem> items() {
        return data == null ? List.of() : data;
    }
}
