package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.Exception;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.NewtonSolver;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.NewtonSolverJava;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.SimultaneousNewtonSolver;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.SimultaneousNewtonSolverJava;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.CudaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.SimpleProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * ODE Java Program Generator class 

 * @author n-washio
 * 
 */

public class ODEJavaProgramGenerator extends ProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
	private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";
	private static final String COMPROG_DEFINE_MAXARRAYNUM_NAME = "__MAX_ARRAY_NUM";

	/*共通変数*/
	protected Math_ci m_pDefinedDataSizeVar;		//データ数として#defineされる定数
	
	public Vector<Math_ci> m_vecMaxArraySizeName;
	public Vector<Math_ci> getMaxArraySizeName() {
		return m_vecMaxArraySizeName;
	}
	public void setMaxArraySizeName(int maxLoopIndexNum) 
	throws MathException {
		for (int i=0; i<maxLoopIndexNum; i++) {
			m_vecMaxArraySizeName.add( (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, COMPROG_DEFINE_MAXARRAYNUM_NAME ));
		}
	}
	
	/*-----コンストラクタ-----*/
	public ODEJavaProgramGenerator(RecMLAnalyzer pRecMLAnalyzer)
	throws MathException 	{
		super(pRecMLAnalyzer);
		m_pDefinedDataSizeVar = null;
		m_vecMaxArraySizeName = new Vector<Math_ci>();
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
		pSynProgram.setJavaFlag();

		/*プリプロセッサ構文生成・追加*/
		SyntaxPreprocessor pSynInclude1 = new SyntaxPreprocessor(ePreprocessorKind.JAVA_IMPORT, "java.lang.Math");

		pSynProgram.addPreprocessor(pSynInclude1);

		
		ArrayList<SyntaxFunction> pSynSolverFuncList = new ArrayList<SyntaxFunction>();
		
		ArrayList<Long> simulIDList = new ArrayList<Long>();
		
		//非線形数式に対してソルバー関数を作成
		for(int i=0;i<m_pRecMLAnalyzer.getExpressionCount();i++){
			if(m_pRecMLAnalyzer.getExpression(i).getNonlinearFlag() && m_pRecMLAnalyzer.getExpression(i).getSimulID()==-1){

				MathExpression exp = m_pRecMLAnalyzer.getExpression(i);
				
				//ニュートン法計算関数
				SyntaxFunction pSynNewtonSolverFunc = this.createSolverFunction(exp);
				
				//左辺関数
				SyntaxFunction pSynNewtonSolverFunc2 = this.createLeftFunction(exp);
				//左辺微分関数
				SyntaxFunction pSynNewtonSolverFunc3 = this.createDiffFunction(exp);
				
				//テンプレート処理するため内部宣言は不要
				pSynSolverFuncList.add(pSynNewtonSolverFunc);
				
				pSynSolverFuncList.add(pSynNewtonSolverFunc2);
				pSynSolverFuncList.add(pSynNewtonSolverFunc3);
				
			}else if(m_pRecMLAnalyzer.getExpression(i).getSimulID()!=-1){
				
				//連立方程式処理
				//ここで重複処理をする.(idにつき3つ生成)
				boolean flag=false;
				for(int j=0;j<simulIDList.size();j++){
					if(m_pRecMLAnalyzer.getExpression(i).getSimulID()==simulIDList.get(j)){
						flag=true;
					}
				}
				if(!flag){
					MathExpression exp = m_pRecMLAnalyzer.getExpression(i);
					
					//ニュートン法計算関数
					SyntaxFunction pSynSimulNewtonSolverFunc = this.createSimulNewtonFunction(exp);
					
					//左辺関数
					SyntaxFunction pSynSimulNewtonSolverFunc2 = this.createSimulFunction(exp);
					//左辺微分関数
					SyntaxFunction pSynSimulNewtonSolverFunc3 = this.createJacobiFunction(exp);
					
					//テンプレート処理するため内部宣言は不要
					pSynSolverFuncList.add(pSynSimulNewtonSolverFunc);
					
					pSynSolverFuncList.add(pSynSimulNewtonSolverFunc2);
					pSynSolverFuncList.add(pSynSimulNewtonSolverFunc3);
					
				}
				
				simulIDList.add(m_pRecMLAnalyzer.getExpression(i).getSimulID());
				
			}
			
		}
		SyntaxFunction pSynMainFunc = this.createMainFunction();
		
		pSynProgram.addFunction(pSynMainFunc);
		
		
		
		
		//----------------------------------------------
		//配列最大長を宣言
		//----------------------------------------------
		
		/*int型構文生成*/
		SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_INT,0);

		/*宣言用変数の生成*/
		Math_ci pDataNumVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"__DATA_NUM");

		/*初期化式の生成*/
		String strElementNum = String.valueOf(m_unElementNum);
		Math_cn pConstDataNum =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,strElementNum);
		pConstDataNum.changeType();
		MathExpression pInitExpression = new MathExpression(pConstDataNum);

		/*宣言の生成*/
		SyntaxDeclaration pSynDataNum = new SyntaxDeclaration(pSynTypeInt,pDataNumVar);

		/*初期化式の追加*/
		pSynDataNum.addInitExpression(pInitExpression);

		/*宣言の追加*/
		pSynMainFunc.addDeclaration(pSynDataNum);
		
		
		
		
		//ソルバー関数を追加
		for(int i=0;i<pSynSolverFuncList.size();i++){
			pSynProgram.addFunction(pSynSolverFuncList.get(i));
		}
		

		
		//連立式セットの配列を宣言に追加
		/*Declare a blank loop for allocating one-dimensional arrays*/
		SyntaxControl pSynMallocBlank_simul = null;
		if(m_pRecMLAnalyzer.simulEquationList.size()!=0){
			
			for(int i=0;i<m_pRecMLAnalyzer.simulEquationList.size();i++){
				/*double型ポインタ配列構文生成*/
				SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
				
				
				
				/*宣言用変数の生成*/
				Math_ci pDecVar1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+i+"[]");
				/*宣言用変数の生成*/
				Math_ci pDecVar2 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+i);
				
				/*宣言の生成*/
				SyntaxDeclaration pSynVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar1);

				/*宣言の追加*/
				pSynMainFunc.addDeclaration(pSynVarDec);
				
				/*データ数を表す数式を生成*/
				Math_apply pMathApply =
					(Math_apply)MathFactory.createOperator(eMathOperator.MOP_APPLY);
				
				pMathApply.addFactor(m_pDefinedDataSizeVar);
		
				/*宣言の追加*/
				pSynMainFunc.addStatement(createNew(pDecVar2, pMathApply,1));	
			}

		}
		
		/*RecurVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			int parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			
			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			
			String declaration = "";
			String indexString="[]";
			for(int j=0;j<parenthesisnum;j++){
				declaration=declaration+indexString;
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString()+declaration);

			/*宣言の生成*/
			SyntaxDeclaration pSynVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynVarDec);
		}
		
		/*ArithVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {
			
			HashMap<Math_ci, Integer> ArithVarHM_G = new HashMap<Math_ci, Integer>();
			ArithVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			int parenthesisnum = ArithVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			
			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			
			String declaration = "";
			String indexString="[]";
			for(int j=0;j<parenthesisnum;j++){
				declaration=declaration+indexString;
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString()+declaration);
			
			/*宣言の生成*/
			SyntaxDeclaration pSynVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynVarDec);
		}
		
		/*Constの宣言*/
		for (int i = 0; i <m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {

			HashMap<Math_ci, Integer> ConstVarHM_G = new HashMap<Math_ci, Integer>();
			ConstVarHM_G = m_pRecMLAnalyzer.getM_HashMapConstVar();
			int parenthesisnum = ConstVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i));
			
			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			
			String declaration = "";
			String indexString="[]";
			for(int j=0;j<parenthesisnum;j++){
				declaration=declaration+indexString;
			}
			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString()+declaration);

			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}
		
		/*OutputVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			
			HashMap<Math_ci, Integer> OutputVarHM_G = new HashMap<Math_ci, Integer>();
			OutputVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			int parenthesisnum = OutputVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			
			String declaration = "";
			String indexString="[]";
			for(int j=0;j<parenthesisnum;j++){
				declaration=declaration+indexString;
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString()+declaration);
			
			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}
		
		/*StepVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListStepVar().size(); i++) {
			
			HashMap<Math_ci, Integer> StepVarHM_G = new HashMap<Math_ci, Integer>();
			StepVarHM_G = m_pRecMLAnalyzer.getM_HashMapStepVar();
			int parenthesisnum = StepVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListStepVar().get(i));

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_INT, 0);
			
			String declaration = "";
			String indexString="[]";
			for(int j=0;j<parenthesisnum;j++){
				declaration=declaration+indexString;
			}
			
			/*宣言用変数の生成*/
			Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListStepVar().get(i).toLegalString()+declaration);
			
			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}
		

		
		//----------------------------------------------
		//配列のインスタンスを行う構文の追加
		//----------------------------------------------

		/*RecurVar変数のインスタンス生成*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			int parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			
			if(parenthesisnum!=0){
				/*データ数を表す数式を生成*/
				Math_apply pMathApply =
					(Math_apply)MathFactory.createOperator(eMathOperator.MOP_APPLY);
				
				pMathApply.addFactor(m_pDefinedDataSizeVar);
				
				
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString());
				
				/*宣言の追加*/
				pSynMainFunc.addStatement(createNew(pDecVar, pMathApply,parenthesisnum));	
			}
			
		}
		
		/*ArithVar変数のインスタンス生成*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			HashMap<Math_ci, Integer> ArithVarHM_G = new HashMap<Math_ci, Integer>();
			ArithVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			int parenthesisnum = ArithVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			
			if(parenthesisnum!=0){
				/*データ数を表す数式を生成*/
				Math_apply pMathApply =
					(Math_apply)MathFactory.createOperator(eMathOperator.MOP_APPLY);
				
				pMathApply.addFactor(m_pDefinedDataSizeVar);
				
				
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString());
				
				/*宣言の追加*/
				pSynMainFunc.addStatement(createNew(pDecVar, pMathApply,parenthesisnum));	
			}
			
		}
		
		/*OutputVar変数のインスタンス生成*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {

			HashMap<Math_ci, Integer> OutputVarHM_G = new HashMap<Math_ci, Integer>();
			OutputVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			int parenthesisnum = OutputVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));
			
			if(parenthesisnum!=0){
				/*データ数を表す数式を生成*/
				Math_apply pMathApply =
					(Math_apply)MathFactory.createOperator(eMathOperator.MOP_APPLY);
				
				pMathApply.addFactor(m_pDefinedDataSizeVar);
				
				
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString());
				
				/*宣言の追加*/
				pSynMainFunc.addStatement(createNew(pDecVar, pMathApply,parenthesisnum));	
			}
			
		}
		
		
		//----------------------------------------------
		//変数の初期化を行う構文の追加
		//----------------------------------------------
		
		//ConstVarの初期化
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {
			
			Math_ci pVariable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());
			String initial_valueString = String.valueOf(m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).getValue());
			Math_cn initial_value = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, initial_valueString);
			
			Math_assign pMathAssign = (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
			Math_cn pZero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
			pZero.changeType();
			pMathAssign.addFactor(pVariable);
			pMathAssign.addFactor(initial_value);
			
			MathExpression pMathApExpression = new MathExpression(pMathAssign);
			SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathApExpression);
			
			//構文追加
			pSynMainFunc.addStatement(pSyntaxExp);
		}
		
		
		//----------------------------------------------
		//計算を行う構文の追加
		//----------------------------------------------
		
		//rootとなるループ番号を取得
		int rootNum = m_pRecMLAnalyzer.getRoot().loopNumber;
		pSynMainFunc.addStatement(this.MainFuncSyntaxStatementList(rootNum));
		
		if(pSynMallocBlank_simul!=null){
			pSynMainFunc.addStatement(pSynMallocBlank_simul);
		}
		/*プログラム構文を返す*/
		return pSynProgram;
	}
	
	protected SyntaxStatementList MainFuncSyntaxStatementList(int root) throws SyntaxException {
		SyntaxStatementList aStatementList = null;

		// add declaration for main function

		// add first do-while loop structure
		// for debug
		//String[] strAttr_Now = new String[] {"pre", null, null};
		String[] strAttr_Now = new String[] {null, null, null, null, null};
		try {

			//取得したルート要素のループ番号から生成
			aStatementList = MakeDowhileLoop(root, strAttr_Now);
		} catch (TranslateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return aStatementList;
	}
	
	protected SyntaxStatementList MakeDowhileLoop(int LoopNumber, String[] strAttr_Now) throws TranslateException, MathException, SyntaxException {
		SyntaxStatementList aStatementList = new SyntaxStatementList();
		
		/*----- process "pre" -----*/
		String[] strAttr_pre = strAttr_Now.clone();
		strAttr_pre[LoopNumber] = "pre";
		
		SyntaxStatementList aStatementList_pre = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_pre)) {
			// strAttr[LoopNumber] = "pre" has inner loopStructure
			aStatementList_pre.addStatement(MakeDowhileLoop(m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_pre), strAttr_pre));
		}
		else {
			// strAttr[LoopNumber] = "pre" has no inner loop
			aStatementList_pre.addStatement(createStatementList(strAttr_pre));
		}
		aStatementList.addStatement(aStatementList_pre);

		
		/*----- process init -----*/
		String[] strAttr_init = strAttr_Now.clone();
		SyntaxStatementList aStatementList_init = new SyntaxStatementList();
		strAttr_init[LoopNumber] = "init";
		if (m_pRecMLAnalyzer.hasChild(strAttr_init)) {
			// strAttr[LoopNumber] = "init" has inner loopStructure
			aStatementList_init.addStatement(MakeDowhileLoop(m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_init), strAttr_init));
		} else {
			// strAttr[LoopNumber] = "init" has no inner loop
			aStatementList_init.addStatement(createStatementList(strAttr_init));
		}
		aStatementList.addStatement(aStatementList_init);


		/*----- process loop structure -----*/
		// strAttr[LoopNumber] has 
		if( m_pRecMLAnalyzer.hasInner(LoopNumber) ){
			/* create loop structure */
			/*----- create "tn" = 0 (loop index string = RecMLAnalyzer.getIndexString(LoopNumber)) -----*/
			aStatementList.addStatement(createInitEqu(LoopNumber));


			/*----- create loop condition -----*/
			SyntaxControl pSynDowhile = createSyntaxDowhile(LoopNumber, strAttr_Now);
			aStatementList.addStatement(pSynDowhile);

			/*----- create inner Statements and add to Do While loop -----*/
			String[] strAttr_inner = strAttr_Now.clone();
			strAttr_inner[LoopNumber] = "inner";
			SyntaxStatementList aStatementList_inner = new SyntaxStatementList();
			if (m_pRecMLAnalyzer.hasChild(strAttr_inner)) {
				//strAttr[LoopNumber] = "inner" has inner loop structure
				aStatementList_inner.addStatement(
						MakeDowhileLoop(m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_inner), strAttr_inner));
			} else {
				//strAttr[LoopNumber] = "inner" has no inner loop
				aStatementList_inner.addStatement(createStatementList(strAttr_inner));
			}
			pSynDowhile.addStatement(aStatementList_inner);
			
			/*----- insert loop counter increment -----*/
			pSynDowhile.addStatement(createIndexIncrementEqu(LoopNumber));

		}
		
		/*----- process final -----*/
		String[] strAttr_final = strAttr_Now.clone();
		strAttr_final[LoopNumber] = "final";
		SyntaxStatementList aStatementList_final = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_final)) {
			// strAttr[LoopNumber] = "final" has inner loopStructure
			aStatementList_final.addStatement(
					MakeDowhileLoop(m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_final), strAttr_final));
		} else {
			// strAttr[LoopNumber] = "final" has no inner loop
			aStatementList_final.addStatement(createStatementList(strAttr_final));
		}
		aStatementList.addStatement(aStatementList_final);
		
		/*----- process post -----*/
		String[] strAttr_post = strAttr_Now.clone();
		strAttr_post[LoopNumber] = "post";
		SyntaxStatementList aStatementList_post = new SyntaxStatementList();
		if (m_pRecMLAnalyzer.hasChild(strAttr_post)) {
			// strAttr[LoopNumber] = "post" has inner loopStructure
			aStatementList_post.addStatement(
					MakeDowhileLoop(m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_post), strAttr_post));
		} else {
			// strAttr[LoopNumber] = "post" has no inner loop
			aStatementList_post.addStatement(createStatementList(strAttr_post));
		}
		aStatementList.addStatement(aStatementList_post);
		
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
			
				
				if(pMathExp.getNonlinearFlag() && pMathExp.getSimulID()==-1){
					
					
					//含まれる変数リストを作成
					Vector<Math_ci> varList= new Vector<Math_ci>();
					pMathExp.getAllVariablesWithSelector(varList);
					
					//非線形式である場合,ソルバー関数を呼び出す代入文を作成.

					
					/*代入文の形成*/
					Math_assign pMathAssign =
						(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
					
					pMathAssign.addFactor(pMathExp.getDerivedVariable());
					
					String[] strAttr = new String[] {null, null, null, null, null};
					Math_fn func = (Math_fn) MathFactory.createOperator(MathMLDefinition.getMathOperatorId("fn"), strAttr);
					Math_ci funcOperand = (Math_ci) MathFactory.createOperand( eMathOperand.MOPD_CI, "newton"+pMathExp.getExID());
					
					func.setFuncOperand((MathOperand)funcOperand);
					
					for(int i=0;i<varList.size();i++){
						func.addFactor(varList.get(i));
						
					}
					pMathAssign.addFactor(func);
					
					MathExpression pNewExp = new MathExpression(pMathAssign);
					
					/*数式ベクタに追加*/
					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
					vecExpressions.add(pSyntaxExp);
						
				} 
				else if(pMathExp.getSimulID()!=-1){
					
					//連立方程式処理
					
					int SimulEquNum = -1;
					for(int k=0;k<m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).size();k++){
						if(m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).get(k).getExID()==pMathExp.getExID()){
							SimulEquNum=k;
						}
					}
					
					if(SimulEquNum==0){
						
						
						//連立成分の最初の式であれば,ソルバーをコールする構文を付加
						
						
						//simulSetへ導出変数の初期値を代入
						//simulSetをソルバーへ渡す
						//simulSetの値を導出変数へ代入
						
						
						Vector<Math_ci> derivedVarList = new Vector<Math_ci>();
						for(int i=0;i<this.m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).size();i++){
							derivedVarList.add(this.m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).get(i).getDerivedVariable());
						}
						
						for(int i=0;i<derivedVarList.size();i++){
							
							Math_assign pMathInitAssign =
									(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
							
							
							pMathInitAssign.addFactor((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+pMathExp.getSimulID()+"["+i+"]"));
				
							pMathInitAssign.addFactor(derivedVarList.get(i));
							/*新たな計算式を生成*/
							MathExpression pNewExp = new MathExpression(pMathInitAssign);
				
							/*数式ベクタに追加*/
							SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
							vecExpressions.add(pSyntaxExp);
							
						}
						
						String[] strAttr = new String[] {null, null, null, null, null};
						Math_fn func = (Math_fn) MathFactory.createOperator(MathMLDefinition.getMathOperatorId("fn"), strAttr);
						Math_ci funcOperand = (Math_ci) MathFactory.createOperand( eMathOperand.MOPD_CI, "simulNewton"+pMathExp.getSimulID());
						
						func.setFuncOperand((MathOperand)funcOperand);
						
						func.addFactor((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+pMathExp.getSimulID()));
						
						
						//含まれる変数リストを作成
						Vector<Math_ci> varList;
						Vector<Math_ci> varList_all = new Vector<Math_ci>();
						
						
						//連立成分の数式全ての変数を格納する.
						for(int k=0;k<m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).size();k++){
							
							varList = new Vector<Math_ci>();
							//各数式の変数リストを取得
							m_pRecMLAnalyzer.simulEquationList.get((int) pMathExp.getSimulID()).get(k).getAllVariablesWithSelector(varList);
							
							for(int i=0;i<varList.size();i++){
		
								//導出変数でなく重複しない場合,引数リストに追加 
								boolean overlap=false;
								for(int j1=0;j1<derivedVarList.size();j1++){
									if(varList.get(i).toLegalString().equals(derivedVarList.get(j1).toLegalString())){
										overlap=true;
									}
								}
								for(int j1=0;j1<varList_all.size();j1++){
									if(varList.get(i).toLegalString().equals(varList_all.get(j1).toLegalString())){
										overlap=true;
									}
								}
								if(!overlap){
									varList_all.add(varList.get(i));
								}
							}
						}
						for(int k=0;k<varList_all.size();k++){
							func.addFactor(varList_all.get(k));
						}
						
						
						/*新たな計算式を生成*/
						MathExpression pNewExp = new MathExpression(func);
						/*数式ベクタに追加*/
						SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
						vecExpressions.add(pSyntaxExp);
						
						/*代入文の形成*/
						for(int i=0;i<derivedVarList.size();i++){
							
							Math_assign pMathInitAssign =
									(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
							
							
							pMathInitAssign.addFactor(derivedVarList.get(i));
							pMathInitAssign.addFactor((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+pMathExp.getSimulID()+"["+i+"]"));
				
							
							/*新たな計算式を生成*/
							MathExpression pNewExp2 = new MathExpression(pMathInitAssign);
				
							/*数式ベクタに追加*/
							SyntaxExpression pSyntaxExp2 = new SyntaxExpression(pNewExp2);
							vecExpressions.add(pSyntaxExp2);
							
						}
						
					}
					
					

					
				}else{
					
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
				

			}

		return vecExpressions;
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
	
	public SyntaxFunction createMainFunction()
	throws MathException {
		/*関数本体の生成*/
		SyntaxDataType pSynIntType = new SyntaxDataType(eDataType.DT_PUB,0);
		SyntaxFunction pSynMainFunc = new SyntaxFunction("main",pSynIntType);

		/*引数宣言の生成*/
		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_STRING,0);
		Math_ci pArgvVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"args[]");
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,pArgvVar);

		/*引数宣言の追加*/
		pSynMainFunc.addParam(pSynArgvDec);

		return pSynMainFunc;
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUB,0);
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
		NewtonSolverJava ns = new NewtonSolverJava();
		//デフォルトで設定.手入力も検討.
		
		double e = 1.0e-5;//収束判定値
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUBDouble,0);
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
		NewtonSolverJava ns = new NewtonSolverJava();
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUBDouble,0);
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
		NewtonSolverJava ns = new NewtonSolverJava();
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUBDouble,0);
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
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPIntType = new SyntaxDataType(eDataType.DT_INT,0);
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "[] simulSet");
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			//導出変数でない場合,引数リストに追加 idによるソートが必要　保留
			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).toLegalString().equals(derivedVarList.get(j).toLegalString())){
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
		
		
		SimultaneousNewtonSolverJava sns = new SimultaneousNewtonSolverJava();
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUBDouble,0);
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
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPIntType = new SyntaxDataType(eDataType.DT_INT,0);
		
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "[] simulSet");
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			//導出変数でない場合,引数リストに追加  idによるソートが必要　保留
			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).toLegalString().equals(derivedVarList.get(j).toLegalString())){
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
		
		SimultaneousNewtonSolverJava sns = new SimultaneousNewtonSolverJava();
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
		SyntaxDataType pSynDoubleType = new SyntaxDataType(eDataType.DT_PUB,0);
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
		

		SyntaxDataType pSynPPCharType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		SyntaxDataType pSynPPCharType2 = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		
		Math_ci set=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "[] simulSet");
		
		
		
		/*引数宣言の追加*/
		SyntaxDeclaration pSynArgvDec = new SyntaxDeclaration(pSynPPCharType,set);
		pSynDiffrFunc.addParam(pSynArgvDec);
		for(int i=0;i<varList_new.size();i++){

			boolean overlap=false;
			for(int j=0;j<derivedVarList.size();j++){
				if(varList_new.get(i).toLegalString().equals(derivedVarList.get(j).toLegalString())){
					overlap=true;
				}
			}
			
			if(!overlap){
				Math_ci var=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, varList_new.get(i).codeName);
				pSynArgvDec = new SyntaxDeclaration(pSynPPCharType2,var);
				pSynDiffrFunc.addParam(pSynArgvDec);
			}
		}
		
		double e = 1.0e-5;//収束判定値
		int max = 1000;//最大反復数
	
	
		SimultaneousNewtonSolverJava sns = new SimultaneousNewtonSolverJava();
		String str = sns.makeSimultaneousNewtonSolver(this.m_pRecMLAnalyzer.simulEquationList.get((int) exp.getSimulID()), derivedVarList,e,max,(int) exp.getSimulID());
		
		pSynDiffrFunc.addString(str);

		return pSynDiffrFunc;
	}
	
	/**
	 * new関数呼び出し生成メソッド
	 * @param pDstVar メモリ確保先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した数式構文インスタンス
	 * @throws MathException
	 * @throws SyntaxException
	 */
	protected SyntaxExpression createNew(Math_ci pDstVar,MathFactor pDataNumFactor,int size)
	throws MathException, SyntaxException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynMallocCall = new SyntaxCallFunction("new");
		
		
		/*引数の追加*/
		pSynMallocCall.addArgFactor(pDataNumFactor);
		
		/*サイズの追加*/
		pSynMallocCall.setSize(size);
		
		/*戻り値をキャスト*/
		SyntaxDataType pSynPDoubleType = new SyntaxDataType(eDataType.DT_DOUBLE,0);
		pSynMallocCall.addCastDataType(pSynPDoubleType);

		/*代入式を生成*/
		Math_assign pMathAssign =
			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		Math_ci pMathTmpVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					pSynMallocCall.toJavaString());
		pMathAssign.addFactor(pDstVar);
		pMathAssign.addFactor(pMathTmpVar);
		MathExpression pNewExpression = new MathExpression(pMathAssign);
		SyntaxExpression pNewSynExpression = new SyntaxExpression(pNewExpression);

		/*関数呼び出しインスタンスを戻す*/
		return pNewSynExpression;
	}
}
