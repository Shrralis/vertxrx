package com.shrralis.blog.resource.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseContainer<T> {

    public static final String PROP_PAYLOAD = "payload";
    public static final String PROP_ERRORS = "errors";

    private T payload;
    private Map<Integer, String> errors;

    public ResponseContainer() {
        payload = null;
        errors = null;
    }

    public static <T> ResponseContainer<T> of(Object obj, Class<T> clazz) {
        val json = JsonObject.class.cast(obj);

        if (!json.containsKey(PROP_ERRORS)) {
            val payload = JsonObject.mapFrom(json).getJsonObject(ResponseContainer.PROP_PAYLOAD).mapTo(clazz);

            return ResponseContainer.<T>builder()
                .payload(payload)
                .build();
        }
        return of(obj);
    }

    public static <T> ResponseContainer<T> of(Object obj) {
        val json = JsonObject.class.cast(obj);

        return Json.decodeValue(json.toBuffer(), new TypeReference<>() {});
    }

    public static <K, V> ResponseContainer<Map<K, V>> mapOf(Object obj) {
        val json = JsonObject.class.cast(obj);

        return Json.decodeValue(json.toBuffer(), new TypeReference<>() {});
    }

    public static <T> ResponseContainer<List<T>> listOf(Object obj) {
        val json = JsonObject.class.cast(obj);

        if (!json.containsKey(PROP_ERRORS)) {
            return Json.decodeValue(json.toBuffer(), new TypeReference<>() {});
        }
        return of(obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseContainer<Page<T>> pageOf(Object obj) {
        val json = JsonObject.class.cast(obj);

        if (!json.containsKey(ResponseContainer.PROP_ERRORS)) {
            val page = json.getJsonObject(PROP_PAYLOAD).mapTo(Page.class);

            return ResponseContainer.<Page<T>>builder().payload((Page<T>) page).build();
        }
        return of(obj);
    }

    @JsonIgnore
    public boolean isValid() {
        return getErrors() == null || getErrors().isEmpty();
    }

    public JsonObject asJson() {
        return JsonObject.mapFrom(this);
    }

    public String asString() {
        return Json.encodePrettily(this);
    }

    @Value
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page<T> {
        private Iterable<T> content;
        private int size;
        private int number;
        private int totalPages;
        private int totalElements;

        @JsonCreator
        public Page(
            @JsonProperty("content") Iterable<T> content,
            @JsonProperty("size") int size,
            @JsonProperty("number") int number,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("totalElements") int totalElements
        ) {
            this.content = content;
            this.size = size;
            this.number = number;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
        }
    }
}
