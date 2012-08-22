package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * 代入演算子クラス
 */
public class Math_assign extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_assign(String[] strAttr) {
		super("=", eMathOperator.MOP_ASSIGN, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ASSIGN, strAttr);
	}

	public Math_assign() {
		this(null);
	}
	
	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != 2){
			throw new MathException("Math_assign","calculate","lack of operand");
		}

		/*左辺値取得*/
		double dLeftValue = m_vecFactor.get(1).getValue();

		/*代入処理*/
		m_vecFactor.get(0).setValue(dLeftValue);

		/*左辺値の値を返す*/
		return dLeftValue;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != 2) {
			throw new MathException("Math_assign","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString()
			+ " = " + m_vecFactor.get(1).toLegalString();
	}

}
