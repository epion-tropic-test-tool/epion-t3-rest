package com.epion_t3.rest.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author takashno
 */
@Getter
@Setter
public class Response implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ステータスコード.
     */
    private int statusCode;

    /**
     * レスポンスヘッダ.
     */
    private Map<String, List<String>> headers;

    //private Map<String, Cookie> cookies;

    /**
     * レスポンスボディ.
     */
    private String body;

    /**
     *
     */
    private long receivedResponseAtMillis;

    private long sentRequestAtMillis;
}
