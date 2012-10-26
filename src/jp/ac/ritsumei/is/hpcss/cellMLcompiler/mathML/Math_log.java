package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子logクラス
 */
public class Math_log extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_log(String[] strAttr) {
		super("log", eMathOperator.MOP_LOG, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG, strAttr);
	}
	
	public Math_log() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG) {
			throw new MathException("Math_log","calculate","lack of operand");
		}
		/*デフォルトの底に設定*/
		if(m_vecFactor.size()==1){
			m_vecFactor.add(1, m_vecFactor.get(0));
			m_vecFactor.add(0, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"10"));
		}
		
		/*演算結果を返す*/
		return Math.log(m_vecFactor.get(1).getValue()) / Math.log(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG) {
			throw new MathException("Math_log","toLegalString","lack of operand");
		}
		/*デフォルトの底に設定*/
		if(m_vecFactor.size()==1){
			m_vecFactor.add(1, m_vecFactor.get(0));
			m_vecFactor.add(0, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"10"));
		}

		return "log " + m_vecFactor.get(0).toLegalString() + "( " + m_vecFactor.get(1).toLegalString() + " )";
	}

}
