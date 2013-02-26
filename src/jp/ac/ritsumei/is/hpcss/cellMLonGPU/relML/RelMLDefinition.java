package jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;

/**
 * RelML定義系.
 */
public class RelMLDefinition {

	//========================================================
	//DEFINE
	//========================================================
	/**RelMLタグ文字列定義 relml*/
	public static final String RELML_TAG_STR_RELML = "relml";
	/**RelMLタグ文字列定義 tecml*/
	public static final String RELML_TAG_STR_TECML = "tecml";
	/**RelMLタグ文字列定義 cellml*/
	public static final String RELML_TAG_STR_CELLML = "cellml";
	/**RelMLタグ文字列定義 variable*/
	public static final String RELML_TAG_STR_VARIABLE = "variable";
	/**RelMLタグ文字列定義 component*/
	public static final String RELML_TAG_STR_COMPONENT = "component";
	/**RelMLタグ文字列定義 math*/
	public static final String RELML_TAG_STR_MATH = "math";
	/**RelMLタグ文字列定義 tecmlvariable*/
	public static final String RELML_TAG_STR_TECMLVARIABLE = "tecmlvariable";
	/**RelMLタグ文字列定義 correspondcellml*/
	public static final String RELML_TAG_STR_CORRESPONDCELLML = "correspondcellml";
	/**RelMLタグ文字列定義 correspondtecml*/
	public static final String RELML_TAG_STR_CORRESPONDTECML = "correspondtecml";
	/**RelMLタグ文字列定義 correspondrelml*/
	public static final String RELML_TAG_STR_CORRESPONDRELML = "correspondrelml";

	/**RelML変数型文字列定義 recurvar*/
	public static final String RELML_VARTYPE_STR_RECURVAR = "recurvar";
	/**RelML変数型文字列定義 arithvar*/
	public static final String RELML_VARTYPE_STR_ARITHVAR = "arithvar";
	/**RelML変数型文字列定義 constvar*/
	public static final String RELML_VARTYPE_STR_CONSTVAR = "constvar";
	/**RelML変数型文字列定義　stepvar*/
	public static final String RELML_VARTYPE_STR_STEPVAR = "stepvar";
	/**RelML variable for partial differential equations */
	public static final String RELML_VARTYPE_STR_PARTIALDIFFVAR = "partialdiffvar";
	
	/**RelML attribute names conditionname*/
	public static final String RELML_ATTR_CONDNAME = "condname";
	/**RelML attribute names loopindex*/
	public static final String RELML_ATTR_LOOPINDEX = "loopindex";

	//========================================================
	//ENUM
	//========================================================

	/**
	 * RelML中で使用されるタグ種類.
	 */
	public enum eRelMLTag {
		RTAG_RELML		(RELML_TAG_STR_RELML),
		RTAG_TECML		(RELML_TAG_STR_TECML),
		RTAG_CELLML		(RELML_TAG_STR_CELLML),
		RTAG_VARIABLE	(RELML_TAG_STR_VARIABLE),
		RTAG_COMPONENT	(RELML_TAG_STR_COMPONENT),
		RTAG_MATH		(RELML_TAG_STR_MATH),
		RTAG_TVAR		(RELML_TAG_STR_TECMLVARIABLE),
		RTAG_CORRCELLML (RELML_TAG_STR_CORRESPONDCELLML),
		RTAG_CORRTECML  (RELML_TAG_STR_CORRESPONDTECML),
		RTAG_CORRRELML  (RELML_TAG_STR_CORRESPONDRELML),
			;
		private final String operatorStr;
		private eRelMLTag(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	/**
	 * RelML中変数で使用される型.
	 */
	public enum eRelMLVarType {
		RVAR_TYPE_RECURVAR	(RELML_VARTYPE_STR_RECURVAR),
		RVAR_TYPE_ARITHVAR	(RELML_VARTYPE_STR_ARITHVAR),
		RVAR_TYPE_CONSTVAR	(RELML_VARTYPE_STR_CONSTVAR),
		RVAR_TYPE_STEPVAR	(RELML_VARTYPE_STR_STEPVAR),
		RVAR_TYPE_PARTIALDIFFVAR	(RELML_VARTYPE_STR_PARTIALDIFFVAR),
			;
		private final String operatorStr;
		private eRelMLVarType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};


	/**
	 * RelMLタグidを取得する.
	 * @param strTag タグ文字列
	 * @return RelMLタグid
	 * @throws RelMLException
	 */
	public static eRelMLTag getRelMLTagId(String strTag)
	throws RelMLException {
		/*演算子と比較*/
		for (eRelMLTag t: eRelMLTag.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new RelMLException("","getRelMLTagId",
				"invalid RelML tag : " + strTag);
	}

	/**
	 * RelML変数型idを取得する.
	 * @param strType タイプ文字列
	 * @return RelML変数型タイプid
	 * @throws RelMLException
	 */
	public static eRelMLVarType getRelMLVarType(String strType)
	throws RelMLException {
		/*演算子と比較*/
		for (eRelMLVarType t: eRelMLVarType.values()) {
			if (t.getOperatorStr().equals(strType)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new RelMLException("","getRelMLVarType",
				"invalid RelML variable type : " + strType);
	}

}
