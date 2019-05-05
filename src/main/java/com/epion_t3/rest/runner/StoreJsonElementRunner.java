package com.epion_t3.rest.runner;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.runner.impl.AbstractCommandRunner;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.rest.bean.Response;
import com.epion_t3.rest.command.StoreJsonElement;
import com.epion_t3.rest.message.RestMessages;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 *
 */
public class StoreJsonElementRunner extends AbstractCommandRunner<StoreJsonElement> {

    /**
     * @param command コマンド
     * @param logger  ロガー
     * @return コマンド結果
     * @throws Exception エラー
     */
    @Override
    public CommandResult execute(StoreJsonElement command, Logger logger) throws Exception {

        // 対象は必須
        if (StringUtils.isEmpty(command.getTarget())) {
            throw new SystemException(RestMessages.REST_ERR_9004);
        }

        // 値は必須
        if (StringUtils.isEmpty(command.getValue())) {
            throw new SystemException(RestMessages.REST_ERR_9005);
        }

        // JSONPathは必須
        if (StringUtils.isEmpty(command.getJsonPath())) {
            throw new SystemException(RestMessages.REST_ERR_9010);
        }

        // オブジェクトエビデンスを参照
        Object objectEvidence = referObjectEvidence(command.getTarget());

        // オブジェクトエビデンスが見つからない場合はエラー
        if (objectEvidence == null) {
            throw new SystemException(RestMessages.REST_ERR_9006, command.getTarget());
        }

        // オブジェクトエビデンスがツール機能が有するレスポンスオブジェクトでない場合はエラー
        if (!Response.class.isAssignableFrom(objectEvidence.getClass())) {
            throw new SystemException(RestMessages.REST_ERR_9007, command.getTarget());
        }

        Response response = Response.class.cast(objectEvidence);

        // ボディが存在しない場合にはエラー
        if (StringUtils.isEmpty(response.getBody())) {
            throw new SystemException(RestMessages.REST_ERR_9008, command.getTarget());
        }

        try {
            // ドキュメント解析
            DocumentContext documentContext = JsonPath.parse(response.getBody());
            Object value = documentContext.read(command.getJsonPath());

            if (JSONArray.class.isAssignableFrom(value.getClass())) {
                throw new SystemException(RestMessages.REST_ERR_9011, command.getValue());
            }

            // 変数を設定
            setVariable(command.getValue(), value);

        } catch (PathNotFoundException e) {
            throw new SystemException(RestMessages.REST_ERR_9009, command.getValue());
        }

        return CommandResult.getSuccess();
    }
}
