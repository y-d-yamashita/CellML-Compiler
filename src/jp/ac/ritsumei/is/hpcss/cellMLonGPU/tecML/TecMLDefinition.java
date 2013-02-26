package jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;

/**
 * TecML定義系.
 */
public class TecMLDefinition {

	//========================================================
	//DEFINE
	//========================================================
	/**TecMLタグ文字列定義 tecml*/
	public static final String TECML_TAG_STR_TECML = "tecml";
	
	/**TecMLタグ文字列定義 variable*/
	public static final String TECML_TAG_STR_VARIABLE = "variable";
	/**TecMLタグ文字列定義 function*/
	public static final String TECML_TAG_STR_FUNCTION = "function";
	/**TecMLタグ文字列定義 argument*/
	public static final String TECML_TAG_STR_ARGUMENT = "argument";
	/**TecMLタグ文字列定義 math*/
	public static final String TECML_TAG_STR_MATH = "math";
	/**TecMLタグ文字列定義 dimension correspond*/
	public static final String TECML_TAG_STR_DIMENSION = "dimension";

	/**TecML変数型文字列定義 recurvar*/
	public static final String TECML_VARTYPE_STR_RECURVAR = "recurvar";
	/**TecML変数型文字列定義 arithvar*/
	public static final String TECML_VARTYPE_STR_ARITHVAR = "arithvar";
	/**TecML変数型文字列定義 constvar*/
	public static final String TECML_VARTYPE_STR_CONSTVAR = "constvar";
	/**TecML変数型文字列定義 condition*/
	public static final String TECML_VARTYPE_STR_CONDITION = "condition";
	/**TecML変数型文字列定義 outputvar*/
	public static final String TECML_VARTYPE_STR_OUTPUT = "output";
	/**TecML変数型文字列定義 stepvar*/
	public static final String TECML_VARTYPE_STR_STEPVAR = "stepvar";


	//========================================================
	//ENUM
	//========================================================

	/**
	 * TecML中で使用されるタグ種類.
	 */
	public enum eTecMLTag {
		TTAG_TECML		(TECML_TAG_STR_TECML),
		TTAG_VARIABLE	(TECML_TAG_STR_VARIABLE),
		TTAG_FUNCTION	(TECML_TAG_STR_FUNCTION),
		TTAG_ARGUMENT	(TECML_TAG_STR_ARGUMENT),
		TTAG_MATH		(TECML_TAG_STR_MATH),
		TTAG_DIMENSION	(TECML_TAG_STR_DIMENSION),
			;
		private final String operatorStr;
		private eTecMLTag(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	/**
	 * TecML中変数で使用される型.
	 */
	public enum eTecMLVarType {
		TVAR_TYPE_RECURVAR		(TECML_VARTYPE_STR_RECURVAR),
		TVAR_TYPE_ARITHVAR		(TECML_VARTYPE_STR_ARITHVAR),
		TVAR_TYPE_CONSTVAR		(TECML_VARTYPE_STR_CONSTVAR),
		TVAR_TYPE_CONDITION		(TECML_VARTYPE_STR_CONDITION),
		TVAR_TYPE_OUTPUT		(TECML_VARTYPE_STR_OUTPUT),
		TVAR_TYPE_STEPVAR		(TECML_VARTYPE_STR_STEPVAR),
			;
		private final String operatorStr;
		private eTecMLVarType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	/**
	 * TecMLタグidを取得する.
	 * @param strTag タグ文字列
	 * @return TecMLタグid
	 * @throws TecMLException
	 */
	public static eTecMLTag getTecMLTagId(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (eTecMLTag t: eTecMLTag.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getTecMLTagId",
					 "invalid TecML tag : " + strTag);
	}

	/**
	 * TecML変数型idを取得する.
	 * @param strTag タグ文字列
	 * @return TecML変数型id
	 * @throws TecMLException
	 */
	public static eTecMLVarType getTecMLVarType(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (eTecMLVarType t: eTecMLVarType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getTecMLVarType",
					 "invalid TecML variable type : " + strTag);
	}

}
