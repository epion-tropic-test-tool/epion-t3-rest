package com.epion_t3.rest.runner;

import com.epion_t3.core.command.bean.AssertCommandResult;
import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.runner.impl.AbstractCommandRunner;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.common.type.AssertStatus;
import com.epion_t3.rest.bean.Response;
import com.epion_t3.rest.command.AssertHttpHeader;
import com.epion_t3.rest.message.RestMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class AssertHttpHeaderRunner extends AbstractCommandRunner<AssertHttpHeader> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(final AssertHttpHeader command, final Logger logger) throws Exception {

        AssertCommandResult commandResult = new AssertCommandResult();

        if (StringUtils.isEmpty(command.getTarget())) {
            throw new SystemException(RestMessages.REST_ERR_9004);
        }

        if (StringUtils.isEmpty(command.getValue())) {
            throw new SystemException(RestMessages.REST_ERR_9005);
        }

        if (StringUtils.isEmpty(command.getHeader())) {
            throw new SystemException(RestMessages.REST_ERR_9016);
        }

        Response response = referObjectEvidence(command.getTarget());

        List<String> actual = response.getHeaders().get(command.getHeader());
        if (actual != null) {
            commandResult.setActual(actual.stream().collect(Collectors.joining(";")));
        }

        commandResult.setExpected(command.getValue());

        if (commandResult.getExpected().equals(commandResult.getActual())) {
            commandResult.setAssertStatus(AssertStatus.OK);
            commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_INFO_1002));
        } else {
            commandResult.setAssertStatus(AssertStatus.NG);
            commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_ERR_9017));
        }

        return commandResult;
    }
}
