package com.github.jinahya.jackson.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utilities for {@link com.fasterxml.jackson.core.JsonParser}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaJsonParserUtils {

    // -----------------------------------------------------------------------------------------------------------------
    public static <T> JsonParser parseArrayElementsAndAccept(final JsonParser parser, final Class<? extends T> type,
                                                             final Consumer<? super T> consumer)
            throws IOException {
        if (parser == null) {
            throw new NullPointerException("parser is null");
        }
        final JsonToken currentToken = parser.getCurrentToken();
        if (currentToken != JsonToken.START_ARRAY) {
            throw new IllegalArgumentException("parser.currentToken(" + currentToken + ") != " + JsonToken.START_ARRAY);
        }
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        if (consumer == null) {
            throw new NullPointerException("consumer is null");
        }
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            consumer.accept(parser.readValueAs(type));
        }
        return parser;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private JinahyaJsonParserUtils() {
        super();
    }
}
