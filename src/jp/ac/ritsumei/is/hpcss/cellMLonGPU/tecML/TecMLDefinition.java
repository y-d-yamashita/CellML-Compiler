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
	/**TecMLタグ文字列定義 inputvar*/
	public static final String TECML_TAG_STR_INPUTVAR = "inputvar";
	/**TecMLタグ文字列定義 outputvar*/
	public static final String TECML_TAG_STR_OUTPUTVAR = "outputvar";
	/**TecMLタグ文字列定義 variable*/
	public static final String TECML_TAG_STR_VARIABLE = "variable";
	/**TecMLタグ文字列定義 function*/
	public static final String TECML_TAG_STR_FUNCTION = "function";
	/**TecMLタグ文字列定義 argument*/
	public static final String TECML_TAG_STR_ARGUMENT = "argument";
	/**TecMLタグ文字列定義 math*/
	public static final String TECML_TAG_STR_MATH = "math";

	/**TecML変数型文字列定義 deltatimevar*/
	public static final String TECML_VARTYPE_STR_DELTATIMEVAR = "deltatimevar";
	/**TecML変数型文字列定義 timevar*/
	public static final String TECML_VARTYPE_STR_TIMEVAR = "timevar";
	/**TecML変数型文字列定義 dimensionvar (includes timevar and spatial var*/
	public static final String TECML_VARTYPE_STR_DIMENSIONVAR = "dimensionvar";
	/**TecML変数型文字列定義 deltavar (includes delta t and x)*/
	public static final String TECML_VARTYPE_STR_DELTAVAR = "deltavar";
	/**TecML変数型文字列定義 indexvar (includes indices for discretization)*/
	public static final String TECML_VARTYPE_STR_INDEXVAR = "indexvar";
	/**TecML変数型文字列定義 diffvar*/
	public static final String TECML_VARTYPE_STR_DIFFVAR = "diffvar";
	/**TecML変数型文字列定義 derivativevar*/
	public static final String TECML_VARTYPE_STR_DERIVATIVEVAR = "derivativevar";
	/**TecML変数型文字列定義 arithvar*/
	public static final String TECML_VARTYPE_STR_ARITHVAR = "arithvar";
	/**TecML変数型文字列定義 constvar*/
	public static final String TECML_VARTYPE_STR_CONSTVAR = "constvar";

	/**TecML関数型文字列定義 nondiffequ*/
	public static final String TECML_FUNCTYPE_STR_NONDIFF = "nondiffequ";
	/**TecML関数型文字列定義 diffequ*/
	public static final String TECML_FUNCTYPE_STR_DIFF = "diffequ";

	//========================================================
	//ENUM
	//========================================================

	/**
	 * TecML中で使用されるタグ種類.
	 */
	public enum eTecMLTag {
		TTAG_TECML		(TECML_TAG_STR_TECML),
		TTAG_INPUTVAR	(TECML_TAG_STR_INPUTVAR),
		TTAG_OUTPUTVAR	(TECML_TAG_STR_OUTPUTVAR),
		TTAG_VARIABLE	(TECML_TAG_STR_VARIABLE),
		TTAG_FUNCTION	(TECML_TAG_STR_FUNCTION),
		TTAG_ARGUMENT	(TECML_TAG_STR_ARGUMENT),
		TTAG_MATH		(TECML_TAG_STR_MATH),
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
		TVAR_TYPE_DELTATIMEVAR	(TECML_VARTYPE_STR_DELTATIMEVAR),
		TVAR_TYPE_TIMEVAR		(TECML_VARTYPE_STR_TIMEVAR),
		TVAR_TYPE_DIMENSIONVAR	(TECML_VARTYPE_STR_DIMENSIONVAR),
		TVAR_TYPE_DELTAVAR		(TECML_VARTYPE_STR_DELTAVAR),
		TVAR_TYPE_INDEXVAR		(TECML_VARTYPE_STR_INDEXVAR),
		TVAR_TYPE_DIFFVAR		(TECML_VARTYPE_STR_DIFFVAR),
		TVAR_TYPE_DERIVATIVEVAR	(TECML_VARTYPE_STR_DERIVATIVEVAR),
		TVAR_TYPE_ARITHVAR		(TECML_VARTYPE_STR_ARITHVAR),
		TVAR_TYPE_CONSTVAR		(TECML_VARTYPE_STR_CONSTVAR),
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
	 * TecML中関数で使用される型.
	 */
	public enum eTecMLFuncType {
		TFUNC_TYPE_NONDIFF	(TECML_FUNCTYPE_STR_NONDIFF),
		TFUNC_TYPE_DIFF		(TECML_FUNCTYPE_STR_DIFF),
			;
		private final String operatorStr;
		private eTecMLFuncType(String operatorstr) {
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

	/**
	 * TecML関数型idを取得する.
	 * @param strTag タグ文字列
	 * @return TecML関数型id
	 * @throws TecMLException
	 */
	public static eTecMLFuncType getTecMLFuncType(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (eTecMLFuncType t: eTecMLFuncType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getTecMLVarType",
					 "invalid TecML variable type : " + strTag);
	}

}
