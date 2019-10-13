package com.github.jinahya.jackson.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

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
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IllegalArgumentException("parser.currentToken != " + JsonToken.START_ARRAY);
        }
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            consumer.accept(parser.readValueAs(type));
        }
        return parser;
    }

//    public static <T> JsonParser parseArrayElementsAndAccept(final JsonParser parser, final TypeReference<?> type,
//                                                             final Consumer<? super T> consumer)
//            throws IOException {
//        if (parser == null) {
//            throw new NullPointerException("parser is null");
//        }
//        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
//            throw new IllegalArgumentException("parser.currentToken != " + JsonToken.START_ARRAY);
//        }
//        while (parser.nextToken() != JsonToken.END_ARRAY) {
//            final T value = parser.readValueAs(type);
//            consumer.accept(value);
//        }
//        return parser;
//    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <T> Publisher<T> getPublisherForArrayElements(
            final Function<? super Subscriber, ? extends Subscription> function, final JsonParser parser,
            final Class<? extends T> type) {
        if (function == null) {
            throw new NullPointerException("function is null");
        }
        if (parser == null) {
            throw new NullPointerException("parser is null");
        }
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IllegalArgumentException("parser.currentToken != " + JsonToken.START_ARRAY);
        }
        return s -> {
            s.onSubscribe(function.apply(s));
            try {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    s.onNext(parser.readValueAs(type));
                }
                s.onComplete();
            } catch (final IOException ioe) {
                s.onError(ioe);
            }
        };
    }

    public static <T> Publisher<T> getPublisherForArrayElements(final JsonParser parser,
                                                                final Class<? extends T> type) {
        if (parser == null) {
            throw new NullPointerException("parser is null");
        }
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IllegalArgumentException("parser.currentToken != " + JsonToken.START_ARRAY);
        }
        return s -> {
            s.onSubscribe(new Subscription() {

                @Override
                public void request(final long n) {
                    if (cancelled) {
                        throw new IllegalStateException("cancelled");
                    }
                    try {
                        for (long i = 0; i < n && parser.nextToken() != JsonToken.END_ARRAY; i++) {
                            s.onNext(parser.readValueAs(type));
                        }
                        if (parser.getCurrentToken() == JsonToken.END_ARRAY) {
                            s.onComplete();
                        }
                    } catch (final IOException ioe) {
                        s.onError(ioe);
                    }
                }

                @Override
                public void cancel() {
                    cancelled = true;
                }

                private boolean cancelled;
            });
//            try {
//                while (parser.nextToken() != JsonToken.END_ARRAY) {
//                    s.onNext(parser.readValueAs(type));
//                }
//                s.onComplete();
//            } catch (final IOException ioe) {
//                s.onError(ioe);
//            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private JinahyaJsonParserUtils() {
        super();
    }
}
