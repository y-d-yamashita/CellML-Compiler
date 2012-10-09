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
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDeclaration.eDeclarationSpecifier;
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
public class CudaRecurrenceProgramGenerator extends ProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	/**生成CUDAプログラム構文 __i*/
	public static final String CUPROG_LOOP_INDEX_NAME1 = "__i";
	/**生成CUDAプログラム構文 __tid*/
	public static final String CUPROG_KERNEL_INDEX = "__tid";
	/**生成CUDAプログラム構文 __tmp_idx_x*/
	public static final String CUPROG_KERNEL_TMP_INDEX1 = "__tmp_idx_x";
	/**生成CUDAプログラム構文 __tmp_idx_y*/
	public static final String CUPROG_KERNEL_TMP_INDEX2 = "__tmp_idx_y";
	/**生成CUDAプログラム構文 __tmp_size_x*/
	public static final String CUPROG_KERNEL_TMP_INDEX3 = "__tmp_size_x";

	/**生成CUDAプログラム構文 cudaMalloc*/
	public static final String CUDA_FUNC_STR_CUDAMALLOC = "cudaMalloc";
	/**生成CUDAプログラム構文 cudaMemcpy*/
	public static final String CUDA_FUNC_STR_CUDAMEMCPY = "cudaMemcpy";
	/**生成CUDAプログラム構文 cudaHostAlloc*/
	public static final String CUDA_FUNC_STR_CUDAHOSTALLOC = "cudaHostAlloc";
	/**生成CUDAプログラム構文 cudaHostAlloc*/
	public static final String CUDA_FUNC_STR_CUDAHOSTGETDEVICEPOINTER = "cudaHostGetDevicePointer";
	/**生成CUDAプログラム構文 cudaMemcpyToSymbol*/
	public static final String CUDA_FUNC_STR_CUDAMEMCPYTOSYMBOL = "cudaMemcpyToSymbol";
	/**生成CUDAプログラム構文 cudaFree*/
	public static final String CUDA_FUNC_STR_CUDAFREE = "cudaFree";
	/**生成CUDAプログラム構文 cudaFreeHost*/
	public static final String CUDA_FUNC_STR_CUDAFREEHOST = "cudaFreeHost";
	/**生成CUDAプログラム構文 CUT_DEVICE_INIT*/
	public static final String CUDA_FUNC_STR_CUTDEVICEINIT = "CUT_DEVICE_INIT";
	/**生成CUDAプログラム構文 CUT_EXIT*/
	public static final String CUDA_FUNC_STR_CUTEXIT = "CUT_EXIT";
	/**生成CUDAプログラム構文 cudaMemcpyHostToDevice*/
	public static final String CUDA_CONST_CUDAMEMCPYHOSTTODEVICE = "cudaMemcpyHostToDevice";
	/**生成CUDAプログラム構文 cudaMemcpyDeviceToHost*/
	public static final String CUDA_CONST_CUDAMEMCPYDEVICETOHOST = "cudaMemcpyDeviceToHost";
	/**生成CUDAプログラム構文 cudaMemcpyToSymbol*/
	public static final String CUDA_CONST_CUDAMEMCPYTOSYMBOL = "cudaMemcpyToSymbol";
	/**生成CUDAプログラム構文 cudaMemcpyToSymbol*/
	public static final String CUDA_CONST_CUDAHOSTALLOCMAPPED = "cudaHostAllocMapped";
	/**生成CUDAプログラム構文 cudaMemcpyToSymbol*/
	public static final String CUDA_CONST_CUDAHOSTALLOCDEFAULT = "cudaHostAllocDefault";
	/**生成CUDAプログラム構文 __DATA_NUM*/
	public static final String CUPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";
	/**生成CUDAプログラム構文 __DATA_NUMX*/
	public static final String CUPROG_DEFINE_DATANUM_NAMEX = "__DATA_NUMX";
	/**生成CUDAプログラム構文 __DATA_NUMY*/
	public static final String CUPROG_DEFINE_DATANUM_NAMEY = "__DATA_NUMY";
	/**生成CUDAプログラム構文 __THREADS_X*/
	public static final String CUPROG_DEFINE_THREADS_SIZE_X_NAME = "__THREADS_X";
	/**生成CUDAプログラム構文 __THREADS_X*/
	public static final String CUPROG_DEFINE_THREADS_SIZE_Y_NAME = "__THREADS_Y";
	/**生成CUDAプログラム構文 __THREADS_Z*/
	public static final String CUPROG_DEFINE_THREADS_SIZE_Z_NAME = "__THREADS_Z";
	/**生成CUDAプログラム構文 __BLOCKS_X*/
	public static final String CUPROG_DEFINE_BLOCKS_SIZE_X_NAME = "__BLOCKS_X";
	/**生成CUDAプログラム構文 __BLOCKS_Y*/
	public static final String CUPROG_DEFINE_BLOCKS_SIZE_Y_NAME = "__BLOCKS_Y";
	/**生成CUDAプログラム構文 __BLOCKS_Z*/
	public static final String CUPROG_DEFINE_BLOCKS_SIZE_Z_NAME = "__BLOCKS_Z";

	/**生成CUDAプログラム構文 スレッド数X*/
	public static final int CUPROG_THREAD_SIZE_X = 256;
	/**生成CUDAプログラム構文 スレッド数Y*/
	public static final int CUPROG_THREAD_SIZE_Y = 1;
	/**生成CUDAプログラム構文 スレッド数Z*/
	public static final int CUPROG_THREAD_SIZE_Z = 1;

	/**生成CUDAプログラム構文 argc*/
	public static final String CUPROG_VAR_STR_ARGC = "argc";
	/**生成CUDAプログラム構文 argv*/
	public static final String CUPROG_VAR_STR_ARGV = "argv";
	protected static final String CUPROG_DEFINE_MAXARRAYNUM_NAME = "__MAX_ARRAY_NUM";

	/*共通変数*/
	protected Math_ci m_pDefinedDataSizeVar;	//データ数として#defineされる定数
	protected Math_ci m_pDefinedDataSizeVarX;
	protected Math_ci m_pDefinedDataSizeVarY;
	protected Math_ci m_pKernelIndexVar;		//カーネル中のスレッドインデックスを表す変数

	/*-----コンストラクタ-----*/
	public CudaRecurrenceProgramGenerator(RecMLAnalyzer pRecMLAnalyzer)
	throws MathException 	{
		super(pRecMLAnalyzer);
		m_pKernelIndexVar = null;
		m_pDefinedDataSizeVar = null;
		m_pDefinedDataSizeVarX = null;
		m_pDefinedDataSizeVarY = null;
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

		/*include構文生成・追加*/
		SyntaxPreprocessor pSynInclude1 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdio.h");
		SyntaxPreprocessor pSynInclude2 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "math.h");
		SyntaxPreprocessor pSynInclude3 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "cutil.h");
		SyntaxPreprocessor pSynInclude4 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "cuda.h");
		pSynProgram.addPreprocessor(pSynInclude1);
		pSynProgram.addPreprocessor(pSynInclude2);
		pSynProgram.addPreprocessor(pSynInclude3);
		pSynProgram.addPreprocessor(pSynInclude4);
		
		
		
		/*データ数定義defineの追加*/
		String strElementNum = String.valueOf(m_unElementNum);
		String strElementNumX = String.valueOf(m_unElementNumX);
		String strElementNumY = String.valueOf(m_unElementNumY);
		SyntaxPreprocessor pSynDefine1 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					CUPROG_DEFINE_DATANUM_NAME + " " + strElementNum);
		SyntaxPreprocessor pSynDefineX =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					CUPROG_DEFINE_DATANUM_NAMEX + " " + strElementNumX);
		SyntaxPreprocessor pSynDefineY =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					CUPROG_DEFINE_DATANUM_NAMEY + " " + strElementNumY);
		pSynProgram.addPreprocessor(pSynDefine1);
		pSynProgram.addPreprocessor(pSynDefineX);
		pSynProgram.addPreprocessor(pSynDefineY);

		/*グリッド定義defineの追加*/
		this.addGridDefinitionToProgram(pSynProgram);


		/*メイン関数の生成*/
	
		CudaRecurrenceMainFuncGenerator pCudaMainFuncGenerator =
			new CudaRecurrenceMainFuncGenerator(m_pRecMLAnalyzer);
		pCudaMainFuncGenerator.setElementNum(m_unElementNum);
		pCudaMainFuncGenerator.setTimeParam(m_dStartTime, m_dEndTime, m_dDeltaTime);

		SyntaxFunction pSynMainFunc = pCudaMainFuncGenerator.getSyntaxMainFunction();
		pSynProgram.addFunction(pSynMainFunc);

		/*初期カーネル生成・追加*/
		//CudaRecurrenceInitKernelGenerator pCudaInitKernelGenerator =
		//	new CudaRecurrenceInitKernelGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer);
		//SyntaxFunction pSynInitKernel = pCudaInitKernelGenerator.getSyntaxInitKernel();
		//pSynProgram.addFunction(pSynInitKernel);

		/*計算カーネル生成・追加*/
		CudaRecurrenceCalcKernelGenerator pCudaCalcKernelGenerator =
			new CudaRecurrenceCalcKernelGenerator(m_pRecMLAnalyzer);
		SyntaxFunction  pSynCalcKernel = pCudaCalcKernelGenerator.getCudaSyntaxCalcKernel();
	
		pSynProgram.addFunction(pSynCalcKernel);
		
//		SyntaxFunction pSynMainFunc = this.createMainFunction();
//		pSynProgram.addFunction(pSynMainFunc);
		
		//String[] strAttr_Original = new String[] {null, null, null};
		//pSynMainFunc = this.MainFunc1(pSynMainFunc, strAttr_Original);

//		int LoopNumber = 0;
		//String[] strAttr_Original = new String[] {null, null, null};
		//pSynMainFunc = this.MainFunc(pSynMainFunc, LoopNumber, strAttr_Original, null);
				
	
		

		/*プログラム構文を返す*/
		return pSynProgram;
	}
	
	
			
	//========================================================
	//initialize
	// 初期化メソッド
	//
	//========================================================
	/*-----初期化・終了処理メソッド-----*/
	/**
	 * 初期化する.
	 * @throws MathException
	 */
	protected void initialize() throws MathException {
		/*共通変数生成*/
		m_pKernelIndexVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_KERNEL_INDEX);
		m_pDefinedDataSizeVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_DEFINE_DATANUM_NAME);
		m_pDefinedDataSizeVarX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_DEFINE_DATANUM_NAMEX);
		m_pDefinedDataSizeVarY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_DEFINE_DATANUM_NAMEY);
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
						CUPROG_LOOP_INDEX_NAME1);

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
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,CUPROG_LOOP_INDEX_NAME1);

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
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,CUPROG_LOOP_INDEX_NAME1);

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
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,CUPROG_LOOP_INDEX_NAME1);

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
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,CUPROG_LOOP_INDEX_NAME1);

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
	
	/*-----定型構文生成系メソッド-----*/

	/**
	 * グリッドプリプロセッサ定義を追加する.
	 * @param pSynDstProgram 追加先のプログラム構文
	 */
	protected void addGridDefinitionToProgram(SyntaxProgram pSynDstProgram) {

		/*スレッド数・ブロック数の初期化*/
		int nThreadsX = CUPROG_THREAD_SIZE_X;
		int nThreadsY = CUPROG_THREAD_SIZE_Y;
		int nThreadsZ = CUPROG_THREAD_SIZE_Z;

		int nBlocksY = 1;

		/*文字列を生成*/
		String strThreadsX = String.valueOf(nThreadsX);
		String strThreadsY = String.valueOf(nThreadsY);
		String strThreadsZ = String.valueOf(nThreadsZ);
		String strBlocksY = String.valueOf(nBlocksY);

		/*define構文の生成*/
		SyntaxPreprocessor pSynDefine1 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       CUPROG_DEFINE_THREADS_SIZE_X_NAME + " " + strThreadsX);
		SyntaxPreprocessor pSynDefine2 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       CUPROG_DEFINE_THREADS_SIZE_Y_NAME + " " + strThreadsY);
		SyntaxPreprocessor pSynDefine3 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       CUPROG_DEFINE_THREADS_SIZE_Z_NAME + " " + strThreadsZ);
		SyntaxPreprocessor pSynDefine4 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       CUPROG_DEFINE_BLOCKS_SIZE_X_NAME +
					       " ( " + CUPROG_DEFINE_DATANUM_NAME + " / " + " ( " +
					       CUPROG_DEFINE_THREADS_SIZE_X_NAME + " * " +
					       CUPROG_DEFINE_THREADS_SIZE_Y_NAME + " * " +
					       CUPROG_DEFINE_THREADS_SIZE_Z_NAME + " ) ) ");
		SyntaxPreprocessor pSynDefine5 =
			new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
					       CUPROG_DEFINE_BLOCKS_SIZE_Y_NAME + " " + strBlocksY);

		/*生成したdefine構文の追加*/
		pSynDstProgram.addPreprocessor(pSynDefine1);
		pSynDstProgram.addPreprocessor(pSynDefine2);
		pSynDstProgram.addPreprocessor(pSynDefine3);
		pSynDstProgram.addPreprocessor(pSynDefine4);
		pSynDstProgram.addPreprocessor(pSynDefine5);
	}

	/**
	 * カーネルを生成する.
	 * @param strKernelName カーネルの名前
	 * @return 生成したカーネル構文インスタンス
	 * @throws MathException
	 */
	protected SyntaxFunction createKernel(String strKernelName)
	throws MathException {
		/*カーネルインスタンス生成*/
		SyntaxDataType pSynVoidType = new SyntaxDataType(eDataType.DT_VOID, 0);
		SyntaxFunction pSynKernelFunc = new SyntaxFunction(strKernelName, pSynVoidType);
		pSynKernelFunc.addDeclarationSpecifier(eDeclarationSpecifier.DS_CUDA_GLOBAL);

		/*カーネルインデックス変数の宣言追加*/
		SyntaxDataType SynIntType = new SyntaxDataType(eDataType.DT_UINT, 0);

		Math_ci pTmpIndexVar1 =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_KERNEL_TMP_INDEX1);
		Math_ci pTmpIndexVar2 =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_KERNEL_TMP_INDEX2);
		Math_ci pTmpIndexVar3 =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					CUPROG_KERNEL_TMP_INDEX3);

		SyntaxDeclaration pSynKernelTempIndexVarDec1 =
			new SyntaxDeclaration(SynIntType, pTmpIndexVar1);
		SyntaxDeclaration pSynKernelTempIndexVarDec2 =
			new SyntaxDeclaration(SynIntType, pTmpIndexVar2);
		SyntaxDeclaration pSynKernelTempIndexVarDec3 =
			new SyntaxDeclaration(SynIntType, pTmpIndexVar3);
		SyntaxDeclaration pSynKernelIndexVarDec =
			new SyntaxDeclaration(SynIntType, m_pKernelIndexVar);

		/*宣言修飾子の追加*/
		pSynKernelTempIndexVarDec1.addDeclarationSpecifier(eDeclarationSpecifier.DS_CONST);
		pSynKernelTempIndexVarDec2.addDeclarationSpecifier(eDeclarationSpecifier.DS_CONST);
		pSynKernelTempIndexVarDec3.addDeclarationSpecifier(eDeclarationSpecifier.DS_CONST);
		pSynKernelIndexVarDec.addDeclarationSpecifier(eDeclarationSpecifier.DS_CONST);

		/*宣言をカーネルに追加*/
		pSynKernelFunc.addDeclaration(pSynKernelTempIndexVarDec1);
		pSynKernelFunc.addDeclaration(pSynKernelTempIndexVarDec2);
		pSynKernelFunc.addDeclaration(pSynKernelTempIndexVarDec3);
		pSynKernelFunc.addDeclaration(pSynKernelIndexVarDec);

		/*初期化式に用いるオペランドと演算子の生成*/
		Math_ci pVarThreadIdxX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "threadIdx.x");
		Math_ci pVarThreadIdxY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "threadIdx.y");
		Math_ci pVarBlockIdxX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "blockIdx.x");
		Math_ci pVarBlockIdxY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "blockIdx.y");
		Math_ci pVarBlockDimX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "blockDim.x");
		Math_ci pVarBlockDimY =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "blockDim.y");
		Math_ci pVarGridDimX =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "gridDim.x");
		Math_plus pMathPlus1 =
			(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);
		Math_plus pMathPlus2 =
			(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);
		Math_plus pMathPlus3 =
			(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
		Math_times pMathTimes2 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
		Math_times pMathTimes3 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
		Math_times pMathTimes4 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		/*初期化式構築*/
		pMathTimes1.addFactor(pVarBlockDimX);
		pMathTimes1.addFactor(pVarBlockIdxX);
		pMathPlus1.addFactor(pVarThreadIdxX);
		pMathPlus1.addFactor(pMathTimes1);
		MathExpression pInitExp1 = new MathExpression(pMathPlus1);

		pMathTimes2.addFactor(pVarBlockDimY);
		pMathTimes2.addFactor(pVarBlockIdxY);
		pMathPlus2.addFactor(pVarThreadIdxY);
		pMathPlus2.addFactor(pMathTimes2);
		MathExpression pInitExp2 = new MathExpression(pMathPlus2);

		pMathTimes3.addFactor(pVarBlockDimX);
		pMathTimes3.addFactor(pVarGridDimX);
		MathExpression pInitExp3 = new MathExpression(pMathTimes3);

		pMathTimes4.addFactor(pTmpIndexVar2);
		pMathTimes4.addFactor(pTmpIndexVar3);
		pMathPlus3.addFactor(pTmpIndexVar1);
		pMathPlus3.addFactor(pMathTimes4);
		MathExpression pInitExp4 = new MathExpression(pMathPlus3);

		/*初期化式の追加*/
		pSynKernelTempIndexVarDec1.addInitExpression(pInitExp1);
		pSynKernelTempIndexVarDec2.addInitExpression(pInitExp2);
		pSynKernelTempIndexVarDec3.addInitExpression(pInitExp3);
		pSynKernelIndexVarDec.addInitExpression(pInitExp4);

		return pSynKernelFunc;
	}

}
