package com.talkhasam.artichat.global.util;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;

public class InstantStringConverter implements AttributeConverter<Instant> {
    @Override
    public AttributeValue transformFrom(Instant input) {
        return AttributeValue.builder()
                .s(input.toString())  // ISO-8601 string
                .build();
    }

    @Override
    public Instant transformTo(AttributeValue attributeValue) {
        return Instant.parse(attributeValue.s());
    }

    @Override
    public EnhancedType<Instant> type() {
        return EnhancedType.of(Instant.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
