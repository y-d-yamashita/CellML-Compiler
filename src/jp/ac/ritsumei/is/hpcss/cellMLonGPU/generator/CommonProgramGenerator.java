package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_and;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_divide;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_geq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * 逐次プログラム構文生成クラス.
 */
public class CommonProgramGenerator extends ProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
	private static final String COMPROG_LOOP_INDEX_NAMEX = "ix";
	private static final String COMPROG_LOOP_INDEX_NAMEY = "iy";
	private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";
	private static final String COMPROG_DEFINE_DATANUM_NAMEX = "__DATA_NUMX";
	private static final String COMPROG_DEFINE_DATANUM_NAMEY = "__DATA_NUMY";
	private static final String COMPROG_VAR_APNAME = "zz";
	private static final String COMPROG_ARRAY_APNAME = "ym";

	/*共通変数*/
	// 次の３変数は public になった(元はprotected)
	/**共通変数 データ数として#defineされる定数*/
	public Math_ci m_pDefinedDataSizeVar;
	/**共通変数X データ数として#defineされる定数*/
	public Math_ci m_pDefinedDataSizeVarX;
	/**共通変数Y データ数として#defineされる定数*/
	public Math_ci m_pDefinedDataSizeVarY;

	/**
	 * 逐次プログラム構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 * @throws MathException
	 */
	public CommonProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
	throws MathException {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
		m_pDefinedDataSizeVar = null;
		m_pDefinedDataSizeVarX = null;
		m_pDefinedDataSizeVarY = null;
		initialize();
	}

	/*-----プログラム構文取得メソッド-----*/
	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator#getSyntaxProgram()
	 */
	public SyntaxProgram getSyntaxProgram()
	throws MathException, CellMLException, RelMLException,
		TranslateException, SyntaxException {

		//----------------------------------------------
		//プログラム生成のための前処理
		//----------------------------------------------
		/*CellMLにRelMLを適用*/
		m_pCellMLAnalyzer.applyRelML(m_pRelMLAnalyzer);

		//----------------------------------------------
		//プログラム構文の生成
		//----------------------------------------------
		/*プログラム構文生成*/
		SyntaxProgram pSynProgram = this.createNewProgram();

		/*プリプロセッサ構文生成・追加*/
		SyntaxPreprocessor pSynInclude1 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdio.h");
		SyntaxPreprocessor pSynInclude2 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdlib.h");
		SyntaxPreprocessor pSynInclude3 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "math.h");
		pSynProgram.addPreprocessor(pSynInclude1);
		pSynProgram.addPreprocessor(pSynInclude2);
		pSynProgram.addPreprocessor(pSynInclude3);

		/*データ数定義defineの追加*/
		String strElementNum = String.valueOf(m_unElementNum);
		String strElementNumX = String.valueOf(m_unElementNumX);
		String strElementNumY = String.valueOf(m_unElementNumY);
		SyntaxPreprocessor pSynDefine1 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       m_pDefinedDataSizeVar.toLegalString() + " "
					       + strElementNum);
		SyntaxPreprocessor pSynDefineX =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       m_pDefinedDataSizeVarX.toLegalString() + " "
					       + strElementNumX);
		SyntaxPreprocessor pSynDefineY =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       m_pDefinedDataSizeVarY.toLegalString() + " "
					       + strElementNumY);
		pSynProgram.addPreprocessor(pSynDefine1);
		pSynProgram.addPreprocessor(pSynDefineX);
		pSynProgram.addPreprocessor(pSynDefineY);

		//----------------------------------------------
		//メイン関数生成
		//----------------------------------------------
		/*メイン関数生成・追加*/
		SyntaxFunction pSynMainFunc = this.createMainFunction();
		pSynProgram.addFunction(pSynMainFunc);

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*ループ変数インスタンス生成*/
		Math_ci pLoopIndexVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

		/*ループ条件に使用した変数を関数に宣言追加*/
		{
			SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_INT, 0);
			SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt, pLoopIndexVar);
			pSynMainFunc.addDeclaration(pDecVar);
		}

		/*微分変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynTimeVarDec);
		}

		/*微係数変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynDiffVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynDiffVarDec);
		}

		/*通常変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynVarDec);
		}

		/*定数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_vecConstVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}

		/*時間変数の宣言*/
		{

			/*double型構文生成*/
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynTimeDec);
		}

		/*デルタ変数の宣言*/
		{
			/*double型構文生成*/
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

			/*初期化式の生成*/
			Math_cn pConstDeltaVal =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						StringUtil.doubleToString(m_dDeltaTime));
			MathExpression pInitExpression = new MathExpression(pConstDeltaVal);

			/*宣言の生成*/
			SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			/*初期化式の追加*/
			pSynDeltaDec.addInitExpression(pInitExpression);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynDeltaDec);
		}

		//*
		Math_ci ymArray =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, COMPROG_ARRAY_APNAME);

		/* Declare the variables array for 2D propagation */ //*
		{
			SyntaxDataType pSynTypePDouble2D =
				new SyntaxDataType(eDataType.DT_DOUBLE, 2);
			SyntaxDeclaration pCellVar = new SyntaxDeclaration(pSynTypePDouble2D, ymArray);
			pSynMainFunc.addDeclaration(pCellVar);
		}

		/* Declare the intercell potential variable to be used for the propagation */
		{
			SyntaxDataType pSynTypePDouble =
				new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			Math_ci pCellAP =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, COMPROG_VAR_APNAME);
			SyntaxDeclaration pCellPotential = new SyntaxDeclaration(pSynTypePDouble, pCellAP);
			pSynMainFunc.addDeclaration(pCellPotential);
		}

		//----------------------------------------------
		//malloc関数呼び出しの追加
		//----------------------------------------------
		/*微分変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*データ数を表す数式を生成*/
			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_cn pMathVarCount =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));
			pMathTimes.addFactor(pMathVarCount);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);

			/*宣言の追加*/
			pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),
					pMathTimes));
		}
		/*一時変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*データ数を表す数式を生成*/
			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_cn pMathVarCount =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size()));
			pMathTimes.addFactor(pMathVarCount);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);

			/*宣言の追加*/
			pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecArithVar().get(i),
					pMathTimes));
		}
		/*微係数変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*データ数を表す数式を生成*/
			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_cn pMathVarCount =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));
			pMathTimes.addFactor(pMathVarCount);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);

			/*宣言の追加*/
			pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer.
					getM_vecDerivativeVar().get(i),
					pMathTimes));
		}
		/*定数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*データ数を表す変数を生成*/
			Math_cn pMathVarCount =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size()));

			/*宣言の追加*/
			pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecConstVar().get(i),
					pMathVarCount));
		}

		/* Create 2D malloc for the propagation array */ //*
		{
			String ymVar = "ym[" + COMPROG_LOOP_INDEX_NAME1 + "]";
			Math_ci ymArrayIdx = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, ymVar);

			/*Declare the 2D malloc array and allocate memory using a loop */ //*
			pSynMainFunc.addStatement(createMalloc(ymArray,m_pDefinedDataSizeVarX, 2));
			SyntaxControl pSynForInit = createSyntaxDataNumLoop(m_pDefinedDataSizeVarX,pLoopIndexVar);
			pSynMainFunc.addStatement(pSynForInit);
			pSynForInit.addStatement(createMalloc(ymArrayIdx,m_pDefinedDataSizeVarY));
		}

		//----------------------------------------------
		//数式部分の追加
		//----------------------------------------------
		/*外側ループ構文生成・追加*/
		Math_ci pTimeVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pTimeVar().createCopy());
		Math_ci pDeltaVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pDeltaVar().createCopy());

		SyntaxControl pSynFor1 = createSyntaxTimeLoop(m_dStartTime,
				m_dEndTime, pTimeVariale, pDeltaVariale);
		pSynMainFunc.addStatement(pSynFor1);

		/* Add the action potential propagation part to the program, together with the time-evolution formulas. */
		this.addAPPropagationToProgram(pSynFor1);
//		/*内側ループ構文生成・追加*/
//		SyntaxControl pSynFor2 = createSyntaxDataNumLoop(m_pDefinedDataSizeVar, pLoopIndexVar);
//		pSynFor1.addStatement(pSynFor2);

//		//----------------------------------------------
//		//数式の生成と追加
//		//----------------------------------------------
//		/*数式を生成し，取得*/
//		Vector<SyntaxExpression> vecExpressions = this.createExpressions();
//
//		/*ループ中に数式を追加*/
//		int nExpressionNum = vecExpressions.size();
//
//		for (int i = 0; i < nExpressionNum;i++) {
//
//			/*数式の追加*/
//			pSynFor2.addStatement(vecExpressions.get(i));
//		}

		//----------------------------------------------
		//free関数呼び出しの追加
		//----------------------------------------------
		/*微分変数のfree*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer.getM_vecDiffVar().get(i)));
		}
		/*一時変数のfree*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer.getM_vecArithVar().get(i)));
		}
		/*微係数変数のfree*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer.
					getM_vecDerivativeVar().get(i)));
		}
		/*定数のfree*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer.getM_vecConstVar().get(i)));
		}

		/*free cell array*/
		for(int i=0;i<m_pTecMLAnalyzer.getM_vecConstVar().size();i++){

			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree(ymArray));
		}

		/*プログラム構文を返す*/
		return pSynProgram;
	}

	/**
	 * 初期化する.
	 * @throws MathException
	 */
	protected void initialize() throws MathException {
		m_pDefinedDataSizeVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_DEFINE_DATANUM_NAME);
		m_pDefinedDataSizeVarX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_DEFINE_DATANUM_NAMEX);
		m_pDefinedDataSizeVarY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_DEFINE_DATANUM_NAMEY);
	}

	/**
	 * 計算式部を生成し，ベクタを返す.
	 * @return 計算式ベクタ
	 * @throws TranslateException
	 * @throws MathException
	 */
	protected Vector<SyntaxExpression> createExpressions()
	throws TranslateException, MathException {
		//---------------------------------------------
		//式生成のための前処理
		//---------------------------------------------
		/*ベクタを初期化*/
		Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();

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
				throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
							     "failed to parse expression");
			}

			/*左辺変数取得*/
			MathOperand pLeftVar = (MathOperand)pLeftExp.getFirstVariable();

			//-------------------------------------------
			//左辺式ごとに数式の追加
			//-------------------------------------------
			/*微係数変数*/
			if (m_pTecMLAnalyzer.isDerivativeVar(pLeftVar)) {

				/*微分式の数を取得*/
				int nDiffExpNum = m_pCellMLAnalyzer.getM_vecDiffExpression().size();

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
				}

			}

			/*微分変数*/
			else if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {

				/*微分式の数を取得*/
				int nDiffVarNum = m_pCellMLAnalyzer.getM_vecDiffVar().size();

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
				}
			}

			/*通常変数*/
			else if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
				/*微分式の数を取得*/
				int nNonDiffExpNum = m_pCellMLAnalyzer.getM_vecNonDiffExpression().size();

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
	protected void expandDiffFunction(MathExpression pExpression,
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
				throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
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
	protected void expandNonDiffFunction(MathExpression pExpression,
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
				throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
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

		/*時間変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecTimeVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nTimeArgIdx).toLegalString());

			/*配列インデックスを追加*/
			//pArgVar->addArrayIndexToBack(i);
			//時間にインデックスを付けず，共通の変数として扱う

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecTimeVar().get(i), pArgVar);
		}

		/*微分変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nTimeVarArgIdx).toLegalString());

			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   String.valueOf(i));
			Math_ci pLoopIndexVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_LOOP_INDEX_NAME1);

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(pLoopIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecDiffVar().get(i),pArgVar);
		}

		/*通常変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						pFunction.getArgumentsVector().get(nVarArgIdx).toLegalString());

			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   String.valueOf(i));
			Math_ci pLoopIndexVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(pLoopIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecArithVar().get(i),pArgVar);
		}

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
	}

	/**
	 * TecML変数へのインデックスを追加する.
	 * @param pExpression 数式インスタンス
	 * @param nIndex 付加するインデックス
	 * @throws MathException
	 */
	protected void addIndexToTecMLVariables(MathExpression pExpression, int nIndex)
	throws MathException {
		/*微分変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
				   m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());


			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   String.valueOf(nIndex));
			Math_ci pLoopIndexVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_LOOP_INDEX_NAME1);

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(pLoopIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),pArgVar);
		}

		/*微係数変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   String.valueOf(nIndex));
			Math_ci pLoopIndexVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_LOOP_INDEX_NAME1);

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(pLoopIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i),pArgVar);
		}

		/*通常変数の置換*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   String.valueOf(nIndex));
			Math_ci pLoopIndexVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_LOOP_INDEX_NAME1);

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(pLoopIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i),pArgVar);
		}
	}

	/*-----Propagation generationメソッド-----*/

	/**
	 * generate the action potential propagation scheme for 1D and 2D array of cells
	 * @param pSynControl the time loop where the propagation scheme will be inserted
	 * @throws MathException
	 * @throws TranslateException
	 */
	protected void addAPPropagationToProgram(SyntaxControl pSynControl)
	throws MathException, TranslateException {
		/* Declare the array size variables */
		Math_ci pLoopIndexVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_LOOP_INDEX_NAME1);
		Math_ci pLoopIndexVarX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_LOOP_INDEX_NAMEX);
		Math_ci pLoopIndexVarY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_LOOP_INDEX_NAMEY);

		/* The AP propagation variable to be used */
		Math_ci pCellAP =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					COMPROG_VAR_APNAME);

		/* Create the AP propagation formula, the input is the index of the voltage variable in the vector of equations */
		int voltageIndex = 0;
		String propArrayYM =
			"ym[" + COMPROG_LOOP_INDEX_NAMEX + "][" + COMPROG_LOOP_INDEX_NAMEY + "]";
		SyntaxExpression pAPPropagationExp = createPropagation(voltageIndex, propArrayYM);

		/* Create the loop for computing the cell potential between adjacent cells (for 1D and 2D array of cells) */ //*
		SyntaxControl pSynAPLoop;
		{
			/* Create the 1D array loop y*/ //*
			SyntaxControl pCellPotLoopX =
				createSyntaxDataNumLoop(m_pDefinedDataSizeVarX, pLoopIndexVarX);
			pSynAPLoop = createSyntaxDataNumLoop(m_pDefinedDataSizeVarY, pLoopIndexVarY);
			pSynControl.addStatement(pCellPotLoopX);
			pCellPotLoopX.addStatement(pSynAPLoop);

			/* Insert the AP propagation formula */
			pSynAPLoop.addStatement(pAPPropagationExp);

		}


		/* Create the double loop for computing the time evolution of CellML formulas */ //*
		SyntaxControl pSynAPLoop2;
		{
			/* Create the 1D array loop y*/ //*
			SyntaxControl pCellPotLoopX =
				createSyntaxDataNumLoop(m_pDefinedDataSizeVarX, pLoopIndexVarX);
			pSynAPLoop2 = createSyntaxDataNumLoop(m_pDefinedDataSizeVarY, pLoopIndexVarY);
			pSynControl.addStatement(pCellPotLoopX);
			pCellPotLoopX.addStatement(pSynAPLoop2);
		}

		/* Reset the value of zz to 0 every cycle in time evolution loop */ //*
		{
			Math_assign pMathAssign =
				(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
			Math_cn pZero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
			pMathAssign.addFactor(pCellAP);
			pMathAssign.addFactor(pZero);

			/* Create new SyntaxExpression for the initialization of zz and add to the AP propagation loop */
			MathExpression pMathApExpression = new MathExpression(pMathAssign);
			SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathApExpression);
			pSynAPLoop2.addStatement(pSyntaxExp);
		}

		/*内側ループ構文生成・追加*/
		//SyntaxControl *pSynFor2 = createSyntaxDataNumLoop(m_pDefinedDataSizeVar,pLoopIndexVar);
		//pSynFor1->addStatement(pSynFor2);

		/* Add the condition for Action Potential progragation using CTRL_IF argumenti.e. if (ix >= 0 && ix < __DATA_NUMX && iy >= 0 && iy < 1)*/ //*
		/* where the AP boundary condition: APsource <= iy < APboundary */
		{
			Math_geq pMathGeq =
				(Math_geq)MathFactory.createOperator(eMathOperator.MOP_GEQ);
			Math_cn pAPSource =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"0");
			pMathGeq.addFactor(pLoopIndexVarX);
			pMathGeq.addFactor(pAPSource);

			Math_lt pMathLt =
				(Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
			pMathLt.addFactor(pLoopIndexVarX);
			pMathLt.addFactor(m_pDefinedDataSizeVarX);

			Math_geq pMathGeq2 =
				(Math_geq)MathFactory.createOperator(eMathOperator.MOP_GEQ);
			pMathGeq2.addFactor(pLoopIndexVarY);
			pMathGeq2.addFactor(pAPSource);

			Math_lt pMathLt2 =
				(Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
			Math_cn pAPBoundary =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"1");
			pMathLt2.addFactor(pLoopIndexVarY);
			pMathLt2.addFactor(pAPBoundary);

			Math_and pMathAnd1 =
				(Math_and)MathFactory.createOperator(eMathOperator.MOP_AND);
			pMathAnd1.addFactor(pMathGeq);
			pMathAnd1.addFactor(pMathLt);

			Math_and pMathAnd2 =
				(Math_and)MathFactory.createOperator(eMathOperator.MOP_AND);
			pMathAnd2.addFactor(pMathAnd1);
			pMathAnd2.addFactor(pMathGeq2);

			Math_and pMathAnd3 =
				(Math_and)MathFactory.createOperator(eMathOperator.MOP_AND);
			pMathAnd3.addFactor(pMathAnd2);
			pMathAnd3.addFactor(pMathLt2);

			MathExpression pConditionExp = new MathExpression(pMathAnd3);
			SyntaxCondition pSynIfCond = new SyntaxCondition(pConditionExp);
			SyntaxControl pSynIf = new SyntaxControl(eControlKind.CTRL_IF,pSynIfCond);
			pSynAPLoop2.addStatement(pSynIf);


//			Math_plus pMathAssign =
				//(Math_plus)
			Math_assign pMathAssign =
				(Math_assign)
				MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
			Math_cn pAPStatement =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"" +
						" // Insert action potential propagation statement here and indicate the action potential voltage");
			pMathAssign.addFactor(pCellAP);
			pMathAssign.addFactor(pAPStatement);

			MathExpression pMathComment = new MathExpression(pMathAssign);
			SyntaxExpression pSynCom = new SyntaxExpression(pMathComment);
			pSynIf.addStatement(pSynCom);
		}

		/* Add the propagated voltage into the cell potential */
		{

			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);
			Math_cn pPropYM =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, propArrayYM);
			pMathPlus.addFactor(pCellAP);
			pMathPlus.addFactor(pPropYM);

			Math_assign pMathAssign =
				(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
			pMathAssign.addFactor(pCellAP);
			pMathAssign.addFactor(pMathPlus);

			/* Create math expression and add to time evolution inner loop */
			MathExpression pMathExp = new MathExpression(pMathAssign);
			SyntaxExpression pSynExp = new SyntaxExpression(pMathExp);
			pSynAPLoop2.addStatement(pSynExp);

		}


		//----------------------------------------------
		//数式の生成と追加
		//----------------------------------------------
		/*数式を生成し，取得*/
		Vector<SyntaxExpression> vecExpressions = this.createExpressions();

		/*ループ中に数式を追加*/
		int nExpressionNum = vecExpressions.size();

		for(int i=0;i<nExpressionNum;i++){
			/*数式の追加*/
			pSynAPLoop2.addStatement(vecExpressions.get(i));
		}

	}

	/**
	 * generate the equation for the field propagation of the action potential (AP):
	 * ym[ix][iy] = (xi[ (  ( 0 * __DATA_NUM )  + (ix * __DATA_NUMX) + ( (iy - 1)%__DATA_NUMY ) ) ] - xi[ (  ( 0 * __DATA_NUM )  + (ix * __DATA_NUMX) + iy ) ])/R;
	 * MathOperand	*pDataNumVariableX	: loop variable index (number of cols)
	 * MathOperand	*pDataNumVariableY	: loop variable index (number of rows)
	 * @param voltageIndexVar the index of the voltage variable V
	 * @param propArray
	 * @return
	 * @throws MathException
	 */
	protected SyntaxExpression createPropagation(int voltageIndexVar, String propArray)
	throws MathException {
		// generate the propagation formula ym[ix][iy] = (xi[ (  ( 0 * __DATA_NUM )  + (ix * __DATA_NUMX) + ( (iy - 1)%__DATA_NUMY ) ) ] - xi[ (  ( 0 * __DATA_NUM )  + (ix * __DATA_NUMX) + iy ) ])/R;
		String voltageIndex = String.valueOf(voltageIndexVar);
		String stringVoltageVar1 = "xi[ (" + voltageIndex + " * "
			+ COMPROG_DEFINE_DATANUM_NAME + ") + (" + COMPROG_LOOP_INDEX_NAMEX
			+ " * " + COMPROG_DEFINE_DATANUM_NAMEX + ") + ( ("
			+ COMPROG_LOOP_INDEX_NAMEY + " - 1)%" + COMPROG_DEFINE_DATANUM_NAMEY + ") ] ";
		String stringVoltageVar2 = "xi[ (" + voltageIndex + " * "
			+ COMPROG_DEFINE_DATANUM_NAME + ") + (" + COMPROG_LOOP_INDEX_NAMEX + " * "
			+ COMPROG_DEFINE_DATANUM_NAMEX + ") + " + COMPROG_LOOP_INDEX_NAMEY + " ]";


		Math_ci pVoltageVar1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, stringVoltageVar1);
		Math_ci pVoltageVar2 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, stringVoltageVar2);
		Math_minus pMathMinusAP = (Math_minus)MathFactory.createOperator(eMathOperator.MOP_MINUS);
		pMathMinusAP.addFactor(pVoltageVar1);
		pMathMinusAP.addFactor(pVoltageVar2);

		/* Insert resistance to divide the propagated voltage at each step */
		Math_ci pResistance = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "R");
		Math_divide pMathDivideR = (Math_divide)MathFactory.createOperator(eMathOperator.MOP_DIVIDE);
		pMathDivideR.addFactor(pMathMinusAP);
		pMathDivideR.addFactor(pResistance);

		/* Assign the equation to the variable designated by the function arguments */
		Math_ci pPropArr = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, propArray);
		Math_assign pMathAssign = (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		pMathAssign.addFactor(pPropArr);
		pMathAssign.addFactor(pMathDivideR);

		/*新たな計算式を生成*/
		MathExpression pNewExp = new MathExpression(pMathAssign);

		/*添え字の付加*/
		SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);

		return pSyntaxExp;
	}

}
