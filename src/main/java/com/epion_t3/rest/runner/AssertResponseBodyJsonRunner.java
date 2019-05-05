package com.epion_t3.rest.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.Logger;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.runner.impl.AbstractCommandRunner;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.common.type.AssertStatus;
import com.epion_t3.rest.bean.AssertResultResponseBodyJson;
import com.epion_t3.rest.bean.JsonDiff;
import com.epion_t3.rest.bean.Response;
import com.epion_t3.rest.command.AssertResponseBodyJson;
import com.epion_t3.rest.helper.CreateJsonDiffListHelper;
import com.epion_t3.rest.message.RestMessages;

/**
 * Httpレスポンスボディのアサート処理.
 * 
 * @author 
 */
public class AssertResponseBodyJsonRunner extends AbstractCommandRunner<AssertResponseBodyJson> {

    /**
     * {@inheritDoc}
     */
	@Override
	public CommandResult execute(AssertResponseBodyJson command, Logger logger) throws Exception {
	
		// リザルトオブジェクトの生成
		AssertResultResponseBodyJson commandResult = new AssertResultResponseBodyJson();
	
		// targetとvalueは必須項目
        if (StringUtils.isEmpty(command.getTarget())) {
            throw new SystemException("えらー");
        }

        if (StringUtils.isEmpty(command.getValue())) {
            throw new SystemException("えらー");
        }

        // Httpレスポンスの取得
        Response response = referObjectEvidence(command.getTarget());

        String actual = response.getBody();
        
        if (actual != null) {
        	commandResult.setActual(actual);
        }
        
        String expectedFileName = command.getValue();
        
        Path expectedPath = Paths.get(getCommandBelongScenarioDirectory(), expectedFileName);

        // expectedの配置パスが存在しなかった場合はエラー
        if (Files.notExists(expectedPath)) {
            throw new SystemException("えらー");
        }

        // Expectedの読み込み
        File expectedFile = expectedPath.toFile();
        StringBuilder builder = new StringBuilder();
        try(BufferedReader expectedReader = new BufferedReader(new FileReader(expectedFile))) {
        	String string = expectedReader.readLine();
		
        	while (string != null) {
        		builder.append(string + System.getProperty("line.separator"));
        		string = expectedReader.readLine();
        	}
        }
        
        String expected = builder.toString();
        
        if (expected != null) {
        	commandResult.setExpected(expected);
        }
        
  
		try {
			// JSONObjectに変換(順序整形)
			JSONObject expectedJsonObject = new JSONObject(expected);
			JSONObject actualJsonObject = new JSONObject(actual);

			// 結果に格納する
			commandResult.setActual(actualJsonObject);
			commandResult.setExpected(expectedJsonObject);

			// Json比較対象外リストの作成
			List<Customization> ignoreList = new ArrayList<>();
			
			if (command.getIgnores() != null) {
				for (String ignore: command.getIgnores()) {
					// ignore指定したものは判定を強制的にtrueにする
					ignoreList.add(new Customization(ignore, (o1, o2) -> true));
				}
			}     

			try {
				// ignoreがあるかないかで分岐
				if (!ignoreList.isEmpty()) {
					Customization[] ignores = ignoreList.toArray(new Customization[ignoreList.size()]);
					CustomComparator customComparator = new CustomComparator(JSONCompareMode.STRICT, ignores);
			
					JSONAssert.assertEquals(
							expectedJsonObject.toString(), actualJsonObject.toString(), customComparator);
				} else {
					JSONAssert.assertEquals(
							expectedJsonObject.toString(), actualJsonObject.toString(), JSONCompareMode.STRICT);
				}

				// 正常終了時
				commandResult.setAssertStatus(AssertStatus.OK);
				commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_INFO_1002));
				commandResult.setJsonDifflist(createJsonDiffList("", expectedJsonObject, actualJsonObject, command.getIgnores()));
			} catch (AssertionError e) {
				// アサートエラー発生時
				commandResult.setAssertStatus(AssertStatus.NG);
				commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_ERR_9017));
				commandResult.setJsonDifflist(createJsonDiffList(e.getMessage(), expectedJsonObject, actualJsonObject, command.getIgnores()));
			}
		} catch (JSONException e) {
			// 実行中のエラー
			commandResult.setAssertStatus(AssertStatus.NG);
			commandResult.setMessage(MessageManager.getInstance().getMessage(RestMessages.REST_ERR_9017));
		}
		
		return commandResult;
	}

	/**
	 * Jsonの比較結果を作成する.
	 * 
	 * @param assertErrorMessage アサートエラーメッセージ
	 * @param expected 期待値Jsonオブジェクト
	 * @param actual 取得値Jsonオブジェクト
	 * @param ignoreStrList ignoreの正規表現文字列一覧
	 * @return Jsonの比較結果
	 * @throws JSONException
	 */
	private static List<JsonDiff> createJsonDiffList(String assertErrorMessage, JSONObject expected, JSONObject actual, List<String> ignoreStrList) throws JSONException {
		// エラーのマップ
		Map<String, JsonDiff> jsonDiffErrorMap = new HashMap<>();
		
		// AssertionErrorの出力からアサート失敗結果を取得する
		if(!assertErrorMessage.isEmpty()) {
		String[] diffMessageList = assertErrorMessage.split("; ");
		
			for (String diffMessage :diffMessageList) {
				// AssertionErrorのフォーマットをもとに文字列解析を行う
				List<String> diffInfoList = Arrays.asList(diffMessage.split("\r\n|\r|\n|: "));
				JsonDiff jsonDiff = new JsonDiff();
				if (diffInfoList.contains("Unexpected")) {
					// Expectedになくて、Actualにある場合のエラー
					if (diffInfoList.get(0).isEmpty()) {
						// Jsonのルート階層の場合
						jsonDiff.setPathName(diffInfoList.get(2));
					} else {
						jsonDiff.setPathName(diffInfoList.get(0) + "." + diffInfoList.get(2));
					}
					jsonDiff.setExpected("This path doesn't exist at the expected");
					jsonDiff.setActual("");
				} else if(diffInfoList.contains("     but none found")) {
					// Expectedにあって、Actualにない場合のエラー
					if (diffInfoList.get(0).isEmpty()) {
						// Jsonのルート階層の場合
						jsonDiff.setPathName(diffInfoList.get(2));
					} else {
						jsonDiff.setPathName(diffInfoList.get(0) + "." + diffInfoList.get(2));
					}
					jsonDiff.setExpected("");
					jsonDiff.setActual("This path doesn't exist at the actual");
				} else if(diffInfoList.contains("Expected") && diffInfoList.contains("     got")){
					// 値比較エラー 
					jsonDiff.setPathName(diffInfoList.get(0));
					jsonDiff.setExpected(diffInfoList.get(2));
					jsonDiff.setActual(diffInfoList.get(4));
				} else {
					// 配列要素数不一致エラー
					String[] listSizeErrorStr = diffInfoList.get(1).split(" ");
					jsonDiff.setPathName(diffInfoList.get(0));
					jsonDiff.setExpected("List size is " + listSizeErrorStr[1]);
					jsonDiff.setActual("List size is " + listSizeErrorStr[5]);
				}
				
				// エラーマップに格納する
				jsonDiffErrorMap.put(jsonDiff.getPathName(), jsonDiff);
			}
		}
		
		// JSON比較結果作成インスタンスの生成
		CreateJsonDiffListHelper cjd = new CreateJsonDiffListHelper(jsonDiffErrorMap, ignoreStrList);
		// JSON比較結果を作成する
		cjd.compareJSON("", expected, actual);
		
		return cjd.getAllDiffList();
	}

}
