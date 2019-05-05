package com.epion_t3.rest.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

@Getter
@Setter
public class QueryParameter implements Serializable {

    @NotEmpty
    private String name;

    @NotEmpty
    private String value;

    private Boolean needUrlEncoded = false;
}
