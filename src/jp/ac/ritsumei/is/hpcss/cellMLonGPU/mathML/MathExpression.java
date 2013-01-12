package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.IndexReplacingVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.MathFactorStackingVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.Visitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML数式クラス.
 */
public class MathExpression {

	/**式のルートとなる要素*/
	MathFactor m_pRootFactor;

	/**現在オペランドを探している演算子を保持するスタック*/
	Stack<MathOperator> m_stackCurOperator;

	/*関数構築用ポインタ*/
	/**現在構築中の関数演算子*/
	MathOperator m_pCurFuncOperator;
	/**関数演算子の親に位置する演算子*/
	MathOperator m_pFuncParentOperand;

	/**変数リスト保持ベクタ*/
	Vector<MathOperand> m_vecVariables;
	
	/**数式ID*/
	long exID = -1;
	/**conditionのreference*/
	long condref = -1;
	
	//非線形フラグ
	boolean nonlinear;
	
	/**
	 * MathML数式インスタンスを作成する.
	 */
	public MathExpression() {
		m_stackCurOperator = new Stack<MathOperator>();
		m_vecVariables = new Vector<MathOperand>();
		m_pRootFactor = null;
		m_pCurFuncOperator = null;
		m_pFuncParentOperand = null;
	}
	/**
	 * MathML数式インスタンスを作成する.
	 * @param pRootFactor 式のルートとなる要素
	 */
	public MathExpression(MathFactor pRootFactor) {
		this();
		m_pRootFactor = pRootFactor;
	}

	/**
	 * 演算子を追加する.
	 * @param pOperator 追加する演算子
	 */
	public void addOperator(MathOperator pOperator) {
		/*開始演算子設定*/
		if (m_stackCurOperator.empty()) {
			m_stackCurOperator.push(pOperator);
			m_pRootFactor = m_stackCurOperator.peek();
		}
		else {
			/*関数構築中の場合*/
			if (m_pCurFuncOperator != null
			    && m_stackCurOperator.peek() == m_pFuncParentOperand) {
				/*関数引数として要素追加*/
				m_pCurFuncOperator.addFactor(pOperator);
			}
			/*通常の演算子追加*/
			else {
				/*演算子に演算子追加*/
				m_stackCurOperator.peek().addFactor(pOperator);
			}

			/*スタックに追加した演算子をプッシュ*/
			m_stackCurOperator.push(pOperator);
		}
	}

	/**
	 * オペランドを追加する.
	 * @param pOperand 追加するオペランド
	 * @throws MathException
	 */
	public void addOperand(MathOperand pOperand)
	throws MathException {
		/*1変数のみの式を構成する場合*/
		if (m_pRootFactor == null) {
			m_pRootFactor = pOperand;
		}

		/*例外処理*/
		if (m_stackCurOperator.empty()) {
			throw new MathException("MathExpression","addOperand",
						"no operator written before this operand");
		}

		/*関数構築中の場合*/
		if (m_pCurFuncOperator != null
		    && m_stackCurOperator.peek() == m_pFuncParentOperand) {
			/*関数引数として要素追加*/
			m_pCurFuncOperator.addFactor(pOperand);
		}

		/*関数演算子fnの場合*/
		else if (m_stackCurOperator.peek().matches(eMathOperator.MOP_FN)) {

			/*変数オペランドMath_ci以外を受理しない*/
			if (pOperand.matches(eMathOperand.MOPD_CI)) {
				((Math_fn)m_stackCurOperator.peek()).setFuncOperand((Math_ci)pOperand);
			}
			else {
				throw new MathException("MathExpression","addOperand",
							"can't use constant number for function operand");
			}
		}

		/*その他の演算子*/
		else {
			/*演算子にオペランド追加*/
			m_stackCurOperator.peek().addFactor(pOperand);
		}
	}

	/**
	 * 変数を追加する.
	 * @param pOperand 追加するオペランド
	 * @throws MathException
	 */
	public void addVariable(MathOperand pOperand)
	throws MathException {
		/*重複チェック*/
		boolean bDuplicate = false;

		for (MathOperand it: m_vecVariables) {

			if (it.matches(pOperand)) {
				bDuplicate = true;
				break;
			}
		}

		/*重複しない場合*/
		if (!bDuplicate) {

			/*変数リストに追加*/
			m_vecVariables.add(pOperand);
		}

		/*オペランドを追加*/
		this.addOperand(pOperand);
	}

	/**
	 * 演算子範囲を終了する.
	 * @param pOperator 追加する演算子
	 * @throws MathException
	 */
	public void breakOperator(MathOperator pOperator)
	throws MathException {
		/*例外処理*/
		if (m_stackCurOperator.empty()) {
			throw new MathException("MathExpression","breakOperator",
						"operator stack is empty");
		}
		
		/*一致する演算子までpop-up*/
		while (!m_stackCurOperator.peek().matches(pOperator)) {

			/*1つpop-up*/
			m_stackCurOperator.pop();

			/*例外処理*/
			if (m_stackCurOperator.empty()) {
				throw new MathException("MathExpression","breakOperator",
				"start operator tag not found");
			}
		}


		/*関数演算子の場合,自身と親演算子のポインタを保存する*/
		if (m_stackCurOperator.peek().matches(eMathOperator.MOP_FN)) {

			/*構築中の関数演算子を保存*/
			m_pCurFuncOperator = m_stackCurOperator.peek();

			/*一致した演算子をpop-up*/
			m_stackCurOperator.pop();

			/*親演算子を保存*/
			m_pFuncParentOperand = m_stackCurOperator.peek();
		}

		/*operandを持たないoperatorはpop-upしない*/
		else if (!m_stackCurOperator.peek().hasFactor()) {
			return;
		}

		else{

			/*関数引数適用範囲の終了判定*/
			if (m_stackCurOperator.peek() == m_pFuncParentOperand) {
				m_pCurFuncOperator = null;
				m_pFuncParentOperand = null;
			}
			/*一致した演算子をpop-up*/
			m_stackCurOperator.pop();
		}
	}
	
	/**
	 * Selector削除
	 */
	public void removeSelector(){
		((MathOperator) m_pRootFactor).removeSelector(m_pRootFactor);
	}
	
	/**
	 * 構造情報をapplyへ割り当てる
	 */
	public void assignStruAttrToApply(HashMap<Integer, String> attrList){
		((MathOperator) m_pRootFactor).assignStruAttrToApply(m_pRootFactor, attrList);
	}
	
	/**
	 * Selector内cnをIntegerに変更
	 */
	public void changeSelectorInteger(){
		((MathOperator) m_pRootFactor).changeSelectorInteger(m_pRootFactor);
	}
	
	/**
	 * index内cnをIntegerに変更
	 */
	public void changeIndexInteger(){
		((MathOperator) m_pRootFactor).changeIndexInteger();
	}
	
	/**
	 *  conditionがあるかどうか検索
	 */
	public MathExpression searchCondition(){
		MathExpression condExp = ((MathOperator) m_pRootFactor).searchCondition(m_pRootFactor);
		return condExp;
	}
	
	/**
	 * conditionのreference番号を登録する
	 */
	public void setCondref(long num){
		this.condref = num;
	}
	
	/**
	 * 数式IDを登録する
	 */
	public void setExID(long num){
		this.exID = num;
	}
	
	/**
	 *数式内の全てのvariableを取得する (selector考慮)
	 * @throws MathException 
	 */
	public void getAllVariables(Vector<Math_ci> pVec) throws MathException{
		((MathOperator)m_pRootFactor).getVariables(m_pRootFactor, pVec);
	}
	/**
	 *数式内の全てのvariableを取得する (selector考慮)
	 * @throws MathException 
	 */
	public void getAllVariablesWithSelector(Vector<Math_ci> pVec) throws MathException{
		((MathOperator)m_pRootFactor).getVariablesWithSelector(m_pRootFactor, pVec);
	}
	
	/**数式内の全てのvariableを重複を許して取得する
	 * @throws MathException 
	 */
	public void getAllVariables_SusceptibleOfOverlap(Vector<Math_ci> pVec) throws MathException{
		((MathOperator)m_pRootFactor).getVariables_SusceptibleOfOverlap(m_pRootFactor, pVec);
	}
	/**数式内の全てのvariableを重複を許して取得する
	 * @throws MathException 
	 */
	public void getAllVariables_SusceptibleOfOverlapWithSelector(Vector<Math_ci> pVec) throws MathException{
		((MathOperator)m_pRootFactor).getVariables_SusceptibleOfOverlapWithSelector(m_pRootFactor, pVec);
	}
	/**
	 * 計算式構築状態を取得する.
	 * @return 計算式構築状態
	 */
	public boolean isConstructing() {
		/*スタックに演算子が残ってなければ非構築状態*/
		if (m_stackCurOperator.empty()) {
			return false;
		}

		return true;
	}

	/**
	 * 変数を取得する.
	 * @param dVariableId 変数id
	 * @return 引数指定idの変数
	 */
	public MathOperand getVariable(int dVariableId) {
		return m_vecVariables.get(dVariableId);
	}
	
	/**
	 * 変数を取得する.
	 * @param dVariableId 変数id
	 * @return 引数指定idの変数
	 * @author y-yamashita
	 */
	public Vector<MathOperand> getVariables() {
		return m_vecVariables;
	}
	
	/**
	 * 数式中の変数の数を取得する.
	 * @return 変数の数
	 */
	public int getVariableCount() {
		return m_vecVariables.size();
	}

	/**
	 * 数式を複製する.
	 * @return 複製数式インスタンス
	 * @throws MathException
	 */
	public MathExpression createCopy()
	throws MathException {
		return new MathExpression(m_pRootFactor.createCopy());
	}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public MathExpression clone(){
		return new MathExpression(m_pRootFactor.clone());
	}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public MathExpression copy(){return this;}

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public MathExpression semiClone(){
		return new MathExpression(m_pRootFactor.semiClone());
}

	
	/**
	 * 置換する.
	 * @param pOldOperand 置換対象のオペランド
	 * @param pNewFactor 置換後の数式
	 * @throws MathException
	 */
	public void replace(MathOperand pOldOperand, MathFactor pNewFactor)
	throws MathException {
		/*ルートが演算子の場合*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {

			/*関数の場合*/
			if (((MathOperator)m_pRootFactor).matches(eMathOperator.MOP_FN)) {

				/*関数名の照合*/
				if (((Math_fn)m_pRootFactor).matches(pOldOperand)) {
					m_pRootFactor = pNewFactor;
				}
			}
			/*その他の演算子*/
			else {
				((MathOperator)m_pRootFactor).replace(pOldOperand, pNewFactor);
			}
		}
		/*オペランドの場合*/
		else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
			m_pRootFactor = pNewFactor;
		}
		/*例外の要素*/
		else {
			throw new MathException("MathExpression","replace",
						"invalid root factor");
		}
	}

	/**
	 * 置換する.
	 * @param pOldFunction 置換対象のオペランド
	 * @param pNewFactor 置換後の数式
	 * @throws MathException
	 */
	public void replace(Math_fn pOldFunction, MathFactor pNewFactor)
	throws MathException {
		/*ルートが演算子の場合*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {

			/*関数の場合*/
			if (((MathOperator)m_pRootFactor).matches(eMathOperator.MOP_FN)) {

				/*関数名の照合*/
				if (((Math_fn)m_pRootFactor).matches(pOldFunction)) {
					m_pRootFactor = pNewFactor;
				}
			}
			/*その他の演算子*/
			else {
				/*数式ツリーを探索する*/
				((MathOperator)m_pRootFactor).replace(pOldFunction, pNewFactor);
			}
		}
		/*オペランドの場合*/
		else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
		}
		/*例外の要素*/
		else {
			throw new MathException("MathExpression","replace",
						"invalid root factor");
		}
	}
		
	/**
	 * Single-step replacement of the differential variables in the MathExpression: Pass to MathOperator replace().
	 * @param pOldOptr 置換対象のオペランド
	 * @param pNewOptr 置換後の数式
	 * @throws MathException
	 */
	public void replace(MathOperator pOldOptr, MathOperator pNewOptr)
	throws MathException {
		
		/*ルートが演算子の場合*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			/*関数の場合*/
			if (((MathOperator)m_pRootFactor).matches(eMathOperator.MOP_FN)) {
				// proceed to the next RootFactor
			}
			/*その他の演算子*/
			else {
				((MathOperator)m_pRootFactor).replaceDiffOptr(pOldOptr, pNewOptr);
			}
		}
		/*オペランドの場合*/
		else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
			// proceed to the next RootFactor
		}
		/*例外の要素*/
		else {
			throw new MathException("MathExpression","replace",
						"invalid root factor");
		}
		
	}
	
	/**
	 * 置換指定ファクタを取得する.
	 * @param pSearchOperand 検索関数オペランド
	 * @param pvecDstFunctions 検索結果取得先ポインタ
	 */
	public void searchFunction(MathOperand pSearchOperand,
			Vector<Math_fn> pvecDstFunctions) {
		/*ルートが演算子の場合*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			/*検索開始*/
			((MathOperator)m_pRootFactor).searchFunction(pSearchOperand,pvecDstFunctions);
		}
	}

	/**
	 * 左辺式を取得する.
	 * @return 左辺式
	 */
	public MathExpression getLeftExpression() {
		/*ルート演算子がない場合は取得不能*/
		if (m_pRootFactor == null
		    || !m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			return null;
		}

		/*左辺式ルート要素取得*/
		MathFactor pLeftRootFactor = ((MathOperator)m_pRootFactor).getLeftExpression();

		/*エラー処理*/
		if(pLeftRootFactor == null){
			return null;
		}

		/*新しい数式インスタンス生成*/
		MathExpression pNewExpression = new MathExpression();

		/*ルート要素の設定*/
		pNewExpression.m_pRootFactor = pLeftRootFactor;

		return pNewExpression;
	}

	/**
	 * 右辺式を取得する.
	 * @return 右辺式
	 */
	public MathExpression getRightExpression() {
		/*ルート演算子がない場合は取得不能*/
		if (m_pRootFactor == null
		    || !m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			return null;
		}

		/*右辺式ルート要素取得*/
		MathFactor pRightRootFactor = ((MathOperator)m_pRootFactor).getRightExpression();

		/*エラー処理*/
		if (pRightRootFactor == null) {
			return null;
		}

		/*新しい数式インスタンス生成*/
		MathExpression pNewExpression = new MathExpression();

		/*オペランドの場合*/
		if (pRightRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
			/*右辺変数をルートに*/
			pNewExpression.m_pRootFactor = (MathOperand)pRightRootFactor;
		}
		/*演算子の場合*/
		else {
			/*右辺式をルートに*/
			pNewExpression.m_pRootFactor = (MathOperator)pRightRootFactor;
		}

		return pNewExpression;
	}

	/**
	 * ルート要素を取得する.
	 * @return ルート要素
	 */
	public MathFactor getRootFactor() {
		return m_pRootFactor;
	}

	/**
	 * 式中第一変数を取得する.
	 * @return 式のはじめの変数
	 */
	public Math_ci getFirstVariable() {
		/*ルートが存在しない場合は取得不可*/
		if (m_pRootFactor == null) {
			return null;
		}

		/*演算子の場合*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			return ((MathOperator)m_pRootFactor).getFirstVariable();
		}
		/*オペランドの場合*/
		else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {

			/*Math_ciインスタンスの場合*/
			if (((MathOperand)m_pRootFactor).matches(eMathOperand.MOPD_CI)) {
				return (Math_ci)m_pRootFactor;
			}
		}

		return null;
	}
	
	/**
	 *数式IDを取得する
	 * @return 数式ID
	 */
	public long getExID(){
		return this.exID;
	}
	
	/**
	 * conditionのReference番号を返す
	 * @return conditionのReference番号
	 */
	public long getCondRef(){
		return this.condref;
	}
	/**
	 * 数式を比較する.
	 * @param pExpression 比較対象の式
	 * @return 比較結果
	 */
	public boolean matches(MathExpression pExpression) {
		return m_pRootFactor.matchesExpression(pExpression.m_pRootFactor);
	}

	/**
	 * 演算する.
	 * @throws MathException
	 */
	public void calculate() throws MathException {
		/*通常の式の場合のみ演算命令が可能*/
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			((MathOperator)m_pRootFactor).calculate();
		}
		/*例外*/
		else {
			throw new MathException("MathExpression","calculate",
						"can't calculate this expression");
		}
	}

	/**
	 * 文字列に変換する.
	 * @return 計算式文字列
	 * @throws MathException
	 */
	public String toLegalString() throws MathException {
		return m_pRootFactor.toLegalString();
	}
	
	/**
	 * Convert the MathExpression to MathML string.
	 * @return String MathML
	 * @throws MathException
	 */
	public String toMathMLString() throws MathException {
		return m_pRootFactor.toMathMLString();
	}
	
	/**
	 * 数式を展開する(Expand expression)
	 * ex. a(x+y) -> ax + ay
	 * @return 展開した数式 (Expanded expression)
	 * @throws MathException
	 */
	public MathExpression expand(MathOperand ci) throws MathException{
		MathExpression clone = this.toBinOperation();
	clone.getRootFactor().expand(ci);
		return clone;
	}
	
	/**
	 * 
	 * @return
	 * @throws MathException
	 */
	public MathExpression toBinOperation() throws MathException{
		MathExpression expr = this.semiClone();
		expr.m_pRootFactor =  expr.m_pRootFactor.toBinOperation();
		return expr;
	}
	
	/**
	 * Change to 0 == F(x) format
	 * @return
	 */
	public MathExpression toZeroEqualFormat(){
		try {
			m_pRootFactor = m_pRootFactor.toZeroEqualFormat();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public MathExpression removeExcessiveArithmeticOperator() throws MathException{
		m_pRootFactor = m_pRootFactor.removeExcessiveArithmeticOperator();
		return this;
	}
	
	/**
	 * Add attribute to equation (Math_eq operator)
	 * @param strAttrName name of the attribute to be added
	 * @param strAttrValue value of the attribute
	 * @throws MathException
	 */
	public void addAttribute(HashMap<String, String> HMapApplyAttr)
	throws MathException {
		/*ルートが演算子の場合*/
		
		if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			if (((MathOperator)m_pRootFactor).matches(eMathOperator.MOP_APPLY)) {
				((Math_apply)m_pRootFactor).setExpInfo(HMapApplyAttr);
			}
			else {
				((MathOperator)m_pRootFactor).addAttribute(HMapApplyAttr);
			}
		}
		/*例外の要素*/
		else {
			throw new MathException("MathExpression","addAttribute",
						"first factor should be Math_apply of Math_Eq");
		}
	}
	
	/**
	 * Vsitorパターンでのtraverse
	 * @param v
	 */
	public void traverse(Visitor v){
		m_pRootFactor.traverse(v);
	}

	public Integer compareFocusOnVariableIndex(MathExpression that,int indexPos) throws MathException{
		//indexPos 0:time, 1:x, 2:y, 3:z

		MathFactorStackingVisitor expr1_StackVisitor = new MathFactorStackingVisitor();
		MathFactorStackingVisitor expr2_StackVisitor = new MathFactorStackingVisitor();
		
		this.traverse(expr1_StackVisitor);
		that.traverse(expr2_StackVisitor);
		
		
			
		return compareMathFactorStack(expr1_StackVisitor.getStack(),expr2_StackVisitor.getStack(),indexPos);
		
	}
	/**
	 * 
	 * @param stack1
	 * @param stack2
	 * @param varName
	 * @param indexPos
	 * @return 同じ式ならばindexPosのIndexの差を返す，異なればnull
	 * @throws MathException 
	 */
	private Integer compareMathFactorStack(Stack<MathFactor> stack1,
			Stack<MathFactor> stack2, int indexPos) throws MathException {
		Integer indexDiff=null;
		
		//stackサイズがことなれば違う式の形
		if(stack1.size()!=stack2.size()){
			return null;
		}
		
		for(MathFactor factor1:stack1){
			MathFactor factor2=stack2.get(stack1.indexOf(factor1));
			
			if(factor1.getClass()!=factor2.getClass()){
				return null;
			}
			if((factor1 instanceof Math_ci )&&(factor2 instanceof Math_ci)){
				Math_ci ci1 = (Math_ci) factor1;
				Math_ci ci2 = (Math_ci) factor2;
				if(!ci1.getM_strPresentText().equals(ci2.getM_strPresentText())){
					return null;
				}else{ //変数名が同じ
					//Indexを持っているなら(定数は持っていない場合もある)
					if((ci1.getM_vecIndexListFactor().size()>indexPos) &&
							(ci2.getM_vecIndexListFactor().size()>indexPos)){
					
					//	System.out.println(this.getClass()+":"+ci1.getM_vecIndexListFactor().get(indexPos).toLegalString()+" "+ci2.getM_vecIndexListFactor().get(indexPos).toLegalString());
						
					Integer index1 = decode(ci1.getM_vecIndexListFactor().get(indexPos).toLegalString());
					Integer index2 = decode(ci2.getM_vecIndexListFactor().get(indexPos).toLegalString());
					if(indexDiff==null){	//一回目のMath_ci
						indexDiff= index2-index1;
					}else{					//二回目以降
						if(!indexDiff.equals(index2-index1)){//indexの差分が１回目と異なるなら別の式の形
							return null;
						}
					}
					}
				}
			}
			
		}
		return indexDiff;
	}
	
	/**
	 * Translate String to Integer  
	 * @param str
	 * @return Translated Integer
	 */
	private Integer decode(String str){
		return Integer.decode(str.replace(" ","").replace("(","").replace(")", ""));
	}
	
	
	public void replaceIndex(MathFactor replaceIndexFactor, int baseIndex,
			int indexPosition) {
		IndexReplacingVisitor indexReplaceVsitor = new IndexReplacingVisitor(replaceIndexFactor,baseIndex,indexPosition);
		this.traverse(indexReplaceVsitor);
	}
	
	public boolean serchIndexVaruable(String index, Math_ci mci) throws MathException {
		boolean flag = false;
		
		flag = ((MathOperator) m_pRootFactor).K_checkIndexVariable(index,mci);
		return flag;
	}
	//非線形数式判定メソッド
	public boolean getNonlinearFlag(){
		return nonlinear;
	}
	public void addNonlinearFlag(){
		nonlinear=true;
	}
	
	public int getExpressionNumfromApply() {
		int num = ((Math_apply)m_pRootFactor).getExpNum();
		return num;
	}
}