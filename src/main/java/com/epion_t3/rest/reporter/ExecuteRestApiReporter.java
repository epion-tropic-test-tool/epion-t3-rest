package com.epion_t3.rest.reporter;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.reporter.impl.AbstractThymeleafCommandReporter;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.util.EvidenceUtils;
import com.epion_t3.rest.command.ExecuteRestApi;

import java.util.Map;

public class ExecuteRestApiReporter
        extends AbstractThymeleafCommandReporter<ExecuteRestApi, CommandResult> {
    @Override
    public String templatePath() {
        return "execute-rest-api-report";
    }

    @Override
    public void setVariables(Map<String, Object> variable,
                             ExecuteRestApi command,
                             CommandResult commandResult,
                             ExecuteContext executeContext,
                             ExecuteScenario executeScenario,
                             ExecuteFlow executeFlow,
                             ExecuteCommand executeCommand) {
        variable.put("request", command.getRequest());
        variable.put("response", EvidenceUtils.getInstance().referObjectEvidence(
                executeContext, executeScenario, executeFlow.getFlow().getId()));
    }
}
