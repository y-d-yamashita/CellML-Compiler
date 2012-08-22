package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.Exception;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CudaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.SimpleProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.*;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.StringUtil;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * 逐次プログラム構文生成クラス
 */
public class RecurrenceRelationGeneratorStatementList extends ProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
	private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";
	private static final String COMPROG_DEFINE_MAXARRAYNUM_NAME = "__MAX_ARRAY_NUM";

	/*共通変数*/
	protected Math_ci m_pDefinedDataSizeVar;		//データ数として#defineされる定数

	/*-----コンストラクタ-----*/
	public RecurrenceRelationGeneratorStatementList(RecMLAnalyzer pRecMLAnalyzer)
	throws MathException 	{
		super(pRecMLAnalyzer);
		m_pDefinedDataSizeVar = null;
		initialize();
	}

	//========================================================
	//getSyntaxProgram
	// プログラム構文を生成し，返す
	//
	//@return
	// プログラム構文インスタンス	: SyntaxProgram*
	//
	//@throws
	// TranslateException
	//
	//========================================================
	/*-----プログラム構文取得メソッド-----*/
	public SyntaxProgram getSyntaxProgram()
	throws MathException, CellMLException, RelMLException, TranslateException, SyntaxException {

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
		
		SyntaxFunction pSynMainFunc = this.createMainFunction();
		pSynProgram.addFunction(pSynMainFunc);
		
		//String[] strAttr_Original = new String[] {null, null, null};
		//pSynMainFunc = this.MainFunc1(pSynMainFunc, strAttr_Original);

//		int LoopNumber = 0;
		//String[] strAttr_Original = new String[] {null, null, null};
		//pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Original, null);
				
		/*RecurVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			String parenthesis = "";
			for(int j = 0; j < parenthesisnum-1; j++){
				parenthesis += "[" + COMPROG_DEFINE_MAXARRAYNUM_NAME + "]";
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString()+ parenthesis);

			/*宣言の生成*/
			SyntaxDeclaration pSynVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynVarDec);
		}
		
		/*ArithVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			String parenthesis = "";
			for(int j = 0; j < parenthesisnum-1; j++){
				parenthesis += "[" + COMPROG_DEFINE_MAXARRAYNUM_NAME + "]";
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString()+ parenthesis);

			/*宣言の生成*/
			SyntaxDeclaration pSynVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynVarDec);
		}
		
		/*Constの宣言*/
		for (int i = 0; i <m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}
		
		/*OutputVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

			if(parenthesisnum < 2){
				/*double型ポインタ配列構文生成*/
				SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

				/*宣言用変数の生成*/
				Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString());

				/*宣言の生成*/
				SyntaxDeclaration pSynConstVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

				/*宣言の追加*/
				pSynMainFunc.addDeclaration(pSynConstVarDec);
			}else{
				/*double型ポインタ配列構文生成*/
				SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
				String parenthesis = "";
				for(int j = 0; j < parenthesisnum-2; j++){
					parenthesis += "[" + COMPROG_DEFINE_MAXARRAYNUM_NAME + "]";
				}
				
				/*宣言用変数の生成*/
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString()+ parenthesis);
	
				/*宣言の生成*/
				SyntaxDeclaration pSynVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);
	
				/*宣言の追加*/
				pSynMainFunc.addDeclaration(pSynVarDec);
			}
		}
		
		/*RecurVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			if(parenthesisnum ==1 ){
				Math_ci pMathRecurSize =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_DEFINE_MAXARRAYNUM_NAME);
				
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i),
						pMathRecurSize));
				
			}else if(1 < parenthesisnum){
				Math_times pMathTimes =
					(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
				for(int j = 0; j < parenthesisnum; j++){
					Math_ci pMathRecurSize =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								COMPROG_DEFINE_MAXARRAYNUM_NAME);
					pMathTimes.addFactor(pMathRecurSize);
				}
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i),
						pMathTimes));
			}
		}
			
		/*ArithVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			if(parenthesisnum ==1 ){
				Math_ci pMathRecurSize =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_DEFINE_MAXARRAYNUM_NAME);
				
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i),
						pMathRecurSize));
				
			}else if(1 < parenthesisnum){
				Math_times pMathTimes =
					(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
				for(int j = 0; j < parenthesisnum; j++){
					Math_ci pMathRecurSize =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								COMPROG_DEFINE_MAXARRAYNUM_NAME);
					pMathTimes.addFactor(pMathRecurSize);
				}
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i),
						pMathTimes));
			}
		}
		
		/*OutputVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

			if(parenthesisnum-1 == 1 ){
				Math_ci pMathRecurSize =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						COMPROG_DEFINE_MAXARRAYNUM_NAME);
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i),
						pMathRecurSize));
				
			}else if(1 < parenthesisnum-1){
				Math_times pMathTimes =
					(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
				for(int j = 0; j < parenthesisnum; j++){
					Math_ci pMathRecurSize =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								COMPROG_DEFINE_MAXARRAYNUM_NAME);
					pMathTimes.addFactor(pMathRecurSize);
				}
				pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i),
						pMathTimes));
			}
		}
		
		
		
		int MaxLoopNumber = 1;
		pSynMainFunc.addStatement(this.MainFuncSyntaxStatementList(MaxLoopNumber));
		
		
//		----------------------------------------------
		//free関数呼び出しの追加
		//----------------------------------------------
		/*RecurVar変数のfree*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {
			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree( m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i)));
		}
		/*ArithVar変数のfree*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {
			/*宣言の追加*/
			pSynMainFunc.addStatement(createFree( m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i)));
		}
		/*OutputVar変数のfree*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			/*宣言の追加*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));
			if(1 < parenthesisnum){
				pSynMainFunc.addStatement(createFree( m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i)));
			}
		}

		/*プログラム構文を返す*/
		return pSynProgram;
	}
	
	protected SyntaxStatementList MainFuncSyntaxStatementList(int MaxLoopNumber) throws SyntaxException {
		SyntaxStatementList aStatementList = null;

		// add declaration for main function

		// add first do-while loop structure
		// for debug
		//String[] strAttr_Now = new String[] {"pre", null, null};
		String[] strAttr_Now = new String[] {null, null, null, null, null};
		try {
			// for debug
			//aStatementList = MakeDowhileLoop(MaxLoopNumber, 1, strAttr_Now);
			aStatementList = MakeDowhileLoop(MaxLoopNumber, 0, strAttr_Now);
		} catch (TranslateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return aStatementList;
	}
	
	protected SyntaxStatementList MakeDowhileLoop(int MaxLoopNumber, int LoopNumber, String[] strAttr_Now) throws TranslateException, MathException, SyntaxException {
		SyntaxStatementList aStatementList = new SyntaxStatementList();
		
		/*----- process "pre" -----*/
		String[] strAttr_pre = strAttr_Now.clone();
		strAttr_pre[LoopNumber] = "pre";
		SyntaxStatementList aStatementList_pre = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_pre)) {
			// strAttr[LoopNumber] = "pre" has inner loopStructure
			aStatementList_pre.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_pre), strAttr_pre));
		}
		else {
			// strAttr[LoopNumber] = "pre" has no inner loop
			aStatementList_pre.addStatement(createStatementList(strAttr_pre));
		}
		aStatementList.addStatement(aStatementList_pre);
		// for debug
		String strNow = null;
		strNow = aStatementList.toLegalString();
		//System.out.println(strNow);
		
		/*----- process init -----*/
		String[] strAttr_init = strAttr_Now.clone();
		SyntaxStatementList aStatementList_init = new SyntaxStatementList();
		strAttr_init[LoopNumber] = "init";
		if (m_pRecMLAnalyzer.hasChild(strAttr_init)) {
			// strAttr[LoopNumber] = "init" has inner loopStructure
			aStatementList_init.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_init), strAttr_init));
		} else {
			// strAttr[LoopNumber] = "init" has no inner loop
			aStatementList_init.addStatement(createStatementList(strAttr_init));
		}
		aStatementList.addStatement(aStatementList_init);
		// for debug
		strNow = aStatementList.toLegalString();
		//System.out.println(strNow);

		/*----- process loop structure -----*/
		// strAttr[LoopNumber] has 
		if( m_pRecMLAnalyzer.hasInner(LoopNumber) ){
			/* create loop structure */
			/*----- create "tn" = 0 (loop index string = RecMLAnalyzer.getIndexString(LoopNumber)) -----*/
			aStatementList.addStatement(createInitEqu(LoopNumber));
			// for debug
			strNow = aStatementList.toLegalString();
			//System.out.println(strNow);

			/*----- create loop condition -----*/
			SyntaxControl pSynDowhile = createSyntaxDowhile(LoopNumber, strAttr_Now);
			aStatementList.addStatement(pSynDowhile);
			// for debug
			strNow = aStatementList.toLegalString();
			//System.out.println(strNow);

			/*----- create inner Statements and add to Do While loop -----*/
			String[] strAttr_inner = strAttr_Now.clone();
			strAttr_inner[LoopNumber] = "inner";
			SyntaxStatementList aStatementList_inner = new SyntaxStatementList();
			if (m_pRecMLAnalyzer.hasChild(strAttr_inner)) {
				//strAttr[LoopNumber] = "inner" has inner loop structure
				aStatementList_inner.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_inner), strAttr_inner));
			} else {
				//strAttr[LoopNumber] = "inner" has no inner loop
				aStatementList_inner.addStatement(createStatementList(strAttr_inner));
			}
			pSynDowhile.addStatement(aStatementList_inner);
			// for debug
			strNow = aStatementList.toLegalString();
			//System.out.println(strNow);

			/*----- insert loop counter increment -----*/
			pSynDowhile.addStatement(createIndexIncrementEqu(LoopNumber));
			// for debug
			strNow = aStatementList.toLegalString();
			//System.out.println(strNow);
		}
		
		/*----- process final -----*/
		String[] strAttr_final = strAttr_Now.clone();
		strAttr_final[LoopNumber] = "final";
		SyntaxStatementList aStatementList_final = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_final)) {
			// strAttr[LoopNumber] = "final" has inner loopStructure
			aStatementList_final.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_final), strAttr_final));
		} else {
			// strAttr[LoopNumber] = "final" has no inner loop
			aStatementList_final.addStatement(createStatementList(strAttr_final));
		}
		aStatementList.addStatement(aStatementList_final);
		// for debug
		strNow = aStatementList.toLegalString();
		//System.out.println(strNow);
		
		/*----- process post -----*/
		String[] strAttr_post = strAttr_Now.clone();
		strAttr_post[LoopNumber] = "post";
		SyntaxStatementList aStatementList_post = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_post)) {
			// strAttr[LoopNumber] = "post" has inner loopStructure
			aStatementList_post.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_post), strAttr_post));
		} else {
			// strAttr[LoopNumber] = "post" has no inner loop
			aStatementList_post.addStatement(createStatementList(strAttr_post));
		}
		aStatementList.addStatement(aStatementList_post);
		// for debug
		strNow = aStatementList.toLegalString();
		//System.out.println(strNow);
		
		return aStatementList;
	}
			
	//========================================================
	//initialize
	// 初期化メソッド
	//
	//========================================================
	/*-----初期化・終了処理メソッド-----*/
	protected void initialize() throws MathException {
		m_pDefinedDataSizeVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   COMPROG_DEFINE_DATANUM_NAME);
	}

	//========================================================
	//createExpressions
	// 計算式部を生成し，ベクタを返す
	//
	//@return
	// 計算式ベクタ
	//
	//@throws
	// TranslateException
	//
	//========================================================
	/*-----計算式生成メソッド-----*/
//	protected Vector<SyntaxExpression> createExpressions(String[] strAttrCE)
//	throws TranslateException, MathException {
//		//---------------------------------------------
//		//式生成のための前処理
//		//---------------------------------------------
//		/*ベクタを初期化*/
//		Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();
//
//		//---------------------------------------------
//		//式の追加
//		//---------------------------------------------
//		/*数式数を取得*/
////		System.out.println("loop1 = " + strAttr2[0] + "\n");
//		ArrayList expIndex2 = new ArrayList();
//		expIndex2 = m_pRecMLAnalyzer.getExpressionWithAttr(strAttrCE);
//			
//			for (int j=0; j < expIndex2.size(); j++){
//				int index = Integer.parseInt(expIndex2.get(j).toString());
//
//				/*数式の複製を取得*/
//				MathExpression pMathExp = m_pRecMLAnalyzer.getExpression(index);
//			
//				/*左辺式・右辺式取得*/
//				MathExpression pLeftExp = pMathExp.getLeftExpression();
//				MathExpression pRightExp = pMathExp.getRightExpression();
//	
//				if (pLeftExp == null || pRightExp == null) {
//					throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
//								     "failed to parse expression");
//				}
//	
//				/*代入文の形成*/
//				Math_assign pMathAssign =
//					(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//				pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
//				pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());
//	
//				/*新たな計算式を生成*/
//				MathExpression pNewExp = new MathExpression(pMathAssign);
//	
//				/*数式ベクタに追加*/
//				SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
//				vecExpressions.add(pSyntaxExp);
//
//			}
//	}
			
			
	protected SyntaxStatementList createStatementList(String[] strAttrCE) throws TranslateException, MathException {
		SyntaxStatementList aStatementList = new SyntaxStatementList();
		Vector<SyntaxExpression> vecExpression = this.createExpressions(strAttrCE);
		for (int i = 0; i < vecExpression.size(); i++) {
			aStatementList.addStatement(vecExpression.get(i));
		}
		return aStatementList;
	}
			
			
	protected Vector<SyntaxExpression> createExpressions(String[] strAttrCE)
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
//				System.out.println("loop1 = " + strAttr2[0] + "\n");
		ArrayList expIndex2 = new ArrayList();
		expIndex2 = m_pRecMLAnalyzer.getExpressionWithAttr(strAttrCE);
			
			for (int j=0; j < expIndex2.size(); j++){
				int index = Integer.parseInt(expIndex2.get(j).toString());

				/*数式の複製を取得*/
				MathExpression pMathExp = m_pRecMLAnalyzer.getExpression(index);
			
				/*左辺式・右辺式取得*/
				MathExpression pLeftExp = pMathExp.getLeftExpression();
				MathExpression pRightExp = pMathExp.getRightExpression();
	
				if (pLeftExp == null || pRightExp == null) {
					throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
								     "failed to parse expression");
				}
	
				/*代入文の形成*/
				Math_assign pMathAssign =
					(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
				pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
				pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());
	
				/*新たな計算式を生成*/
				MathExpression pNewExp = new MathExpression(pMathAssign);
	
				/*数式ベクタに追加*/
				SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
				vecExpressions.add(pSyntaxExp);

			}
//		//---------------------------------------------
//		//出力変数から入力変数への代入式の追加
//		// (TecMLには記述されていない式を追加する)
//		//---------------------------------------------
//		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
//			/*代入式の構成*/
//			Math_assign pMathAssign =
//				(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar().createCopy());
//			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar().createCopy());
//			MathExpression pMathExp = new MathExpression(pMathAssign);
////
////			/*添え字の追加*/
////			this.addIndexToTecMLVariables(pMathExp, i);
//
//			/*数式ベクタに追加*/
//			SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
//			vecExpressions.add(pSyntaxExp);
//		}
		return vecExpressions;
	}

//	/*-----計算式生成メソッド-----*/
//	protected Vector<SyntaxExpression> createExpressions()
//	throws TranslateException, MathException {
//		//---------------------------------------------
//		//式生成のための前処理
//		//---------------------------------------------
//		/*ベクタを初期化*/
//		Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();
//
//		//---------------------------------------------
//		//式の追加
//		//---------------------------------------------
//		/*数式数を取得*/
//		int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();
//
//		for (int i = 0; i < nExpressionNum; i++) {
//
//			/*数式の複製を取得*/
//			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);
//
//			/*左辺式・右辺式取得*/
//			MathExpression pLeftExp = pMathExp.getLeftExpression();
//			MathExpression pRightExp = pMathExp.getRightExpression();
//
//			if (pLeftExp == null || pRightExp == null) {
//				throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
//							     "failed to parse expression");
//			}
//
//			/*左辺変数取得*/
//			MathOperand pLeftVar = (MathOperand)pLeftExp.getFirstVariable();
//
//			//-------------------------------------------
//			//左辺式ごとに数式の追加
//			//-------------------------------------------
//			/*微係数変数*/
//			if (m_pTecMLAnalyzer.isDerivativeVar(pLeftVar)) {
//
//				/*微分式の数を取得*/
//				int nDiffExpNum = m_pCellMLAnalyzer.getM_vecDiffExpression().size();
//
//				/*数式の出力*/
//				for (int j = 0; j < nDiffExpNum; j++) {
//
//					/*代入文の形成*/
//					Math_assign pMathAssign =
//						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
//					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());
//
//					/*新たな計算式を生成*/
//					MathExpression pNewExp = new MathExpression(pMathAssign);
//
//					/*TecML変数に添え字を付加*/
//					this.addIndexToTecMLVariables(pNewExp, j);
//
//					/*微分式インスタンスのコピー取得*/
//					MathExpression pDiffExpression =
//						m_pCellMLAnalyzer.getM_vecDiffExpression().get(j).createCopy();
//
//					/*微分関数の展開*/
//					this.expandDiffFunction(pNewExp, pDiffExpression);
//
//					/*数式ベクタに追加*/
//					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
//					vecExpressions.add(pSyntaxExp);
//				}
//
//			}
//
//			/*微分変数*/
//			else if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {
//
//				/*微分式の数を取得*/
//				int nDiffVarNum = m_pCellMLAnalyzer.getM_vecDiffVar().size();
//
//				/*数式の出力*/
//				for (int j = 0; j < nDiffVarNum; j++) {
//
//					/*代入文の形成*/
//					Math_assign pMathAssign =
//						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
//					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());
//
//					/*新たな計算式を生成*/
//					MathExpression pNewExp = new MathExpression(pMathAssign);
//
//					/*添え字の付加*/
//					this.addIndexToTecMLVariables(pNewExp, j);
//
//					/*数式ベクタに追加*/
//					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
//					vecExpressions.add(pSyntaxExp);
//				}
//			}
//
//			/*通常変数*/
//			else if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
//				/*微分式の数を取得*/
//				int nNonDiffExpNum = m_pCellMLAnalyzer.getM_vecNonDiffExpression().size();
//
//				/*数式の出力*/
//				for (int j = 0; j < nNonDiffExpNum; j++) {
//
//					/*代入文の形成*/
//					Math_assign pMathAssign =
//						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//					pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
//					pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());
//
//					/*新たな計算式を生成*/
//					MathExpression pNewExp = new MathExpression(pMathAssign);
//
//					/*TecML変数に添え字を付加*/
//					this.addIndexToTecMLVariables(pNewExp, j);
//
//					/*微分式インスタンスのコピー取得*/
//					MathExpression pNonDiffExpression =
//						m_pCellMLAnalyzer.getM_vecNonDiffExpression().get(j).createCopy();
//
//					/*微分関数の展開*/
//					this.expandNonDiffFunction(pNewExp, pNonDiffExpression);
//
//					/*数式ベクタに追加*/
//					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
//					vecExpressions.add(pSyntaxExp);
//				}
//			}
//
//			/*定数変数*/
//			else if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
//			}
//
//		}
//
//		//---------------------------------------------
//		//出力変数から入力変数への代入式の追加
//		// (TecMLには記述されていない式を追加する)
//		//---------------------------------------------
//		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
//			/*代入式の構成*/
//			Math_assign pMathAssign =
//				(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar().createCopy());
//			pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar().createCopy());
//			MathExpression pMathExp = new MathExpression(pMathAssign);
//
//			/*添え字の追加*/
//			this.addIndexToTecMLVariables(pMathExp, i);
//
//			/*数式ベクタに追加*/
//			SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
//			vecExpressions.add(pSyntaxExp);
//		}
//
//		return vecExpressions;
//	}
	
	/*-----関数展開・変数置換メソッド-----*/

	//========================================================
	//expandDiffFunction
	// 微分関数展開メソッド
	//
	//@arg
	// MathExpression*	pExpression	: 数式インスタンス
	// MathExpression*	pDiffExpression	: 微分式インスタンス
	//
	//========================================================
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
			pExpression.replace(pFunction, pDiffExpression.getRightExpression().getRootFactor());

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

	//========================================================
	//expandDiffFunction
	// 非微分関数展開メソッド
	//
	//@arg
	// MathExpression*	pExpression			: 数式インスタンス
	// MathExpression*	pNonDiffExpression	: 非微分式インスタンス
	//
	//========================================================
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
			pExpression.replace(pFunction, pNonDiffExpression.getRightExpression().getRootFactor());

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

	//========================================================
	//replaceFunctionVariables
	// 関数中変数の置換メソッド
	//
	//@arg
	// MathExpression*	pExpression	: 数式インスタンス
	// Math_fn*		pFunction		: 置換関数
	// int			nTimeVarArgIdx	: 微分変数引数インデックス
	// int			nTimeArgIdx		: 時間変数引数インデックス
	// int			nVarArgIdx		: 通常変数引数インデックス
	// int			nConstVarArgIdx	: 定数引数インデックス
	//
	//========================================================
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

	//========================================================
	//addIndexToTecMLVariables
	// TecML変数へのインデックス追加メソッド
	//
	//@arg
	// MathExpression*	pExpression	: 数式インスタンス
	// int	nIndex	: 付加するインデックス
	//
	//========================================================
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
			pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i),pArgVar);
		}
	}

	protected boolean hasInner(int LoopNumber, String[] strAttr) {
		String[] strAttr_inner = strAttr.clone();
		strAttr_inner[LoopNumber] = "inner";
		try {
			Vector<SyntaxExpression> vecExpressions = createExpressions(strAttr_inner);
			if (vecExpressions.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (TranslateException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// create "tn = 0"
	protected SyntaxStatement createInitEqu(int LoopNumber) {
		MathExpression tnINIT = new MathExpression();
		Math_apply tnINITApply = new Math_apply();
		tnINIT.addOperator(tnINITApply);
		Math_assign tnINITAssign = new Math_assign();
		tnINITApply.addFactor(tnINITAssign);
//		Math_ci tnINITTn = new Math_ci("tn");
//		Math_ci tnINITTn = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
		Math_ci tnINITTn = new Math_ci(m_pRecMLAnalyzer.getIndexHashMap(LoopNumber));
		tnINITAssign.addFactor(tnINITTn);
		Math_cn tnINIT0 = new Math_cn("0");
		/*double消す*/
		tnINIT0.changeType();
		tnINITAssign.addFactor(tnINIT0);
		SyntaxStatement aStatement = new SyntaxExpression(tnINIT);
		return aStatement;
	}
	
	// create "tn = tn + 1"
	protected SyntaxExpression createIndexIncrementEqu(int LoopNumber) {
		MathExpression tnpINNER = new MathExpression();
		Math_apply tnpINNERApply = new Math_apply();
		tnpINNER.addOperator(tnpINNERApply);
		Math_assign tnpINNERAssign = new Math_assign();
		tnpINNERApply.addFactor(tnpINNERAssign);
		//Math_ci tnpINNERTnL = new Math_ci("tn");
//		Math_ci tnpINNERTnL = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
		Math_ci tnpINNERTnL = new Math_ci(m_pRecMLAnalyzer.getIndexHashMap(LoopNumber));
		tnpINNERAssign.addFactor(tnpINNERTnL);
		Math_apply tnpINNERApplyR = new Math_apply();
		tnpINNERAssign.addFactor(tnpINNERApplyR);
		Math_plus tnpINNERPlusR = new Math_plus();
		tnpINNERApplyR.addFactor(tnpINNERPlusR);
		//Math_ci tnpINNERTnR = new Math_ci("tn");
//		Math_ci tnpINNERTnR = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
		Math_ci tnpINNERTnR = new Math_ci(m_pRecMLAnalyzer.getIndexHashMap(LoopNumber));
		tnpINNERPlusR.addFactor(tnpINNERTnR);
		Math_cn tnpINNER1R = new Math_cn("1");
		/*double消す*/
		tnpINNER1R.changeType();
		tnpINNERPlusR.addFactor(tnpINNER1R);
		SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
		return pSyntaxExpINNER;
	}
	
	protected SyntaxControl createSyntaxDowhile(int LoopNumber, String[] strAttr) {
		String[] strAttr_loopcond = strAttr.clone();
		strAttr_loopcond[LoopNumber] = "loopcond";
		ArrayList expIndex = new ArrayList();
		expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_loopcond);

		//Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
		//pMathLt.addFactor(null);
		//pMathLt.addFactor(null);
		//MathExpression pConditionExp = new MathExpression(pMathLt);
		MathExpression pConditionExp = m_pRecMLAnalyzer.getExpression((Integer)expIndex.get(0));
		/*double消す*/
		((MathOperator)(pConditionExp.getRootFactor())).changeIndexInteger();
		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
		SyntaxControl pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
		return pSynDowhile;
	}
	
	
}
