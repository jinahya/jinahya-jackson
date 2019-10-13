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
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.InputStream;

import static com.github.jinahya.jackson.core.JinahyaJsonParserUtils.getPublisherForArrayElements;
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

    private static class StringSubscriber implements Subscriber<String> {

        @Override
        public void onSubscribe(final Subscription s) {
            log.debug("subscribed: {}", s);
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final String s) {
            log.debug("data: {}", s);
        }

        @Override
        public void onError(final Throwable t) {
            log.error("error: {}", t.getMessage(), t);
        }

        @Override
        public void onComplete() {
            log.debug("completed");
        }
    }

    private static class ItemSubscriber implements Subscriber<Item> {

        @Override
        public void onSubscribe(final Subscription s) {
            log.debug("subscription: {}", s);
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final Item t) {
            log.debug("data: {}", t);
        }

        @Override
        public void onError(final Throwable t) {
            log.error("error: {}", t.getMessage(), t);
        }

        @Override
        public void onComplete() {
            log.debug("completed.");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void testParseArrayElementsAndAccept_array01() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, String.class, e -> {
                    log.debug("element string: {}", e);
                });
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parseArrayElementsAndAccept(parser, JsonNode.class, e -> {
                    log.debug("element node as text: {}", e.asText());
                });
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
                    log.debug("element item: {}", e);
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
                        log.debug("element node item: {}", item);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
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
                    log.debug("element: {}", e);
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

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void testGetPublisherForArrayElements_array01() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("array01.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                final Publisher<String> publisher = getPublisherForArrayElements(parser, String.class);
                publisher.subscribe(new StringSubscriber());
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }

    @Test
    void testGetPublisherForArrayElements_array02() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("array02.json")) {
            try (JsonParser parser = JSON_FACTORY.createParser(resource)) {
                assertNull(parser.currentToken());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                final Publisher<Item> publisher = getPublisherForArrayElements(parser, Item.class);
                publisher.subscribe(new ItemSubscriber());
                assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            }
        }
    }
}
