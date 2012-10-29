package jp.ac.ritsumei.is.hpcss.cellMLcompiler.pdesML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TecMLException;

/**
 * TecML定義系ヘッダ
 */
public class PdesMLDefinition {

	//========================================================
	//DEFINE
	//========================================================
	/*PdesMLタグ文字列定義*/
	public static final String PDESML_TAG_STR_PDESML = "pdesml";
	public static final String PDESML_TAG_STR_MESH = "mesh";
	public static final String PDESML_TAG_STR_RECTILINEAR = "RECTILINEAR_GRID";
	public static final String PDESML_TAG_STR_VARIABLE = "variable";
	public static final String PDESML_TAG_STR_MATH = "math";
	
	/*PdesML変数型文字列定義*/
	public static final String PDESML_VARTYPE_STR_PARTIALDIFFVAR = "partialdiffvar";
	public static final String PDESML_VARTYPE_STR_TIMEVAR = "timevar";
	public static final String PDESML_VARTYPE_STR_CONSTVAR = "constvar";
	public static final String PDESML_VARTYPE_STR_DIMENSIONVAR = "dimensionvar";
	public static final String PDESML_VARTYPE_STR_INDEXVAR = "indexvar";
	public static final String PDESML_VARTYPE_STR_DELTAVAR = "deltavar";

	//========================================================
	//ENUM
	//========================================================

	//-------------------------------------PdesML Tags and their definition
	public enum ePdesMLTag {
		TTAG_PDESML	(PDESML_TAG_STR_PDESML),
		TTAG_MESH	(PDESML_TAG_STR_MESH),
		TTAG_VARIABLE (PDESML_TAG_STR_VARIABLE),
		TTAG_MATH	(PDESML_TAG_STR_MATH),
			;
		private final String operatorStr;
		private ePdesMLTag(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	//-------------------------------------PdesML Types of mesh in the discretization
	public enum ePdesMLMeshType {
		TMESH_TYPE_RECTILINEAR	(PDESML_TAG_STR_RECTILINEAR),
			;
		private final String operatorStr;
		private ePdesMLMeshType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	//	-------------------------------------PdesML中変数で使用される型
	public enum ePdesMLVarType {
		TVAR_TYPE_PARTIALDIFFVAR	(PDESML_VARTYPE_STR_PARTIALDIFFVAR),
		TVAR_TYPE_TIMEVAR			(PDESML_VARTYPE_STR_TIMEVAR),
		TVAR_TYPE_CONSTVAR			(PDESML_VARTYPE_STR_CONSTVAR),
		TVAR_TYPE_DIMENSIONVAR		(PDESML_VARTYPE_STR_DIMENSIONVAR),
		TVAR_TYPE_INDEXVAR			(PDESML_VARTYPE_STR_INDEXVAR),
		TVAR_TYPE_DELTAVAR			(PDESML_VARTYPE_STR_DELTAVAR),
			;
		private final String operatorStr;
		private ePdesMLVarType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};



	//========================================================
	//PROTOTYPE
	//========================================================

	//========================================================
	//getPdesMLTagId
	// PdesMLタグid取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// TecMLタグid	: ePdesMLTag
	//
	//@throws
	// TecMLException
	//
	//========================================================
	public static ePdesMLTag getPdesMLTagId(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (ePdesMLTag t: ePdesMLTag.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getPdesMLTagId",
					 "invalid PdesML tag : " + strTag);
	}

	//========================================================
	//getPdesMLMeshType
	// Mesh type to be used in the discretization described in PdesML
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// TecMLタグid	: ePdesMLMeshType
	//
	//@throws
	// TecMLException
	//
	//========================================================
	public static ePdesMLMeshType getPdesMLMeshType(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (ePdesMLMeshType t: ePdesMLMeshType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getPdesMLMeshType",
					 "invalid PdesML mesh type : " + strTag);
	}
	
//	========================================================
	//getPdesMLVarType
	// PdesML変数型id取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// TecMLタグid	: ePdesMLVarType
	//
	//@throws
	// PdesMLException
	//
	//========================================================
	public static ePdesMLVarType getPdesMLVarType(String strTag)
	throws TecMLException {
		/*演算子と比較*/
		for (ePdesMLVarType t: ePdesMLVarType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new TecMLException("","getPdesMLVarType",
					 "invalid PdesML variable type : " + strTag);
	}

}
