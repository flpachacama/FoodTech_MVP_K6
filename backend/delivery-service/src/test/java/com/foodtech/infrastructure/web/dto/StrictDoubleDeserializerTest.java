package com.foodtech.infrastructure.web.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrictDoubleDeserializerTest {

    private StrictDoubleDeserializer deserializer;

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext context;

    @BeforeEach
    void setUp() {
        deserializer = new StrictDoubleDeserializer();
    }

    @Test
    void deserialize_cuandoTokenEsString_llamaReportInputMismatchYRetornaNull() throws IOException {
        when(parser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
        when(parser.getText()).thenReturn("1.5");

        Double result = deserializer.deserialize(parser, context);

        assertThat(result).isNull();
        verify(context).reportInputMismatch(any(Class.class), anyString(), anyString());
    }

    @Test
    void deserialize_cuandoTokenEsNumero_retornaDoubleValue() throws IOException {
        when(parser.currentToken()).thenReturn(JsonToken.VALUE_NUMBER_FLOAT);
        when(parser.getDoubleValue()).thenReturn(3.14);

        Double result = deserializer.deserialize(parser, context);

        assertThat(result).isEqualTo(3.14);
    }

    @Test
    void deserialize_cuandoTokenEsEntero_retornaDoubleValue() throws IOException {
        when(parser.currentToken()).thenReturn(JsonToken.VALUE_NUMBER_INT);
        when(parser.getDoubleValue()).thenReturn(42.0);

        Double result = deserializer.deserialize(parser, context);

        assertThat(result).isEqualTo(42.0);
    }
}
