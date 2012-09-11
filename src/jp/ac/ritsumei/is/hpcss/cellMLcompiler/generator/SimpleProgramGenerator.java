package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.tecML.TecMLDefinition.eTecMLVarType;

/**
 * テスト用プログラム構文生成クラス.
 */
public class SimpleProgramGenerator extends ProgramGenerator {

	/**
	 * テスト用プログラム構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 */
	public SimpleProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer) {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
	}

	/*-----プログラム構文取得メソッド-----*/
	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator#getSyntaxProgram()
	 */
	public SyntaxProgram getSyntaxProgram()
	throws MathException, CellMLException, RelMLException, TranslateException {
		/*プログラム構文生成*/
		SyntaxProgram pSynProgram = null;

		/*数式を生成し，取得*/
		Vector<SyntaxExpression> vecExpressions = this.createExpressions();

		//-------------------------------------現状では式を表示するのみ
		int nExpressionNum = vecExpressions.size();

		/*出力開始線*/
		System.out.println("[output]------------------------------------");

		for (int i = 0; i < nExpressionNum; i++) {
			System.out.println(vecExpressions.get(i).toLegalString());
		}

		//-------------------------------------

		/*プログラム構文を返す*/
		return pSynProgram;
	}

	/**
	 * 計算式部を生成し，ベクタを返す.
	 * @return 計算式ベクタ
	 * @throws CellMLException
	 * @throws RelMLException
	 * @throws MathException
	 * @throws TranslateException
	 */
	private Vector<SyntaxExpression> createExpressions()
	throws CellMLException, RelMLException, MathException, TranslateException {
		//---------------------------------------------
		//式生成のための前処理
		//---------------------------------------------
		/*ベクタを初期化*/
		Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();

		/*CellMLにRelMLを適用*/
		m_pCellMLAnalyzer.applyRelML(m_pRelMLAnalyzer);
//		m_pTecMLAnalyzer.printContents();	// debug
		//---------------------------------------------
		//式の追加
		//---------------------------------------------
		/*数式数を取得*/
		int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();

		for (int i = 0; i < nExpressionNum; i++) {

			/*数式の複製を取得*/
			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);

			/*左辺式・右辺式取得*/
			MathExpression pLeftExp = pMathExp.getLeftExpression();
			MathExpression pRightExp = pMathExp.getRightExpression();

			if (pLeftExp == null || pRightExp == null) {
				throw new TranslateException("SyntaxProgram","SimpleProgramGenerator",
							     "failed to parse expression");
			}

			/*左辺変数取得*/
			MathOperand pLeftVar = (MathOperand)pLeftExp.getFirstVariable();
//			System.out.print("左辺変数 " + pLeftVar.toLegalString());

			//-------------------------------------------
			//左辺式ごとに数式の追加
			//-------------------------------------------
			/*微係数変数*/
			if (m_pTecMLAnalyzer.isDerivativeVar(pLeftVar)) {

				/*微分式の数を取得*/
				int nDiffExpNum = m_pCellMLAnalyzer.getM_vecDiffExpression().size();
//				System.out.println("\t" + nDiffExpNum);

				/*数式の出力*/
				for (int j = 0; j < nDiffExpNum; j++) {

					/*代入文の形成*/
					Math_assign pMathAssign =
						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

					/*新たな計算式を生成*/
					MathExpression pNewExp = new MathExpression(pMathAssign);

					/*TecML変数に添え字を付加*/
					this.addIndexToTecMLVariables(pNewExp, j);

					/*微分式インスタンスのコピー取得*/
					MathExpression pDiffExpression =
						m_pCellMLAnalyzer.getM_vecDiffExpression().get(j).createCopy();

					/*微分関数の展開*/
					this.expandDiffFunction(pNewExp, pDiffExpression);

					/*数式ベクタに追加*/
					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
					vecExpressions.add(pSyntaxExp);
//					System.out.println("微係数変数" + pSyntaxExp.toLegalString());
				}
			}

			/*微分変数*/
			else if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {

				/*微分式の数を取得*/
				int nDiffVarNum = m_pCellMLAnalyzer.getM_vecDiffVar().size();
//				System.out.println("\t" + nDiffVarNum);

				/*数式の出力*/
				for (int j = 0; j < nDiffVarNum; j++) {

					/*代入文の形成*/
					Math_assign pMathAssign =
						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

					/*新たな計算式を生成*/
					MathExpression pNewExp = new MathExpression(pMathAssign);

					/*添え字の付加*/
					this.addIndexToTecMLVariables(pNewExp, j);

					/*数式ベクタに追加*/
					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
					vecExpressions.add(pSyntaxExp);
//					System.out.println("微分変数" + pSyntaxExp.toLegalString());
				}
			}

			/*通常変数*/
			else if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
				/*微分式の数を取得*/
				int nNonDiffExpNum = m_pCellMLAnalyzer.getM_vecNonDiffExpression().size();
//				System.out.println("\t" + nNonDiffExpNum);

				/*数式の出力*/
				for (int j = 0; j < nNonDiffExpNum; j++) {

					/*代入文の形成*/
					Math_assign pMathAssign =
						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

					/*新たな計算式を生成*/
					MathExpression pNewExp = new MathExpression(pMathAssign);

					/*TecML変数に添え字を付加*/
					this.addIndexToTecMLVariables(pNewExp, j);

					/*微分式インスタンスのコピー取得*/
					MathExpression pNonDiffExpression =
						m_pCellMLAnalyzer.getM_vecNonDiffExpression().get(j).createCopy();

					/*微分関数の展開*/
					this.expandNonDiffFunction(pNewExp, pNonDiffExpression);

					/*数式ベクタに追加*/
					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
					vecExpressions.add(pSyntaxExp);
//					System.out.println("通常変数" + pSyntaxExp.toLegalString());
				}
			}

			/*定数変数*/
			else if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
			}

		}

		//---------------------------------------------
		//出力変数から入力変数への代入式の追加
		// (TecMLには記述されていない式を追加する)
		//---------------------------------------------
//		System.out.println("その他\t" + m_pCellMLAnalyzer.getM_vecDiffVar().size());
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
			/*代入式の構成*/
			Math_assign pMathAssign =
				(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar().createCopy());
			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar().createCopy());
			MathExpression pMathExp = new MathExpression(pMathAssign);

			/*添え字の追加*/
			this.addIndexToTecMLVariables(pMathExp, i);

			/*数式ベクタに追加*/
			SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
			vecExpressions.add(pSyntaxExp);
//			System.out.println("その他" + pSyntaxExp.toLegalString());
		}

		return vecExpressions;
	}

	/*-----関数展開・変数置換メソッド-----*/

	/**
	 * 微分関数を展開する.
	 * @param pExpression 数式インスタンス
	 * @param pDiffExpression 微分式インスタンス
	 * @throws MathException
	 * @throws TranslateException
	 */
	private void expandDiffFunction(MathExpression pExpression,
			MathExpression pDiffExpression)
	throws MathException, TranslateException {
		/*展開関数の検索*/
		Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

		pExpression.searchFunction(m_pTecMLAnalyzer.getM_pDiffFuncVar(), vecFunctions);

		/*検索結果のすべての関数を展開*/
		int nFunctionNum = vecFunctions.size();

		for (int i = 0; i < nFunctionNum; i++) {

			/*置換対象の関数*/
			Math_fn pFunction = (Math_fn)vecFunctions.get(i).createCopy();

			/*関数の置換*/
			pExpression.replace(pFunction,
					pDiffExpression.getRightExpression().getRootFactor());

			/*関数引数型ごとのidを取得*/
			HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer.getDiffFuncArgTypeIdx();

			if (ati.size() == 0) {
				throw new TranslateException("SyntaxProgram","SimpleProgramGenerator",
							     "failed to get arguments index of differential function ");
			}

			/*変数の置換*/
			this.replaceFunctionVariables(pExpression, pFunction, ati);
		}
	}

	/**
	 * 非微分関数を展開する.
	 * @param pExpression 数式インスタンス
	 * @param pNonDiffExpression 非微分式インスタンス
	 * @throws MathException
	 * @throws TranslateException
	 */
	private void expandNonDiffFunction(MathExpression pExpression,
			MathExpression pNonDiffExpression)
	throws MathException, TranslateException {
		/*展開関数の検索*/
		Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

		pExpression.searchFunction(m_pTecMLAnalyzer.getM_pNonDiffFuncVar(), vecFunctions);

		/*検索結果のすべての関数を展開*/
		int nFunctionNum = vecFunctions.size();

		for (int i = 0; i < nFunctionNum; i++) {

			/*置換対象の関数*/
			Math_fn pFunction = (Math_fn)vecFunctions.get(i).createCopy();

			/*関数の置換*/
			pExpression.replace(pFunction,
					pNonDiffExpression.getRightExpression().getRootFactor());

			/*関数引数型ごとのidを取得*/
			HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer.getDiffFuncArgTypeIdx();

			if (ati.size() == 0) {
				throw new TranslateException("SyntaxProgram","SimpleProgramGenerator",
							     "failed to get arguments index of differential function ");
			}

			/*変数の置換*/
			this.replaceFunctionVariables(pExpression, pFunction, ati);
		}
	}

	/**
	 * 関数中変数の置換をする.
	 * @param pExpression 数式インスタンス
	 * @param pFunction 置換関数
	 * @param ati 関数引数型ごとのidとインデックス
	 * @throws MathException
	 */
	private void replaceFunctionVariables(MathExpression pExpression, Math_fn pFunction,
					      HashMap<eTecMLVarType, Integer> ati)
	throws MathException {
		/*関数引数型ごとのidを取得*/
		int nTimeArgIdx = 0;
		int nTimeVarArgIdx = 0;
		int nVarArgIdx = 0;
		int nConstVarArgIdx = 0;

		if (ati.containsKey(eTecMLVarType.TVAR_TYPE_TIMEVAR)) {
			nTimeArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_TIMEVAR);
		}
		if (ati.containsKey(eTecMLVarType.TVAR_TYPE_DIFFVAR)) {
			nTimeVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_DIFFVAR);
		}
		if (ati.containsKey(eTecMLVarType.TVAR_TYPE_ARITHVAR)) {
			nVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_ARITHVAR);
		}
		if (ati.containsKey(eTecMLVarType.TVAR_TYPE_CONSTVAR)) {
			nConstVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_CONSTVAR);
		}


		//----------------------------------時間変数型
		/*時間変数の置換*/
		for(int i = 0; i < m_pCellMLAnalyzer.getM_vecTimeVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nTimeArgIdx).toLegalString());
			/*配列インデックスを追加*/
			//pArgVar.addArrayIndexToBack(i);
			//時間にインデックスを付けず，共通の変数として扱う

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecTimeVar().get(i) ,pArgVar);
		}

		//----------------------------------微分変化変数型
		/*微分変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nTimeVarArgIdx).toLegalString());

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(i);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecDiffVar().get(i), pArgVar);
		}

		//----------------------------------通常変数型
		/*通常変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nVarArgIdx).toLegalString());
			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(i);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecArithVar().get(i), pArgVar);
		}

		//----------------------------------定数型
		/*定数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nConstVarArgIdx).toLegalString());

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(i);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecConstVar().get(i),pArgVar);
		}


//		for (eTecMLVarType varType: ati.keySet()) {
//
//			switch (varType) {
//
//				//----------------------------------時間変数型
//			case TVAR_TYPE_TIMEVAR:
//				break;
//			}
//		}
	}

	/**
	 * TecML変数へのインデックスを追加する.
	 * @param pExpression 数式インスタンス
	 * @param nIndex 付加するインデックス
	 * @throws MathException
	 */
	private void addIndexToTecMLVariables(MathExpression pExpression, int nIndex)
	throws MathException {
		/*微分変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(nIndex);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDiffVar().get(i), pArgVar);
		}

		/*微係数変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(nIndex);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i), pArgVar);
		}

		/*通常変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(nIndex);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i), pArgVar);
		}
	}

}
