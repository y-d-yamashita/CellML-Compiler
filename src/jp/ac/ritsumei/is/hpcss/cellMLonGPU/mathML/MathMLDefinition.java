package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;

/**
 * MathML定義系ヘッダ
 */
public class MathMLDefinition {

	//========================================================
	//DEFINE
	//========================================================

	/*MathMLタグ定義*/
	public static final String MATHML_TAG_STR = "math";

	/*演算子文字列定義*/
	public static final String MATH_OPERATOR_STR_APPLY	 = "apply";
	public static final String MATH_OPERATOR_STR_DIVIDE	 = "divide";
	public static final String MATH_OPERATOR_STR_EQ		 = "eq";
	public static final String MATH_OPERATOR_STR_NEQ	 = "neq";
	public static final String MATH_OPERATOR_STR_MINUS	 = "minus";
	public static final String MATH_OPERATOR_STR_PLUS	 = "plus";
	public static final String MATH_OPERATOR_STR_TIMES	 = "times";
	public static final String MATH_OPERATOR_STR_EXP	 = "exp";
	public static final String MATH_OPERATOR_STR_LOG	 = "log";
	public static final String MATH_OPERATOR_STR_POWER	 = "power";
	public static final String MATH_OPERATOR_STR_SIN	 = "sin";
	public static final String MATH_OPERATOR_STR_COS	 = "cos";
	public static final String MATH_OPERATOR_STR_TAN	 = "tan";
	public static final String MATH_OPERATOR_STR_DIFF	 = "diff";
	public static final String MATH_OPERATOR_STR_BVAR	 = "bvar";
	public static final String MATH_OPERATOR_STR_LT		 = "lt";
	public static final String MATH_OPERATOR_STR_LEQ	 = "leq";
	public static final String MATH_OPERATOR_STR_GT		 = "gt";
	public static final String MATH_OPERATOR_STR_GEQ	 = "geq";
	public static final String MATH_OPERATOR_STR_AND	 = "and";
	public static final String MATH_OPERATOR_STR_OR		 = "or";
	public static final String MATH_OPERATOR_STR_INC	 = "inc";
	public static final String MATH_OPERATOR_STR_DEC	 = "dec";
	public static final String MATH_OPERATOR_STR_ASSIGN	 = "assign";
	public static final String MATH_OPERATOR_STR_FN		 = "fn";
	public static final String MATH_OPERATOR_STR_LN		 = "ln";
	public static final String MATH_OPERATOR_STR_ROOT	 = "root";
	public static final String MATH_OPERATOR_STR_FLOOR	 = "floor";
	public static final String MATH_OPERATOR_STR_CEIL	 = "ceil";
	public static final String MATH_OPERATOR_STR_PIECEWISE	 = "piecewise";
	public static final String MATH_OPERATOR_STR_PIECE	 = "piece";
	public static final String MATH_OPERATOR_STR_OTHERWISE	 = "otherwise";
	public static final String MATH_OPERATOR_STR_SELECTOR	 = "selector";
	public static final String MATH_OPERATOR_STR_CONDITION	 = "condition";
	public static final String MATH_OPERATOR_STR_PARTIALDIFF = "partialdiff";
	public static final String MATH_OPERATOR_STR_LAPLACIAN = "laplacian";
	public static final String MATH_OPERATOR_STR_DEGREE = "degree";

	/*演算子における非演算子の最低個数*/
	public static final int MATH_OPERATOR_MIN_FACTOR_APPLY = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_DIVIDE = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_EQ = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_NEQ = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_MINUS = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_PLUS = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_TIMES = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_EXP = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_LOG = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_POWER = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_SIN = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_COS = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_TAN = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_DIFF = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_BVAR = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_LT = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_LEQ = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_GT = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_GEQ = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_AND = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_OR	 = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_INC = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_DEC = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_ASSIGN = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_FN	 = 0;
	public static final int MATH_OPERATOR_MIN_FACTOR_LN	 = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_ROOT = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_FLOOR = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_CEIL = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_PIECEWISE = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_PIECE	 = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_OTHERWISE = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_SELECTOR = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_CONDITION = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_PARTIALDIFF = 2;
	public static final int MATH_OPERATOR_MIN_FACTOR_LAPLACIAN = 1;
	public static final int MATH_OPERATOR_MIN_FACTOR_DEGREE = 1;

	/*オペレータ文字列定義*/
	public static final String MATH_OPERAND_STR_CI		 = "ci";
	public static final String MATH_OPERAND_STR_CN		 = "cn";

	/*その他の要素文字列定義*/
	public static final String MATH_OPTIONAL_SEP	 = "sep";

	/*MathMLタグ属性文字列定義*/
	public static final String MATHML_ATTR_STR_TYPE = "type";
	
	/* MathML loop attribute */
	public static final String MATHML_ATTR_LOOP1 = "loop1";

	/*sepタイプ文字列定義*/
	public static final String MATHML_SEP_TYPE_E_NOTATION = "e-notation";


	//========================================================
	//ENUM
	//========================================================

	//-------------------------------------MathML要素大分類
	public enum eMathMLClassification {
		MML_OPERATOR,
		MML_OPERAND,
		MML_OPTIONAL,
	};

	//-------------------------------------オペレータ列挙
	public enum eMathOperator {
		MOP_APPLY	(MATH_OPERATOR_STR_APPLY),
		MOP_DIVIDE	(MATH_OPERATOR_STR_DIVIDE),
		MOP_EQ		(MATH_OPERATOR_STR_EQ),
		MOP_NEQ		(MATH_OPERATOR_STR_NEQ),
		MOP_MINUS	(MATH_OPERATOR_STR_MINUS),
		MOP_PLUS	(MATH_OPERATOR_STR_PLUS),
		MOP_TIMES	(MATH_OPERATOR_STR_TIMES),
		MOP_EXP		(MATH_OPERATOR_STR_EXP),
		MOP_LOG		(MATH_OPERATOR_STR_LOG),
		MOP_POWER	(MATH_OPERATOR_STR_POWER),
		MOP_SIN		(MATH_OPERATOR_STR_SIN),
		MOP_COS		(MATH_OPERATOR_STR_COS),
		MOP_TAN		(MATH_OPERATOR_STR_TAN),
		MOP_DIFF	(MATH_OPERATOR_STR_DIFF),
		MOP_BVAR	(MATH_OPERATOR_STR_BVAR),
		MOP_LT		(MATH_OPERATOR_STR_LT),
		MOP_LEQ		(MATH_OPERATOR_STR_LEQ),
		MOP_GT		(MATH_OPERATOR_STR_GT),
		MOP_GEQ		(MATH_OPERATOR_STR_GEQ),
		MOP_AND		(MATH_OPERATOR_STR_AND),
		MOP_OR		(MATH_OPERATOR_STR_OR),
		MOP_INC		(MATH_OPERATOR_STR_INC),
		MOP_DEC		(MATH_OPERATOR_STR_DEC),
		MOP_ASSIGN	(MATH_OPERATOR_STR_ASSIGN),
		MOP_FN		(MATH_OPERATOR_STR_FN),
		MOP_LN		(MATH_OPERATOR_STR_LN),
		MOP_ROOT	(MATH_OPERATOR_STR_ROOT),
		MOP_FLOOR	(MATH_OPERATOR_STR_FLOOR),
		MOP_CEIL	(MATH_OPERATOR_STR_CEIL),
		MOP_PIECEWISE	(MATH_OPERATOR_STR_PIECEWISE),
		MOP_PIECE	(MATH_OPERATOR_STR_PIECE),
		MOP_OTHERWISE	(MATH_OPERATOR_STR_OTHERWISE),
		MOP_SELECTOR	(MATH_OPERATOR_STR_SELECTOR),
		MOP_CONDITION	(MATH_OPERATOR_STR_CONDITION),
		MOP_PARTIALDIFF	(MATH_OPERATOR_STR_PARTIALDIFF),
		MOP_LAPLACIAN	(MATH_OPERATOR_STR_LAPLACIAN),
		MOP_DEGREE	(MATH_OPERATOR_STR_DEGREE),
			;
		private final String operatorStr;
		private eMathOperator(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	//-------------------------------------オペランド列挙
	public enum eMathOperand {
		MOPD_CI(MATH_OPERAND_STR_CI),
		MOPD_CN(MATH_OPERAND_STR_CN),
			;
		private final String operatorStr;
		private eMathOperand(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	//-------------------------------------その他の要素の列挙
	public enum eMathOptional{
		MOPT_SEP(MATH_OPTIONAL_SEP),
			;
		private final String operatorStr;
		private eMathOptional(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	//-------------------------------------sepのタイプ列挙
	public enum eMathSepType {
		MSEP_E_NOTATION(MATHML_SEP_TYPE_E_NOTATION),
			;
		private final String operatorStr;
		private eMathSepType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	private static String prevEnumString;

	//========================================================
	//specifyMathMLClassification
	// MathMLタグ分類取得
	//
	//@arg
	// string			strTag	: タグ文字列
	// unsigned int*	pudId	: 小分類id取得先ポインタ
	//
	//@return
	// MathMLタグ大分類id	: eMathMLClassification
	//
	//@throws
	// MathException
	//
	//========================================================
	public static eMathMLClassification specifyMathMLClassification(String strTag)
	throws MathException {
		/*演算子と比較*/
		for (eMathOperator t: eMathOperator.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				prevEnumString = t.toString();
				return eMathMLClassification.MML_OPERATOR;
			}
		}

		/*オペランドと比較*/
		for (eMathOperand t: eMathOperand.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				prevEnumString = t.toString();
				return eMathMLClassification.MML_OPERAND;
			}
		}

		/*その他の要素比較*/
		for (eMathOptional t: eMathOptional.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				prevEnumString = t.toString();
				return eMathMLClassification.MML_OPTIONAL;
			}
		}

		/*見つからなければ例外*/
		throw new MathException("","specifyMathMLClassification",
					"invalid MathML tag [ " + strTag + " ] ");
	}

	//========================================================
	//getMathOperatorId
	// MathML演算子id取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// MathML演算子id	: eMathOperator
	//
	//@throws
	// MathException
	//
	//========================================================
	public static eMathOperator getMathOperatorId(String strTag)
	throws MathException {
		/*演算子と比較*/
		for (eMathOperator t: eMathOperator.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new MathException("","getMathOperatorId","invalid MathML tag");
	}

	public static eMathOperator getMathOperatorId() {
		return eMathOperator.valueOf(prevEnumString);
	}

	//========================================================
	//getMathOperandId
	// MathMLオペランドid取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// MathMLオペランドid	: eMathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static eMathOperand getMathOperandId(String strTag)
	throws MathException {
		/*オペランドと比較*/
		for (eMathOperand t: eMathOperand.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new MathException("","getMathOperandId","invalid MathML tag");
	}

	public static eMathOperand getMathOperandId() {
		return eMathOperand.valueOf(prevEnumString);
	}

	//========================================================
	//getMathOptionalId
	// MathML補助的要素のid取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// MathML補助的要素のid	: eMathOptional
	//
	//@throws
	// MathException
	//
	//========================================================
	public static eMathOptional getMathOptionalId(String strTag)
	throws MathException {
		/*その他の要素比較*/
		for (eMathOptional t: eMathOptional.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new MathException("","getMathOptionalId","invalid MathML tag");
	}

	public static eMathOptional getMathOptionalId() {
		return eMathOptional.valueOf(prevEnumString);
	}

	//========================================================
	//getMathSepTypeId
	// sepのタイプid取得
	//
	//@arg
	// string	strTag	: タグ文字列
	//
	//@return
	// sepタイプid	: eMathSepType
	//
	//@throws
	// MathException
	//
	//========================================================
	public static eMathSepType getMathSepTypeId(String strTag)
	throws MathException {
		/*オペランドと比較*/
		for (eMathSepType t: eMathSepType.values()) {
			if (t.getOperatorStr().equals(strTag)) {
				return t;
			}
		}

		/*見つからなければ例外*/
		throw new MathException("","getMathSepTypeId","invalid sep type");
	}

}
