package com.gcc.impossible.ontology;

import java.util.Map;

/** ontology.json 최상위 구조 — 데이터팀(주희) 노션 스펙 그대로 */
record OntologyFile(Map<String, OntologyEntryJson> categories) {

    record OntologyEntryJson(String type, int minVisits, int periodMonths) {
    }
}
