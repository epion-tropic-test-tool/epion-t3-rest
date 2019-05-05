package com.epion_t3.rest.command;

import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.rest.runner.AssertHttpHeaderRunner;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

/**
 * HTTPステータス確認コマンド.
 *
 * @author takashno
 */
@Getter
@Setter
@CommandDefinition(id = "AssertHttpHeader",
        runner = AssertHttpHeaderRunner.class,
        assertCommand = true)
public class AssertHttpHeader extends Command {

    @NotEmpty
    private String header;
}
