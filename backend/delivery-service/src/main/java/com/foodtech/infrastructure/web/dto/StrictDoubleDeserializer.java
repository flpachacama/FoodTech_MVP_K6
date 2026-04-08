package com.foodtech.infrastructure.web.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StrictDoubleDeserializer extends StdDeserializer<Double> {

    public StrictDoubleDeserializer() {
        super(Double.class);
    }

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            ctxt.reportInputMismatch(Double.class,
                    "Se esperaba un número (double), pero se recibió un String: \"%s\". No se permiten comillas.", p.getText());
            return null;
        }
        return p.getDoubleValue();
    }
}
