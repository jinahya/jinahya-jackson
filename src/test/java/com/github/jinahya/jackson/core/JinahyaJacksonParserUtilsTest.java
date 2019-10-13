package com.github.jinahya.jackson.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static com.github.jinahya.jackson.core.JinahyaJsonParserUtils.parseArrayElementsAndAccept;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * A class for unit-testing {@link JinahyaJsonParserUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class JinahyaJacksonParserUtilsTest {

    // -----------------------------------------------------------------------------------------------------------------
    private static final JsonFactory JSON_FACTORY = new ObjectMapper().getFactory();

    // -----------------------------------------------------------------------------------------------------------------
    @ToString
    private static class Item {

        @Setter
        @Getter
        private String name;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void testParseArrayElementsAndAccept_array01() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, String.class, e -> {
                    log.debug("item: {}", e);
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, JsonNode.class, e -> {
                    log.debug("node: {}", e.asText());
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                for (final Iterator<String> i = parser.readValuesAs(String.class); i.hasNext(); ) {
                    log.debug("i: {}", i.next());
                }
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }

    @Test
    void testParseArrayElementsAndAccept_array02() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("array02.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, Item.class, e -> {
                    log.debug("item: {}", e);
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
        try (InputStream resource = getClass().getResourceAsStream("array02.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, JsonNode.class, e -> {
                    try {
                        final Item item = new ObjectMapper().treeToValue(e, Item.class);
                        log.debug("node: {}", item);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
        try (InputStream resource = getClass().getResourceAsStream("array02.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                for (final Iterator<Item> i = parser.readValuesAs(Item.class); i.hasNext();) {
                    log.debug("i: {}", i.next());
                }
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }

    @Test
    void testParseArrayElementsAndAccept_arrayInObject01() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("arrayInObject01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, String.class, e -> {
                    log.debug("item: {}", e);
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }

    @Test
    void testParseArrayElementsAndAccept_arrayInObject02() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("arrayInObject02.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, Item.class, e -> {
                    log.debug("element: {}", e);
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }
}
