package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;

/**
 * CUDA計算カーネル構文生成クラス.
 * CudaProgramGeneratorクラスから計算カーネル生成部を切り離したクラス
 */
public class CudaCalcKernelGenerator extends CudaProgramGenerator {

	/**CUDA計算カーネルの名前*/
	public static final String CUPROG_CALC_KERNEL_NAME = "__calc_kernel";

	/**
	 * CUDA計算カーネル構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 * @throws MathException
	 */
	public CudaCalcKernelGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
	throws MathException {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
	}

	/**
	 * 計算カーネルを生成し，返す.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 * @throws TranslateException
	 */
	public SyntaxFunction getSyntaxCalcKernel()
	throws MathException, TranslateException {
		//----------------------------------------------
		//カーネル生成
		//----------------------------------------------
		/*カーネル生成*/
		SyntaxFunction  pSynKernel = this.createKernel(CUPROG_CALC_KERNEL_NAME);

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*微分変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
					   m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}

		/*微係数変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*double型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}

		/*通常変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*double型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}

		/*定数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*double型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecConstVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
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

			/*カーネルへの引数宣言追加*/
			pSynKernel.addParam(pSynTimeDec);
		}

		/*デルタ変数の宣言*/
		{
			/*double型構文生成*/
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			/*カーネルへの引数宣言追加*/
			pSynKernel.addParam(pSynDeltaDec);
		}

		/* Add resistance R to the parameters */ //*
		{
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			Math_ci pCellAP =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "R");
			SyntaxDeclaration pCellPotential = new SyntaxDeclaration(pSynTypePDouble, pCellAP);
			pSynKernel.addParam(pCellPotential);
		}

		/* Declare the intercell potential variable to be used for the AP propagation */
		String propVarYM = "ym";
		{
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			Math_ci pCellAP =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, propVarYM);
			SyntaxDeclaration pCellPotential = new SyntaxDeclaration(pSynTypePDouble, pCellAP);
			pSynKernel.addDeclaration(pCellPotential);
		}

		/* Create the AP propagation formula.
		 *  The inputs are the index of the voltage variable and propagation variable ym */
		int voltageIndex = 0;
		SyntaxExpression pAPPropagationExp = this.createPropagation(voltageIndex, propVarYM);
		pSynKernel.addStatement(pAPPropagationExp);

		//----------------------------------------------
		//数式の生成と追加
		//----------------------------------------------
		/*数式を生成し，取得*/
		Vector<SyntaxExpression> vecExpressions = this.createExpressions();

		/*カーネル中に数式を追加*/
		int nExpressionNum = vecExpressions.size();

		for (int i = 0; i < nExpressionNum; i++) {

			/*数式の追加*/
			pSynKernel.addStatement(vecExpressions.get(i));
		}

		/*生成したカーネルインスタンスを返す*/
		return pSynKernel;
	}

}
