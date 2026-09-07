package com.gcc.impossible.ontology;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** @RequestParam(예: ?category=meal) 에서 Category 로 변환하기 위한 컨버터 — PaySourceConverter와 동일한 이유 */
@Component
public class CategoryConverter implements Converter<String, Category> {

    @Override
    public Category convert(String source) {
        return Category.fromValue(source);
    }
}
