package com.talkhasam.artichat.global.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class JsonRequestNumericStringToLongConfig extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException, IOException {
        if(org.springframework.util.StringUtils.hasText(jsonParser.getText()) && StringUtils.isNumeric(jsonParser.getText())){
            return Long.parseLong(jsonParser.getText());
        }
        return null;
    }
}