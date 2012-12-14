package jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLGraphDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;

/**
 * RecML definitions
 */
public class SimpleRecMLDefinition {

	//========================================================
	//DEFINE
	//========================================================
	/* RecML tag names */
	public static final String SIMPLERECML_TAG_STR_RECML = "recml";
	public static final String SIMPLERECML_TAG_STR_MATH = "math";
	public static final String SIMPLERECML_TAG_STR_LOOPINDEX = "loopindex";
	public static final String SIMPLERECML_TAG_STR_LOOPSTRUCT = "loopstruct";
	public static final String SIMPLERECML_TAG_STR_POSITION = "position";
	
	public static final String SIMPLERECML_TAG_STR_VARIABLE = "variable";
	
	public static final String SIMPLERECML_TAG_STR_GRAPH=RecMLGraphDefinition.RECML_TAG_STR_GRAPH;
	
	public static final String SIMPLERECML_TAG_STR_NODE = "node";
	public static final String SIMPLERECML_TAG_STR_EDGE = "edge";
	public static final String SIMPLERECML_TAG_STR_VARREF = "varref";
	
	/*RecML変数型文字列定義*/
	public static final String SIMPLERECML_VARTYPE_STR_RECURVAR = "recurvar";
	public static final String SIMPLERECML_VARTYPE_STR_ARITHVAR = "arithvar";
	public static final String SIMPLERECML_VARTYPE_STR_CONSTVAR = "constvar";
	public static final String SIMPLERECML_VARTYPE_STR_OUTPUT = "output";
	public static final String SIMPLERECML_VARTYPE_STR_STEPVER = "stepvar";
	public static final String SIMPLERECML_VARTYPE_STR_DOUBLE = "double";


	public static final String SIMPLERECML_ATTR_STR_ID = "id";
	public static final String SIMPLERECML_ATTR_STR_EQUATION = "equation";
	public static final String SIMPLERECML_ATTR_STR_VARIABLE = "variable";
	public static final String SIMPLERECML_ATTR_STR_SOURCE = "source";
	public static final String SIMPLERECML_ATTR_STR_DEST = "dest";
	
	/*Simple RecML attribute names */
	public static final String SIMPLERECML_ATTR_NUM = "num";
	public static final String SIMPLERECML_ATTR_TYPE = "type";
	public static final String SIMPLERECML_ATTR_CONDREF = "condref";
	public static final String SIMPLERECML_ATTR_LOOPNUM = "loopnum";


	
	public enum eRecMLTag {
		CTAG_RECML		(SIMPLERECML_TAG_STR_RECML),
		CTAG_MATH		(SIMPLERECML_TAG_STR_MATH),
		CTAG_LOOPINDEX		(SIMPLERECML_TAG_STR_LOOPINDEX),
		CTAG_LOOPSTRUCT		(SIMPLERECML_TAG_STR_LOOPSTRUCT),
		CTAG_POSITION	(SIMPLERECML_TAG_STR_POSITION),
		CTAG_VARIABLE	(SIMPLERECML_TAG_STR_VARIABLE),
		CTAG_GRAPH	(SIMPLERECML_TAG_STR_GRAPH),
		
		CTAG_NODE		(SIMPLERECML_TAG_STR_NODE), 
		CTAG_EDGE		(SIMPLERECML_TAG_STR_EDGE),
		CTAG_VARREF		(SIMPLERECML_TAG_STR_VARREF), 
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
		CVAR_TYPE_RECURVAR	(SIMPLERECML_VARTYPE_STR_RECURVAR),
		CVAR_TYPE_ARITHVAR	(SIMPLERECML_VARTYPE_STR_ARITHVAR),
		CVAR_TYPE_CONSTVAR	(SIMPLERECML_VARTYPE_STR_CONSTVAR),
		CVAR_TYPE_OUTPUT	(SIMPLERECML_VARTYPE_STR_OUTPUT),
		CVAR_TYPE_STEPVER	(SIMPLERECML_VARTYPE_STR_STEPVER),
		CVAR_TYPE_DOUBLE	(SIMPLERECML_VARTYPE_STR_DOUBLE),
		
		CVAR_TYPE_ID	(SIMPLERECML_ATTR_STR_ID),
		CVAR_TYPE_EQUATION	(SIMPLERECML_ATTR_STR_EQUATION),
		CVAR_TYPE_VARIABLE	(SIMPLERECML_ATTR_STR_VARIABLE),
		CVAR_TYPE_SOURCE	(SIMPLERECML_ATTR_STR_SOURCE),
		CVAR_TYPE_DEST	(SIMPLERECML_ATTR_STR_DEST),
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