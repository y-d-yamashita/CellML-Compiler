package jp.ac.ritsumei.is.hpcss.cellMLonGPU.expansion;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.ExpandException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.GraphManipulator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecML;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;

/**
 *１CellML数式展開クラス.
 *@author m-ara
 */
public class SimpleExpansion {
	
	/*各解析器インスタンス*/
	protected CellMLAnalyzer m_pCellMLAnalyzer;
	protected RelMLAnalyzer m_pRelMLAnalyzer;
	protected TecMLAnalyzer m_pTecMLAnalyzer;
	protected RecMLAnalyzer m_pRecMLAnalyzer;
	 SimpleRecML m_pSimpleRecML;
	
	/*CellML導出変数と数式番号登録HashMap*/
	HashMap<Math_ci,Integer>  m_mapDerivedVariable;
	
	/*SimpleRecML登録済変数リスト*/
	Vector<Math_ci> vecRegisteredVar;

	/**
	 *数式展開インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 */
	public SimpleExpansion(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer) {
		this.m_pCellMLAnalyzer = pCellMLAnalyzer;
		this.m_pRelMLAnalyzer = pRelMLAnalyzer;
		this.m_pTecMLAnalyzer = pTecMLAnalyzer;
		/*SimpleRecML生成*/
		this.m_pSimpleRecML = new SimpleRecML();
		this.vecRegisteredVar = new Vector<Math_ci>();
	}

	/*-----プログラム構文取得メソッド-----*/
	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator#getSyntaxProgram()
	 */
	public SimpleRecML getSimpleRecML() 
			throws CellMLException, RelMLException, MathException, TranslateException, ExpandException, TableException {

		/*数式を生成し，取得*/
		Vector<MathExpression> vecExpressions = this.createExpressions();
		
		

		//-------------------------------------現状では式を表示するのみ
//		int nExpressionNum = vecExpressions.size();
//
//		/*出力開始線*/
//		System.out.println("[output]------------------------------------");
//
//		for (int i = 0; i < nExpressionNum; i++) {
//			System.out.println("ExID["+vecExpressions.get(i).getExID()+"] " +
//					"condref["+vecExpressions.get(i).getCondRef()+"]:"+
//					vecExpressions.get(i).toLegalString());
//		}
//		
//		System.out.println("End.");

		//-------------------------------------

		m_pSimpleRecML.setMathExpression(vecExpressions);
		
		/*プログラム構文を返す*/
		return m_pSimpleRecML;
	}

	/**
	 * 計算式部を生成し，ベクタを返す.
	 * @return 計算式ベクタ
	 * @throws CellMLException
	 * @throws RelMLException
	 * @throws MathException
	 * @throws TranslateException
	 * @throws TableException 
	 */
	private Vector<MathExpression> createExpressions()
	throws CellMLException, RelMLException, MathException, TranslateException ,ExpandException, TableException{
		//---------------------------------------------
		//式生成のための前処理
		//---------------------------------------------
		
		/*ベクタを初期化*/
		Vector<MathExpression> vecExpressions = new Vector<MathExpression>();
		
		/*CellMLファイルに最大マッチングを適用*/
		//現在は手動でファイルを設定
		m_mapDerivedVariable = new HashMap<Math_ci,Integer>();
		
//		//dx/dtをひとつの変数に変換する(未完成)
//		m_pCellMLAnalyzer.changeDifferentialParameterToOneVariable();
		
		/*Maximum matching*/
		this.doMaximumMatching();
		
		//print result of maximum matching
//		System.out.println("[result of maximum matching]-------");
//		for (Math_ci key : m_mapDerivedVariable.keySet()) {
//			System.out.println(key.toLegalString() + " = " + m_mapDerivedVariable.get(key));
//		}
		
		//---------------------------------------------
		//式の追加
		//---------------------------------------------
		/*数式数を取得*/
		int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();
		if(nExpressionNum == 0){
			throw new ExpandException("SimpleExpansion","createExpression",
					"no Expression in TecML");
		}
		
		//変換済のConditionNameと数式IDを登録
		HashMap<String, Integer> mapCondition = new HashMap<String, Integer>();
		
		//loopindex変換リスト作成
		HashMap<String, String> mapLoopindex = new HashMap<String,String>();
		for(int i=0; i < m_pTecMLAnalyzer.getM_vecStepVar().size(); i++){
			mapLoopindex.put(m_pTecMLAnalyzer.getM_vecStepVar().get(i).toLegalString(), String.valueOf(i));
			m_pSimpleRecML.setIndexHashMap(i, m_pTecMLAnalyzer.getM_vecStepVar().get(i).toLegalString());
			//SimpleRecMLに登録
			int num = m_pSimpleRecML.getM_HashMapStepVar().size();
			m_pSimpleRecML.setM_HashMapStepVar(m_pTecMLAnalyzer.getM_vecStepVar().get(i), num);
		}
		
		/*TecML order*/
		for (int i = 0; i < nExpressionNum; i++) {

			/*数式を取得*/
			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);
			
			/*数式のtypeがあれば取得*/
			String mathType = pMathExp.getExpInfo("type");
			int condref = -1;
			MathExpression pCondExp = null;
			
			/*type="final"であればCondition数式を抜き出す*/
			if(mathType!=null && mathType.equals("final")){
				//condition探索、存在すれば取り出す
				pCondExp  = ((MathOperator)pMathExp.getRootFactor()).searchCondition(pMathExp.getRootFactor());
			
				//condition数式は
				if(pCondExp != null){
					//RelML数式からcondition数式取得
					Vector<String[]> pvecCondInfo = m_pRelMLAnalyzer.getConditionInformation();
					for(int j=0; j<pvecCondInfo.size();j++){
						if(pvecCondInfo.get(j)[0].equals(pCondExp.toLegalString())){
							//すでに登録済みか確認
							if(!mapCondition.containsKey(pvecCondInfo.get(j)[0])){
								//Condition変換予定ベクタに追加
								mapCondition.put(pCondExp.toLegalString(),vecExpressions.size());
								/*condition数式ベクタはcondition情報をRelMLより取得し変換、数式ベクタに追加*/
								//RelML情報と変換
								pCondExp = m_pRelMLAnalyzer.getExpression(Integer.parseInt(pvecCondInfo.get(j)[2]));
								pCondExp.setExID(vecExpressions.size());
								//数式情報を追加（SimpleRecMLWriter用）
								pCondExp.addExpInfoToApply("num", String.valueOf(pCondExp.getExID()));
								pCondExp.addExpInfoToApply("type", "condition");
								//数式ベクタに追加
								vecExpressions.add(pCondExp);
							}
							//CondRefを記憶しておく
							condref = mapCondition.get(pvecCondInfo.get(j)[0]);
						}
					}
					
					if(condref == -1){
					//対応するCondition変数の記述がRelMLにない場合エラー処理
					throw new ExpandException("SimpleExpansion", "createExpression", 
							"Condition "+"\""+ pCondExp.toLegalString()+"\"" + " information is missing");
					}
				}
				
				else if(pCondExp == null){
					//数式にそもそもConditionがない場合エラー処理
					throw new ExpandException("SimpleExpansion", "createExpression", 
							"\""+ pMathExp.toLegalString()+"\"" + " don't have condition though it is type=\"final\" ");
				}
			}
			
			
			/*普通の数式の場合*/
			/*左辺式・右辺式取得*/
			MathExpression pLeftExp = pMathExp.getLeftExpression();
			MathExpression pRightExp = pMathExp.getRightExpression();
	
			if (pLeftExp == null || pRightExp == null) {
				throw new ExpandException("SimpleExpansion","createExpression",
							     "failed to divide expression: "+ pMathExp.toLegalString());
			};
	
			/*左辺変数取得*/
			Math_ci pLeftVar = pLeftExp.getFirstVariable();
				
			//-------------------------------------------
			//関数であるか、関数でないかで展開を分ける
			//-------------------------------------------
			boolean isFunc = pMathExp.isFunction();
				
			/*関数を含む*/
			if (isFunc) {
				//対応CellML数式ベクタ取得
					Vector<MathExpression> pVecCellExp = applyCellML(pLeftVar);
				
				for(MathExpression pExp : pVecCellExp){
					MathExpression pNewExp = pExp.createCopy();
					/*変数変換*/
					this.transformationOfCellMLVariable(pRightExp, pNewExp);
					//数式番号、Condition番号登録
					pNewExp.setExID(vecExpressions.size());
					pNewExp.setCondref(condref);
					//数式情報登録（SimpleRecMLWriter用）
					pNewExp.addExpInfoToApply("num", String.valueOf(pNewExp.getExID()));
						//元数式からtype,loopindexコピー
					if(pMathExp.getExpInfo("type") != null){
						pNewExp.addExpInfoToApply("type", pMathExp.getExpInfo("type"));
						pNewExp.addExpInfoToApply("loopnum", mapLoopindex.get(pMathExp.getExpInfo("loopindex")));
					}
					if(pNewExp.getCondRef() > 0){
						pNewExp.addExpInfoToApply("condref", String.valueOf(pNewExp.getCondRef()));
					}
					//数式ベクタに追加
					vecExpressions.add(pNewExp);
				}
					
			}
				
			else{
					
				int variableNum = m_pRelMLAnalyzer.getVecTecMLTransformation(pLeftVar).size();
				
				/*数式の出力*/
				for (int j = 0; j < variableNum; j++) {
	
						/*新たな計算式を生成*/
						MathExpression pNewExp = pMathExp.createCopy();
	
						/*変数変換*/
						this.transformationOfTecMLVariable(pNewExp, j);
	
						//数式番号、Condition番号登録
						pNewExp.setExID(vecExpressions.size());
						pNewExp.setCondref(condref);
						//数式情報登録（SimpleRecMLWriter用）
						pNewExp.addExpInfoToApply("num", String.valueOf(pNewExp.getExID()));
							//元数式からtypeコピー
						if(pMathExp.getExpInfo("type") != null){
							pNewExp.addExpInfoToApply("type", pMathExp.getExpInfo("type"));
							pNewExp.addExpInfoToApply("loopnum", mapLoopindex.get(pMathExp.getExpInfo("loopindex")));
						}
						if(pNewExp.getCondRef() > 0){
							pNewExp.addExpInfoToApply("condref", String.valueOf(pNewExp.getCondRef()));
						}
						
						/*数式ベクタに追加*/
						vecExpressions.add(pNewExp);				
						
						//type="init"式はinitend式を作成
						if(mathType!=null && mathType.equals("init")){
							MathExpression pInitEndExp = this.makeInitEndExp(pNewExp);
							
//							if(pInitEndExp == null){
//								throw new ExpandException("SimpleExpansion", "makeInitEndExp",
//										pMathExp.toLegalString() + " can't make initend expression.");
//							}
							
							//数式番号登録
							pInitEndExp.setExID(vecExpressions.size());
							//数式情報登録（SImpleRecMLWriter用）
							pInitEndExp.addExpInfoToApply("num", String.valueOf(pInitEndExp.getExID()));
							pInitEndExp.addExpInfoToApply("type", "initend");
							pInitEndExp.addExpInfoToApply("loopnum", mapLoopindex.get(pInitEndExp.getExpInfo("loopindex")));
							//数式ベクタに追加
							vecExpressions.add(pInitEndExp);
						}
				}
			}

		}
		
		
		return vecExpressions;
	}

	/**
	 * type="init"の数式に対して
	 *左辺変数のIndexに応じてinitendとなる数式を作成する
	 *indexにeqがある配列変数に対してinitend式を作成
	 * @throws MathException 
	 */
	public MathExpression makeInitEndExp(MathExpression pExp) throws MathException{
		//数式左辺変数取得
		Math_ci pVar = pExp.getLeftExpression().getFirstVariable();
		//左辺変数IndexList取得
		Vector<MathFactor> vecIndexList = pVar.getIndexList();
		MathExpression pInitEndExp = null;
		boolean flag = false;
		

		for(int i = 0; i< vecIndexList.size(); i++){
			MathFactor pFactor = vecIndexList.get(i);
			if(pFactor.matches(eMathMLClassification.MML_OPERATOR)){
		
				flag = ((MathOperator)pFactor).hasEqual();
				//Math_Eqをもつループ配列変数に対して
				if(flag){
					
					MathFactor pLeftIndex = ((MathOperator)pFactor).getLeftExpression();
					MathFactor pRightIndex = ((MathOperator)pFactor).getRightExpression();
					
					//元数式のIndexを右辺のみにする
					vecIndexList.set(i, pRightIndex);
					
					//新しい数式の作成
					Math_eq pMathEq =
						(Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
					MathFactor pLeftVar = pVar.createCopy();
					((Math_ci)pLeftVar).getIndexList().set(i, pLeftIndex);
					pMathEq.addFactor(pLeftVar);
					MathFactor pRightVar = pVar.createCopy();
					((Math_ci)pRightVar).getIndexList().set(i, pRightIndex);
					pMathEq.addFactor(pRightVar);
					
					Math_apply pMathApply =
						(Math_apply)MathFactory.createOperator(eMathOperator.MOP_APPLY);
					pMathApply.addFactor(pMathEq);
					pInitEndExp = new MathExpression(pMathApply);
					pInitEndExp.addExpInfoToApply("loopindex", pLeftIndex.toLegalString());
				}
			}
		}
		return pInitEndExp;
	}

	/*-----関数展開・変数置換メソッド-----*/

	/**
	 * 対応するCellML数式に変換する
	 * @param pLeftvar　導出左辺変数
	 * @return pNewExpression 数式リスト
	 * @throws MathException 
	 * @throws ExpandException 
	 */
	private Vector<MathExpression> applyCellML(Math_ci pLeftVar) throws MathException, ExpandException{
		Vector<MathExpression> pNewExpression = new Vector<MathExpression>();
		
		/*TecML左辺値(pLeftVar)に対応するCellML変数VectorをHashMapから取得する*/
		Vector<MathOperand> pVecTransformation = m_pRelMLAnalyzer.getVecTecMLTransformation(pLeftVar);
		if(pVecTransformation == null || pVecTransformation.size()==0){
			throw new ExpandException("SimpleExpansion", "applyCellML", 
					"can't find correspond CellML variables to "  + "\"" + pLeftVar.toLegalString() + "\"");
		}
		
		/*vector内のCellML変数の数だけ式を取得*/
		for(MathFactor pFactor : pVecTransformation){
			
			/*名前の取得と分解*/
			String strVarName = pFactor.toLegalString();
			//コンポーネント名切り分け
			int nIndexPos = strVarName.indexOf(".");
			if(nIndexPos > 0){
				strVarName = strVarName.substring(nIndexPos+1);
			}
			
			/*CellML導出変数リストから対応する数式IDを取得*/
			int pExID = getDerivedVariable(strVarName);
			
			if(pExID<0){
				throw new ExpandException("SimpleExpansion", "applyCellML", 
						"can't find CellML expression which derived "+ "\"" + strVarName + "\"");
			}else{
				/*ExIDから数式を取り出す*/
				MathExpression pExp = m_pCellMLAnalyzer.getExpression(pExID);
				pNewExpression.add(pExp);
			}
		}
		
		return pNewExpression;
	}
	
	/**
	 * CellML導出変数リストから対応する数式IDを取得
	 * @throws MathException 
	 */
	private int getDerivedVariable(String pStr) throws MathException{
		int exID = -1;
		
		/*HashMapから検索*/
		//キーを取得する
        Set<Math_ci> keys = m_mapDerivedVariable.keySet();

        // mapの中身すべてと比較
        for(Math_ci key : keys) {
        	if(key.toLegalString().equals(pStr)){
        		exID = m_mapDerivedVariable.get(key);
        	}
        }
		
        return exID;
		
	}
	
	/**
	 * TecML数式中変数の置換をする.
	 * @param pExpression 数式インスタンス
	 * @throws MathException
	 * @throws ExpandException 
	 */
	private void transformationOfTecMLVariable(MathExpression pExpression, int index)throws MathException, ExpandException {
		
		//数式中から変数取得
		Vector<Math_ci> pAllVariables = new Vector<Math_ci>();
		pExpression.getAllVariables(pAllVariables);
		
		for(int i=0; i<pAllVariables.size(); i++){
		
			//対象変換TecML変数
			Math_ci pTargetVar = pAllVariables.get(i);
			
			//RelMLから対応する変数ないし値を取得
			MathFactor pCorrespondVar;
			if(m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar) == null|| m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar).size() == 0){
				throw new ExpandException("SimpleExpansion", "applyCellML", 
						"can't find correspond CellML variables to "  + "\"" + pTargetVar.getName() + "\"");
			}
			else  if(m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar).size() == 1){
				pCorrespondVar = m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar).get(0);
			}else
				pCorrespondVar = m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar).get(index);
			
			
			//変数の場合
			if(((MathOperand)pCorrespondVar).matches(eMathOperand.MOPD_CI)){
				Math_ci pnewVariable = this.makeNewVariable((Math_ci)pCorrespondVar, pTargetVar);
				/*変数変換*/
				pExpression.replace(pTargetVar, pnewVariable);
			}
			//定数の場合
			else if(((MathOperand)pCorrespondVar).matches(eMathOperand.MOPD_CN)){
				pExpression.replace(pTargetVar, pCorrespondVar);
			} 
		}
	
	}
	
	/**
	 * CellML数式中変数の置換をする.
	 * @param pExpression 数式インスタンス
	 * @throws MathException
	 * @throws ExpandException 
	 */
	private void transformationOfCellMLVariable(MathExpression pTecMLExpression, MathExpression pExpression)throws MathException, ExpandException {
		
		//TecML数式中から変数取得
		Vector<Math_ci> pTecAllVariables = new Vector<Math_ci>();
		pTecMLExpression.getAllVariables(pTecAllVariables);
		
		//CellML数式中から変数取得
		Vector<Math_ci> pCellAllVariables = new Vector<Math_ci>();
		pExpression.getAllVariables(pCellAllVariables);
		
		for(int i=0; i<pTecAllVariables.size(); i++){
			
			//対象変換TecML変数
			Math_ci pTargetVar = pTecAllVariables.get(i);
			
			//RelMLから対応する変数ないし値を取得
			Vector<MathOperand> pCorrespondVar = m_pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar);
			if(pCorrespondVar == null|| pCorrespondVar.size() == 0){
				throw new ExpandException("SimpleExpansion", "transformationOfCellMLVariable", 
						"can't find correspond CellML variables to "  + "\"" + pTargetVar.getName() + "\"");
			}else{
				//1つずつ取り出して数式中にあれば変数変換
				for(MathFactor pCorVar : pCorrespondVar){
					for(Math_ci pCellVariable : pCellAllVariables){
						if(pCellVariable.getName().equals(pCorVar.getName())){
							Math_ci pnewVariable = this.makeNewVariable((Math_ci)pCorVar, pTargetVar);
							/*変数変換*/
							pExpression.replace(pCellVariable, pnewVariable);
							
						}
					}
				}
			}
			
		}
		
	}
	
	/**
	 * 新しい変数を作成する
	 * @param pCellMLVar
	 * @param pTecMLVar
	 * @return
	 * @throws MathException
	 */
	private Math_ci makeNewVariable(Math_ci pCellMLVar, Math_ci pTecMLVar) throws MathException{
		
		/*新しい変数名を作成*/
		String pCellMLVarName = pCellMLVar.getName().replaceAll("Main.", "Main_");
		String newName = pCellMLVarName + "_" + pTecMLVar.getName();
		
		/*Initial value を取得する 初期値0.00*/
		String initialValue = "0.00";
		
		if(pCellMLVar.getCorrespondTag() == eRelMLTag.RTAG_CORRCELLML){
			try {
				initialValue = m_pCellMLAnalyzer.getInitialValue(pCellMLVar);
			} catch (TableException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}else{
			if(pCellMLVar.isInitialized()){
				initialValue = Double.toString(pCellMLVar.getValue());
			}
			newName = pCellMLVarName;
		}
		
		/*変数名から変数インスタンス生成*/
		Math_ci pnewVariable =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, newName , Double.parseDouble(initialValue));
		
		
		/*Indexを変換してCopy*/
		Vector<MathFactor> pIndexList = pTecMLVar.getIndexList();
		pnewVariable.addIndexList(pIndexList);
		
		//SimpleRecMLクラスに登録メソッドへ
		this.putSimpleRecML(pnewVariable, pTecMLVar);
				
		return pnewVariable;
	}
	
//	/**
//	 * IndexList変数変換対象探索メソッド(Index変数もRelMLから変更できるようにしようとしたんですが断念)
//	 * @throws MathException 
//	 */
//	public MathFactor  transformationOfIndexList(MathFactor pFactor) throws MathException{
//		MathFactor correspond = null;
//		
//		if(pFactor.matches(eMathMLClassification.MML_OPERAND)){
//			if(((MathOperand)pFactor).matches(eMathOperand.MOPD_CI)){
//				//RelMLから対応する変数ないし値を取得
//				Vector<MathOperand> pCorrespondVar = m_pRelMLAnalyzer.getVecTecMLTransformation((Math_ci)pFactor);
//				if(pCorrespondVar != null){
//					if(pCorrespondVar.get(0).matches(eMathOperand.MOPD_CI)){
//						if(((Math_ci)(pCorrespondVar.get(0))).getCorrespondTag() == eRelMLTag.RTAG_CORRRELML){
//							correspond = pCorrespondVar.get(0);
//						}
//					}
//				}
//			}
//		}
//		else{
//			Vector<Math_ci> pvecVariable;
//			((MathOperator)pFactor).getVariables(pFactor, pvecVariable);
//			if(){
//				((MathOperator)pFactor).findObj();
//			}
//		}
//		return correspond;
//	}
	
	/**
	 *新しい変数を SimpleRecMLクラスに登録する
	 * @throws MathException 
	 *
	 */
	public void putSimpleRecML(Math_ci pVariable, Math_ci pTecMLVariable) throws MathException{
		
		//IndexListを抜いた新たな変数を作成
		Math_ci pnewVariable = (Math_ci)pVariable.createCopy();
		pnewVariable.clearIndexList();
		if(pVariable.isInitialized()){
			pnewVariable.setValue(pVariable.getValue());
		}
		
		//重複判定
		if (! vecRegisteredVar.contains(pnewVariable)){
				
			vecRegisteredVar.add(pnewVariable);
				
			/*Recurvarなら*/
			if(m_pTecMLAnalyzer.isRecurVar(pTecMLVariable)){
				m_pSimpleRecML.setM_HashMapRecurVar(pnewVariable, 0);
			}
				
			/*Arithvarなら*/
			else if(m_pTecMLAnalyzer.isArithVar(pTecMLVariable)){
				m_pSimpleRecML.setM_HashMapArithVar(pnewVariable, 0);
			}
				
			/*Constvarなら*/
			else if(m_pTecMLAnalyzer.isConstVar(pTecMLVariable)){
				m_pSimpleRecML.setM_HashMapConstVar(pnewVariable, 0);
			}
			
		}
		
	}
	
	/**
	 * CellMLに最大マッチングを適用する
	 * @throws TableException 
	 * @throws MathException 
	 */
	public void doMaximumMatching() throws MathException, TableException{
		
		/*Attach information about assignment and reference equations*/
		//変数が各数式の右辺左辺どちらにあるか探す
		m_pCellMLAnalyzer.setLeftsideRightsideVariable();

		/*Set variable type (ex. recvar, constvar)*/
		//変数のタイプを設定する
		try {
			m_pCellMLAnalyzer.setRefVariableType(m_pTecMLAnalyzer, m_pRelMLAnalyzer);
		} catch (RelMLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		GraphManipulator graphManipulator= new GraphManipulator();
		BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph=null;
		PairList<RecMLVertex,RecMLVertex> resultTestMaximumMatching=null;
		
		CellMLEquationAndVariableContainer container2 = 
				new CellMLEquationAndVariableContainer(m_pCellMLAnalyzer);
		
		
		/* Create a bipartite graph */
		try {		
			resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph_Cell(container2);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		/*Maximum matching*/
		try {
			resultTestMaximumMatching = 
					graphManipulator.maximumMatching(resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		/*結果からHashMapを作成*/
		this.setAnalyzedData(resultTestMaximumMatching);
	}
	
	/**
	 * 最大マッチングの結果をハッシュマップに登録
	 * @param resultMaximumMatching
	 * @throws MathException
	 * @throws TableException
	 */
	public void setAnalyzedData(PairList<RecMLVertex,RecMLVertex> resultMaximumMatching) 
			throws MathException, TableException{
		
		VariableTable m_pVariableTable = m_pCellMLAnalyzer.getVariableTable();
		for(Pair<RecMLVertex, RecMLVertex> m_Pair : resultMaximumMatching){
			int variableID = m_Pair.getFirst().getVariableID();
			int equationID = m_Pair.getSecond().getExpressionID();
			//debug
//			System.out.println(variableID + " " + equationID);
			
			String variableName = m_pVariableTable.getVariableReference(variableID).getVariableName();
			Math_ci variable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, variableName);
			m_mapDerivedVariable.put(variable,equationID);
			
		}

	}
	
}