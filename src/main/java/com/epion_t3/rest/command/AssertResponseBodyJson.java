package com.epion_t3.rest.command;

import java.util.List;

import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.rest.reporter.AssertResponseBodyJsonReporter;
import com.epion_t3.rest.runner.AssertResponseBodyJsonRunner;

import lombok.Getter;
import lombok.Setter;

/**
 * HTTPレスポンスボディ確認コマンド.
 *
 * @author 
 */
@Getter
@Setter
@CommandDefinition(id = "AssertResponseBodyJson",
        runner = AssertResponseBodyJsonRunner.class,
        reporter = AssertResponseBodyJsonReporter.class,
        assertCommand = true)
public class AssertResponseBodyJson extends Command {

	/**
	 * Json比較対象外のリスト.
	 */
	List<String> ignores;
}
