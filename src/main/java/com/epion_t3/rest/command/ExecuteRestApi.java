package com.epion_t3.rest.command;

import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.rest.bean.Request;
import com.epion_t3.rest.reporter.ExecuteRestApiReporter;
import com.epion_t3.rest.runner.ExecuteRestApiRunner;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author takashno
 */
@Getter
@Setter
@CommandDefinition(id = "ExecuteRestApi",
        runner = ExecuteRestApiRunner.class,
        reporter = ExecuteRestApiReporter.class)
public class ExecuteRestApi extends Command {

    @NotNull
    @Valid
    private Request request;

    /**
     * 接続タイムアウト.
     * Valiableのバインドを利用したい場合があるんではなかろうか？と思い文字列としている.
     */
    private String connectTimeout = "3000";

    /**
     * 読み込みタイムアウト.
     * Valiableのバインドを利用したい場合があるんではなかろうか？と思い文字列としている.
     */
    private String readTimeout = "3000";

    /**
     * ボディのエンコーディング指定.
     */
    private String bodyEncoding;

}
