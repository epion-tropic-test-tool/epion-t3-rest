package com.epion_t3.rest.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> headers;

    @NotEmpty
    private String method;

    @NotEmpty
    private String targetUrl;

    private List<QueryParameter> queryParameters;

    private String body;

}
