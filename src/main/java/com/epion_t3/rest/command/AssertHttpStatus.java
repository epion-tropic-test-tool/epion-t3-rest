package com.epion_t3.rest.command;

import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.rest.runner.AssertHttpStatusRunner;
import lombok.Getter;
import lombok.Setter;

/**
 * HTTPステータス確認コマンド.
 *
 * @author takashno
 */
@Getter
@Setter
@CommandDefinition(id = "AssertHttpStatus",
        runner = AssertHttpStatusRunner.class,
        assertCommand = true)
public class AssertHttpStatus extends Command {
}
