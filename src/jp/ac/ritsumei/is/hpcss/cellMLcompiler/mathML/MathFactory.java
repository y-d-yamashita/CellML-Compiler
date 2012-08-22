package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathSepType;

/**
 * Math要素インスタンス生成クラス
 */
public class MathFactory {

	/*-----コンストラクタ-----*/
	public MathFactory() {
	}

	/*-----生成系メソッド-----*/

	//========================================================
	//createOperator
	// タグ文字列よりオペレータインスタンスに変換
	//
	//@arg
	// eMathOperator	operatorId	: 演算子列挙型id
	//
	//@return
	// オペレータインスタンス	: MathOperator
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperator createOperator(eMathOperator operatorId)
	throws MathException {
		return createOperator(operatorId, null);
	}

	public static MathOperator createOperator(eMathOperator operatorId, String[] strAttr)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operatorId) {

		case MOP_APPLY:
			return new Math_apply(strAttr);
		case MOP_DIVIDE:
			return new Math_divide(strAttr);
		case MOP_EQ:
			return new Math_eq(strAttr);
		case MOP_NEQ:
			return new Math_neq(strAttr);
		case MOP_MINUS:
			return new Math_minus(strAttr);
		case MOP_PLUS:
			return new Math_plus(strAttr);
		case MOP_TIMES:
			return new Math_times(strAttr);
		case MOP_EXP:
			return new Math_exp(strAttr);
		case MOP_LOG:
			return new Math_log(strAttr);
		case MOP_POWER:
			return new Math_power(strAttr);
		case MOP_SIN:
			return new Math_sin(strAttr);
		case MOP_COS:
			return new Math_cos(strAttr);
		case MOP_TAN:
			return new Math_tan(strAttr);
		case MOP_DIFF:
			return new Math_diff(strAttr);
		case MOP_BVAR:
			return new Math_bvar(strAttr);
		case MOP_LT:
			return new Math_lt(strAttr);
		case MOP_LEQ:
			return new Math_leq(strAttr);
		case MOP_GT:
			return new Math_gt(strAttr);
		case MOP_GEQ:
			return new Math_geq(strAttr);
		case MOP_AND:
			return new Math_and(strAttr);
		case MOP_OR:
			return new Math_or(strAttr);
		case MOP_INC:
			return new Math_inc(strAttr);
		case MOP_DEC:
			return new Math_dec(strAttr);
		case MOP_ASSIGN:
			return new Math_assign(strAttr);
		case MOP_FN:
			return new Math_fn(strAttr);
		case MOP_LN:
			return new Math_ln(strAttr);
		case MOP_ROOT:
			return new Math_root(strAttr);
		case MOP_FLOOR:
			return new Math_floor(strAttr);
		case MOP_CEIL:
			return new Math_ceil(strAttr);
		case MOP_PIECEWISE:
			return new Math_piecewise(strAttr);
		case MOP_PIECE:
			return new Math_piece(strAttr);
		case MOP_OTHERWISE:
			return new Math_otherwise(strAttr);
		case MOP_SELECTOR:
			return new Math_selector(strAttr);
		case MOP_CONDITION:
			return new Math_condition(strAttr);
		case MOP_PARTIALDIFF:
			return new Math_partialdiff(strAttr);
		case MOP_LAPLACIAN:
			return new Math_laplacian(strAttr);
		case MOP_DEGREE:
			return new Math_degree(strAttr);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperator",
						"Invalid Operator id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	// double		dInitValue	: 初期値
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId,
			String strName, double dInitValue)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName,dInitValue);
		case MOPD_CN:
			return new Math_cn(strName);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	// (オーバーロード : 初期値無し)
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId, String strName)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName);
		case MOPD_CN:
			return new Math_cn(strName);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	// (オーバーロード : sepタイプ指定)
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	// eMathSepType	sepType		: sepタイプ
	// string		strSepValue	: sepに使う値
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId,
			String strName, eMathSepType sepType, String strSepValue)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName);
		case MOPD_CN:
			return new Math_cn(strName,sepType,strSepValue);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

}
