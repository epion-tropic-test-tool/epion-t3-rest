package com.epion_t3.rest.command;

import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.rest.runner.StoreJsonElementRunner;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

/**
 * JSONボディ要素ストア.
 *
 * @author takashno
 */
@Getter
@Setter
@CommandDefinition(id = "StoreJsonElement", runner = StoreJsonElementRunner.class)
public class StoreJsonElement extends Command {

    /**
     * JSONPath.
     */
    @NotEmpty
    private String jsonPath;
}
