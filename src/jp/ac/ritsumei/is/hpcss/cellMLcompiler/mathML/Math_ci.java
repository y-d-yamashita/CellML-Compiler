package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML変数被演算子ciクラス
 */
public class Math_ci extends MathOperand {

	/*配列インデックス*/
	Vector<MathFactor> m_vecArrayIndexFactor;
	
	/*Selector追加要素*/
	private Vector<MathFactor> m_vecIndexListFactor;
	public void addIndexList(MathFactor pFactor){
		/*オペランドをベクタに追加*/
		getM_vecIndexListFactor().add(pFactor);
	}

	/*ポインタ演算子数(マイナスの場合は&演算子)*/
	int m_nPointerNum;

	/*-----コンストラクタ-----*/
	public Math_ci(String strVariableName,double dValue) {
		super(strVariableName, dValue, eMathOperand.MOPD_CI);
		m_vecArrayIndexFactor = new Vector<MathFactor>();
		setM_vecIndexListFactor(new Vector<MathFactor>());
		m_nPointerNum = 0;
	}
	public Math_ci(String strVariableName) {
		super(strVariableName, eMathOperand.MOPD_CI);
		m_vecArrayIndexFactor = new Vector<MathFactor>();
		setM_vecIndexListFactor(new Vector<MathFactor>());
		m_nPointerNum = 0;
	}

	/*-----値設定メソッド-----*/
	public void setValue(double dValue){
		m_dValue = dValue;
		m_bInitFlag = true;
	}

	/*-----配列インデックス追加メソッド(前方)-----*/
	public void addArrayIndexToFront(MathFactor pFactor){

		/*オペランドをベクタに追加*/
		m_vecArrayIndexFactor.add(0, pFactor);
	}
	public void addArrayIndexToFront(int nIndex)
	throws MathException{

		/*文字列に変換*/
		String strIndex = Integer.toString(nIndex);

		/*整数より定数オペランドインスタンスを生成*/
		Math_cn pConst = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strIndex);

		/*オーバーロードメソッドに投げる*/
		this.addArrayIndexToFront(pConst);
	}

	/*-----配列インデックス追加メソッド(後方)-----*/
	public void addArrayIndexToBack(MathFactor pFactor){

		/*オペランドをベクタに追加*/
		m_vecArrayIndexFactor.add(pFactor);
	}
	public void addArrayIndexToBack(int nIndex)
	throws MathException{

		/*文字列に変換*/
		String strIndex = Integer.toString(nIndex);

		/*整数より定数オペランドインスタンスを生成*/
		Math_cn pConst = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strIndex);

		/*オーバーロードメソッドに投げる*/
		this.addArrayIndexToBack(pConst);
	}

	/*-----ポインタ演算子数設定メソッド-----*/
	public void setPointerNum(int nPointerNum){
		m_nPointerNum = nPointerNum;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*ポインタ演算子の追加*/
		String strVariable = "";

		if(m_nPointerNum<0){
			strVariable += "&";
		}
		else if(m_nPointerNum>0){
			for(int i=0;i<m_nPointerNum;i++){
				strVariable += "*";
			}
		}

		/*変数名を追加*/
		strVariable += getM_strPresentText();

		/*配列インデックスの追加*/
		for (MathFactor it: m_vecArrayIndexFactor) {
			/*項を追加*/
			strVariable += "[" + it.toLegalString() + "]";
		}

		/*Selector要素*/
		/*配列インデックスの追加*/
		for (MathFactor it: getM_vecIndexListFactor()) {
			/*項を追加*/
//			strVariable += "[" + it.toSelectorLegalString() + "]";
			/*項を追加*/
			strVariable += "[" + it.toLegalString() + "]";
		}
		
		return strVariable;
	}
	
	/*-----数式複製メソッド-----*/
	public MathFactor createCopy() throws MathException {
		MathOperand newOperand =  MathFactory.createOperand(m_operandKind,getM_strPresentText());
		
		/*すべての子要素を複製*/
		for (MathFactor it: getM_vecIndexListFactor()) {
			((Math_ci)newOperand).addIndexList(it.createCopy());
		}
		
		return newOperand;
	}
	
	/**
	 * 比較メソッド
	 */
	public boolean equals(Object obj){
		if(obj instanceof Math_ci){
			Math_ci that = (Math_ci) obj;
			String thisStr=null;
			String thatStr=null;
				try {
					thisStr=this.toLegalString();
					thatStr=that.toLegalString();
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(thisStr.equals(thatStr)){
					return true;
				}
		}	
		return false;
	}
	public Vector<MathFactor> getM_vecIndexListFactor() {
		return m_vecIndexListFactor;
	}
	public void setM_vecIndexListFactor(Vector<MathFactor> m_vecIndexListFactor) {
		this.m_vecIndexListFactor = m_vecIndexListFactor;
	}
}
