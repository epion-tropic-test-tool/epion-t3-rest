package com.epion_t3.rest.helper;

import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.qualify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.epion_t3.rest.bean.JsonDiff;

/**
 * JSON比較結果一覧(レポート用)を作成する.
 * このクラスのメソッドの大半は、JSONAssertライブラリの中身をもとに作成している.
 * 
 * @author 
 */
public class CreateJsonDiffListHelper {
	
	/**
	 * 比較エラー結果
	 */
	private Map<String, JsonDiff> errorMap;
	
	/**
	 * JSON比較結果一覧
	 */
	private List<JsonDiff> allDifflist;
	
	/**
	 * ignoreの正規表現リスト
	 */
	private List<Pattern> ignorePatternList;
	
	/**
	 * コンストラクタ.
	 * 
	 * @param assertErrorDiffMap 比較エラー結果
	 * @param ignoreStrList ignore条件一覧
	 */
	public CreateJsonDiffListHelper(Map<String, JsonDiff> assertErrorDiffMap, List<String> ignoreStrList) {
		errorMap = assertErrorDiffMap;
		ignorePatternList = new ArrayList<>();
		if (ignoreStrList != null) {
			for(String ignoreStr: ignoreStrList) {
				// Customizationで行っている変換と同じように文字列解析し正規表現にする.
				ignorePatternList.add(Pattern.compile(ignoreStr
						.replace("[", "\\[")
						.replace("]", "\\]")
						.replace(".", "\\.")
						.replace("**\\.", "(?:.+\\\\.)?")
						.replace("**", ".+")
						.replace("*", "[^\\\\.]+")));
			}
		}
		allDifflist = new ArrayList<>();
	}
	
	/**
	 * JSON比較結果一覧を返却する.
	 * 
	 * @return JSON比較結果一覧
	 */
	public List<JsonDiff> getAllDiffList() {
		return allDifflist;
	}

	/**
	 * JSON比較の基底メソッド.
	 * 
	 * @param prefix JSONパス
	 * @param expected 期待値オブジェクト
	 * @param actual 取得値オブジェクト
	 * @throws JSONException
	 */
    public void compareJSON(String prefix, JSONObject expected, JSONObject actual)
            throws JSONException {
        // Check that actual contains all the expected values
        checkJsonObjectKeysExpectedInActual(prefix, expected, actual);

        // actualにあってExpectedにないものを調べる
        checkJsonObjectKeysActualInExpected(prefix, expected, actual);
    }
    
    /**
     * actualにあってExpectedにないものを調べる
     * 
     * @param prefix　JSONパス
     * @param expected　期待値オブジェクト
     * @param actual　取得値オブジェクト
     * @throws JSONException
     */
    private void checkJsonObjectKeysActualInExpected(String prefix, JSONObject expected, JSONObject actual) throws JSONException {
        Set<String> actualKeys = getKeys(actual);
        for (String key : actualKeys) {
            if (!expected.has(key)) {
            	// actualにあってExpectedにない場合
            	JsonDiff jsonDiff = errorMap.get(qualify(prefix, key));
                if (jsonDiff != null) {
                	Object actualValue = actual.get(key);
                	jsonDiff.setActual(actualValue);
                	allDifflist.add(jsonDiff);
                }
            }
        }
    }

    /**
     * Expectedにあるものを確認する.
     * 
     * @param prefix　JSONパス
     * @param expected　期待値オブジェクト
     * @param actual　取得値オブジェクト
     * @throws JSONException
     */
    private void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual) throws JSONException {
        Set<String> expectedKeys = getKeys(expected);
        for (String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
            	// Expected,Actualともに存在する場合
                Object actualValue = actual.get(key);
                compareValues(qualify(prefix, key), expectedValue, actualValue);
            } else {
            	// ExpectedにあってActualにない場合
            	JsonDiff jsonDiff = errorMap.get(qualify(prefix, key));
                if (jsonDiff != null) {
                	jsonDiff.setExpected(expectedValue);
                	allDifflist.add(jsonDiff);
                }
            }
        }
    }

    /**
     * 値の比較を行う.
     * 
     * @param prefix　JSONパス
     * @param expected　期待値オブジェクト
     * @param actual　取得値オブジェクト
     * @throws JSONException
     */
    private void compareValues(String prefix, Object expectedValue, Object actualValue)
            throws JSONException {
    	// クラスが一致しているか
    	if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
            	// 配列の場合
                compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue);
            } else if (expectedValue instanceof JSONObject) {
            	// オブジェクトの場合
                compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue);
            } else {
            	// その他の場合
            	JsonDiff jsonDiff = errorMap.get(prefix);
            	// エラーにない場合は比較成功しているため、新たにJsonDiffオブジェクトを生成する
                if (jsonDiff == null) {
                	jsonDiff = new JsonDiff();
                	jsonDiff.setPathName(prefix);
                	jsonDiff.setExpected(expectedValue);
                	jsonDiff.setActual(actualValue);
                	jsonDiff.setSuccess(true);
                	jsonDiff.setIgnore(checkIgnore(prefix));
                }
                allDifflist.add(jsonDiff);
            }
        } else {
            JsonDiff jsonDiff = errorMap.get(prefix);
            if (jsonDiff != null) {
            	allDifflist.add(jsonDiff);
            }
        }
    }
    
    /**
     * ignore指定であるかの確認.
     * 
     * @param path Jsonパス
     * @return
     */
    private boolean checkIgnore(String path) {
    	for (Pattern ignorePattern: ignorePatternList) {
    		if (ignorePattern.matcher(path).matches()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 配列の場合の処理.
     * 
     * @param prefix　JSONパス
     * @param expected　期待値オブジェクト
     * @param actual　取得値オブジェクト
     * @throws JSONException
     */
    private void compareJSONArray(String prefix, JSONArray expected, JSONArray actual)
            throws JSONException {
        if (expected.length() != actual.length()) {
        	// 配列の数が一致していない場合
            JsonDiff jsonDiff = errorMap.get(prefix + "[]");
            if (jsonDiff != null) {
            	allDifflist.add(jsonDiff);
            }
            return;
        } else if (expected.length() == 0) {
            return; // Nothing to compare
        }

        compareJSONArrayWithStrictOrder(prefix, expected, actual);
    }

    /**
     * 配列の中を比較していく.
     * 
     * @param prefix　JSONパス
     * @param expected　期待値オブジェクト
     * @param actual　取得値オブジェクト
     * @throws JSONException
     */
    private void compareJSONArrayWithStrictOrder(String key, JSONArray expected, JSONArray actual) throws JSONException {
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedValue = expected.get(i);
            Object actualValue = actual.get(i);
            compareValues(key + "[" + i + "]", expectedValue, actualValue);
        }
    }
}
