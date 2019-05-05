package com.epion_t3.rest.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * HTTPメソッド.
 */
@Getter
@AllArgsConstructor
public enum HttpMethodType {

    /**
     * GET.
     */
    GET("get"),

    /**
     * POST.
     */
    POST("post"),

    /**
     * PUT.
     */
    PUT("put"),

    /**
     * PATCH.
     */
    PATCH("patch"),

    /**
     * DELETE.
     */
    DELETE("delete");

    /**
     * 値.
     */
    private String value;

    /**
     * 値からHTTPメソッドを取得する.
     *
     * @param value
     * @return
     */
    public static HttpMethodType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}
