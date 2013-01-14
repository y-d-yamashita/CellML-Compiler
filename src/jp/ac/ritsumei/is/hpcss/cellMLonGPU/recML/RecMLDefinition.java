package jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;

/**
 * RecML definitions
 */
public class RecMLDefinition {

	//========================================================
	//DEFINE
	//========================================================
	/* RecML tag names */
	public static final String RECML_TAG_STR_RECML = "recml";
	public static final String RECML_TAG_STR_MATH = "math";
	public static final String RECML_TAG_STR_LOOPINDEX = "loopindex";
	public static final String RECML_TAG_STR_LOOPSTRUCT = "loopstruct";
	public static final String RECML_TAG_STR_POSITION = "position";
	
	public static final String RECML_TAG_STR_VARIABLE = "variable";
	public static final String RECML_TAG_STR_GRAPH=RecMLGraphDefinition.RECML_TAG_STR_GRAPH;
	
	public static final String RECML_TAG_STR_NODE = "node";
	public static final String RECML_TAG_STR_EDGE = "edge";
	public static final String RECML_TAG_STR_REFVARIABLE = "refvariable";
	public static final String RECML_TAG_STR_SIMULLTANEOUS = "simulltaneous";
	
	/*RecML変数型文字列定義*/
	public static final String RECML_VARTYPE_STR_RECURVAR = "recurvar";
	public static final String RECML_VARTYPE_STR_ARITHVAR = "arithvar";
	public static final String RECML_VARTYPE_STR_CONSTVAR = "constvar";
	public static final String RECML_VARTYPE_STR_OUTPUT = "output";
	public static final String RECML_VARTYPE_STR_DOUBLE = "double";
	public static final String RECML_VARTYPE_STR_STEPVAR = "stepvar";

	/* RecML attribute names */
	public static final String RECML_ATTR_LOOP1 = "loop1";
	public static final String RECML_ATTR_LOOP2 = "loop2";
	public static final String RECML_ATTR_LOOP3 = "loop3";
	public static final String RECML_ATTR_LOOP4 = "loop4";
	public static final String RECML_ATTR_LOOP5 = "loop5";
	
	public static final String RECML_ATTR_PRE = "pre";
	public static final String RECML_ATTR_INIT = "init";
	public static final String RECML_ATTR_INNER = "inner";
	public static final String RECML_ATTR_LOOPCOND = "loopcond";
	public static final String RECML_ATTR_FINAL = "final";
	public static final String RECML_ATTR_POST = "post";
	
	public static final String RECML_ATTR_EQUATION = "equation";
	public static final String RECML_ATTR_VARIABLE = "variable";
	public static final String RECML_ATTR_DEST = "dest";
	public static final String RECML_ATTR_SOURCE = "source";
	public static final String RECML_ATTR_VARIABLENAME = "variableName";
	
	public enum eRecMLTag {
		CTAG_RECML		(RECML_TAG_STR_RECML),
		CTAG_MATH		(RECML_TAG_STR_MATH),
		CTAG_LOOPINDEX		(RECML_TAG_STR_LOOPINDEX),
		CTAG_LOOPSTRUCT		(RECML_TAG_STR_LOOPSTRUCT),
		CTAG_POSITION	(RECML_TAG_STR_POSITION),
		CTAG_VARIABLE	(RECML_TAG_STR_VARIABLE),
		CTAG_GRAPH	(RECML_TAG_STR_GRAPH),
		CTAG_NODE	(RECML_TAG_STR_NODE),
		CTAG_EDGE	(RECML_TAG_STR_EDGE),
		CTAG_REFVARIABLE	(RECML_TAG_STR_REFVARIABLE),
		CTAG_SIMULLTANEOUS	(RECML_TAG_STR_SIMULLTANEOUS),
			;
		private final String operatorStr;
		private eRecMLTag(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};
	
	public enum eRecMLVarType {
		CVAR_TYPE_RECURVAR	(RECML_VARTYPE_STR_RECURVAR),
		CVAR_TYPE_ARITHVAR	(RECML_VARTYPE_STR_ARITHVAR),
		CVAR_TYPE_CONSTVAR	(RECML_VARTYPE_STR_CONSTVAR),
		CVAR_TYPE_OUTPUT	(RECML_VARTYPE_STR_OUTPUT),
		CVAR_TYPE_STEPVAR	(RECML_VARTYPE_STR_STEPVAR),
		CVAR_TYPE_DOUBLE	(RECML_VARTYPE_STR_DOUBLE),
			;
		private final String operatorStr;
		private eRecMLVarType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};
	
	public static eRecMLTag getRecMLTagId(String strTag)
	throws RecMLException {
		/*演算子と比較*/
		for (eRecMLTag t: eRecMLTag.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new RecMLException("","getRecMLTagId",
					  "invalid RecML tag : " + strTag);
	}
	
	//========================================================
	//getTecMLVarType
	// TecML変数型id取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// TecMLタグid	: eTecMLVarType
	//
	//@throws
	// TecMLException
	//
	//========================================================
	public static eRecMLVarType getRecMLVarType(String strTag)
	throws RecMLException {
		/*演算子と比較*/
		for (eRecMLVarType t: eRecMLVarType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなrければ例外*/
		throw new RecMLException("","getRecMLVarType",
					 "invalid RecML variable type : " + strTag);
	}
}