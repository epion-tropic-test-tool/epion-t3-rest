package com.epion_t3.rest.runner;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.runner.impl.AbstractCommandRunner;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.rest.command.ExecuteRestApi;
import com.epion_t3.rest.message.RestMessages;
import com.epion_t3.rest.type.HttpMethodType;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * REST-API実行処理.
 *
 * @author takashno
 */
public class ExecuteRestApiRunner extends AbstractCommandRunner<ExecuteRestApi> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(
            final ExecuteRestApi command,
            Logger logger) throws Exception {

        // タイムアウト系の値チェック
        if (StringUtils.isEmpty(command.getConnectTimeout())) {
            throw new SystemException(RestMessages.REST_ERR_9012);
        }
        if (!StringUtils.isNumeric(command.getConnectTimeout())) {
            throw new SystemException(RestMessages.REST_ERR_9013, command.getConnectTimeout());
        }
        if (StringUtils.isEmpty(command.getReadTimeout())) {
            throw new SystemException(RestMessages.REST_ERR_9013);
        }
        if (!StringUtils.isNumeric(command.getReadTimeout())) {
            throw new SystemException(RestMessages.REST_ERR_9014, command.getReadTimeout());
        }

        // メソッド解決
        HttpMethodType httpMethodType =
                HttpMethodType.valueOfByValue(command.getRequest().getMethod().toLowerCase());

        // クライアント作成
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Long.valueOf(command.getConnectTimeout()), TimeUnit.MILLISECONDS)
                .readTimeout(Long.valueOf(command.getReadTimeout()), TimeUnit.MILLISECONDS)
                .build();

        // ヘッダ作成 & MIMEの解決
        Headers headers = null;
        MediaType mimeType = null;
        if (command.getRequest().getHeaders() != null
                && !command.getRequest().getHeaders().isEmpty()) {
            headers = Headers.of(command.getRequest().getHeaders());
            for (Map.Entry<String, String> entry : command.getRequest().getHeaders().entrySet()) {
                if (entry.getKey().toLowerCase().equals("content-type")) {
                    mimeType = MediaType.parse(entry.getValue());
                }
            }
        } else {
            headers = Headers.of(new String[0]);
        }

        // URL作成
        HttpUrl.Builder urlBuilder = HttpUrl.parse(
                command.getRequest().getTargetUrl()).newBuilder();
        if (command.getRequest().getQueryParameters() != null
                && !command.getRequest().getQueryParameters().isEmpty()) {
            command.getRequest().getQueryParameters().stream().forEach(
                    x -> urlBuilder.addQueryParameter(x.getName(), x.getValue()));
        }
        HttpUrl url = urlBuilder.build();

        // リクエスト作成
        Request.Builder requestBuilder = new Request.Builder().headers(headers).url(url);
        Request request = null;

        switch (httpMethodType) {
            case GET:
                request = requestBuilder.get().build();
                break;
            case DELETE:
                request = requestBuilder.delete().build();
                break;
            case PUT:
                request = requestBuilder
                        .put(RequestBody.create(mimeType, getEncodedBody(command))).build();
                break;
            case POST:
                request = requestBuilder
                        .post(RequestBody.create(mimeType, getEncodedBody(command))).build();
                break;
            case PATCH:
                request = requestBuilder
                        .patch(RequestBody.create(mimeType, getEncodedBody(command))).build();
                break;
            default:

        }

        // HTTP通信を実行
        Response response = client.newCall(request).execute();

        // 結果オブジェクトを作成
        com.epion_t3.rest.bean.Response result =
                new com.epion_t3.rest.bean.Response();
        // ステータスコード
        result.setStatusCode(response.code());
        // レスポンスヘッダ
        result.setHeaders(response.headers().toMultimap());
        // レスポンスボディ
        if (response.body() != null) {
            result.setBody(response.body().string());
        }

        // 受信時間
        result.setReceivedResponseAtMillis(response.receivedResponseAtMillis());
        result.setSentRequestAtMillis(response.sentRequestAtMillis());

        // ログ出力
        logger.info("StatusCode:{}", response.code());
        logger.info("Headers:");
        for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
            logger.info(entry.getKey().toString() + ":" + entry.getValue().toString());
        }
        logger.info("Body:{}", result.getBody());


        // エビデンス登録
        registrationObjectEvidence(result);


        return CommandResult.getSuccess();
    }

    /**
     * リクエストボディをエンコーディングして返却.
     * 不要な場合は、そのまま返却.
     *
     * @param command
     * @return
     */
    private String getEncodedBody(ExecuteRestApi command) {
        String body = null;
        if (StringUtils.isNotEmpty(command.getBodyEncoding())) {
            if (Charset.isSupported(command.getBodyEncoding())) {
                Charset targetCharset = Charset.forName(command.getBodyEncoding());
                body = new String(command.getRequest().getBody().getBytes(targetCharset),
                        targetCharset);
            } else {
                throw new SystemException(RestMessages.REST_ERR_9018, command.getBodyEncoding());
            }
        } else {
            body = command.getRequest().getBody();
        }
        return body;
    }
}
