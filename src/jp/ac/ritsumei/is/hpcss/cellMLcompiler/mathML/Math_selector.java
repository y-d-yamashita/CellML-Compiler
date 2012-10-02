package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子selectorクラス
 */
public class Math_selector extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_selector(String[] strAttr) {
		super("", eMathOperator.MOP_SELECTOR, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SELECTOR, strAttr);
	}
	
	public Math_selector() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_selector","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SELECTOR){
			throw new MathException("Math_selector","toLegalString","lack of operand");
		}
		/* if there is only one index */
		if (m_vecFactor.size() == 2){
			return m_vecFactor.get(0).toLegalString()
			+ "[ " + m_vecFactor.get(1).toLegalString() + " ]";
		}
		/* if there are two indices */
		else if (m_vecFactor.size() == 3){
			return m_vecFactor.get(0).toLegalString()
			+ "[ " + m_vecFactor.get(1).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(2).toLegalString() + " ]";
		}
		else if (m_vecFactor.size() == 4){
			return m_vecFactor.get(0).toLegalString()
			+ "[ " + m_vecFactor.get(1).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(2).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(3).toLegalString() + " ]";
		}
		else if (m_vecFactor.size() == 5){
			return m_vecFactor.get(0).toLegalString()
			+ "[ " + m_vecFactor.get(1).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(2).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(3).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(4).toLegalString() + " ]";
		}
		else if (m_vecFactor.size() == 6){
			return m_vecFactor.get(0).toLegalString()
			+ "[ " + m_vecFactor.get(1).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(2).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(3).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(4).toLegalString() + " ]"
			+ "[ " + m_vecFactor.get(5).toLegalString() + " ]";
		}
		/* if there are more than two indices (can add more indices but now, limit is 2) */
		else {
			throw new MathException("Math_selector","toLegalString","too many operands");
		}
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SELECTOR){
			throw new MathException("Math_selector","toMathMLString","lack of operand");
		}

		/*文字列を追加していく*/
		String strExpression = "<apply><selector/>" + "\n";

		for(int i=0; i < m_vecFactor.size(); i++) {

			/* &&演算子を追加 */
			if(i != 0){
				strExpression += "\n";
			}

			/*項を追加*/
			strExpression += "\t" + (m_vecFactor.get(i)).toMathMLString();
		}

		strExpression += "\n" + "</apply>";
		
		return strExpression;
		
	}

}
