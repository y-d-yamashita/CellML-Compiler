package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;

import java.text.DecimalFormat;

/**
 * 文字列関連ユーティリティクラス.
 */
public class StringUtil {

	/**
	 * 改行文字列.
	 * 実行環境に依存する.
	 */
	public static final String lineSep = System.getProperty("line.separator");

	// 小数点以下の桁数をVC++版と同じにする
	private static DecimalFormat df = new DecimalFormat("0.000000");

	/**
	 * double型データを文字列に変換する.
	 * @param dValue 変換元double
	 * @return 変換後文字列
	 */
	public static String doubleToString(double dValue) {
//		return String.valueOf(dValue);
		return df.format(dValue);
	}

}
