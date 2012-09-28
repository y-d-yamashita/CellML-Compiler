package jp.ac.ritsumei.is.hpcss.cellMLcompiler.relML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;

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
	/**RelMLタグ文字列定義 diffequ*/
	public static final String RELML_TAG_STR_DIFFEQU = "diffequ";
	/**RelMLタグ文字列定義 component*/
	public static final String RELML_TAG_STR_COMPONENT = "component";
	/**RelMLタグ文字列定義 math*/
	public static final String RELML_TAG_STR_MATH = "math";
	/**RelMLタグ文字列定義 morphology*/
	public static final String RELML_TAG_STR_MORPHOLOGY = "morphology";
	/**RelMLタグ文字列定義 geometry*/
	public static final String RELML_TAG_STR_GEOMETRY = "geometry";
	/**RelMLタグ文字列定義 mesh*/
	public static final String RELML_TAG_STR_MESH = "mesh";
	/**RelMLタグ文字列定義 boundary conditions*/
	public static final String RELML_TAG_STR_BOUNDARY = "boundary-cond";
	/**RelMLタグ文字列定義 distributed parameters*/
	public static final String RELML_TAG_STR_PARAMETER = "parameter-cond";

	/**RelML変数型文字列定義 timevar*/
	public static final String RELML_VARTYPE_STR_TIMEVAR = "timevar";
	/**RelML変数型文字列定義 diffvar*/
	public static final String RELML_VARTYPE_STR_DIFFVAR = "diffvar";
	/**RelML変数型文字列定義 arithvar*/
	public static final String RELML_VARTYPE_STR_ARITHVAR = "arithvar";
	/**RelML変数型文字列定義 constvar*/
	public static final String RELML_VARTYPE_STR_CONSTVAR = "constvar";
	/**RelML temporal and spatial dimension variable names*/
	public static final String RELML_VARTYPE_STR_DIMENSIONVAR = "dimensionvar";
	/**RelML variable for discretization indices */
	public static final String RELML_VARTYPE_STR_INDEXVAR = "indexvar";
	/**RelML variable for delta time and delta space */
	public static final String RELML_VARTYPE_STR_DELTAVAR = "deltavar";


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
		RTAG_DIFFEQU	(RELML_TAG_STR_DIFFEQU),
		RTAG_COMPONENT	(RELML_TAG_STR_COMPONENT),
		RTAG_MATH		(RELML_TAG_STR_MATH),
		RTAG_MORPHOLOGY	(RELML_TAG_STR_MORPHOLOGY),
		RTAG_GEOMETRY	(RELML_TAG_STR_GEOMETRY),
		RTAG_MESH		(RELML_TAG_STR_MESH),
		RTAG_BOUNDARY	(RELML_TAG_STR_BOUNDARY),
		RTAG_PARAMETER	(RELML_TAG_STR_PARAMETER),
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
		RVAR_TYPE_TIMEVAR	(RELML_VARTYPE_STR_TIMEVAR),
		RVAR_TYPE_DIFFVAR	(RELML_VARTYPE_STR_DIFFVAR),
		RVAR_TYPE_ARITHVAR	(RELML_VARTYPE_STR_ARITHVAR),
		RVAR_TYPE_CONSTVAR	(RELML_VARTYPE_STR_CONSTVAR),
		RVAR_TYPE_DIMENSIONVAR	(RELML_VARTYPE_STR_DIMENSIONVAR),
		RVAR_TYPE_INDEXVAR		(RELML_VARTYPE_STR_INDEXVAR),
		RVAR_TYPE_DELTAVAR		(RELML_VARTYPE_STR_DELTAVAR),
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
