package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.io.PrintWriter;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_inc;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_leq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.VariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.StringUtil;

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
	
	public ProgramGenerator() {
		// TODO Remove after testing
		
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
		pMathLeq.addFactor(pTimeVariable);
		pMathLeq.addFactor(pLoopCount);
		MathExpression pConditionExp = new MathExpression(pMathLeq);

		/*ループ中初期化式生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_cn pLoopInit =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,strStartTime);
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

}
