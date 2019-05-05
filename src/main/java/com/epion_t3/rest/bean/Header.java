package com.epion_t3.rest.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Header implements Serializable {

    private String name;

    private String value;

}
