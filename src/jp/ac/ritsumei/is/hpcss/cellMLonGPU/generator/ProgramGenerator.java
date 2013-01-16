package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.io.PrintWriter;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_inc;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_leq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.NewtonSolver;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.SimultaneousNewtonSolver;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * プログラム構文生成クラス.
 */
public abstract class ProgramGenerator {

	/**生成プログラム構文 main*/
	public static final String PROG_FUNC_STR_MAIN = "main";
	/**生成プログラム構文 argc*/
	public static final String PROG_VAR_STR_ARGC = "argc";
	/**生成プログラム構文 argv*/
	public static final String PROG_VAR_STR_ARGV = "argv";

	/**生成プログラム構文 memset*/
	public static final String PROG_FUNC_STR_MEMSET = "memset";
	/**生成プログラム構文 malloc*/
	public static final String PROG_FUNC_STR_MALLOC = "malloc";
	/**生成プログラム構文 free*/
	public static final String PROG_FUNC_STR_FREE = "free";

	/*各解析器インスタンス*/
	protected CellMLAnalyzer m_pCellMLAnalyzer;
	protected RelMLAnalyzer m_pRelMLAnalyzer;
	protected TecMLAnalyzer m_pTecMLAnalyzer;
	protected RecMLAnalyzer m_pRecMLAnalyzer;

	/*プログラム生成用パラメータ*/
	protected int m_unElementNum;	//計算要素数
	protected int m_unElementNumX;	//*	number of columns in the cell array
	protected int m_unElementNumY;	//* number of rows in the cell array
	protected double m_dStartTime;	//実験開始時刻
	protected double m_dEndTime;	//実験終了時刻
	protected double m_dDeltaTime;	//時間増分

	/**
	 * プログラム構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 */
	public ProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer) {
		m_pCellMLAnalyzer = pCellMLAnalyzer;
		m_pRelMLAnalyzer = pRelMLAnalyzer;
		m_pTecMLAnalyzer = pTecMLAnalyzer;
		m_unElementNum = 0;
		m_dStartTime = 0.0;
		m_dEndTime = 0.0;
		m_dDeltaTime = 0.0;
		m_unElementNumX = 0;
		m_unElementNumY = 0;
	}

	public ProgramGenerator(RecMLAnalyzer recMLAnalyzer) {
		// TODO 自動生成されたコンストラクター・スタブ
		m_pRecMLAnalyzer = recMLAnalyzer;
	}

	/**
	 * プログラム構文を生成し，返す.
	 * @return プログラム構文インスタンス
	 * @throws MathException
	 * @throws CellMLException
	 * @throws RelMLException
	 * @throws TranslateException
	 * @throws SyntaxException
	 */
	public abstract SyntaxProgram getSyntaxProgram()
	throws MathException, CellMLException, RelMLException,
	TranslateException, SyntaxException;

	/*-----プログラム生成用パラメータ設定メソッド-----*/

	/**
	 * 要素数(配列サイズ)パラメータを設定する.
	 * unElementNumX	: number of columns in the 2D cell array
	 * unElementNUmY	: number of rows in the 2D cell array
	 * @param unElementNum 要素数
	 */
	public void setElementNum(int unElementNum) {
		m_unElementNum = unElementNum;
		m_unElementNumX = unElementNum;
		m_unElementNumY = 1;
	}

	/**
	 * 要素数(配列サイズ)パラメータを設定する.
	 * @param unElementNumX number of columns in the 2D cell array
	 * @param unElementNumY number of rows in the 2D cell array
	 */
	public void setElementNum(int unElementNumX, int unElementNumY) {
		m_unElementNumX = unElementNumX;
		m_unElementNumY = unElementNumY;
		m_unElementNum  = unElementNumX * unElementNumY;
	}

	/**
	 * 時間関係パラメータを設定する.
	 * @param dStartTime 実験開始時刻
	 * @param dEndTime 実験終了時刻
	 * @param dDeltaTime 時間刻み幅
	 */
	public void setTimeParam(double dStartTime, double dEndTime, double dDeltaTime) {
		m_dStartTime = dStartTime;
		m_dEndTime = dEndTime;
		m_dDeltaTime = dDeltaTime;
	}

	/*-----変数リスト出力メソッド-----*/

	/**
	 * 変数対応関係を出力する.
	 * @param out 出力先
	 * @throws MathException
	 */
	public void outputVarRelationList(PrintWriter out)
	throws MathException {
		for (Math_ci it: m_pCellMLAnalyzer.getM_vecTimeVar()) {
			out.println(it.toLegalString() + "\t"
					+  m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());
		}
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecDiffVar().get(i);
			out.println(it.toLegalString() + "\t"
					+ m_pTecMLAnalyzer.getM_pInputVar().toLegalString()
					+ "[" + i + "]");
		}
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecArithVar().get(i);
			out.println(it.toLegalString() + "\t"
					+ m_pTecMLAnalyzer.getM_vecArithVar().get(0).toLegalString()
					+ "[" + i + "]");
		}
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecConstVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecConstVar().get(i);
			out.println(it.toLegalString() + "\t"
					+  m_pTecMLAnalyzer.getM_vecConstVar().get(0).toLegalString()
					+ "[" + i + "]");
		}
	}

	/**
	 * 変数初期化式を出力する.
	 * @param out 出力先
	 * @param pComponentTable 変数テーブル
	 * @throws MathException
	 */
	public void outputInitializeList(PrintWriter out, ComponentTable pComponentTable)
	throws MathException {
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecDiffVar().get(i);

			/*名前の取得と分解*/
			String strVarName = it.toLegalString();
			int nDotPos = strVarName.indexOf(".");
			String strCompName = strVarName.substring(0, nDotPos);
			String strLocalName = strVarName.substring(nDotPos + 1);
			String strInitValue;

			/*テーブルの探索*/
			try{
				VariableTable pVariableTable = pComponentTable.searchTable(strCompName);
				strInitValue = pVariableTable.getInitValue(strLocalName);
			}
			catch(TableException e){
				System.err.println(strVarName + " " + e.getMessage());
				continue;
			}

			out.println(m_pTecMLAnalyzer.getM_pInputVar().toLegalString()
					+ "[" + i + "] = " + strInitValue + ";");
		}
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecArithVar().get(i);

			/*名前の取得と分解*/
			String strVarName = it.toLegalString();
			int nDotPos = strVarName.indexOf(".");
			String strCompName = strVarName.substring(0, nDotPos);
			String strLocalName = strVarName.substring(nDotPos + 1);
			String strInitValue;

			/*テーブルの探索*/
			try{
				VariableTable pVariableTable = pComponentTable.searchTable(strCompName);
				strInitValue = pVariableTable.getInitValue(strLocalName);
			}
			catch(TableException e){
				continue;
			}

			out.println(m_pTecMLAnalyzer.getM_vecArithVar().get(0).toLegalString()
					+ "[" + i + "] = " + strInitValue + ";");
		}
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecConstVar().size(); i++) {
			Math_ci it = m_pCellMLAnalyzer.getM_vecConstVar().get(i);

			/*名前の取得と分解*/
			String strVarName = it.toLegalString();
			int nDotPos = strVarName.indexOf(".");
			String strCompName = strVarName.substring(0, nDotPos);
			String strLocalName = strVarName.substring(nDotPos + 1);
			String strInitValue;

			/*テーブルの探索*/
			try{
				VariableTable pVariableTable = pComponentTable.searchTable(strCompName);
				strInitValue = pVariableTable.getInitValue(strLocalName);
			}
			catch(TableException e){
				continue;
			}

			out.println(m_pTecMLAnalyzer.getM_vecConstVar().get(0).toLegalString()
					+ "[" + i + "] = " + strInitValue + ";");
		}
	}

	/*-----定型構文生成メソッド-----*/

	/**
	 * 新規プログラム構文インスタンスを生成する.
	 * @return プログラム構文インスタンス
	 */
	public SyntaxProgram createNewProgram() {
		return new SyntaxProgram();
	}

	/**
	 * メイン関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createMainFunction()
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynIntType = new SyntaxDataType(eDataType.DT_INT,0);
		SyntaxFunction pSynMainFunc = new SyntaxFunction(PROG_FUNC_STR_MAIN,pSynIntType);

		/*引数宣言の生成*/
		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_CHAR,2);
		Math_ci pArgcVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,PROG_VAR_STR_ARGC);
		Math_ci pArgvVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,PROG_VAR_STR_ARGV);
		SyntaxDeclaration pSynArgcDec = new SyntaxDeclaration(pSynIntType,pArgcVar);
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,pArgvVar);

		/*引数宣言の追加*/
		pSynMainFunc.addParam(pSynArgcDec);
		pSynMainFunc.addParam(pSynArgvDec);

		return pSynMainFunc;
	}
	
	/**
	 * データ数ループ構文インスタンスを生成する.
	 * @param pDataNumVariable データ数変数インスタンス
	 * @param pIndexVariable インデックス変数インスタンス
	 * @return 制御文構文インスタンス
	 * @throws MathException
	 */
	public SyntaxControl createSyntaxDataNumLoop(MathOperand pDataNumVariable,
			MathOperand pIndexVariable)
	throws MathException {
		/*ループ条件式生成*/
		Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
		pMathLt.addFactor(pIndexVariable);
		pMathLt.addFactor(pDataNumVariable);
		MathExpression pConditionExp = new MathExpression(pMathLt);

		/*ループ中初期化式生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_cn pLoopInit = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"0");
		pLoopInit.changeType();
		pMathAssign.addFactor(pIndexVariable);
		pMathAssign.addFactor(pLoopInit);
		MathExpression pInitExp = new MathExpression(pMathAssign);

		/*ループ再初期化式生成*/
		Math_inc pMathInc = (Math_inc)MathFactory.createOperator(eMathOperator.MOP_INC);
		pMathInc.addFactor(pIndexVariable);
		MathExpression pReInitExp = new MathExpression(pMathInc);

		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
		pSynLoopCond.setInitExpression(pInitExp,pReInitExp);

		/*ループを生成*/
		SyntaxControl pSynFor = new SyntaxControl(eControlKind.CTRL_FOR,pSynLoopCond);

		/*ループ構文を返す*/
		return pSynFor;
	}

	//========================================================
	//createSyntaxBlankLoop
	// 	generates a blank loop
	//
	//@arg
	// void
	//
	//@return
	// 制御文構文インスタンス	: SyntaxControl*
	//
	//========================================================
	public SyntaxControl createSyntaxBlankLoop()
	throws MathException {
		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition();

		/*ループを生成*/
		SyntaxControl pSynBlank = new SyntaxControl(eControlKind.CTRL_BLANK,pSynLoopCond);

		/*ループ構文を返す*/
		return pSynBlank;
	}
	
	//========================================================
	//createSyntaxDoWhileLoop
	// 	generates dowhile loop
	//
	//@arg
	// MathExpression* 	pConditionExp 	: condition for the dowhile loop
	//
	//@return
	// 制御文構文インスタンス	: SyntaxControl*
	//
	//========================================================
	public SyntaxControl createSyntaxDoWhileLoop(MathExpression pConditionExp)
	throws MathException {
		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);

		/*ループを生成*/
		SyntaxControl pSynDoWhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);

		/*ループ構文を返す*/
		return pSynDoWhile;
	}
	
	//========================================================
	//createSyntaxWhileLoop
	// 	generates while loop
	//
	//@arg
	// MathExpression* 	pConditionExp 	: condition for the while loop
	//
	//@return
	// 制御文構文インスタンス	: SyntaxControl*
	//
	//========================================================
	public SyntaxControl createSyntaxWhileLoop(MathExpression pConditionExp)
	throws MathException {
		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);

		/*ループを生成*/
		SyntaxControl pSynFor = new SyntaxControl(eControlKind.CTRL_WHILE,pSynLoopCond);

		/*ループ構文を返す*/
		
		return pSynFor;
	}
	
	//========================================================
	//createSyntaxIfStatement
	// 	generates if statement as syntax
	//
	//@arg
	// MathExpression* 	pConditionExp 	: condition for if statement
	//
	//@return
	// 制御文構文インスタンス	: SyntaxControl*
	//
	//========================================================
	public SyntaxControl createSyntaxIfStatement(MathExpression pConditionExp)
	throws MathException {
		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);

		/*ループを生成*/
		SyntaxControl pSynFor = new SyntaxControl(eControlKind.CTRL_IF,pSynLoopCond);

		/*ループ構文を返す*/
		return pSynFor;
	}
	
	/**
	 * 時間によるループ構文を生成する.
	 * @param dStartTime 開始時刻
	 * @param dEndTime 終了時刻
	 * @param pTimeVariable 時間変数インスタンス
	 * @param pDeltaOperand デルタ変数インスタンス
	 * @return ループ構文
	 * @throws MathException
	 */
	public SyntaxControl createSyntaxTimeLoop(double dStartTime,
			double dEndTime, MathOperand pTimeVariable, MathOperand pDeltaOperand)
	throws MathException {
		/*時間を文字列化*/
		String strStartTime = StringUtil.doubleToString(dStartTime);
		String strEndTime = StringUtil.doubleToString(dEndTime);

		/*ループ条件式生成*/
		Math_leq pMathLeq = (Math_leq)MathFactory.createOperator(eMathOperator.MOP_LEQ);
		Math_cn pLoopCount =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,strEndTime);
		pLoopCount.changeType();
		pMathLeq.addFactor(pTimeVariable);
		pMathLeq.addFactor(pLoopCount);
		MathExpression pConditionExp = new MathExpression(pMathLeq);

		/*ループ中初期化式生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_cn pLoopInit =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,strStartTime);
		pLoopInit.changeType();
		pMathAssign.addFactor(pTimeVariable);
		pMathAssign.addFactor(pLoopInit);
		MathExpression pInitExp = new MathExpression(pMathAssign);

		/*ループ再初期化式生成*/
		Math_assign pMathAssign2 =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_plus pMathPlus =
			(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);
		pMathPlus.addFactor(pTimeVariable);
		pMathPlus.addFactor(pDeltaOperand);
		pMathAssign2.addFactor(pTimeVariable);
		pMathAssign2.addFactor(pMathPlus);
		MathExpression pReInitExp = new MathExpression(pMathAssign2);

		/*ループ条件生成*/
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
		pSynLoopCond.setInitExpression(pInitExp,pReInitExp);

		/*ループを生成*/
		SyntaxControl pSynFor = new SyntaxControl(eControlKind.CTRL_FOR,pSynLoopCond);

		/*ループ構文を返す*/
		return pSynFor;
	}

	/**
	 * 0初期化用memset関数呼び出しを生成する.
	 * @param pDstVar コピー先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	public SyntaxCallFunction createZeroMemset(Math_ci pDstVar, MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynMemsetCall = new SyntaxCallFunction(PROG_FUNC_STR_MEMSET);

		/*第一引数追加*/
		pSynMemsetCall.addArgFactor(pDstVar);

		/*第二引数追加*/
		Math_cn pZeroConst =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"0");
		pZeroConst.changeType();
		//Syntax構文群が完成するまでの暫定処置
		pSynMemsetCall.addArgFactor(pZeroConst);

		/*第三引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*第三引数の追加*/
		pSynMemsetCall.addArgFactor(pMathTimes1);

		/*関数呼び出しインスタンスを戻す*/
		return pSynMemsetCall;
	}

	/**
	 * malloc関数呼び出しを生成する.
	 * @param pDstVar メモリ確保先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した数式構文インスタンス
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public SyntaxExpression createMalloc(Math_ci pDstVar, MathFactor pDataNumFactor)
	throws MathException, SyntaxException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynMallocCall = new SyntaxCallFunction(PROG_FUNC_STR_MALLOC);

		/*引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*引数の追加*/
		pSynMallocCall.addArgFactor(pMathTimes1);

		/*戻り値をキャスト*/
		SyntaxDataType pSynPDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,1);
//		pSynMallocCall.addCastDataType(pSynPDoubleType); //remove casting for declaration of multidimensional arrays

		/*代入式を生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_ci pMathTmpVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					pSynMallocCall.toLegalString());
		pMathAssign.addFactor(pDstVar);
		pMathAssign.addFactor(pMathTmpVar);
		MathExpression pNewExpression = new MathExpression(pMathAssign);
		SyntaxExpression pNewSynExpression = new SyntaxExpression(pNewExpression);

		/*関数呼び出しインスタンスを戻す*/
		return pNewSynExpression;
	}

	/**
	 * override class to create a malloc with ix dimension
	 * @param pDstVar メモリ確保先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @param pointerNum dimension of the malloc array
	 * @return 生成した数式構文インスタンス
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public SyntaxExpression createMalloc(Math_ci pDstVar, MathFactor pDataNumFactor,
			int pointerNum)
	throws MathException, SyntaxException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynMallocCall = new SyntaxCallFunction(PROG_FUNC_STR_MALLOC);

		StringBuilder strSizeofVar = new StringBuilder("sizeof(double "); 
		for (int i = 0; i < (pointerNum - 1); i++) {
			strSizeofVar.append("*");
		}
		strSizeofVar.append(")");
		
		/*引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
			strSizeofVar.toString());	//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*引数の追加*/
		pSynMallocCall.addArgFactor(pMathTimes1);

		/*戻り値をキャスト*/
		SyntaxDataType pSynPDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE, pointerNum);
		pSynMallocCall.addCastDataType(pSynPDoubleType);

		/*代入式を生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_ci pMathTmpVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					pSynMallocCall.toLegalString());
		pMathAssign.addFactor(pDstVar);
		pMathAssign.addFactor(pMathTmpVar);
		MathExpression pNewExpression = new MathExpression(pMathAssign);
		SyntaxExpression pNewSynExpression = new SyntaxExpression(pNewExpression);

		/*関数呼び出しインスタンスを戻す*/
		return pNewSynExpression;
	}

	/**
	 * free関数呼び出しを生成する.
	 * @param pDstVar メモリ解放対象変数インスタンス
	 * @return 生成した関数呼び出し構文インスタンス
	 */
	public SyntaxCallFunction createFree(Math_ci pDstVar) {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynFreeCall = new SyntaxCallFunction(PROG_FUNC_STR_FREE);

		/*第一引数追加*/
		pSynFreeCall.addArgFactor(pDstVar);

		/*生成したインスタンスを返す*/
		return pSynFreeCall;
	}
	
	
	//非線形方程式用ソルバー生成@n-washio
	/**
	 * ソルバー関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createSolverFunction(MathExpression exp)
	throws MathException {
		
		MathExpression expression = new MathExpression();
		expression = exp.clone();
		expression.setExID(exp.getExID());
		expression.setDerivedVariable(exp.getDerivedVariable());
		
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxFunction pSynSolverFunc = new SyntaxFunction("newton"+exp.getExID(),pSynDoubleType);

		/*引数宣言の生成*/
		//含まれる変数リストを作成
		Vector<Math_ci> varList= new Vector<Math_ci>();
		exp.getAllVariablesWithSelector(varList);
		
		
		//CodeNameを登録.
		for(int i=0;i<varList.size();i++){
			varList.get(i).setCodeName("var"+i);
		}
		exp.setCodeVariable(varList);
		exp.setDerivedVariableCodeName();
		
		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		
		for(int i=0;i<varList.size();i++){
			/*引数宣言の追加*/
			Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList.get(i).codeName);
			SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,var);
			pSynSolverFunc.addParam(pSynArgvDec);
		}
		NewtonSolver ns = new NewtonSolver();
		//デフォルトで設定.手入力も検討.
		
		double e = 1.0e-50;//収束判定値
		int max = 1000;//最大反復数
		
		//DerivedVariableがセレクターを含む場合,変数名だけを扱うようにする.
		//同変数名で競合する可能性(x[n],x[n+1]等)は存在しないものとする.
		String str="";
		str = ns.makeNewtonSolver(exp, exp.getDerivedVariable(),e,max);
		pSynSolverFunc.addString(str);

		return pSynSolverFunc;
	}
	/**
	 * 左辺関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createLeftFunction(MathExpression exp)
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxFunction pSynLeftFunc = new SyntaxFunction("func"+exp.getExID(),pSynDoubleType);

		/*引数宣言の生成*/
		//含まれる変数リストを作成
		Vector<Math_ci> varList= new Vector<Math_ci>();
		exp.getAllVariablesWithSelector(varList);
		
		//CodeNameを登録.
		for(int i=0;i<varList.size();i++){
			varList.get(i).setCodeName("var"+i);
		}
		exp.setCodeVariable(varList);
		exp.setDerivedVariableCodeName();
		
		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		
		for(int i=0;i<varList.size();i++){
			/*引数宣言の追加*/
			Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList.get(i).codeName);
			SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,var);
			pSynLeftFunc.addParam(pSynArgvDec);
		}
		NewtonSolver ns = new NewtonSolver();
		String str = ns.makeLeftFunc(exp, exp.getDerivedVariable());
		
		pSynLeftFunc.addString(str);
		return pSynLeftFunc;
	}
	/**
	 * 左辺微分関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createDiffFunction(MathExpression exp)
	throws MathException {
		
		
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxFunction pSynDiffrFunc = new SyntaxFunction("dfunc"+exp.getExID(),pSynDoubleType);

		/*引数宣言の生成*/
		//含まれる変数リストを作成
		Vector<Math_ci> varList= new Vector<Math_ci>();
		exp.getAllVariablesWithSelector(varList);
		
		//CodeNameを登録
		for(int i=0;i<varList.size();i++){
			varList.get(i).setCodeName("var"+i);
		}
		
		exp.setCodeVariable(varList);
		exp.setDerivedVariableCodeName();
		
		
		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		
		for(int i=0;i<varList.size();i++){
			/*引数宣言の追加*/
			Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList.get(i).codeName);
			SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,var);
			pSynDiffrFunc.addParam(pSynArgvDec);
		}
		NewtonSolver ns = new NewtonSolver();
		String str = ns.makeDiffFunc(exp, exp.getDerivedVariable());
		
		
		
		pSynDiffrFunc.addString(str);

		return pSynDiffrFunc;
	}
	
	/**
	 * ヤコビ行列関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createJacobiFunction(MathExpression exp)
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxFunction pSynDiffrFunc = new SyntaxFunction("jacobi"+exp.getSimulID(),pSynDoubleType);
		
		Vector<Math_ci> derivedVarList = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			derivedVarList.add(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getDerivedVariable());
		}
		
		/*引数宣言の生成*/
		//含まれる変数リストを作成
		//連立成分の数式全てを取得
		Vector<Math_ci> varList;
		Vector<Math_ci> varList_new = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			varList= new Vector<Math_ci>();
			this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getAllVariablesWithSelector(varList);
			
			for(int j=0;j<varList.size();j++){
				boolean flag=false;
				for(int k=0;k<varList_new.size();k++){
					if(varList_new.get(k).toLegalString().equals(varList.get(j).toLegalString())){
						flag=true;
					}
				}
				if(!flag) varList_new.add(varList.get(j));
			}
		}
		
		//CodeNameを登録
		for(int i=0;i<varList_new.size();i++){
			varList_new.get(i).setCodeName("var"+i);
		}


		
		//連立成分の数式全てにCodeNameを共有
		for(int i=0;i<m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
		
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setCodeVariable(varList_new);
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setDerivedVariableCodeName();
			
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setAllVariableCodeName();
		}
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,1);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPIntType = new SyntaxDataType(eDataType.DT_INT,0);
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet");
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			//導出変数でない場合,引数リストに追加 idによるソートが必要　保留
			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).getName().equals(derivedVarList.get(j).getName())){
					overlap=true;
				}
			}
			if(!overlap){
				Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList_new.get(i).codeName);
				pSynArgvDec = new SyntaxDeclaration(pSynPPCharType2,var);
				pSynDiffrFunc.addParam(pSynArgvDec);
			}
		}
		
		//int i, j 追加
		Math_ci i=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "i");
		Math_ci j=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "j");
		pSynArgvDec = new SyntaxDeclaration(pSynPPIntType,i);
		pSynDiffrFunc.addParam(pSynArgvDec);
		pSynArgvDec = new SyntaxDeclaration(pSynPPIntType,j);
		pSynDiffrFunc.addParam(pSynArgvDec);
		
		
		SimultaneousNewtonSolver sns = new SimultaneousNewtonSolver();
		String str = sns.makeJacobiFunc(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()), derivedVarList);
		
		pSynDiffrFunc.addString(str);

		return pSynDiffrFunc;
	}
	/**
	 * 連立関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createSimulFunction(MathExpression exp)
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxFunction pSynDiffrFunc = new SyntaxFunction("simulFunc"+exp.getSimulID(),pSynDoubleType);
		
		
		Vector<Math_ci> derivedVarList = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			derivedVarList.add(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getDerivedVariable());
		}
		
		/*引数宣言の生成*/
		//含まれる変数リストを作成
		//連立成分の数式全てを取得
		Vector<Math_ci> varList;
		Vector<Math_ci> varList_new = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			varList= new Vector<Math_ci>();
			this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getAllVariablesWithSelector(varList);
			
			for(int j=0;j<varList.size();j++){
				boolean flag=false;
				for(int k=0;k<varList_new.size();k++){
					if(varList_new.get(k).toLegalString().equals(varList.get(j).toLegalString())){
						flag=true;
					}
				}
				if(!flag) varList_new.add(varList.get(j));
			}
		}
		//CodeNameを登録
		for(int i=0;i<varList_new.size();i++){
			varList_new.get(i).setCodeName("var"+i);
		}
				
		//連立成分の数式全てにCodeNameを共有
		for(int i=0;i<m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
		
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setCodeVariable(varList_new);
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setDerivedVariableCodeName();
			
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setAllVariableCodeName();
		}
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,1);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPIntType = new SyntaxDataType(eDataType.DT_INT,0);
		
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet");
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			//導出変数でない場合,引数リストに追加  idによるソートが必要　保留
			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).getName().equals(derivedVarList.get(j).getName())){
					overlap=true;
				}
			}
			if(!overlap){
				Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList_new.get(i).codeName);
				pSynArgvDec = new SyntaxDeclaration(pSynPPCharType2,var);
				pSynDiffrFunc.addParam(pSynArgvDec);
			}
		}
		
		//int i 追加
		Math_ci i=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "i");
		pSynArgvDec = new SyntaxDeclaration(pSynPPIntType,i);
		pSynDiffrFunc.addParam(pSynArgvDec);
		
		SimultaneousNewtonSolver sns = new SimultaneousNewtonSolver();
		String str = sns.makeFunc(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()), derivedVarList);
		
		pSynDiffrFunc.addString(str);

		return pSynDiffrFunc;
	}
	
	/**
	 * 連立方程式ニュートン法計算関数構文インスタンスを生成する.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 */
	public SyntaxFunction createSimulNewtonFunction(MathExpression exp)
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_VOID,0);
		SyntaxFunction pSynDiffrFunc = new SyntaxFunction("simulNewton"+exp.getSimulID(),pSynDoubleType);
	
		
		Vector<Math_ci> derivedVarList = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			derivedVarList.add(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getDerivedVariable());
		}
		
		/*引数宣言の生成*/
		//含まれる変数リストを作成
		//連立成分の数式全てを取得
		Vector<Math_ci> varList;
		Vector<Math_ci> varList_new = new Vector<Math_ci>();
		for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
			varList= new Vector<Math_ci>();
			this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).getAllVariablesWithSelector(varList);
			
			for(int j=0;j<varList.size();j++){
				boolean flag=false;
				for(int k=0;k<varList_new.size();k++){
					if(varList_new.get(k).toLegalString().equals(varList.get(j).toLegalString())){
						flag=true;
					}
				}
				if(!flag) varList_new.add(varList.get(j));
			}
		}
		//CodeNameを登録
		for(int i=0;i<varList_new.size();i++){
			varList_new.get(i).setCodeName("var"+i);
		}
				
		//連立成分の数式全てにCodeNameを共有
		for(int i=0;i<m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).size();i++){
		
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setCodeVariable(varList_new);
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setDerivedVariableCodeName();
			
			m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()).get(i).setAllVariableCodeName();
		}
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,1);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet");
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).getName().equals(derivedVarList.get(j).getName())){
					overlap=true;
				}
			}
			if(!overlap){
				Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList_new.get(i).codeName);
				pSynArgvDec = new SyntaxDeclaration(pSynPPCharType2,var);
				pSynDiffrFunc.addParam(pSynArgvDec);
			}
		}
		
		double e = 1.0e-50;//収束判定値
		int max = 1000;//最大反復数
	
	
		SimultaneousNewtonSolver sns = new SimultaneousNewtonSolver();
		String str = sns.makeSimultaneousNewtonSolver(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()), derivedVarList,e,max,(int) exp.getSimulID());
		
		pSynDiffrFunc.addString(str);

		return pSynDiffrFunc;
	}

}
