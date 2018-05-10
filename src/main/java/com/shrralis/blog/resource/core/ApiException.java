package com.shrralis.blog.resource.core;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 6718186634748785460L;

    @Getter
    private final int status;

    public static ApiException of(Throwable ex) {
        return new ApiException(ex);
    }

    public static ApiException of(int status, String message, Object... args) {
        return new ApiException(status, args.length == 0 ? message : MessageFormat.format(message, args));
    }

    public static <T> Flowable<T> flowable(Throwable ex) {
        return Flowable.error(of(ex));
    }

    public static <T> Flowable<T> flowable(int status, String message, Object... args) {
        return Flowable.error(of(status, message, args));
    }

    public static <T> Observable<T> observable(Throwable ex) {
        return Observable.error(of(ex));
    }

    public static <T> Observable<T> observable(int status, String message, Object... args) {
        return Observable.error(of(status, message, args));
    }

    private ApiException(Throwable ex) {
        super(ex.getMessage(), ex);

        status = ex instanceof ApiException ? ((ApiException) ex).getStatus() : 500;
    }

    public ApiException(int status, Throwable ex) {
        super(ex.getMessage(), ex);

        this.status = status;
    }

    public ApiException(int status, String message) {
        super(message);

        this.status = status;
    }

    public Map.Entry<Integer, String> toEntry() {
        return Map.entry(status, StringUtils.defaultString(getMessage(), "Unknown internal error"));
    }

    public Map<Integer, String> toMap() {
        return Map.of(status, StringUtils.defaultString(getMessage(), "Unknown internal error"));
    }
}
