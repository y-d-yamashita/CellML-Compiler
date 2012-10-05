package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor.ForceSingleLevelIndexArrayVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxStatement;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxStatementList;

/**
 * CUDA計算カーネル構文生成クラス.
 * CudaProgramGeneratorクラスから計算カーネル生成部を切り離したクラス
 */
public class CudaRecurrenceCalcKernelGenerator extends CudaRecurrenceProgramGenerator {

	/**CUDA計算カーネルの名前*/
	public static final String CUPROG_CALC_KERNEL_NAME = "__calc_kernel";
	private static Vector<SyntaxExpression> vecExpressions;
	/**
	 * CUDA計算カーネル構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 * @throws MathException
	 */
	public CudaRecurrenceCalcKernelGenerator(RecMLAnalyzer pRecMLAnalyzer)
	throws MathException {
		super(pRecMLAnalyzer);
	}

	
	/**
	 * 計算カーネルを生成し，返す.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 * @throws TranslateException
	 * @throws SyntaxException 
	 */
	public SyntaxFunction getCudaSyntaxCalcKernel()
	throws MathException, TranslateException, SyntaxException {
		//----------------------------------------------
		//カーネル生成
		//----------------------------------------------
		/*カーネル生成*/
		SyntaxFunction  pSynKernel = super.createKernel(CUPROG_CALC_KERNEL_NAME);

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*RecVarの宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}
/*
		//微係数変数の宣言
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			//double型構文生成
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
			//宣言用変数の生成
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						   m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			//宣言の生成
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			//カーネル引数宣言の追加
			pSynKernel.addParam(pSynTimeVarDec);
		}
*/
		/*ArithVarの宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			/*double型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}

		/*定数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {

			/*double型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynKernel.addParam(pSynTimeVarDec);
		}

		/*
		//時間変数の宣言
		{
			//double型構文生成
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			//宣言用変数の生成
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());

			//宣言の生成
			SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			//カーネルへの引数宣言追加
			pSynKernel.addParam(pSynTimeDec);
		}
		 */
		/*
		//デルタ変数の宣言
		{
			//double型構文生成
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			//宣言用変数の生成
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

			//宣言の生成
			SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			//カーネルへの引数宣言追加
			pSynKernel.addParam(pSynDeltaDec);
		}
		 */
	//----------------------------------------------
		//数式の生成と追加
		//----------------------------------------------
		/*数式を生成し，取得*/
/*
		Vector<SyntaxExpression> vecExpressions = this.createExpressions();
*/
		//カーネル中に数式を追加
		int nExpressionNum = getVecExpressions().size();

		for (int i = 0; i < nExpressionNum; i++) {
			
			ForceSingleLevelIndexArrayVisitor visitor = new ForceSingleLevelIndexArrayVisitor();
			List<String> maxSizeList = new ArrayList<String>();
			maxSizeList.add(super.CUPROG_DEFINE_MAXARRAYNUM_NAME);
			maxSizeList.add(super.CUPROG_DEFINE_MAXARRAYNUM_NAME);
			maxSizeList.add(super.CUPROG_DEFINE_MAXARRAYNUM_NAME);
			visitor.setIndexMaxSizeList(maxSizeList);
			getVecExpressions().get(i).getExpression().traverse(visitor);
			//数式の追加
			pSynKernel.addStatement(getVecExpressions().get(i));
		}
	

		
	
		/*生成したカーネルインスタンスを返す*/
		return pSynKernel;
	}


	public static Vector<SyntaxExpression> getVecExpressions() {
		return vecExpressions;
	}


	public static void setVecExpressions(Vector<SyntaxExpression> expressions) {
		vecExpressions = expressions;
	}

}
