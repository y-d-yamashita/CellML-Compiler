package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.Visitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLTag;

/**
 * MathML変数被演算子ciクラス
 */
public class Math_ci extends MathOperand {

	/*配列インデックス*/
	Vector<MathFactor> m_vecArrayIndexFactor;
	
	/*Selector追加要素*/
	Vector<MathFactor> m_vecIndexListFactor;
	
	/*CodeName*/
	public String codeName;
	
	public void addIndexList(MathFactor pFactor){
		/*オペランドをベクタに追加*/
		m_vecIndexListFactor.add(pFactor);
	}
	public void addIndexList(Vector<MathFactor> pIndexList){
		this.m_vecIndexListFactor.addAll(pIndexList);
	}
	public Vector<MathFactor> getIndexList(){
		/*オペランドをベクタに追加*/
		return m_vecIndexListFactor;
	}
	public void clearIndexList(){
		m_vecIndexListFactor.clear();
	}


	/*ポインタ演算子数(マイナスの場合は&演算子)*/
	int m_nPointerNum;


	/*cellmlfile名*/
	String m_CellMLFileName = null;
	public void setCellMLFileName(String filename){
		m_CellMLFileName = filename;
	}
	public String getCellMLFileName(){
		return m_CellMLFileName;
	}

	/*CellML対応もしくはRelML対応*/
	//SimpleExpansion用 @m-ara
	protected eRelMLTag m_CorrespondTag;
	public eRelMLTag getCorrespondTag(){
		return m_CorrespondTag;
	}
	public void setCorrespondTag(eRelMLTag pTag){
		m_CorrespondTag = pTag;
	}
	
	/*-----コンストラクタ-----*/
	public Math_ci(String strVariableName,double dValue) {
		super(strVariableName, dValue, eMathOperand.MOPD_CI);
		m_vecArrayIndexFactor = new Vector<MathFactor>();
		m_vecIndexListFactor = new Vector<MathFactor>();
		m_nPointerNum = 0;
		m_CorrespondTag = null;
	}
	public Math_ci(String strVariableName) {
		super(strVariableName, eMathOperand.MOPD_CI);
		m_vecArrayIndexFactor = new Vector<MathFactor>();
		m_vecIndexListFactor = new Vector<MathFactor>();
		m_nPointerNum = 0;
		m_CorrespondTag = null;
	}

	/*-----値設定メソッド-----*/
	public void setValue(double dValue){
		m_dValue = dValue;
		m_bInitFlag = true;
	}
	
	/*-----名前を返すメソッド-----*/
	public String getName(){
		return m_strPresentText;
	}
	
	/*-----コード名を返すメソッド-----*/
	public String getCodeName(){
		return codeName;
	}
	
	/*-----コード名を設定するメソッド-----*/
	public void setCodeName(String name){
		codeName = name;
	}
	

	/*-----配列インデックス追加メソッド(前方)-----*/
	public void addArrayIndexToFront(MathFactor pFactor){

		 if(pFactor.matches(eMathMLClassification.MML_OPERAND)){
				
			if(((MathOperand)pFactor).matches(eMathOperand.MOPD_CN)){
					((Math_cn)pFactor).changeType();
			}
		}
		/*オペランドをベクタに追加*/
		m_vecArrayIndexFactor.add(0, pFactor);
	}
	public void addArrayIndexToFront(int nIndex)
	throws MathException{

		/*文字列に変換*/
		String strIndex = Integer.toString(nIndex);

		/*整数より定数オペランドインスタンスを生成*/
		Math_cn pConst = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strIndex);
		
		pConst.changeType();
		/*オーバーロードメソッドに投げる*/
		this.addArrayIndexToFront(pConst);
	}

	/*-----配列インデックス追加メソッド(後方)-----*/
	public void addArrayIndexToBack(MathFactor pFactor){
		
		 if(pFactor.matches(eMathMLClassification.MML_OPERAND)){
				
			if(((MathOperand)pFactor).matches(eMathOperand.MOPD_CN)){
					((Math_cn)pFactor).changeType();
			}
		}
		
		/*オペランドをベクタに追加*/
		m_vecArrayIndexFactor.add(pFactor);
	}
	public void addArrayIndexToBack(int nIndex)
	throws MathException{

		/*文字列に変換*/
		String strIndex = Integer.toString(nIndex);

		/*整数より定数オペランドインスタンスを生成*/
		Math_cn pConst = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strIndex);
		pConst.changeType();

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
		strVariable += m_strPresentText;

		/*配列インデックスの追加*/
		for (MathFactor it: m_vecArrayIndexFactor) {
			/*項を追加*/
			strVariable += "[" + it.toLegalString() + "]";
		}

		/*Selector要素*/
		/*配列インデックスの追加*/
		for (MathFactor it: m_vecIndexListFactor) {
			/*項を追加*/
			strVariable += "[" + it.toLegalString() + "]";
		}
		
		return strVariable;
	}
	
	/*-----Java文字列変換メソッド-----*/
	public String toLegalJavaString() throws MathException {

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
		strVariable += m_strPresentText;

		/*配列インデックスの追加*/
		for (MathFactor it: m_vecArrayIndexFactor) {
			/*項を追加*/
			strVariable += "[" + it.toLegalJavaString() + "]";
		}

		/*Selector要素*/
		/*配列インデックスの追加*/
		for (MathFactor it: m_vecIndexListFactor) {
			/*項を追加*/
			strVariable += "[" + it.toLegalJavaString() + "]";
		}
		
		return strVariable;
	}
	
	/*-----数式複製メソッド-----*/
	public MathFactor createCopy() throws MathException {
		MathOperand newOperand =  MathFactory.createOperand(m_operandKind,m_strPresentText);
		
		/*すべての子要素を複製*/
		for (MathFactor it: m_vecIndexListFactor) {
			((Math_ci)newOperand).addIndexList(it.createCopy());
		}
		
		return newOperand;
	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {
		/*ポインタ演算子の追加*/
		String strExpression = "";
		
		/*Selector要素*/
		if(m_vecIndexListFactor.size()>0){
			/*文字列を追加していく*/
			strExpression = "\t" + "<apply>" + "\n";
			strExpression += "\t" + "<selector/>" + "\n";
			strExpression += "\t" + "<ci> " + m_strPresentText + " </ci>" + "\n";

			/*Selector要素*/
			/*配列インデックスの追加*/
			for(int i=0; i < m_vecIndexListFactor.size(); i++) {
				/*項を追加*/
				strExpression += "\t" +(m_vecIndexListFactor.get(i)).toMathMLString() + "\n";
			}
			
			strExpression += "\t" + "</apply>";
			
		}else{
			strExpression += "<ci> " + m_strPresentText + " </ci>";
		}
		return 	strExpression;
	}
	/* override hashcode and equals of Math_ci to make objects work as hash map keys */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((m_vecArrayIndexFactor == null) ? 0 : m_vecArrayIndexFactor
						.hashCode());
		result = prime
				* result
				+ ((m_vecIndexListFactor == null) ? 0 : m_vecIndexListFactor
						.hashCode());
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
		Math_ci other = (Math_ci) obj;
		if (m_vecArrayIndexFactor == null) {
			if (other.m_vecArrayIndexFactor != null)
				return false;
		} else if (!m_vecArrayIndexFactor.equals(other.m_vecArrayIndexFactor))
			return false;
		if (m_vecIndexListFactor == null) {
			if (other.m_vecIndexListFactor != null)
				return false;
		} else if (!m_vecIndexListFactor.equals(other.m_vecIndexListFactor))
			return false;
		return true;
	}
	
	public Vector<MathFactor> getM_vecIndexListFactor() {
		return m_vecIndexListFactor;
	}
	
	public void setM_vecIndexListFactor(Vector<MathFactor> m_vecIndexListFactor) {
		this.m_vecIndexListFactor = m_vecIndexListFactor;
	}
	
	/**
	 * Vsitorパターンでのtraverse
	 * @param v
	 */
	public void traverse(Visitor v){
//		m_pRootFactor.traverse(v);
		v.visit(this);
	}
	/**
	 * 初期化済か判定
	 * @return boolean
	 * @author m-ara
	 */
	public boolean isInitialized(){
		return m_bInitFlag;
	}
}
