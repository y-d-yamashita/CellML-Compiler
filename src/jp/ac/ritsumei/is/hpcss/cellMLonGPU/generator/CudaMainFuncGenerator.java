package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * CUDAメイン関数構文生成クラス.
 * CudaProgramGeneratorクラスからメイン関数生成部を切り離したクラス
 */
public class CudaMainFuncGenerator extends CudaProgramGenerator {

	//========================================================
	//DEFINE
	//========================================================
	private static final String CUPROG_DEVICE_VARIABLES_PREFIX = "__dev_";
	private static final String CUPROG_HOST_VARIABLES_PREFIX = "__host_";

	private static final String CUPROG_BLOCK_VAR_NAME = "__block";
	private static final String CUPROG_GRID_VAR_NAME = "__grid";

	/*宣言変数ベクタ*/
	SyntaxDeclaration m_pSynHostInputVarDec;
	SyntaxDeclaration m_pSynHostOutputVarDec;
	SyntaxDeclaration m_pSynDevInputVarDec;
	SyntaxDeclaration m_pSynDevOutputVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostTimeVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostDiffVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostConstVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevTimeVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevDiffVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevConstVarDec;
	SyntaxDeclaration m_pSynTimeDec;
	SyntaxDeclaration m_pSynDeltaDec;
	SyntaxDeclaration m_pSynBlockDec;
	SyntaxDeclaration m_pSynGridDec;

	/**
	 * CUDAメイン関数構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 * @throws MathException
	 */
	public CudaMainFuncGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
	throws MathException {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
		m_vecSynHostTimeVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostDiffVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostConstVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevTimeVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevDiffVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevConstVarDec = new Vector<SyntaxDeclaration>();
	}

	/**
	 * メイン関数構文を生成し，返す.
	 * @return 関数構文インスタンス
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public SyntaxFunction getSyntaxMainFunction()
	throws MathException, SyntaxException {
		//----------------------------------------------
		//メイン関数生成
		//----------------------------------------------
		/*メイン関数生成・追加*/
		SyntaxFunction pSynMainFunc = this.createMainFunction();

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*微分変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostTimeVarDec.add(pSynHostVarDec);
			m_vecSynDevTimeVarDec.add(pSynDevVarDec);

			/*入出力変数をメンバ変数に格納*/
			if (m_pTecMLAnalyzer.getM_vecDiffVar().get(i).
					matches(m_pTecMLAnalyzer.getM_pInputVar())) {
				m_pSynHostInputVarDec = pSynHostVarDec;
				m_pSynDevInputVarDec = pSynDevVarDec;
			}
			else if (m_pTecMLAnalyzer.getM_vecDiffVar().get(i).
					matches(m_pTecMLAnalyzer.getM_pOutputVar())) {
				m_pSynHostOutputVarDec = pSynHostVarDec;
				m_pSynDevOutputVarDec = pSynDevVarDec;
			}
		}

		/*微係数変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostDiffVarDec.add(pSynHostVarDec);
			m_vecSynDevDiffVarDec.add(pSynDevVarDec);
		}

		/*通常変数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostVarDec.add(pSynHostVarDec);
			m_vecSynDevVarDec.add(pSynDevVarDec);
		}

		/*定数の宣言*/
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecConstVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecConstVar().get(i).toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostConstVarDec.add(pSynHostVarDec);
			m_vecSynDevConstVarDec.add(pSynDevVarDec);
		}

		/*時間変数の宣言*/
		{
			/*型構文生成*/
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynTimeDec);

			/*メンバ変数に格納*/
			m_pSynTimeDec = pSynTimeDec;
		}

		/*デルタ変数の宣言*/
		{
			/*型構文生成*/
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

			/*初期化式の生成*/
			Math_cn pConstDeltaVal =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						StringUtil.doubleToString(m_dDeltaTime));
			MathExpression pInitExpression = new MathExpression(pConstDeltaVal);

			/*宣言の生成*/
			SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			/*初期化式の追加*/
			pSynDeltaDec.addInitExpression(pInitExpression);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynDeltaDec);

			/*メンバ変数に格納*/
			m_pSynDeltaDec = pSynDeltaDec;
		}

		/*グリッド・ブロック変数宣言を生成*/
		Math_ci pBlockVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, CUPROG_BLOCK_VAR_NAME);
		Math_ci pGridVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, CUPROG_GRID_VAR_NAME);

		m_pSynBlockDec = this.createBlockDeclaration(pBlockVar);
		m_pSynGridDec = this.createGridDeclaration(pGridVar);

		/*グリッド・ブロック宣言の追加*/
		pSynMainFunc.addDeclaration(m_pSynBlockDec);
		pSynMainFunc.addDeclaration(m_pSynGridDec);

		//----------------------------------------------
		//CUT_DEVICE_INIT呼び出しの追加
		//----------------------------------------------
		pSynMainFunc.addStatement(this.createCutDeviceInit());

		//----------------------------------------------
		//ホスト変数用malloc呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*malloc文を追加*/
			pSynMainFunc.addStatement(this.createMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostDiffVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*malloc文を追加*/
			pSynMainFunc.addStatement(this.createMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*malloc文を追加*/
			pSynMainFunc.addStatement(this.createMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			/*malloc文を追加*/
			pSynMainFunc.addStatement(this.createMalloc(pTmpDstVar, pDataNumVar));
		}

		//----------------------------------------------
		//ホスト変数用memset呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostDiffVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pDataNumVar));
		}

		//----------------------------------------------
		//cudaMalloc呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynDevDiffVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynDevVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaMalloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaMalloc(pTmpDstVar, pDataNumVar));
		}

		//----------------------------------------------
		//cudaMemcpy呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー元・コピー先変数インスタンス生成*/
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMemcpy文を追加*/
			pSynMainFunc.addStatement(this.createCudaMemcpyH2D(pTmpDstVar, pTmpSrcVar, pMathTimes1));

		}
		for (int i = 0; i < m_vecSynHostDiffVarDec.size(); i++) {

			/*コピー元・コピー先変数インスタンス生成*/
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMemcpy文を追加*/
			pSynMainFunc.addStatement(this.createCudaMemcpyH2D(pTmpDstVar, pTmpSrcVar, pMathTimes1));
		}
		for(int i = 0; i < m_vecSynHostVarDec.size(); i++) {

			/*コピー元・コピー先変数インスタンス生成*/
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			/*cudaMemcpy文を追加*/
			pSynMainFunc.addStatement(this.createCudaMemcpyH2D(pTmpDstVar, pTmpSrcVar, pMathTimes1));
		}
		for(int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			/*コピー元・コピー先変数インスタンス生成*/
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			/*cudaMemcpy文を追加*/
			pSynMainFunc.addStatement(this.createCudaMemcpyH2D(pTmpDstVar, pTmpSrcVar, pDataNumVar));
		}

		//----------------------------------------------
		//初期化カーネル呼び出し追加
		//----------------------------------------------
		/*初期カーネル呼び出しの追加*/
		SyntaxCallFunction pInitKernelCall =
			new SyntaxCallFunction(CudaInitKernelGenerator.CUPROG_INIT_KERNEL_NAME);
		pSynMainFunc.addStatement(pInitKernelCall);

		/*初期カーネル呼び出しパラメータ追加*/
		pInitKernelCall.addKernelParam(pGridVar);
		pInitKernelCall.addKernelParam(pBlockVar);

		/*引数の追加*/
		pInitKernelCall.addArgFactor(m_pSynDevInputVarDec.getDeclaredVariable());

		//----------------------------------------------
		//ループ構文の生成
		//----------------------------------------------
		/*ループ構文生成・追加*/
		Math_ci pTimeVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pTimeVar().createCopy());
		Math_ci pDeltaVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pDeltaVar().createCopy());

		SyntaxControl pSynFor1 =
			createSyntaxTimeLoop(m_dStartTime, m_dEndTime, pTimeVariale, pDeltaVariale);
		pSynMainFunc.addStatement(pSynFor1);

		/* Create the double loop for computing the time evolution of CellML formulas */ //*
		SyntaxControl pSynAPLoop2D;
		{
			Math_ci pLoopIndexVarX = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"ix");
			Math_ci pLoopIndexVarY = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"iy");
			/* Create the 1D array loop y*/ //*
			SyntaxControl pCellPotLoopX = createSyntaxDataNumLoop(m_pDefinedDataSizeVarX, pLoopIndexVarX);
			pSynAPLoop2D = createSyntaxDataNumLoop(m_pDefinedDataSizeVarY, pLoopIndexVarY);
			pSynFor1.addStatement(pCellPotLoopX);
			pCellPotLoopX.addStatement(pSynAPLoop2D);
		}

		//----------------------------------------------
		//計算カーネル呼び出し追加生成
		//----------------------------------------------
		/*計算カーネル呼び出しの追加*/
		SyntaxCallFunction pKernelCall =
			new SyntaxCallFunction(CudaCalcKernelGenerator.CUPROG_CALC_KERNEL_NAME);
		pSynAPLoop2D.addStatement(pKernelCall);

		/*計算カーネル呼び出しパラメータ追加*/
		pKernelCall.addKernelParam(pGridVar);
		pKernelCall.addKernelParam(pBlockVar);

		/*計算カーネル引数追加*/
		for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevDiffVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		pKernelCall.addArgFactor(m_pSynTimeDec.getDeclaredVariable());
		pKernelCall.addArgFactor(m_pSynDeltaDec.getDeclaredVariable());


		//----------------------------------------------
		//ホスト変数用free関数呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynHostDiffVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createFree(pTmpDstVar));
		}
		for(int i = 0; i < m_vecSynHostVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createFree(pTmpDstVar));
		}


		//----------------------------------------------
		//cudaFree呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevDiffVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevDiffVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}

		//----------------------------------------------
		//CUT_EXIT呼び出しの追加
		//----------------------------------------------
		pSynMainFunc.addStatement(this.createCutExit());

		return pSynMainFunc;
	}

}
