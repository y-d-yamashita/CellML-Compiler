package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
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
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;
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
public class RecurrenceRelationGenerator extends ProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
	private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";

	/*共通変数*/
	protected Math_ci m_pDefinedDataSizeVar;		//データ数として#defineされる定数

	/*-----コンストラクタ-----*/
	public RecurrenceRelationGenerator(RecMLAnalyzer pRecMLAnalyzer)
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
	throws MathException, CellMLException, RelMLException, TranslateException {

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
		
		String[] strAttr_Original = new String[] {null, null, null};
		pSynMainFunc = this.MainFunc1(pSynMainFunc, strAttr_Original);

//		int LoopNumber = 0;
//		String[] strAttr_Original = new String[] {null, null, null};
//		pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Original, null);

		/*プログラム構文を返す*/
		return pSynProgram;
	}
	
	protected SyntaxFunction MainFunc1(SyntaxFunction pSynMainFunc, String[] strAttr_Now) throws TranslateException, MathException{
		// loop1 = "null" 
		//pSynMainFunc.addStatement();
		Vector<SyntaxExpression> vecExpressions_NULL = this.createExpressions(strAttr_Now);
		int nExpressionNum_INIT = vecExpressions_NULL.size();
		for (int i = 0; i < nExpressionNum_INIT;i++){
			/*数式の追加*/
			pSynMainFunc.addStatement(vecExpressions_NULL.get(i));
		}
		int LoopNumber = 0;
		//pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE);
		SyntaxControl pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,null);
		MakeDowhileLoop_Out(pSynMainFunc, pSynDowhile, LoopNumber, strAttr_Now);
		return pSynMainFunc;
	}

	protected SyntaxControl MakeDowhileLoop_Inner(SyntaxControl pSynDowhile_Out, int LoopNumber, String[] strAttr_Now) throws TranslateException, MathException {
		int MaxLoopNumber = strAttr_Now.length - 1;
		/*-----↓↓↓↓　initの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "init";
		Vector<SyntaxExpression> vecExpressions_INIT = this.createExpressions(strAttr_Now);
		if(vecExpressions_INIT.size()>0){
			/*---- ループ中に数式を追加 ----*/
			int nExpressionNum_INIT = vecExpressions_INIT.size();
			for (int i = 0; i < nExpressionNum_INIT;i++) {
				/*数式の追加*/
				pSynDowhile_Out.addStatement(vecExpressions_INIT.get(i));
			}
		}
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynDowhile_Out = this.MakeDowhileLoop_Inner(pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		/*-----↑↑↑↑　initの追加 ↑↑↑↑-----*/
		
		
		/*-----↓↓↓↓ loop構造の作成 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "inner";
		Vector<SyntaxExpression> vecExpressions_INNER = this.createExpressions(strAttr_Now);
		if(vecExpressions_INNER.size()>0){
			/*-----↓↓↓↓　"tn = 0"の追加 ↓↓↓↓-----*/
			Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
			MathExpression tnINIT = new MathExpression();
			Math_apply tnINITApply = new Math_apply();
			tnINIT.addOperator(tnINITApply);
			Math_assign tnINITAssign = new Math_assign();
			tnINITApply.addFactor(tnINITAssign);
			Math_ci tnINITTn = new Math_ci("tm");
			tnINITAssign.addFactor(tnINITTn);
			Math_cn tnINIT0 = new Math_cn("0");
			tnINITAssign.addFactor(tnINIT0);
			SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
			vectnINIT.add(pSyntaxExpINIT);
			
			for (int i = 0; i < vectnINIT.size();i++) {
				/*数式の追加*/
				pSynDowhile_Out.addStatement(vectnINIT.get(i));
			}
			/*-----↑↑↑↑　"tn = 0"の追加 ↑↑↑↑-----*/

			/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
			/*終了条件を取得*/
			Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
			pMathLt.addFactor(null);
			pMathLt.addFactor(null);
			MathExpression pConditionExp = new MathExpression(pMathLt);
			
			strAttr_Now[LoopNumber] = "loopcond";
			ArrayList expIndex = new ArrayList();
			expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ loopひな型作成↓↓↓↓-----*/
			for (int i=0; i < expIndex.size(); i++){
				int index = Integer.parseInt(expIndex.get(i).toString());
					pConditionExp = m_pRecMLAnalyzer.getExpression(index);
			}
			SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
			SyntaxControl pSynDowhile_Inner = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
			pSynDowhile_Out.addStatement(pSynDowhile_Inner);
			/*-----↑↑↑↑ loopひな型作成 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
			strAttr_Now[LoopNumber] = "inner";
			
			/*-----　再帰処理　-----*/
			if(LoopNumber < MaxLoopNumber){
				LoopNumber++;
				pSynDowhile_Inner = this.MakeDowhileLoop_Inner(pSynDowhile_Inner, LoopNumber, strAttr_Now);
				strAttr_Now[LoopNumber] = null;
				LoopNumber--;
			}
			int nExpressionNum = vecExpressions_INNER.size();
			for (int i = 0; i < nExpressionNum;i++) {
				/*数式の追加*/
				pSynDowhile_Inner.addStatement(vecExpressions_INNER.get(i));
			}
			/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ tn=tn+1の追加 ↓↓↓↓-----*/
			Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
			MathExpression tnpINNER = new MathExpression();
				Math_apply tnpINNERApply = new Math_apply();
				tnpINNER.addOperator(tnpINNERApply);
					Math_assign tnpINNERAssign = new Math_assign();
					tnpINNERApply.addFactor(tnpINNERAssign);
						Math_ci tnpINNERTnL = new Math_ci("tm");
						tnpINNERAssign.addFactor(tnpINNERTnL);
						Math_apply tnpINNERApplyR = new Math_apply();
						tnpINNERAssign.addFactor(tnpINNERApplyR);
							Math_plus tnpINNERPlusR = new Math_plus();
							tnpINNERApplyR.addFactor(tnpINNERPlusR);
								Math_ci tnpINNERTnR = new Math_ci("tm");
								tnpINNERPlusR.addFactor(tnpINNERTnR);
								Math_cn tnpINNER1R = new Math_cn("1");
								tnpINNERPlusR.addFactor(tnpINNER1R);
			SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
			vectnpINNER.add(pSyntaxExpINNER);
			
			for (int i = 0; i < vectnpINNER.size();i++) {
				/*数式の追加*/
				pSynDowhile_Inner.addStatement(vectnpINNER.get(i));
			}
			/*-----↑↑↑↑ tn=tn+1の追加 ↑↑↑↑-----*/
		}
		
		/*-----↓↓↓↓ finalの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "final";
		Vector<SyntaxExpression> vecExpressions_FINAL = this.createExpressions(strAttr_Now);			
		if(vecExpressions_FINAL.size() > 0){
		int nExpressionNum_FINAL = vecExpressions_FINAL.size();
			for (int i = 0; i < nExpressionNum_FINAL;i++) {
				/*数式の追加*/
				pSynDowhile_Out.addStatement(vecExpressions_FINAL.get(i));
			}
		}
		/*-----↑↑↑↑ finalの追加 ↑↑↑↑-----*/
		
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynDowhile_Out = this.MakeDowhileLoop_Inner(pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		
		strAttr_Now[LoopNumber] = null;
		
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynDowhile_Out = this.MakeDowhileLoop_Inner(pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		
		return pSynDowhile_Out;
	}
	
	
	protected SyntaxFunction MakeDowhileLoop_Out(SyntaxFunction pSynMainFunc_Out, SyntaxControl pSynDowhile_Out, int LoopNumber, String[] strAttr_Now) throws TranslateException, MathException {
		int MaxLoopNumber = strAttr_Now.length - 1;
		/*-----↓↓↓↓　initの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "init";
		Vector<SyntaxExpression> vecExpressions_INIT = this.createExpressions(strAttr_Now);
		if(vecExpressions_INIT.size()>0){
			/*---- ループ中に数式を追加 ----*/
			int nExpressionNum_INIT = vecExpressions_INIT.size();
			for (int i = 0; i < nExpressionNum_INIT;i++) {
				/*数式の追加*/
				pSynMainFunc_Out.addStatement(vecExpressions_INIT.get(i));
			}
		}
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynMainFunc_Out = this.MakeDowhileLoop_Out(pSynMainFunc_Out, pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		/*-----↑↑↑↑　initの追加 ↑↑↑↑-----*/
		
		
		/*-----↓↓↓↓ loop構造の作成 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "inner";
		Vector<SyntaxExpression> vecExpressions_INNER = this.createExpressions(strAttr_Now);
		if(vecExpressions_INNER.size()>0){
			/*-----↓↓↓↓　"tn = 0"の追加 ↓↓↓↓-----*/
			Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
			MathExpression tnINIT = new MathExpression();
			Math_apply tnINITApply = new Math_apply();
			tnINIT.addOperator(tnINITApply);
			Math_assign tnINITAssign = new Math_assign();
			tnINITApply.addFactor(tnINITAssign);
			Math_ci tnINITTn = new Math_ci("tn");
			tnINITAssign.addFactor(tnINITTn);
			Math_cn tnINIT0 = new Math_cn("0");
			tnINITAssign.addFactor(tnINIT0);
			SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
			vectnINIT.add(pSyntaxExpINIT);
			
			for (int i = 0; i < vectnINIT.size();i++) {
				/*数式の追加*/
				pSynMainFunc_Out.addStatement(vectnINIT.get(i));
			}
			/*-----↑↑↑↑　"tn = 0"の追加 ↑↑↑↑-----*/

			/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
			/*終了条件を取得*/
			Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
			pMathLt.addFactor(null);
			pMathLt.addFactor(null);
			MathExpression pConditionExp = new MathExpression(pMathLt);
			
			for(int h=0; h <= MaxLoopNumber; h++){
				strAttr_Now[h] = null;
			}
			
			strAttr_Now[LoopNumber] = "loopcond";
			ArrayList expIndex = new ArrayList();
			expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ loopひな型作成↓↓↓↓-----*/
			for (int i=0; i < expIndex.size(); i++){
				int index = Integer.parseInt(expIndex.get(i).toString());
					pConditionExp = m_pRecMLAnalyzer.getExpression(index);
			}
			SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
			SyntaxControl pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
			pSynMainFunc_Out.addStatement(pSynDowhile);
			/*-----↑↑↑↑ loopひな型作成 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
			strAttr_Now[LoopNumber] = "inner";
			
			/*-----　再帰処理　-----*/
			if(LoopNumber < MaxLoopNumber){
				LoopNumber++;
				pSynDowhile = this.MakeDowhileLoop_Inner(pSynDowhile, LoopNumber, strAttr_Now);
				strAttr_Now[LoopNumber] = null;
				LoopNumber--;
			}
			int nExpressionNum = vecExpressions_INNER.size();
			for (int i = 0; i < nExpressionNum;i++) {
				/*数式の追加*/
				pSynDowhile.addStatement(vecExpressions_INNER.get(i));
			}
			/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
			
			/*-----↓↓↓↓ tn=tn+1の追加 ↓↓↓↓-----*/
			Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
			MathExpression tnpINNER = new MathExpression();
				Math_apply tnpINNERApply = new Math_apply();
				tnpINNER.addOperator(tnpINNERApply);
					Math_assign tnpINNERAssign = new Math_assign();
					tnpINNERApply.addFactor(tnpINNERAssign);
						Math_ci tnpINNERTnL = new Math_ci("tn");
						tnpINNERAssign.addFactor(tnpINNERTnL);
						Math_apply tnpINNERApplyR = new Math_apply();
						tnpINNERAssign.addFactor(tnpINNERApplyR);
							Math_plus tnpINNERPlusR = new Math_plus();
							tnpINNERApplyR.addFactor(tnpINNERPlusR);
								Math_ci tnpINNERTnR = new Math_ci("tn");
								tnpINNERPlusR.addFactor(tnpINNERTnR);
								Math_cn tnpINNER1R = new Math_cn("1");
								tnpINNERPlusR.addFactor(tnpINNER1R);
			SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
			vectnpINNER.add(pSyntaxExpINNER);
			
			for (int i = 0; i < vectnpINNER.size();i++) {
				/*数式の追加*/
				pSynDowhile.addStatement(vectnpINNER.get(i));
			}
			/*-----↑↑↑↑ tn=tn+1の追加 ↑↑↑↑-----*/
		}
		
		/*-----↓↓↓↓ finalの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "final";
		Vector<SyntaxExpression> vecExpressions_FINAL = this.createExpressions(strAttr_Now);
				
		if(vecExpressions_FINAL.size() > 0){
		int nExpressionNum_FINAL = vecExpressions_FINAL.size();
			for (int i = 0; i < nExpressionNum_FINAL;i++) {
				/*数式の追加*/
				pSynMainFunc_Out.addStatement(vecExpressions_FINAL.get(i));
			}
		}
		/*-----↑↑↑↑ finalの追加 ↑↑↑↑-----*/
		
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynMainFunc_Out = this.MakeDowhileLoop_Out(pSynMainFunc_Out, pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}

		/*-----↓↓↓↓ nullの処理 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = null;
		
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynMainFunc_Out = this.MakeDowhileLoop_Out(pSynMainFunc_Out, pSynDowhile_Out, LoopNumber, strAttr_Now);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		// syninit = "init"
		//pSynMainFunc.addStatement(syninit);
		//pSynMainFunc.addStatement(pSynDowhile);
		// syninner = "inner"
		// pSynDowhile2 = new SyntaxControl(eControlKind.CTRL_DOWHILE);
		// syninner2 = MakeDowhileLoop(pSynDowhile, pSynDowhile2);
		//pSynDowhile.addStatement(syninner);
		// synloopcond = "loopcond"
		//pSynDowhile.addCondition(synloopcond);
		// synfinal = "final"
		//pSynMainFunc.addStatement(synfinal);
		return pSynMainFunc_Out;
	}
	
//	public SyntaxProgram getSyntaxProgram()
//	throws MathException, CellMLException, RelMLException, TranslateException {
//
//		//----------------------------------------------
//		//プログラム構文の生成
//		//----------------------------------------------
//		/*プログラム構文生成*/
//		SyntaxProgram pSynProgram = this.createNewProgram();
//
//		/*プリプロセッサ構文生成・追加*/
//		SyntaxPreprocessor pSynInclude1 =
//			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdio.h");
//		SyntaxPreprocessor pSynInclude2 =
//			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdlib.h");
//		SyntaxPreprocessor pSynInclude3 =
//			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "math.h");
//		pSynProgram.addPreprocessor(pSynInclude1);
//		pSynProgram.addPreprocessor(pSynInclude2);
//		pSynProgram.addPreprocessor(pSynInclude3);
//
//		
//
//		//----------------------------------------------
//		//メイン関数生成
//		//----------------------------------------------
//		/*メイン関数生成・追加*/
//		SyntaxFunction pSynMainFunc = this.createMainFunction();
//		pSynProgram.addFunction(pSynMainFunc);

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*ループ変数インスタンス生成*/

		
		
		//----------------------------------------------
		//数式部分の追加
		//----------------------------------------------
		/*外側ループ構文生成・追加*/
//		Math_ci pTimeVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pTimeVar().createCopy());
//		Math_ci pDeltaVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pDeltaVar().createCopy());
//
//		SyntaxControl pSynFor1 = createSyntaxTimeLoop(m_dStartTime,
//				m_dEndTime, pTimeVariale, pDeltaVariale);
//		pSynMainFunc.addStatement(pSynFor1);
//
//		/*内側ループ構文生成・追加*/
//		SyntaxControl pSynFor2 = createSyntaxDataNumLoop(m_pDefinedDataSizeVar, pLoopIndexVar);
//		pSynFor1.addStatement(pSynFor2);

		
		
		
		
		
		
//		/*-----↓↓↓↓ initの追加 ↓↓↓↓-----*/
//		String[] strAttr_INIT = new String[] {"init", null, null};
//		Vector<SyntaxExpression> vecExpressions_INIT = this.createExpressions(strAttr_INIT);
//
//		/*ループ中に数式を追加*/
//		int nExpressionNum_INIT = vecExpressions_INIT.size();
//		for (int i = 0; i < nExpressionNum_INIT;i++) {
//			/*数式の追加*/
//			pSynMainFunc.addStatement(vecExpressions_INIT.get(i));
//		}
//		System.out.println("initの表示　じっこうしたお!!" + nExpressionNum_INIT + "\n");
//		/*-----↑↑↑↑　initの追加 ↑↑↑↑-----*/
//		
//		
//		/*-----↓↓↓↓　"tn = 0"の追加 ↓↓↓↓-----*/
//		Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
//		MathExpression tnINIT = new MathExpression();
//		Math_apply tnINITApply = new Math_apply();
//		tnINIT.addOperator(tnINITApply);
//		Math_assign tnINITAssign = new Math_assign();
//		tnINITApply.addFactor(tnINITAssign);
//		Math_ci tnINITTn = new Math_ci("tn");
//		tnINITAssign.addFactor(tnINITTn);
//		Math_cn tnINIT0 = new Math_cn("0");
//		tnINITAssign.addFactor(tnINIT0);
//		SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
//		vectnINIT.add(pSyntaxExpINIT);
//		
//		for (int i = 0; i < vectnINIT.size();i++) {
//			/*数式の追加*/
//			pSynMainFunc.addStatement(vectnINIT.get(i));
//		}
//		
//		System.out.println("tn = 0の追加　じっこうしたお!!" + "\n");
//		/*-----↑↑↑↑　"tn = 0"の追加 ↑↑↑↑-----*/
//		
//		
//		/*-----↓↓↓↓　do-while loop　構文の追加 ↓↓↓↓-----*/	
////		String strdowhileFinalcond = "";
////		if (equ != null) {
////			strdowhileFinalcond += m_pInitExpression.toLegalString();
////		}
//
//		System.out.println("do-while loop　構文の追加だお!!" + "\n");
//		
//		
//		/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
//		/*終了条件を取得*/
//		Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
//		pMathLt.addFactor(null);
//		pMathLt.addFactor(null);
//		MathExpression pConditionExp = new MathExpression(pMathLt);
//		
//		String[] strAttr = new String[] {"loopcond", null, null};
////		System.out.println("loop1 = " + strAttr[0] + "\n");
//		ArrayList expIndex = new ArrayList();
//		expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr);
//		
//		for (int i=0; i < expIndex.size(); i++){
//			int index = Integer.parseInt(expIndex.get(i).toString());
//				pConditionExp = m_pRecMLAnalyzer.getExpression(index);
////				System.out.println(pConditionExp.toLegalString());
//		}
//		/*ループ条件生成*/
//		SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
//		SyntaxControl pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
//		pSynMainFunc.addStatement(pSynDowhile);
//		System.out.println("終了条件を取得　じっこうしたお!!" + "\n");
//		/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
//		
//		
//		/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
//		String[] strAttr_INNER = new String[] {"inner", null, null};
//		Vector<SyntaxExpression> vecExpressions = this.createExpressions(strAttr_INNER);
//
//		/*ループ中に数式を追加*/
//		int nExpressionNum = vecExpressions.size();
//		for (int i = 0; i < nExpressionNum;i++) {
//			/*数式の追加*/
//			pSynDowhile.addStatement(vecExpressions.get(i));
//		}
//		
//		/*-----↓↓↓↓ tn=tn+1の追加 ↓↓↓↓-----*/
//		Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
//		MathExpression tnpINNER = new MathExpression();
//			Math_apply tnpINNERApply = new Math_apply();
//			tnpINNER.addOperator(tnpINNERApply);
//				Math_assign tnpINNERAssign = new Math_assign();
//				tnpINNERApply.addFactor(tnpINNERAssign);
//					Math_ci tnpINNERTnL = new Math_ci("tn");
//					tnpINNERAssign.addFactor(tnpINNERTnL);
//					Math_apply tnpINNERApplyR = new Math_apply();
//					tnpINNERAssign.addFactor(tnpINNERApplyR);
//						Math_plus tnpINNERPlusR = new Math_plus();
//						tnpINNERApplyR.addFactor(tnpINNERPlusR);
//							Math_ci tnpINNERTnR = new Math_ci("tn");
//							tnpINNERPlusR.addFactor(tnpINNERTnR);
//							Math_cn tnpINNER1R = new Math_cn("1");
//							tnpINNERPlusR.addFactor(tnpINNER1R);
//		SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
//		vectnpINNER.add(pSyntaxExpINNER);
//		
//		for (int i = 0; i < vectnpINNER.size();i++) {
//			/*数式の追加*/
//			pSynDowhile.addStatement(vectnpINNER.get(i));
//		}
//		
//		System.out.println("tn=tn+1の追加　じっこうしたお!!" + "\n");
//		/*-----↑↑↑↑ tn=tn+1の追加 ↑↑↑↑-----*/
//		
//		System.out.println("ループ中に数式を追加　じっこうしたお!!" + nExpressionNum + "\n");
//		/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
//		
//		
//		/*-----↑↑↑↑　do-while loop　構文の追加 ↑↑↑↑-----*/
//		
//		
//		/*-----↓↓↓↓ finalの追加 ↓↓↓↓-----*/
//		String[] strAttr_FINAL = new String[] {"final", null, null};
//		Vector<SyntaxExpression> vecExpressions_FINAL = this.createExpressions(strAttr_FINAL);
//		int nExpressionNum_FINAL = vecExpressions_FINAL.size();
//		for (int i = 0; i < nExpressionNum_FINAL;i++) {
//			/*数式の追加*/
//			pSynMainFunc.addStatement(vecExpressions_FINAL.get(i));
//		}
//		System.out.println("finalの追加じっこうしたお!!" + nExpressionNum_FINAL + "\n");
//		/*-----↑↑↑↑ finalの追加 ↑↑↑↑-----*/
//		
//		int LoopNumber = 0;
//		
//		String[] strAttr_Original = new String[] {null, null, null};
//		pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Original, null);
//		
//		/*プログラム構文を返す*/
//		return pSynProgram;
//	}
		
		
	protected SyntaxFunction MainFunc(SyntaxFunction pSynMainFunc, int LoopNumber, String[] strAttr_Now, SyntaxControl pSynDowhile) throws TranslateException, MathException{
		
		int MaxLoopNumber = 1;
//		int MaxLoopNumber = 0;

		/*-----↓↓↓↓　initの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "init";
		Vector<SyntaxExpression> vecExpressions_INIT = this.createExpressions(strAttr_Now);
		
		System.out.println("じっこうしたお!!" + vecExpressions_INIT.size() + "," + LoopNumber + "\n");
		for(int h=0; h <= MaxLoopNumber; h++){
			System.out.println(strAttr_Now[h]+ ",");
		}
		System.out.println("\n");
		
		if(vecExpressions_INIT.size()>0){
			/*---- ループ中に数式を追加 ----*/
			int nExpressionNum_INIT = vecExpressions_INIT.size();
			/*---- LoopNumberが0かそれ以外かで、処理が変更 ----*/
			if(LoopNumber == 0){
				for (int i = 0; i < nExpressionNum_INIT;i++) {
					/*数式の追加*/
					pSynMainFunc.addStatement(vecExpressions_INIT.get(i));
				}
			}else{
				for (int i = 0; i < nExpressionNum_INIT;i++) {
					/*数式の追加*/
					pSynDowhile.addStatement(vecExpressions_INIT.get(i));
				}
			}
		}
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		/*-----↑↑↑↑　initの追加 ↑↑↑↑-----*/

		
		/*-----↓↓↓↓ loop構造の作成 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "inner";
		
		Vector<SyntaxExpression> vecExpressions_INNER = this.createExpressions(strAttr_Now);
		
		System.out.println("じっこうしたお!!" + vecExpressions_INNER.size() + "," + LoopNumber + "\n");
		for(int h=0; h <= MaxLoopNumber; h++){
			System.out.println(strAttr_Now[h]+ ",");
		}
		System.out.println("\n");
				
		if(vecExpressions_INNER.size()>0){
			/*ループ条件生成*/
			if(LoopNumber == 0){
				/*-----↓↓↓↓　"tn = 0"の追加 ↓↓↓↓-----*/
				Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
				MathExpression tnINIT = new MathExpression();
				Math_apply tnINITApply = new Math_apply();
				tnINIT.addOperator(tnINITApply);
				Math_assign tnINITAssign = new Math_assign();
				tnINITApply.addFactor(tnINITAssign);
				Math_ci tnINITTn = new Math_ci("tn");
				tnINITAssign.addFactor(tnINITTn);
				Math_cn tnINIT0 = new Math_cn("0");
				tnINITAssign.addFactor(tnINIT0);
				SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
				vectnINIT.add(pSyntaxExpINIT);
				
				for (int i = 0; i < vectnINIT.size();i++) {
					/*数式の追加*/
					pSynMainFunc.addStatement(vectnINIT.get(i));
				}
				/*-----↑↑↑↑　"tn = 0"の追加 ↑↑↑↑-----*/

				/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
				/*終了条件を取得*/
				Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
				pMathLt.addFactor(null);
				pMathLt.addFactor(null);
				MathExpression pConditionExp = new MathExpression(pMathLt);
				
				for(int h=0; h <= MaxLoopNumber; h++){
					strAttr_Now[h] = null;
				}
				
				strAttr_Now[LoopNumber] = "loopcond";
				ArrayList expIndex = new ArrayList();
				expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_Now);
				strAttr_Now[LoopNumber] = null;
				/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ loopひな型作成↓↓↓↓-----*/
				for (int i=0; i < expIndex.size(); i++){
					int index = Integer.parseInt(expIndex.get(i).toString());
						pConditionExp = m_pRecMLAnalyzer.getExpression(index);
				}
				SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
				pSynDowhile = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
				pSynMainFunc.addStatement(pSynDowhile);
				/*-----↑↑↑↑ loopひな型作成 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
				strAttr_Now[LoopNumber] = "inner";
				if(LoopNumber < MaxLoopNumber){
					LoopNumber++;
					pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
					strAttr_Now[LoopNumber] = null;
					LoopNumber--;
				}
				int nExpressionNum = vecExpressions_INNER.size();
				for (int i = 0; i < nExpressionNum;i++) {
					/*数式の追加*/
					pSynDowhile.addStatement(vecExpressions_INNER.get(i));
				}
				/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ tn=tn+1の追加 ↓↓↓↓-----*/
				Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
				MathExpression tnpINNER = new MathExpression();
					Math_apply tnpINNERApply = new Math_apply();
					tnpINNER.addOperator(tnpINNERApply);
						Math_assign tnpINNERAssign = new Math_assign();
						tnpINNERApply.addFactor(tnpINNERAssign);
							Math_ci tnpINNERTnL = new Math_ci("tn");
							tnpINNERAssign.addFactor(tnpINNERTnL);
							Math_apply tnpINNERApplyR = new Math_apply();
							tnpINNERAssign.addFactor(tnpINNERApplyR);
								Math_plus tnpINNERPlusR = new Math_plus();
								tnpINNERApplyR.addFactor(tnpINNERPlusR);
									Math_ci tnpINNERTnR = new Math_ci("tn");
									tnpINNERPlusR.addFactor(tnpINNERTnR);
									Math_cn tnpINNER1R = new Math_cn("1");
									tnpINNERPlusR.addFactor(tnpINNER1R);
				SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
				vectnpINNER.add(pSyntaxExpINNER);
				
				for (int i = 0; i < vectnpINNER.size();i++) {
					/*数式の追加*/
					pSynDowhile.addStatement(vectnpINNER.get(i));
				}
				/*-----↑↑↑↑ tn=tn+1の追加 ↑↑↑↑-----*/
			}else{
				/*-----↓↓↓↓　"tm = 0"の追加 ↓↓↓↓-----*/
				Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
				MathExpression tnINIT = new MathExpression();
				Math_apply tnINITApply = new Math_apply();
				tnINIT.addOperator(tnINITApply);
				Math_assign tnINITAssign = new Math_assign();
				tnINITApply.addFactor(tnINITAssign);
				Math_ci tnINITTn = new Math_ci("tm");
				tnINITAssign.addFactor(tnINITTn);
				Math_cn tnINIT0 = new Math_cn("0");
				tnINITAssign.addFactor(tnINIT0);
				SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
				vectnINIT.add(pSyntaxExpINIT);
				
				for (int i = 0; i < vectnINIT.size();i++) {
					/*数式の追加*/
					pSynDowhile.addStatement(vectnINIT.get(i));
				}
				/*-----↑↑↑↑　"tm = 0"の追加 ↑↑↑↑-----*/

				/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
				/*終了条件を取得*/
				Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
				pMathLt.addFactor(null);
				pMathLt.addFactor(null);
				MathExpression pConditionExpIN = new MathExpression(pMathLt);
				
				strAttr_Now[LoopNumber] = "loopcond";
				ArrayList expIndex = new ArrayList();
				expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_Now);
				strAttr_Now[LoopNumber] = null;
				/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ loopのひな型を作成 ↓↓↓↓-----*/
				for (int i=0; i < expIndex.size(); i++){
					int index = Integer.parseInt(expIndex.get(i).toString());
					pConditionExpIN = m_pRecMLAnalyzer.getExpression(index);
				}
				SyntaxCondition pSynLoopCondIN = new SyntaxCondition(pConditionExpIN);
				SyntaxControl pSynDowhileIN = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCondIN);
				pSynDowhile.addStatement(pSynDowhileIN);
				/*-----↑↑↑↑ loopのひな型を作成 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
				strAttr_Now[LoopNumber] = "inner";
//				if(LoopNumber < MaxLoopNumber){
//					LoopNumber++;
//					pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
//					strAttr_Now[LoopNumber] = null;
//					LoopNumber--;
//				}
				/*ループ中に数式を追加*/
				int nExpressionNum = vecExpressions_INNER.size();
				for (int i = 0; i < nExpressionNum;i++) {
					/*数式の追加*/
					pSynDowhileIN.addStatement(vecExpressions_INNER.get(i));
				}
				/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
				
				/*-----↓↓↓↓ tm=tm+1の追加 ↓↓↓↓-----*/
				Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
				MathExpression tnpINNER = new MathExpression();
					Math_apply tnpINNERApply = new Math_apply();
					tnpINNER.addOperator(tnpINNERApply);
						Math_assign tnpINNERAssign = new Math_assign();
						tnpINNERApply.addFactor(tnpINNERAssign);
							Math_ci tnpINNERTnL = new Math_ci("tm");
							tnpINNERAssign.addFactor(tnpINNERTnL);
							Math_apply tnpINNERApplyR = new Math_apply();
							tnpINNERAssign.addFactor(tnpINNERApplyR);
								Math_plus tnpINNERPlusR = new Math_plus();
								tnpINNERApplyR.addFactor(tnpINNERPlusR);
									Math_ci tnpINNERTnR = new Math_ci("tm");
									tnpINNERPlusR.addFactor(tnpINNERTnR);
									Math_cn tnpINNER1R = new Math_cn("1");
									tnpINNERPlusR.addFactor(tnpINNER1R);
				SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
				vectnpINNER.add(pSyntaxExpINNER);				
				for (int i = 0; i < vectnpINNER.size();i++) {
					/*数式の追加*/
					pSynDowhileIN.addStatement(vectnpINNER.get(i));
				}
				/*-----↑↑↑↑ tm=tm+1の追加 ↑↑↑↑-----*/
			}		
		}
		/*-----↑↑↑↑ loop構造の作成 ↑↑↑↑-----*/
		
		/*-----↓↓↓↓ finalの追加 ↓↓↓↓-----*/
		strAttr_Now[LoopNumber] = "final";
		Vector<SyntaxExpression> vecExpressions_FINAL = this.createExpressions(strAttr_Now);
		
		System.out.println("じっこうしたお!!" + vecExpressions_FINAL.size() + "," + LoopNumber + "\n");
		for(int h=0; h <= MaxLoopNumber; h++){
			System.out.println(strAttr_Now[h]+ ",");
		}
		System.out.println("\n");
				
		if(vecExpressions_FINAL.size() > 0){
			int nExpressionNum_FINAL = vecExpressions_FINAL.size();
			if(LoopNumber == 0){
				for (int i = 0; i < nExpressionNum_FINAL;i++) {
					/*数式の追加*/
					pSynMainFunc.addStatement(vecExpressions_FINAL.get(i));
				}
			}else{
				for (int i = 0; i < nExpressionNum_FINAL;i++) {
					/*数式の追加*/
					pSynDowhile.addStatement(vecExpressions_FINAL.get(i));
				}
			}
		}
		/*-----↑↑↑↑ finalの追加 ↑↑↑↑-----*/
		
		/*-----　再帰処理　-----*/
		if(LoopNumber < MaxLoopNumber){
			LoopNumber++;
			pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
			strAttr_Now[LoopNumber] = null;
			LoopNumber--;
		}
		
		return pSynMainFunc;		
	}

	
	
	
	
	
//	protected SyntaxFunction MainFunc(SyntaxFunction pSynMainFunc, int LoopNumber, String[] strAttr_Now, SyntaxControl pSynDowhile) throws TranslateException, MathException{
//		
//		int MaxLoopNumber = 2;
//		
//		/*-----↓↓↓↓ initの追加 ↓↓↓↓-----*/
//		if(LoopNumber == 0){
//			for(int h=0; h <= MaxLoopNumber; h++){
//				strAttr_Now[h] = null;
//			}
//		}
//		strAttr_Now[LoopNumber] = "init";
//		Vector<SyntaxExpression> vecExpressions_INIT = this.createExpressions(strAttr_Now);
//		System.out.println("じっこうしたお!!" + vecExpressions_INIT.size() + "," + LoopNumber + "\n");
//		
//		if(vecExpressions_INIT.size()>0){
//			/*ループ中に数式を追加*/
//			int nExpressionNum_INIT = vecExpressions_INIT.size();
//			for (int i = 0; i < nExpressionNum_INIT;i++) {
//				/*数式の追加*/
//				pSynMainFunc.addStatement(vecExpressions_INIT.get(i));
//			}
//		}	
//		
//		if(LoopNumber < MaxLoopNumber){
//			LoopNumber++;
//			pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
//			LoopNumber--;
//		}
//		/*-----↑↑↑↑　initの追加 ↑↑↑↑-----*/
//		
//		
//		
//		/*-----↓↓↓↓ innerの追加 ↓↓↓↓-----*/
//		if(LoopNumber == 0){
//			for(int h=0; h <= MaxLoopNumber; h++){
//				strAttr_Now[h] = null;
//			}
//		}
//		strAttr_Now[LoopNumber] = "inner";
//		Vector<SyntaxExpression> vecExpressions_INNER = this.createExpressions(strAttr_Now);
//		System.out.println("じっこうしたお!!" + vecExpressions_INNER.size() + "," + LoopNumber + "\n");
//		/*-----↑↑↑↑ innerの追加 ↑↑↑↑-----*/
//		if(LoopNumber < MaxLoopNumber){
//			LoopNumber++;
//			pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
//			LoopNumber--;
//		}
//		if(vecExpressions_INNER.size()>0){
//			/*-----↓↓↓↓　"tn = 0"の追加 ↓↓↓↓-----*/
//			Vector<SyntaxExpression> vectnINIT = new Vector<SyntaxExpression>();
//			MathExpression tnINIT = new MathExpression();
//			Math_apply tnINITApply = new Math_apply();
//			tnINIT.addOperator(tnINITApply);
//			Math_assign tnINITAssign = new Math_assign();
//			tnINITApply.addFactor(tnINITAssign);
//			Math_ci tnINITTn = new Math_ci("tn");
//			tnINITAssign.addFactor(tnINITTn);
//			Math_cn tnINIT0 = new Math_cn("0");
//			tnINITAssign.addFactor(tnINIT0);
//			SyntaxExpression pSyntaxExpINIT = new SyntaxExpression(tnINIT);
//			vectnINIT.add(pSyntaxExpINIT);
//			
//			for (int i = 0; i < vectnINIT.size();i++) {
//				/*数式の追加*/
//				pSynMainFunc.addStatement(vectnINIT.get(i));
//			}
//			
////			System.out.println("tn = 0の追加　じっこうしたお!!" + "\n");
//			/*-----↑↑↑↑　"tn = 0"の追加 ↑↑↑↑-----*/
//
//			/*-----↓↓↓↓ loopcondの追加 ↓↓↓↓-----*/
//			/*終了条件を取得*/
//			Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
//			pMathLt.addFactor(null);
//			pMathLt.addFactor(null);
//			MathExpression pConditionExp = new MathExpression(pMathLt);
//			
//			if(LoopNumber == 0){
//				for(int h=0; h <= MaxLoopNumber; h++){
//					strAttr_Now[h] = null;
//				}
//			}
//			strAttr_Now[LoopNumber] = "loopcond";
//	//		System.out.println("loop1 = " + strAttr[0] + "\n");
//			ArrayList expIndex = new ArrayList();
//			expIndex = m_pRecMLAnalyzer.getExpressionWithAttr(strAttr_Now);
//			
//			for (int i=0; i < expIndex.size(); i++){
//				int index = Integer.parseInt(expIndex.get(i).toString());
//					pConditionExp = m_pRecMLAnalyzer.getExpression(index);
//	//				System.out.println(pConditionExp.toLegalString());
//			}
//
//			/*ループ条件生成*/
//				SyntaxCondition pSynLoopCond = new SyntaxCondition(pConditionExp);
//				SyntaxControl pSynDowhile1 = new SyntaxControl(eControlKind.CTRL_DOWHILE,pSynLoopCond);
//				pSynDowhile.addStatement(pSynDowhile1);
//				pSynMainFunc.addStatement(pSynDowhile);
////			System.out.println("終了条件を取得　じっこうしたお!!" + "\n");
//		
//
//			
//			/*ループ中に数式を追加*/
//			int nExpressionNum = vecExpressions_INNER.size();
//			for (int i = 0; i < nExpressionNum;i++) {
//				/*数式の追加*/
//				pSynDowhile.addStatement(vecExpressions_INNER.get(i));
//			}
//
//			/*-----↓↓↓↓ tn=tn+1の追加 ↓↓↓↓-----*/
//			Vector<SyntaxExpression> vectnpINNER = new Vector<SyntaxExpression>();
//			MathExpression tnpINNER = new MathExpression();
//				Math_apply tnpINNERApply = new Math_apply();
//				tnpINNER.addOperator(tnpINNERApply);
//					Math_assign tnpINNERAssign = new Math_assign();
//					tnpINNERApply.addFactor(tnpINNERAssign);
//						Math_ci tnpINNERTnL = new Math_ci("tn");
//						tnpINNERAssign.addFactor(tnpINNERTnL);
//						Math_apply tnpINNERApplyR = new Math_apply();
//						tnpINNERAssign.addFactor(tnpINNERApplyR);
//							Math_plus tnpINNERPlusR = new Math_plus();
//							tnpINNERApplyR.addFactor(tnpINNERPlusR);
//								Math_ci tnpINNERTnR = new Math_ci("tn");
//								tnpINNERPlusR.addFactor(tnpINNERTnR);
//								Math_cn tnpINNER1R = new Math_cn("1");
//								tnpINNERPlusR.addFactor(tnpINNER1R);
//			SyntaxExpression pSyntaxExpINNER = new SyntaxExpression(tnpINNER);
//			vectnpINNER.add(pSyntaxExpINNER);
//			
//			
//			
//			
//			for (int i = 0; i < vectnpINNER.size();i++) {
//				/*数式の追加*/
//				pSynDowhile.addStatement(vectnpINNER.get(i));
//			}
//			
////			System.out.println("tn=tn+1の追加　じっこうしたお!!" + "\n");
//			/*-----↑↑↑↑ tn=tn+1の追加 ↑↑↑↑-----*/
//			
//		}
//		
//		/*-----↓↓↓↓ finalの追加 ↓↓↓↓-----*/
//		if(LoopNumber == 0){
//			for(int h=0; h <= MaxLoopNumber; h++){
//				strAttr_Now[h] = null;
//			}
//		}
//		strAttr_Now[LoopNumber] = "final";
//		Vector<SyntaxExpression> vecExpressions_FINAL = this.createExpressions(strAttr_Now);
//		System.out.println("じっこうしたお!!" + vecExpressions_FINAL.size() + "," + LoopNumber + "\n");
//		/*-----↑↑↑↑ finalの追加 ↑↑↑↑-----*/	
//	
//		/*-----↑↑↑↑ loopcondの追加 ↑↑↑↑-----*/
//		
//		if(vecExpressions_FINAL.size() > 0){
//			int nExpressionNum_FINAL = vecExpressions_FINAL.size();
//			for (int i = 0; i < nExpressionNum_FINAL;i++) {
//				/*数式の追加*/
//				pSynMainFunc.addStatement(vecExpressions_FINAL.get(i));
//			}
//		}
//		if(LoopNumber < MaxLoopNumber){
//			LoopNumber++;
//			pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Now, pSynDowhile);
//			LoopNumber--;
//		}
//		return pSynMainFunc;		
//	}
	
	
	
	
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


		
	
}
