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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration.eDeclarationSpecifier;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;

/**
 * CUDAプログラム構文生成クラス.
 */
public class CudaProgramGenerator extends ProgramGenerator {

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
	/**生成CUDAプログラム構文 cudaMemcpyToSymbol*/
	public static final String CUDA_FUNC_STR_CUDAMEMCPYTOSYMBOL = "cudaMemcpyToSymbol";
	/**生成CUDAプログラム構文 cudaFree*/
	public static final String CUDA_FUNC_STR_CUDAFREE = "cudaFree";
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

	/*共通変数*/
	protected Math_ci m_pDefinedDataSizeVar;	//データ数として#defineされる定数
	protected Math_ci m_pDefinedDataSizeVarX;
	protected Math_ci m_pDefinedDataSizeVarY;
	protected Math_ci m_pKernelIndexVar;		//カーネル中のスレッドインデックスを表す変数

	/**
	 * CUDAプログラム構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 * @throws MathException
	 */
	public CudaProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
	throws MathException {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
		m_pKernelIndexVar = null;
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
	throws CellMLException, RelMLException, MathException,
	TranslateException, SyntaxException {
		/*CellMLにRelMLを適用*/
		m_pCellMLAnalyzer.applyRelML(m_pRelMLAnalyzer);

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
		CudaMainFuncGenerator pCudaMainFuncGenerator =
			new CudaMainFuncGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer);
		pCudaMainFuncGenerator.setElementNum(m_unElementNum);
		pCudaMainFuncGenerator.setTimeParam(m_dStartTime, m_dEndTime, m_dDeltaTime);
		SyntaxFunction pSynMainFunc = pCudaMainFuncGenerator.getSyntaxMainFunction();
		pSynProgram.addFunction(pSynMainFunc);

		/*初期カーネル生成・追加*/
		CudaInitKernelGenerator pCudaInitKernelGenerator =
			new CudaInitKernelGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer);
		SyntaxFunction pSynInitKernel = pCudaInitKernelGenerator.getSyntaxInitKernel();
		pSynProgram.addFunction(pSynInitKernel);

		/*計算カーネル生成・追加*/
		CudaCalcKernelGenerator pCudaCalcKernelGenerator =
			new CudaCalcKernelGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer);
		SyntaxFunction  pSynCalcKernel = pCudaCalcKernelGenerator.getSyntaxCalcKernel();
		pSynProgram.addFunction(pSynCalcKernel);

		/*プログラム構文を返す*/
		return pSynProgram;
	}

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

	/**
	 * 計算式部を生成し，ベクタを返す.
	 * @return 計算式ベクタ
	 * @throws TranslateException
	 * @throws MathException
	 */
	protected Vector<SyntaxExpression> createExpressions()
	throws TranslateException, MathException {
		//---------------------------------------------
		//式の追加
		//---------------------------------------------
		/*生成した式を格納するベクタ*/
		Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();

		/*数式数を取得*/
		int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();

		for (int i = 0; i < nExpressionNum; i++) {

			/*数式の複製を取得*/
			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);

			/*左辺式・右辺式取得*/
			MathExpression pLeftExp = pMathExp.getLeftExpression();
			MathExpression pRightExp = pMathExp.getRightExpression();

			if (pLeftExp == null || pRightExp == null) {
				throw new TranslateException("CudaProgramGenerator","createExpressions",
							     "failed to parse expression");
			}

			/*左辺変数取得*/
			MathOperand pLeftVar = (MathOperand)pLeftExp.getFirstVariable();

			//-------------------------------------------
			//左辺式ごとに数式の追加
			//-------------------------------------------
			/*微分変数*/
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
				throw new TranslateException("CudaProgramGenerator","createExpressions",
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
				throw new TranslateException("CudaProgramGenerator","createExpressions",
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
	protected void replaceFunctionVariables(MathExpression pExpression, Math_fn pFunction,
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
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,String.valueOf(i));

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(m_pKernelIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecDiffVar().get(i), pArgVar);
		}

		/*通常変数の置換*/
		for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*引数変数のコピーを取得*/
			Math_ci pArgVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   pFunction.getArgumentsVector().get(nVarArgIdx).toLegalString());

			/*配列インデックスを作成*/
			Math_ci pTmpIndex =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,String.valueOf(i));

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(m_pKernelIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pCellMLAnalyzer.getM_vecArithVar().get(i), pArgVar);
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
			pExpression.replace(m_pCellMLAnalyzer.getM_vecConstVar().get(i), pArgVar);
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

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(m_pKernelIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDiffVar().get(i), pArgVar);
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

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(m_pKernelIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i), pArgVar);
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

			Math_times pMathTimes =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
			Math_plus pMathPlus =
				(Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

			pMathTimes.addFactor(pTmpIndex);
			pMathTimes.addFactor(m_pDefinedDataSizeVar);
			pMathPlus.addFactor(pMathTimes);
			pMathPlus.addFactor(m_pKernelIndexVar);

			MathFactor pIndexFactor = pMathPlus;

			/*配列インデックスを追加*/
			pArgVar.addArrayIndexToBack(pIndexFactor);

			/*置換*/
			pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i), pArgVar);
		}
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

	/**
	 * ブロック宣言を生成する.
	 * @param pBlockVar ブロック変数インスタンス
	 * @return 生成した宣言インスタンス
	 * @throws MathException
	 */
	protected SyntaxDeclaration createBlockDeclaration(Math_ci pBlockVar)
	throws MathException {
		/*宣言に必要なオペランドの生成*/
		Math_cn pThreadsVarX =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_THREADS_SIZE_X_NAME);
		pThreadsVarX.changeType();
		Math_cn pThreadsVarY =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_THREADS_SIZE_Y_NAME);
		pThreadsVarY.changeType();
		Math_cn pThreadsVarZ =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_THREADS_SIZE_Z_NAME);
		pThreadsVarZ.changeType();

		/*オペランドより式を生成*/
		MathExpression pArgExpressionX = new MathExpression(pThreadsVarX);
		MathExpression pArgExpressionY = new MathExpression(pThreadsVarY);
		MathExpression pArgExpressionZ = new MathExpression(pThreadsVarZ);

		/*宣言の生成*/
		SyntaxDataType pSynDim3Type = new SyntaxDataType(eDataType.DT_DIM3, 0);
		SyntaxDeclaration pSynBlockVarDec =
			new SyntaxDeclaration(pSynDim3Type, pBlockVar);

		/*宣言にコンストラクタ引数を追加*/
		pSynBlockVarDec.addConstructArgExpression(pArgExpressionX);
		pSynBlockVarDec.addConstructArgExpression(pArgExpressionY);
		pSynBlockVarDec.addConstructArgExpression(pArgExpressionZ);

		/*宣言インスタンスを戻す*/
		return pSynBlockVarDec;
	}

	/**
	 * グリッド宣言を生成する.
	 * @param pGridVar グリッド変数インスタンス
	 * @return 生成した宣言インスタンス
	 * @throws MathException
	 */
	protected SyntaxDeclaration createGridDeclaration(Math_ci pGridVar)
	throws MathException {
		/*宣言に必要なオペランドの生成*/
		Math_cn pBlocksVarX =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_BLOCKS_SIZE_X_NAME);
		pBlocksVarX.changeType();
		Math_cn pBlocksVarY =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_BLOCKS_SIZE_Y_NAME);
		pBlocksVarY.changeType();

		/*オペランドより式を生成*/
		MathExpression pArgExpressionX = new MathExpression(pBlocksVarX);
		MathExpression pArgExpressionY = new MathExpression(pBlocksVarY);

		/*宣言の生成*/
		SyntaxDataType pSynDim3Type = new SyntaxDataType(eDataType.DT_DIM3, 0);
		SyntaxDeclaration pSynGridVarDec =
			new SyntaxDeclaration(pSynDim3Type, pGridVar);

		/*宣言にコンストラクタ引数を追加*/
		pSynGridVarDec.addConstructArgExpression(pArgExpressionX);
		pSynGridVarDec.addConstructArgExpression(pArgExpressionY);

		/*宣言インスタンスを戻す*/
		return pSynGridVarDec;
	}

	/**
	 * CudaMalloc関数呼び出しを生成する.
	 * @param pDstVar メモリ割り当て先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaMalloc(Math_ci pDstVar,
			MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMallocCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAMALLOC);

		/*第一引数の構築*/
		Math_ci pNewDstVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "( void** ) &" + pDstVar.toLegalString());
		//Syntax構文群が完成するまでの暫定処置

		/*第一引数追加*/
		pSynCuMallocCall.addArgFactor(pNewDstVar);

		/*第二引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*第二引数の追加*/
		pSynCuMallocCall.addArgFactor(pMathTimes1);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMallocCall;
	}

	/**
	 * CudaMemcpy関数呼び出し(Host to Device)を生成する.
	 * @param pDstVar コピー先変数インスタンス
	 * @param pSrcVar コピー元変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaMemcpyH2D(Math_ci pDstVar,
			Math_ci pSrcVar, MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMemcpyCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAMEMCPY);

		/*第一・第二引数追加*/
		pSynCuMemcpyCall.addArgFactor(pDstVar);
		pSynCuMemcpyCall.addArgFactor(pSrcVar);

		/*第三引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*第三引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pMathTimes1);

		/*第四引数の構築*/
		Math_ci pFlagVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUDA_CONST_CUDAMEMCPYHOSTTODEVICE);
		//Syntax構文群が完成するまでの暫定処置

		/*第四引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pFlagVar);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMemcpyCall;
	}

	/**
	 * CudaMemcpy関数呼び出し(Device to Host)を生成する.
	 * @param pDstVar コピー先変数インスタンス
	 * @param pSrcVar コピー元変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaMemcpyD2H(Math_ci pDstVar,
			Math_ci pSrcVar, MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMemcpyCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAMEMCPY);

		/*第一・第二引数追加*/
		pSynCuMemcpyCall.addArgFactor(pDstVar);
		pSynCuMemcpyCall.addArgFactor(pSrcVar);

		/*第三引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*第三引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pMathTimes1);

		/*第四引数の構築*/
		Math_ci pFlagVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUDA_CONST_CUDAMEMCPYDEVICETOHOST);
		//Syntax構文群が完成するまでの暫定処置

		/*第四引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pFlagVar);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMemcpyCall;
	}

	/**
	 * createCudaMemcpyToSymbol関数呼び出しを生成する.
	 * @param pDstVar コピー先変数インスタンス
	 * @param pSrcVar コピー元変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaMemcpyToSymbol(Math_ci pDstVar,
			Math_ci pSrcVar, MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMemcpyCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAMEMCPYTOSYMBOL);

		/*第一・第二引数追加*/
		pSynCuMemcpyCall.addArgFactor(pDstVar);
		pSynCuMemcpyCall.addArgFactor(pSrcVar);

		/*第三引数の構築*/
		Math_ci pSizeofVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "sizeof( double )");
		//Syntax構文群が完成するまでの暫定処置
		Math_times pMathTimes1 =
			(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

		pMathTimes1.addFactor(pSizeofVar);
		pMathTimes1.addFactor(pDataNumFactor);

		/*第三引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pMathTimes1);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMemcpyCall;
	}

	/**
	 * cudaFree関数呼び出しを生成する.
	 * @param pDstVar 解放変数インスタンス
	 * @return 生成した関数呼び出しインスタンス
	 */
	protected SyntaxCallFunction createCudaFree(Math_ci pDstVar) {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuFreeCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAFREE);

		/*第一引数追加*/
		pSynCuFreeCall.addArgFactor(pDstVar);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuFreeCall;
	}

	/**
	 * 初期化関数呼び出しを生成する.
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCutDeviceInit()
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCutInitCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUTDEVICEINIT);

		/*引数の生成*/
		Math_ci pArgcVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUPROG_VAR_STR_ARGC);
		Math_ci pArgvVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUPROG_VAR_STR_ARGV);

		/*引数の追加*/
		pSynCutInitCall.addArgFactor(pArgcVar);
		pSynCutInitCall.addArgFactor(pArgvVar);

		/*関数呼び出しを戻す*/
		return pSynCutInitCall;
	}

	/**
	 * 終了関数呼び出しを生成する.
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCutExit()
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCutExitCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUTEXIT);

		/*引数の生成*/
		Math_ci pArgcVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUPROG_VAR_STR_ARGC);
		Math_ci pArgvVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUPROG_VAR_STR_ARGV);

		/*引数の追加*/
		pSynCutExitCall.addArgFactor(pArgcVar);
		pSynCutExitCall.addArgFactor(pArgvVar);

		/*関数呼び出しを戻す*/
		return pSynCutExitCall;
	}

	/**
	 * generate the equation for the field propagation of the action potential (AP):
	 * ym = (__tmp_idx_y!=0)?(xi[ ( 0 * __DATA_NUM ) +(  __tmp_idx_x + ( (__tmp_idx_y - 1)
	 *  *  __tmp_size_x)) ] - xi[ ( 0 * __DATA_NUM ) + __tid ])/R:(double)0.0;
	 * @param voltageIndexVar the index of the voltage variable V
	 * @param propVariable AP propagation variable
	 * @return
	 * @throws MathException
	 */
	protected SyntaxExpression createPropagation(int voltageIndexVar, String propVariable)
	throws MathException {
		// generate the propagation formula ym = (__tmp_idx_y!=0)?(xi[ ( 0 * __DATA_NUM )
		// +(  __tmp_idx_x + ( (__tmp_idx_y - 1)  *  __tmp_size_x)) ] - xi[ ( 0 * __DATA_NUM ) + __tid ])/R:(double)0.0;
		String voltageIndex = String.valueOf(voltageIndexVar);
		String strPropArg = "(__tmp_idx_y!=0)?(xi[ (" + voltageIndex + " * "
			+ CUPROG_DEFINE_DATANUM_NAME + ") + (" + CUPROG_KERNEL_TMP_INDEX1 + ""
			+ " + ( (" + CUPROG_KERNEL_TMP_INDEX2 + " - 1)  *  "
			+ CUPROG_KERNEL_TMP_INDEX3 + ") ) ] - xi[ (" + voltageIndex + " * "
			+ CUPROG_DEFINE_DATANUM_NAME + ") + " + CUPROG_KERNEL_INDEX + " ])/R:(double)0.0";

		/* Assign the equation to the variable designated by the function argument */
		Math_ci pPropagationArg = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strPropArg);
		Math_ci pPropagationVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, propVariable);
		Math_assign pMathAssign = (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
		pMathAssign.addFactor(pPropagationVar);
		pMathAssign.addFactor(pPropagationArg);

		/*新たな計算式を生成*/
		MathExpression pNewExp = new MathExpression(pMathAssign);

		/*添え字の付加*/
		SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);

		return pSyntaxExp;
	}

}
