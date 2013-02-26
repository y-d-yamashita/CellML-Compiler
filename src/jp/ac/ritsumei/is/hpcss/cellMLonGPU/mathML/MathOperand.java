package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * MathML被演算子クラス.
 */
public abstract class MathOperand extends MathFactor {

	/**要素の値変数*/
	protected double m_dValue;

	/**初期化判定*/
	protected boolean m_bInitFlag;

	/**被演算子種類*/
	protected eMathOperand m_operandKind;

	/**
	 * MathML被演算子インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param dValue 要素の値変数
	 * @param operandKind 被演算子種類
	 */
	public MathOperand(String strPresentText,double dValue, eMathOperand operandKind) {
		super(strPresentText, eMathMLClassification.MML_OPERAND);
		m_dValue = dValue;
		m_bInitFlag = true;
		m_operandKind = operandKind;
	}

	/**
	 * MathML被演算子インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param operandKind 被演算子種類
	 */
	public MathOperand(String strPresentText, eMathOperand operandKind) {
		super(strPresentText, eMathMLClassification.MML_OPERAND);
		m_dValue = 0.0;
		m_bInitFlag = false;
		m_operandKind = operandKind;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#getValue()
	 */
	public double getValue() throws MathException {
		/*未初期化の場合は例外処理*/
		if(!m_bInitFlag){
			throw new MathException("MathOperand","getValue",
					"uninitialized operand referenced");
		}

		return m_dValue;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#createCopy()
	 */
	public MathFactor createCopy() throws MathException{
		return MathFactory.createOperand(m_operandKind, m_strPresentText, m_dValue);
	}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public MathFactor clone(){
		MathFactor factor=null;
		try {
			factor= MathFactory.createOperand(m_operandKind, m_strPresentText, m_dValue);
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return factor;
	}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public MathFactor copy(){return this;}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	@Override
	public MathFactor semiClone(){
		return this;}

	/**
	 * オブジェクトを比較する.
	 * @param pOperand 比較するオブジェクト
	 * @return 同一判定
	 */
	public boolean matches(MathOperand pOperand){
		return m_strPresentText.equals(pOperand.m_strPresentText);
	}

	/**
	 * オブジェクトを比較する.
	 * @param operandKind 被演算子種類
	 * @return 同一判定
	 */
	public boolean matches(eMathOperand operandKind){
		return m_operandKind == operandKind;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#matchesExpression(jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor)
	 */
	public boolean matchesExpression(MathFactor pFactor){

		/*オペランドの場合*/
		if(pFactor.matches(eMathMLClassification.MML_OPERAND)){
			return matches((MathOperand)pFactor);
		}
		/*その他の要素とは比較しない*/
		else{
			return false;
		}
	}

	/**
	 * @return
	 * @throws MathException
	 */
	public MathFactor toBinOperation() throws MathException{ return this;}

	/**
	 * @return
	 * @throws MathException
	 */
	public MathFactor expand(MathOperand ci) throws MathException{ return this;}
	
	
	/**
	 * Change to 0 == F(x) format
	 * @return
	 * @throws MathException 
	 */
	@Override
	public MathFactor toZeroEqualFormat() throws MathException{return this;}


	/**
	 * Remove excessive arithmetic operator
	 * @return
	 */
	@Override
	public MathFactor removeExcessiveArithmeticOperator()throws MathException{return this;}

	/* override hashcode and equals of MathOperand to make objects work as hash map keys */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(m_dValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((m_operandKind == null) ? 0 : m_operandKind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MathOperand other = (MathOperand) obj;
		if (Double.doubleToLongBits(m_dValue) != Double
				.doubleToLongBits(other.m_dValue))
			return false;
		if (m_operandKind != other.m_operandKind)
			return false;
		return true;
	}

	
}
