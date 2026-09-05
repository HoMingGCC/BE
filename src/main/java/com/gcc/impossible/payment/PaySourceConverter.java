package com.gcc.impossible.payment;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** @RequestParam(예: ?sources=localpay,im-card) 에서 PaySource 로 변환하기 위한 컨버터 */
@Component
public class PaySourceConverter implements Converter<String, PaySource> {

    @Override
    public PaySource convert(String source) {
        return PaySource.fromValue(source);
    }
}
