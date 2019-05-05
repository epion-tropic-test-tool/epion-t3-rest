package com.epion_t3.rest.message;

import com.epion_t3.core.message.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ETTTのRest系メッセージ.
 */
@Getter
@AllArgsConstructor
public enum RestMessages implements Messages {

    REST_ERR_9001("com.epion_t3.rest.err.9001"),
    REST_ERR_9002("com.epion_t3.rest.err.9002"),
    REST_ERR_9003("com.epion_t3.rest.err.9003"),
    REST_ERR_9004("com.epion_t3.rest.err.9004"),
    REST_ERR_9005("com.epion_t3.rest.err.9005"),
    REST_ERR_9006("com.epion_t3.rest.err.9006"),
    REST_ERR_9007("com.epion_t3.rest.err.9007"),
    REST_ERR_9008("com.epion_t3.rest.err.9008"),
    REST_ERR_9009("com.epion_t3.rest.err.9009"),
    REST_ERR_9010("com.epion_t3.rest.err.9010"),
    REST_ERR_9011("com.epion_t3.rest.err.9011"),
    REST_ERR_9012("com.epion_t3.rest.err.9012"),
    REST_ERR_9013("com.epion_t3.rest.err.9013"),
    REST_ERR_9014("com.epion_t3.rest.err.9014"),
    REST_ERR_9015("com.epion_t3.rest.err.9015"),
    REST_ERR_9016("com.epion_t3.rest.err.9016"),
    REST_ERR_9017("com.epion_t3.rest.err.9017"),
    REST_ERR_9018("com.epion_t3.rest.err.9018"),
    REST_INFO_1001("com.epion_t3.rest.info.1001"),
    REST_INFO_1002("com.epion_t3.rest.info.1002"),;


    private String messageCode;

}
