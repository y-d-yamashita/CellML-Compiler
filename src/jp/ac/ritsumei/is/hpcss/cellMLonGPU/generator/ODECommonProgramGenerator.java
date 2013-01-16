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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
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
 * 逐次プログラム構文生成クラス
 */
public class ODECommonProgramGenerator extends ProgramGenerator {

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
	public ODECommonProgramGenerator(RecMLAnalyzer pRecMLAnalyzer)
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
		
		//ソルバー関数を追加
		for(int i=0;i<pSynSolverFuncList.size();i++){
			pSynProgram.addFunction(pSynSolverFuncList.get(i));
		}
		
		//連立式セットの配列を宣言に追加
		/*Declare a blank loop for allocating one-dimensional arrays*/
		SyntaxControl pSynMallocBlank_simul = createSyntaxBlankLoop();
		if(m_pRecMLAnalyzer.simulEquationList.size()!=0){
			
			for(int i=0;i<m_pRecMLAnalyzer.simulEquationList.size();i++){
				/*double型ポインタ配列構文生成*/
				SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
				
				/*宣言用変数の生成*/
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+i);
				
				/*宣言の生成*/
				SyntaxDeclaration pSynVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

				/*宣言の追加*/
				pSynMainFunc.addDeclaration(pSynVarDec);
				
				
				/*Declare a blank loop for allocating one-dimensional arrays*/
				pSynMallocBlank_simul = createSyntaxBlankLoop();
				
				String size = String.valueOf(m_pRecMLAnalyzer.simulEquationList.get(i).size());
				Math_cn sizeNum = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,size );
				pSynMainFunc.addStatement(createMalloc(pDecVar, sizeNum, 1));
				
				/* free 1D memory after use */
				pSynMallocBlank_simul.addStatement(createFree(pDecVar));

			}

		}
		/*RecurVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			int parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			
			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, parenthesisnum);
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString());

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
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, parenthesisnum);
			
			/*宣言用変数の生成*/
			Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString());

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
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, parenthesisnum);

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
			
			HashMap<Math_ci, Integer> OutputVarHM_G = new HashMap<Math_ci, Integer>();
			OutputVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			int parenthesisnum = OutputVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

			/*double型ポインタ配列構文生成*/
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, parenthesisnum);
			
			/*宣言用変数の生成*/
			Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString());
			
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
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_INT, parenthesisnum);
			
			/*宣言用変数の生成*/
			Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListStepVar().get(i).toLegalString());
			
			/*宣言の生成*/
			SyntaxDeclaration pSynConstVarDec =
				new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynConstVarDec);
		}
		
		//------------------------------------------------------
		// Memory allocation codes for the simulation variables
		//------------------------------------------------------

		/* Get the maximum number of loop indices in the Recurrence Relation file and set max index number names */
		int maxIndexNum = m_pRecMLAnalyzer.getM_HashMapIndexList().size();
		this.setMaxArraySizeName(maxIndexNum);
		String strIndex0 = "i0";
		String strIndex1 = "i1";
		Math_ci loopIndex0 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strIndex0);
		Math_ci loopIndex1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strIndex1);
		
		/*Declare a blank loop for allocating one-dimensional arrays*/
		SyntaxControl pSynMallocBlank = createSyntaxBlankLoop();
		
		/*Declare the for loops for allocating multidimensional arrays*/
		SyntaxControl pSynMallocFor1 = createSyntaxDataNumLoop(this.m_vecMaxArraySizeName.get(0),loopIndex0);
		SyntaxControl pSynMallocFor2 = new SyntaxControl(null, null);
		if(this.m_vecMaxArraySizeName.size()>1){
			pSynMallocFor2 = createSyntaxDataNumLoop(this.m_vecMaxArraySizeName.get(1),loopIndex1);
		}
		/*Declare the for loops for freeing multidimensional arrays*/
		SyntaxControl pSynFreeFor1 = createSyntaxDataNumLoop(this.m_vecMaxArraySizeName.get(0),loopIndex0);
		SyntaxControl pSynFreeFor2 = new SyntaxControl(null, null);
		if(this.m_vecMaxArraySizeName.size()>1){
			pSynFreeFor2 = createSyntaxDataNumLoop(this.m_vecMaxArraySizeName.get(1),loopIndex1);
		}
		
		/*RecurVar変数へのmallocによるメモリ割り当て: Use the multidimensional arrays*/
		//TODO: the variable name can be created as dynamic and changes during compilation using Java Reflection
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			Math_ci pVariable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString());
			Math_ci pNewVariable0 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "["+ strIndex0 +"]");
			Math_ci pNewVariable1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "["+ strIndex0 +"]" + "["+ strIndex1 +"]");
			
			/* Allocate and free memory for different number of loop indices*/
			if(parenthesisnum == 1 ){	
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 1));	
				/* free 1D memory after use */
				pSynMallocBlank.addStatement(createFree(pVariable));
			}else if(parenthesisnum == 2){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 2));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 1));
					/* free 2D memory after use */
					pSynFreeFor1.addStatement(createFree(pNewVariable0));
				} 
			}else if(parenthesisnum == 3){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 3));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 2));				
					pSynMallocFor2.addStatement(createMalloc(pNewVariable1, this.m_vecMaxArraySizeName.get(2), 1));
					/* free 3D memory after use */
					pSynFreeFor2.addStatement(createFree(pNewVariable1)); 
				}
				
			}
		}
			
		/*ArithVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			Math_ci pVariable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString());
			Math_ci pNewVariable0 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0]");
			Math_ci pNewVariable1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0][i1]");
			
			/* Allocate and free memory for different number of loop indices*/
			if(parenthesisnum == 1 ){	
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 1));		
				/* free 1D memory after use */
				pSynMallocBlank.addStatement(createFree(pVariable));
			}else if(parenthesisnum == 2){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 2));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 1));
					/* free 2D memory after use */
					pSynFreeFor1.addStatement(createFree(pNewVariable0));
				}
			}else if(parenthesisnum == 3){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 3));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 2));				
					pSynMallocFor2.addStatement(createMalloc(pNewVariable1, this.m_vecMaxArraySizeName.get(2), 1));
					/* free 3D memory after use */
					pSynFreeFor2.addStatement(createFree(pNewVariable1));
				}
			}
		}
		
		/*ConstVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {

			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapConstVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i));
			Math_ci pVariable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());
			Math_ci pNewVariable0 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0]");
			Math_ci pNewVariable1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0][i1]");
			
			/* Allocate and free memory for different number of loop indices*/
			if(parenthesisnum == 1){	
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 1));		
				/* free 1D memory after use */
				pSynMallocBlank.addStatement(createFree(pVariable));
			}else if(parenthesisnum == 2){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 2));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 1));
					/* free 2D memory after use */
					pSynFreeFor1.addStatement(createFree(pNewVariable0)); 
				}
			}else if(parenthesisnum == 3){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 3));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 2));				
					pSynMallocFor2.addStatement(createMalloc(pNewVariable1, this.m_vecMaxArraySizeName.get(2), 1));
					/* free 3D memory after use */
					pSynFreeFor2.addStatement(createFree(pNewVariable1));
				}
			}
		}
		
		/*OutputVar変数へのmallocによるメモリ割り当て*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));
			Math_ci pVariable = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString());
			Math_ci pNewVariable0 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0]");
			Math_ci pNewVariable1 = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pVariable.toLegalString() + "[i0][i1]");
			
			if(parenthesisnum == 1 ){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 1));		
				/* free 1D memory after use */
				pSynMallocBlank.addStatement(createFree(pVariable));
			}else if(parenthesisnum == 2 ){
				pSynMainFunc.addStatement(createMalloc(pVariable, this.m_vecMaxArraySizeName.get(0), 2));
				if(this.m_vecMaxArraySizeName.size()>1){
					pSynMallocFor1.addStatement(createMalloc(pNewVariable0, this.m_vecMaxArraySizeName.get(1), 1));
					/* free 2D memory after use */
					pSynFreeFor1.addStatement(createFree(pNewVariable0)); 
				}
			}
		}
		
		/* Put the memory allocation for loops in the main program. Check for the number of loop index*/
		if (m_pRecMLAnalyzer.getM_HashMapIndexList().size() == 2) {
			pSynMainFunc.addStatement(pSynMallocFor1);
		}
		if (m_pRecMLAnalyzer.getM_HashMapIndexList().size() == 3) {
			pSynMainFunc.addStatement(pSynMallocFor1);
			pSynMallocFor1.addStatement(pSynMallocFor2);
		}
		
		int MaxLoopNumber = 1;
		pSynMainFunc.addStatement(this.MainFuncSyntaxStatementList(MaxLoopNumber));
		
		
		//----------------------------------------------
		//free関数呼び出しの追加
		//----------------------------------------------
		
		/* Put the memory release for loops in the main program. Check for the number of loop index*/
		if (m_pRecMLAnalyzer.getM_HashMapIndexList().size() == 1) {
			pSynMainFunc.addStatement(pSynMallocBlank);
		}
		else if (m_pRecMLAnalyzer.getM_HashMapIndexList().size() == 2) {
			pSynMainFunc.addStatement(pSynMallocBlank);
			pSynMainFunc.addStatement(pSynFreeFor1);
		}
		else if (m_pRecMLAnalyzer.getM_HashMapIndexList().size() == 3) {
			pSynMainFunc.addStatement(pSynMallocBlank);
			pSynMainFunc.addStatement(pSynFreeFor1);
			pSynFreeFor1.addStatement(pSynFreeFor2);
		}	
		
		pSynMainFunc.addStatement(pSynMallocBlank_simul);
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
									if(varList.get(i).getName().equals(derivedVarList.get(j1).getName())){
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
					}
					
					
					/*代入文の形成*/
					
					Math_assign pMathAssign2 =
							(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
					
					pMathAssign2.addFactor(pMathExp.getDerivedVariable());
					pMathAssign2.addFactor((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "simulSet"+pMathExp.getSimulID()+"["+SimulEquNum+"]"));
		
					/*新たな計算式を生成*/
					MathExpression pNewExp = new MathExpression(pMathAssign2);
		
					/*数式ベクタに追加*/
					SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
					vecExpressions.add(pSyntaxExp);
					
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
