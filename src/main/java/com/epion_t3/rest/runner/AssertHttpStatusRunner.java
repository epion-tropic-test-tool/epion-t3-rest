package com.epion_t3.rest.runner;

import com.epion_t3.core.command.bean.AssertCommandResult;
import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.runner.impl.AbstractCommandRunner;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.common.type.AssertStatus;
import com.epion_t3.rest.bean.Response;
import com.epion_t3.rest.command.AssertHttpStatus;
import com.epion_t3.rest.message.RestMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class AssertHttpStatusRunner extends AbstractCommandRunner<AssertHttpStatus> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(AssertHttpStatus command, Logger logger) throws Exception {

        AssertCommandResult commandResult = new AssertCommandResult();

        Response response = referObjectEvidence(command.getTarget());

        if (!StringUtils.isNumeric(command.getValue())) {
            throw new SystemException(RestMessages.REST_ERR_9002, command.getValue());
        }

        int statusCode = Integer.valueOf(command.getValue());

        commandResult.setExpected(statusCode);
        commandResult.setActual(response.getStatusCode());

        if (response.getStatusCode() == statusCode) {
            commandResult.setAssertStatus(AssertStatus.OK);
            commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_INFO_1001));
        } else {
            commandResult.setAssertStatus(AssertStatus.NG);
            commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_ERR_9003));
        }

        return commandResult;
    }
}
