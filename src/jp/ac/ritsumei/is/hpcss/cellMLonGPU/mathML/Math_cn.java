package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.math.BigDecimal;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathSepType;

/**
 * MathML定数被演算子cnクラス
 */
public class Math_cn extends MathOperand {

	eMathSepType m_sepType;
	String m_strSepValue;
	String m_Type;

	/*-----コンストラクタ-----*/
	public Math_cn(String strValueString) {
		super(strValueString, eMathOperand.MOPD_CN);
		/*初期値設定*/
		try {
			if (strValueString.startsWith("(double)")) {
				m_dValue = Double.parseDouble(strValueString.substring(8));
			} else {
				m_dValue = Double.parseDouble(strValueString);
			}
		} catch (NumberFormatException e) {
			m_dValue = 0;
		}
		m_bInitFlag = true;
		m_Type = "double";
	}
	public Math_cn(String strValueString, eMathSepType sepType, String strSepValue) {
		super(strValueString, eMathOperand.MOPD_CN);
		m_sepType = sepType;
		m_strSepValue = strSepValue;
		/*初期値設定*/
		try {
			if (strValueString.startsWith("(double)")) {
				m_dValue = Double.parseDouble(strValueString.substring(8));
			} else {
				m_dValue = Double.parseDouble(strValueString);
			}
		} catch (NumberFormatException e) {
			m_dValue = 0;
		}
		m_bInitFlag = true;
		m_Type = "double";
	}

	/*-----値格納メソッド-----*/
	public void setValue(double dValue) throws MathException {
		throw new MathException("Math_cn","setValue",
				"can't set value on constant number");
	}

	/*-----数式複製メソッド-----*/
	public MathFactor createCopy() throws MathException {
		MathFactor newFactor = MathFactory.createOperand(m_operandKind,m_strPresentText,
				m_sepType,m_strSepValue);
		
		((Math_cn)newFactor).m_Type = this.m_Type;
		
		return newFactor;
	}

	/*(double)を消すメソッド*/
	public void changeType(){
		m_Type = "integer";
	}
	
	/*小数点以下がなければ(double)を消すメソッド. scale+1桁以降は切り捨て*/
	public void autoChangeType(int scale){
		BigDecimal value = new BigDecimal(this.m_strPresentText);
	
		//整数部
		BigDecimal integerPart = new BigDecimal(value.intValue());
		//小数点部
		BigDecimal fractionPart = value.subtract(integerPart).setScale(scale,BigDecimal.ROUND_DOWN);

		System.out.println(fractionPart);
		
		//小数点以下が0なら(double)と".0"表記を消す
		if(fractionPart.compareTo(new BigDecimal(0))==0){
			changeType();
			m_strPresentText=integerPart.toString();
		}
	}
	/*小数点以下がなければ(double)を消すメソッド. 10桁以降は切り捨て*/
	public void autoChangeType(){
		autoChangeType(10);
	}
	
	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*sepが利用される場合*/
		if(m_strSepValue != null && m_strSepValue.length()!=0){

			/*sepタイプに応じた文字列化*/
			switch(m_sepType){

				//------------------------指数表記
				case MSEP_E_NOTATION:
					return m_strPresentText + "E" + m_strSepValue;

				//------------------------定義されないタイプ
				default:
					throw new MathException("Math_cn","toLegalString",
							"can't set value on constant number");
			}
		}

		/*sepのない場合*/
		else{
			if(m_Type == "double"){
				return "(double)" + m_strPresentText;
			}else{
				return m_strPresentText;
			}
		}
	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {
		return 	"<cn> " + m_strPresentText + " </cn>";
	}
	
	
}
