package com.epion_t3.rest.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * JsonAssert表示用のオブジェクト.
 * 
 * @author
 */
@Getter
@Setter
public class JsonDiff {

	/**
	 * Jsonパス(ドット表記).
	 */
	private String pathName;
	
	/**
	 * 期待値
	 */
	private Object expected;
	
	/**
	 * 対象値
	 */
	private Object actual;
	
	/**
	 * アサート結果
	 */
	private boolean success;
	
    /**
     * ignore状態
     */
	private boolean ignore;
}
