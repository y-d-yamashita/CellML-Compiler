package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子piecewiseクラス
 */
public class Math_piecewise extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_piecewise(String[] strAttr) {
		super("", eMathOperator.MOP_PIECEWISE, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PIECEWISE, strAttr);
	}
	
	public Math_piecewise() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_piecewise","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PIECEWISE) {
			throw new MathException("Math_piecewise","toLegalString","lack of operand");
		}

		//-----------------------------------------------------
		//pieceとotherwiseに分類
		//-----------------------------------------------------
		Vector<Math_piece> vecMathPiece = new Vector<Math_piece>();
		Math_otherwise pMathOtherWise = null;

		for (MathFactor it1: m_vecFactor) {

			/*オペレータの場合*/
			if (it1.matches(eMathMLClassification.MML_OPERATOR)) {

				/*piece演算子*/
				if (((MathOperator)it1).matches(eMathOperator.MOP_PIECE)) {

					/*ベクタに追加*/
					vecMathPiece.add((Math_piece)it1);
				}

				/*otherwise演算子*/
				else if (((MathOperator)it1).matches(eMathOperator.MOP_OTHERWISE)) {

					/*重複チェック*/
					if(pMathOtherWise != null){
						throw new MathException("Math_piecewise","toLegalString","too many otherwise");
					}
					else{
						pMathOtherWise = (Math_otherwise)it1;
					}
				}

				/*その他の演算子は例外*/
				else{
					throw new MathException("Math_piecewise","toLegalString","invalid operator");
				}

			}

			/*オペレータでない場合は例外*/
			else{
				throw new MathException("Math_piecewise","toLegalString","invalid operand");
			}
		}

		//-----------------------------------------------------
		//文字列の構築
		//-----------------------------------------------------
		String strExpression = "";

		/*piece文の文字列を追加していく*/
		for (Math_piece it2: vecMathPiece) {
			strExpression += " ( " + it2.toLegalString() + " : ";
		}

		/*otherwise文の文字列を追加*/
		strExpression += pMathOtherWise.toLegalString();

		/*piece文の閉じ括弧を追加*/
		for (Math_piece it2: vecMathPiece) {
			strExpression += " ) ";
		}

		return strExpression;
	}

}
