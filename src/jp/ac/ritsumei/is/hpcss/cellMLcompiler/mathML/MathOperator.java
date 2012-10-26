package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子クラス.
 */
public abstract class MathOperator extends MathFactor {

	/**被演算要素ベクタ*/
	protected Vector<MathFactor> m_vecFactor;

	/**演算子種類*/
	protected eMathOperator m_operatorKind;

	/**有効判定*/
	protected boolean m_bEnable;

	/**必要被演算要素個数*/
	protected int m_unMinFactorNum;
	
	/* attribute string list */
	protected String[] attr;

	/**
	 * MathML演算子インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param operatorKind 演算子種類
	 * @param unMinFactorNum 必要被演算要素個数
	 */
	public MathOperator(String strPresentText, eMathOperator operatorKind,
			int unMinFactorNum, String[] strAttr) {
		super(strPresentText,eMathMLClassification.MML_OPERATOR);
		m_vecFactor = new Vector<MathFactor>();
		m_operatorKind = operatorKind;
		m_bEnable = true;
		m_unMinFactorNum = unMinFactorNum;
		attr = strAttr;
	}

	public MathOperator(String strPresentText, eMathOperator operatorKind,
			int unMinFactorNum) {
		this(strPresentText, operatorKind, unMinFactorNum, null);
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#getValue()
	 */
	public double getValue() throws MathException {
		/*演算結果を返す*/
		return calculate();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#setValue(double)
	 */
	public void setValue(double dValue) throws MathException {
		throw new MathException("MathOperator","setValue",
					"can't set value on operator");
	}
	
	
	/* 	(非 Javadoc)	autor: n-washio
	 *	add six methods for LeftSideTransposition for code generator
	 *
	 *	1.getOperatorKind()		演算子の種類を返す
	 *	2.getAttribute()		数式の属性を返す
	 *	3.getUnderFactor()		直下の要素を返す
	 *	4.getChildFactor(int n)	n番目の子要素を返す
	 *	5.getChildFactorNum()	保持している子要素の数を返す
	 *	6.searchVariablePosition_inChildFactor(Math_ci)	変数がどの子要素に含まれるかを返す
	 *
	*/
	
	/*-----演算子の種類を返すメソッド-----*/
	public eMathOperator getOperatorKind() {
		return m_operatorKind;
	}
	
	/*-----数式の属性を返すメソッド-----*/
	public String[] getAttribute(){
		return attr;
	}
	
	/*-----直下の要素を返すメソッド-----*/
	public MathFactor getUnderFactor() {
		MathFactor underFactor = null;
		for(int i=0;i<m_vecFactor.size();i++){
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERATOR)){
				underFactor= ((MathOperator)m_vecFactor.get(i));break;
			}
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERAND)){
				underFactor= ((MathOperand)m_vecFactor.get(i));break;
			}
		}
		return (MathFactor) underFactor;
	}
	
	/*-----n番目の子要素を返すメソッド-----*/
	public MathFactor getChildFactor(int n) throws MathException {
		MathFactor underFactor = null;
		int count=0;
		for(int i=0;i<m_vecFactor.size();i++){	
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERATOR)){
				count++;
				if(count==n) underFactor = ((MathOperator)m_vecFactor.get(i));
			}
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERAND)){
				count++;
				if(count==n) underFactor = ((MathOperand)m_vecFactor.get(i));
			}
		}
		return (MathFactor) underFactor;
	}
	
	/*-----保持している子要素の数を返すメソッド-----*/
	public int getChildFactorNum() throws MathException {
		int count=0;	
		for(int i=0;i<m_vecFactor.size();i++){
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERATOR)){
				count++;
			}
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERAND)){
				count++;
			}
		}
		return count;
	}
	
	/*-----変数が何番目の子要素に含まれるかを返すメソッド-----*/
	public int searchVariablePosition(Math_ci val) throws MathException  {
		int count=0;
		int val_num = 0;
		for(int i=0;i<m_vecFactor.size();i++){
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERATOR)){
				count++;
				MathOperator underOperator= ((MathOperator)m_vecFactor.get(i));
				Vector<Math_ci> pVec= new Vector<Math_ci>();
				underOperator.getVariables(underOperator, pVec);
				
				for(int j=0;j<pVec.size();j++){
					if(pVec.get(j).matches(val)){
						val_num=count;break;
					}
				}
			}
			if(m_vecFactor.get(i).matches(eMathMLClassification.MML_OPERAND)){
				if(((MathOperand)(m_vecFactor.get(i))).matches(eMathOperand.MOPD_CI)){
					count++;
					if(val.getName().equals(((Math_ci)m_vecFactor.get(i)).getName())){
						val_num=count;break;
					}
				}else{
					count++;
				}
			}
		}
		return val_num;
	}
	
	
	/**
	 * 要素を追加する.
	 * @param pFactor 追加要素
	 */
	public void addFactor(MathFactor pFactor) {
		/*ベクタへの追加*/
		m_vecFactor.add(pFactor);
	}

	/**
	 * インスタンス有効判定を設定する.
	 * @param bEnable 有効無効フラグ
	 */
	public void setEnable(boolean bEnable) {
		m_bEnable = bEnable;
	}

	/**
	 * インスタンス有効か判定する.
	 * @return 有効判定
	 */
	public boolean isEnable() {
		return m_bEnable;
	}

	/**
	 * 要素所持か判定する.
	 * @return 要素所持判定
	 */
	public boolean hasFactor() {
		return m_vecFactor.size() > 0;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#createCopy()
	 */
	public MathFactor createCopy() throws MathException {
		/*関数の場合*/
		if(this.matches(eMathOperator.MOP_FN)){
			return ((Math_fn)this).createCopy();
		}

		/*その他の演算子*/
		else {

			/*演算子の複製*/
			MathOperator newOperator = MathFactory.createOperator(m_operatorKind);

			/*すべての子要素を複製*/
			for (MathFactor it: m_vecFactor) {
				newOperator.addFactor(it.createCopy());
			}

			return newOperator;
		}
	}

	/**
	 * 数式を置換する.
	 * @param pOldOperand 置換対象のオペランド
	 * @param pNewFactor 置換後の数式
	 */
	public void replace(MathOperand pOldOperand, MathFactor pNewFactor) {
		/*関数の場合(ここで一致する場合は置換対象ではない．*/
		//if(this->matches(MOP_FN)){
		//	return;
		//}

		/*すべての要素を調べる*/
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);

			/*オペランドの場合*/
			if (it.matches(eMathMLClassification.MML_OPERAND)) {

				/*オペランドの置換*/
				if (((MathOperand)it).matches(pOldOperand)) {
					m_vecFactor.set(i, pNewFactor);
				}
			}
			/*オペレータの場合*/
			else if (it.matches(eMathMLClassification.MML_OPERATOR)) {
				/*関数の場合*/
				if (((MathOperator)it).matches(eMathOperator.MOP_FN)) {

					/*関数の置換*/
					if (((Math_fn)it).matches(pOldOperand)) {
						m_vecFactor.set(i, pNewFactor);
					}
				}

				/*その他の場合*/
				else {
					/*再帰呼び出し*/
					((MathOperator)it).replace(pOldOperand,pNewFactor);
				}
			}
		}
	}

	/**
	 * 数式を置換する.
	 * 関数置換用オーバーロード
	 * こちらは引数の一致まで検査
	 * @param pOldFunction 置換対象の関数
	 * @param pNewFactor 置換後の数式
	 */
	public void replace(Math_fn pOldFunction, MathFactor pNewFactor) {
		/*関数の場合(ここで一致する場合は置換対象ではない．*/
		//if(this->matches(MOP_FN)){
		//	return;
		//}

		/*すべての要素を調べる*/
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);

			/*オペレータの場合*/
			if (it.matches(eMathMLClassification.MML_OPERATOR)) {

				/*関数の場合*/
				if (((MathOperator)it).matches(eMathOperator.MOP_FN)) {

					/*関数の置換*/
					if (((Math_fn)it).matches(pOldFunction)) {
						m_vecFactor.set(i, pNewFactor);
					}
				}

				/*その他の場合*/
				else {
					/*再帰呼び出し*/
					((MathOperator)it).replace(pOldFunction,pNewFactor);
				}
			}
		}
	}
	
	/**
	 *selector時apply削除用
	 */
	public void replace(int i, MathFactor pNewFactor) {
			m_vecFactor.set(i, pNewFactor);
	}
	
	/**
	 * Selector内探索
	 */
	public void changeSelectorInteger(MathFactor rootFactor){
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);
			if(it.matches(eMathMLClassification.MML_OPERATOR)){
				/*selectorなら*/
				if(((MathOperator)it).matches(eMathOperator.MOP_SELECTOR)){
					((MathOperator)it).changeIndexInteger();
				}else{
					((MathOperator)it).changeSelectorInteger(it);
				}
			}
		}
	}
	
	/**
	 * Index要素のcnをIntegerに変える
	 * */
	public void changeIndexInteger(){
		/*子要素をたどる*/
		for(MathFactor it : m_vecFactor){
				if(it.matches(eMathMLClassification.MML_OPERATOR)){
					((MathOperator)it).changeIndexInteger();
				}else if(it.matches(eMathMLClassification.MML_OPERAND)){
					/*cnなら*/
					if(((MathOperand)it).matches(eMathOperand.MOPD_CN)){
					((Math_cn)it).changeType();
				}
			}
		}
	}
	
	/**
	 * condition部分を抜き取る
	 */
	public MathExpression searchCondition(MathFactor rootFactor){
		MathExpression condExp = null;
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);
			if(it.matches(eMathMLClassification.MML_OPERATOR)){
				/*conditionなら*/
				if(((MathOperator)it).matches(eMathOperator.MOP_CONDITION)){
					/*conditionの子要素（抜き取る数式）を取り出しておく*/
					condExp = new MathExpression();
					/*conditionタグ以下のapplyから新しい数式とする*/
					condExp.addOperator((MathOperator)((MathOperator)it).m_vecFactor.elementAt(0));
					
					/*conditionの親要素検索*/
					MathOperator parent = findParentOperator((MathOperator)rootFactor, it);
					int setNum = parent.findObj((MathOperator)it);
					/*conditionを削除*/
					try {
						parent.removeFactor(setNum);
					} catch (MathException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
		}
		return condExp;
	}
	
	/**
	 * Selectorを削除する
	 */
	public void removeSelector(MathFactor rootFactor){
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);
			if(it.matches(eMathMLClassification.MML_OPERATOR)){
				/*selectorなら*/
				if(((MathOperator)it).matches(eMathOperator.MOP_SELECTOR)){
					/*selectorの親要素検索*/
					MathOperator parent = findParentOperator((MathOperator)rootFactor, it);
					int setNum = parent.findObj((MathOperator)it);
					/*selectorの子要素を取得*/
					Math_ci selectorFactor = null;
					for(MathFactor it2 : ((MathOperator)it).m_vecFactor){
						if(selectorFactor == null){
							/*１つ目ならselector親要素につなげる*/
							parent.replace(setNum,it2);
							MathOperand temp = (MathOperand)it2;
							selectorFactor = (Math_ci)temp;
						}else{
							/*２つ目以降なら１つ目の子要素につなげる*/
							selectorFactor.addIndexList(it2);
						}
					}
				}else{
					((MathOperator)it).removeSelector(rootFactor);
				}
			}
		}
	}
	
	/**
	 * 親Operatorを探す
	 */
	public MathOperator findParentOperator(MathOperator beforeOperator, MathFactor findOperator){
		MathOperator parent = null;
		for (int i = 0; i < beforeOperator.m_vecFactor.size(); i++) {
			if(parent == null){
				MathFactor it = beforeOperator.m_vecFactor.get(i);
				if(it.matches(eMathMLClassification.MML_OPERATOR)){
					if (it == findOperator) {
						return beforeOperator;
					}else{
						parent =  findParentOperator((MathOperator)it,findOperator);
					}
				}
			}
		}
		return parent;
	}
	/**
	 * 指定する非演算子が何番目にあるかintを返す
	 */
	public int findObj(MathOperator obj){
		int number = 0;
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);
			if(it == obj){
				number = i;
			}
		}
		return number;
	}
	
	/**
	 * 数式中のvariablesを取得する(selectorを考慮する)
	 */
	public void getVariables(MathFactor rootFactor, Vector<Math_ci> pVec) throws MathException{
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it = m_vecFactor.get(i);
			if(it.matches(eMathMLClassification.MML_OPERATOR)){
				((MathOperator)it).getVariables(it, pVec);
			}else if(it.matches(eMathMLClassification.MML_OPERAND)){
				if(((MathOperand)it).matches(eMathOperand.MOPD_CI)){
					boolean flag = true;
					/*重複判定*/
					String string1 = it.toLegalString();
					String string2 = null;
					
					for(Math_ci it2 : pVec){
					
						string2 = it2.toLegalString();
						
						if(string1.equals(string2))
								flag = false;
					}
					/*重複なしなら追加*/
					if(flag){
						MathFactor pVariable = ((Math_ci)it).createCopy();
						pVec.add((Math_ci)pVariable);
					}
				}
			}
		}
	}
	
	/**
	 * 置換指定ファクタを取得する.
	 * @param pSearchOperand 検索関数オペランド
	 * @param pvecDstFunctions 検索結果取得先
	 */
	public void searchFunction(MathOperand pSearchOperand,
			Vector<Math_fn> pvecDstFunctions) {
		/*検索関数に一致する場合*/
		if (this.matches(eMathOperator.MOP_FN)
				&& ((Math_fn)this).matches(pSearchOperand)) {
			pvecDstFunctions.add((Math_fn)this);
		}

		/*その他の演算子*/
		else{
			/*すべての要素を調べる*/
			for (MathFactor it: m_vecFactor) {

				/*演算子の場合*/
				if (it.matches(eMathMLClassification.MML_OPERATOR)) {

					/*再帰呼び出し*/
					((MathOperator)it).searchFunction(pSearchOperand,pvecDstFunctions);
				}
			}
		}
	}

	/**
	 * 未知変数を探索する.
	 * @param stcOperatorTracks 数式ツリー探索時に発見した演算子を保存しておくスタック
	 * @param vecUnknownVar 未知変数リストを保持するベクタ
	 * @return 見つからない場合	/ NULL, 未知変数発見時	/ 未知変数インスタンス
	 */
	public MathOperand searchUnknownVariable(Stack<OperatorTracks> stcOperatorTracks,
			Vector<MathOperand> vecUnknownVar) {
		/*四則演算子以外は探さない*/
		if (!this.matches(eMathOperator.MOP_PLUS)
				&& !this.matches(eMathOperator.MOP_MINUS)
				&& !this.matches(eMathOperator.MOP_TIMES)
				&& !this.matches(eMathOperator.MOP_DIVIDE)) {
			return null;
		}

		/*すべての要素を調べる*/
		for(int nOperatorNum = 0; nOperatorNum < m_vecFactor.size(); nOperatorNum++) {
			MathFactor it1 = m_vecFactor.get(nOperatorNum);

			/*演算子の場合*/
			if(it1.matches(eMathMLClassification.MML_OPERATOR)){

				/*スタックに探索演算子情報を追加*/
				OperatorTracks tracks = new OperatorTracks();
				tracks.pMathOperator = this;
				tracks.unEdgeNumber = nOperatorNum;
				stcOperatorTracks.push(tracks);

				/*再帰呼び出し*/
				MathOperand pUnknownVar =
					((MathOperator)it1).searchUnknownVariable(stcOperatorTracks,vecUnknownVar);

				/*未知変数が見つかれば戻す*/
				if(pUnknownVar != null){
					return pUnknownVar;
				}
			}
			/*被演算子の場合*/
			else if(it1.matches(eMathMLClassification.MML_OPERAND)){

				/*すべての未知変数を調べる*/
				for (MathOperand it2: vecUnknownVar) {

					/*未知変数との一致判定*/
					if (it2.matches((MathOperand)it1)) {

						/*未知変数を戻す*/
						return (MathOperand)it1;
					}
				}
			}
		}

		/*未知変数を検出できなかった*/
		return null;
	}

	/**
	 * 数式要素を削除する.
	 * @param unFactorNum 何番目の要素を削除するか
	 * @throws MathException
	 */
	public void removeFactor(int unFactorNum) throws MathException {
		/*削除しようとした要素が存在しない場合*/
		if(unFactorNum >= m_vecFactor.size()){

			/*例外を投げる*/
			throw new MathException("MathOperator","removeFactor",
					"missing factor is being removed");
		}

		/*要素の削除*/
		m_vecFactor.remove(unFactorNum);

		/*必要最低限の被演算要素があるか*/
		if (m_vecFactor.size() < m_unMinFactorNum) {

			/*最低限の被演算要素がなければ無効化*/
			this.setEnable(false);
		}
	}

	/**
	 * removeFactor後の数式ツリーを修復する.
	 * 仮想メソッドであり，演算子によって処理が異なる
	 * @param unRemovedFactorNum 何番目の要素が削除されたか
	 */
	public void restoreFactor(int unRemovedFactorNum) {
		/*すべての要素を調べる*/
		for (MathFactor it: m_vecFactor) {

			/*演算子をチェック*/
			if (it.matches(eMathMLClassification.MML_OPERATOR)) {

				/*有効でないノードを削除する*/
				if (!((MathOperator)it).isEnable()) {

					/*子ノードの削除*/
					m_vecFactor.remove(it);
				}
			}
		}
	}

	/**
	 * オブジェクトを比較する.
	 * @param pOperator 演算子
	 * @return 比較結果
	 */
	public boolean matches(MathOperator pOperator) {
		return m_operatorKind == pOperator.m_operatorKind;
	}

	/**
	 * オブジェクトを比較する.
	 * @param operatorKind 演算子種類
	 * @return 比較結果
	 */
	public boolean matches(eMathOperator operatorKind) {
		return m_operatorKind == operatorKind;
	}

	/**
	 * オブジェクトを比較する.
	 * @param pOperand オペランド
	 * @return 比較結果
	 */
	public boolean matches(MathOperand pOperand) {
		return false;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#matchesExpression(jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor)
	 */
	public boolean matchesExpression(MathFactor pFactor) {
		/*比較要素が演算子でなければ終了*/
		if (!pFactor.matches(eMathMLClassification.MML_OPERATOR)) {
			return false;
		}

		/*要素ベクタサイズ比較*/
		if (((MathOperator)pFactor).m_vecFactor.size() != m_vecFactor.size()) {
			return false;
		}

		/*すべての要素を比較*/
		for (int i = 0; i < m_vecFactor.size(); i++) {
			MathFactor it1 = m_vecFactor.get(i);
			MathFactor it2 = ((MathOperator)pFactor).m_vecFactor.get(i);
			if (!it1.matchesExpression(it2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 左辺式を取得する.
	 * @return 左辺式
	 */
	public MathFactor getLeftExpression() {
		/*=演算子の場合*/
		if (this.matches(eMathOperator.MOP_EQ)) {

			/*左辺式を返す*/
			return m_vecFactor.get(0);
		}
		/*その他の演算子の場合*/
		else {
			if (m_vecFactor.size() > 0) {

				/*その他の場合*/
				if (m_vecFactor.get(0).matches(eMathMLClassification.MML_OPERATOR)) {
					return ((MathOperator)m_vecFactor.get(0)).getLeftExpression();
				}
				/*演算子の場合*/
				else {
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * 右辺式を取得する.
	 * @return 右辺式
	 */
	public MathFactor getRightExpression() {
		/*=演算子の場合*/
		if (this.matches(eMathOperator.MOP_EQ)) {

			/*右辺式を返す*/
			return m_vecFactor.get(1);
		}
		/*その他の演算子の場合*/
		else {
			if (m_vecFactor.size() > 0) {

				/*演算子の場合*/
				if (m_vecFactor.get(0).matches(eMathMLClassification.MML_OPERATOR)) {
					return ((MathOperator)m_vecFactor.get(0)).getRightExpression();
				}
				/*その他の場合*/
				else {
					return null;
				}
			}
		}

		return null;
	}
	
	/**
	 * 式中第一変数を取得する.
	 * @return 式のはじめの変数
	 */
	public Math_ci getFirstVariable() {
		/*すべての要素を調べる*/
		for (MathFactor it: m_vecFactor) {

			/*オペランドの場合*/
			if (it.matches(eMathMLClassification.MML_OPERAND)
					&& ((MathOperand)it).matches(eMathOperand.MOPD_CI)) {
				return (Math_ci)it;
			}

			/*オペレータの場合*/
			else if (it.matches(eMathMLClassification.MML_OPERATOR)) {

				/*diffの場合*/
				if (((MathOperator)it).matches(eMathOperator.MOP_DIFF)) {
					return (Math_ci)(((MathOperator)it).m_vecFactor.get(1));
				}

				/*再帰呼び出し*/
				Math_ci pTmpVar = ((MathOperator)it).getFirstVariable();

				/*結果が得られていれば返す*/
				if (pTmpVar != null) {
					return pTmpVar;
				}
			}
			/*その他の要素は現状調査する必要がない*/
			else {
			}
		}

		/*変数が見つからなかった*/
		return null;
	}

	/**
	 * 演算する.
	 * @return 演算結果値
	 * @throws MathException
	 */
	public abstract double calculate() throws MathException;

}
