package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.StringUtil;

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
 * CUDAメイン関数構文生成クラス.
 * CudaProgramGeneratorクラスからメイン関数生成部を切り離したクラス
 */
public class CudaRecurrenceMainFuncGenerator extends CudaRecurrenceProgramGenerator{

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
	Vector<SyntaxDeclaration> m_vecSynHostRecVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostTimeVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostDiffVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostArithVarDec;
	Vector<SyntaxDeclaration> m_vecSynHostConstVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevTimeVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevRecVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevDiffVarDec;
	Vector<SyntaxDeclaration> m_vecSynDevArithVarDec;
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
	public CudaRecurrenceMainFuncGenerator(RecMLAnalyzer pRecMLAnalyzer)
	throws MathException {
		super(pRecMLAnalyzer);
		super.m_pRecMLAnalyzer.replaceAllVariable("\\.","_");
		m_vecSynHostTimeVarDec = new Vector<SyntaxDeclaration>();
		//m_vecSynHostDiffVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostRecVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostArithVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynHostConstVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevTimeVarDec = new Vector<SyntaxDeclaration>();
		//m_vecSynDevDiffVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevRecVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevArithVarDec = new Vector<SyntaxDeclaration>();
		m_vecSynDevConstVarDec = new Vector<SyntaxDeclaration>();
		//pKernelCall=new SyntaxCallFunction(CudaCalcKernelGenerator.CUPROG_CALC_KERNEL_NAME);
		
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
		/*RecVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
			String parenthesis = "";
			
			//for(int j = 0; j < parenthesisnum-1; j++){
			//	parenthesis += "[" + CUPROG_DEFINE_MAXARRAYNUM_NAME + "]";
			//}
	
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString()+parenthesis);
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).toLegalString()+parenthesis);

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostRecVarDec.add(pSynHostVarDec);
			m_vecSynDevRecVarDec.add(pSynDevVarDec);

			
			//入出力変数をメンバ変数に格納
		
			/*
			  if (m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).
			 
					matches(m_pRecMLAnalyzer.getM_pInputVar())) {
				m_pSynHostInputVarDec = pSynHostVarDec;
				m_pSynDevInputVarDec = pSynDevVarDec;
			}else if (m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i).
					matches(m_pRecMLAnalyzer.getM_ArrayListOutputVar())) {
				m_pSynHostOutputVarDec = pSynHostVarDec;
				m_pSynDevOutputVarDec = pSynDevVarDec;
			}
			*/
		}

		/*微係数変数の宣言*/
		/*
		for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

			//型構文生成
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			//宣言用変数の生成
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

			///宣言の生成
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			//宣言の追加
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			//ベクタに追加
			m_vecSynHostDiffVarDec.add(pSynHostVarDec);
			m_vecSynDevDiffVarDec.add(pSynDevVarDec);
		}
*/
		/*Arith変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
			String parenthesis = "";
			//for(int j = 0; j < parenthesisnum-1; j++){
			//	parenthesis += "[" + CUPROG_DEFINE_MAXARRAYNUM_NAME + "]";
			//}

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString()+parenthesis);
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i).toLegalString()+parenthesis);

			/*宣言の生成*/
			SyntaxDeclaration pSynHostVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecHostVar);
			SyntaxDeclaration pSynDevVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecDevVar);

			/*宣言の追加*/
			pSynMainFunc.addDeclaration(pSynHostVarDec);
			pSynMainFunc.addDeclaration(pSynDevVarDec);

			/*ベクタに追加*/
			m_vecSynHostArithVarDec.add(pSynHostVarDec);
			m_vecSynDevArithVarDec.add(pSynDevVarDec);
		}

		/*Constの宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListConstVar().size(); i++) {

			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecHostVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_HOST_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());
			Math_ci pDecDevVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						CUPROG_DEVICE_VARIABLES_PREFIX +
						m_pRecMLAnalyzer.getM_ArrayListConstVar().get(i).toLegalString());

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
		/*
		{
			//型構文生成
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			//宣言用変数の生成
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());

			//宣言の生成
			SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			//宣言の追加
			pSynMainFunc.addDeclaration(pSynTimeDec);

			//メンバ変数に格納
			m_pSynTimeDec = pSynTimeDec;
		}
*/
		/*デルタ変数の宣言*/
		/*
		{
			//型構文生成
			SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

			//宣言用変数の生成
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

			//初期化式の生成
			Math_cn pConstDeltaVal =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
						StringUtil.doubleToString(m_dDeltaTime));
			MathExpression pInitExpression = new MathExpression(pConstDeltaVal);

			//宣言の生成
			SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

			//初期化式の追加
			pSynDeltaDec.addInitExpression(pInitExpression);

			//宣言の追加
			pSynMainFunc.addDeclaration(pSynDeltaDec);

			//メンバ変数に格納
			m_pSynDeltaDec = pSynDeltaDec;
		}
		*/
		/*OutputVar変数の宣言*/
		for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
			
			/*[]の数を取得する*/
			int parenthesisnum = 0;
			HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
			RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
			parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

			
			//if(parenthesisnum < 2){
				//double型ポインタ配列構文生成
			//SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 0);
			SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

				//宣言用変数の生成
				Math_ci pDecVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString());

				//宣言の生成
				SyntaxDeclaration pSynConstVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

				//宣言の追加
				pSynMainFunc.addDeclaration(pSynConstVarDec);
		/*	
		}else{
				
				//double型ポインタ配列構文生成
				SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);
				String parenthesis = "";
				for(int j = 0; j < parenthesisnum-2; j++){
					parenthesis += "[" + CUPROG_DEFINE_MAXARRAYNUM_NAME + "]";
				}
				
				//宣言用変数の生成
				Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
						m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i).toLegalString()+ parenthesis);
	
				//;宣言の生成
				SyntaxDeclaration pSynVarDec =
					new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);
	
				//宣言の追加
				pSynMainFunc.addDeclaration(pSynVarDec);
			}
			*/
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
		//pSynMainFunc.addStatement(this.createCutDeviceInit());

		//----------------------------------------------
		//ホスト変数用malloc呼び出しの追加
		//----------------------------------------------

		/*
		//RecurVar変数へのmallocによるメモリ割り当て
			for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListRecurVar().size(); i++) {

				int parenthesisnum = 0;
				HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
				RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapRecurVar();
				parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i));
				if(parenthesisnum ==1 ){
					Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							CUPROG_DEFINE_MAXARRAYNUM_NAME);
					
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i),
							pMathRecurSize));
					
				}else if(1 < parenthesisnum){
					Math_times pMathTimes =
						(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
					for(int j = 0; j < parenthesisnum; j++){
						Math_ci pMathRecurSize =
							(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
									CUPROG_DEFINE_MAXARRAYNUM_NAME);
						pMathTimes.addFactor(pMathRecurSize);
					}
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListRecurVar().get(i),
							pMathTimes));
				}
			}
				
			//ArithVar変数へのmallocによるメモリ割り当て
			for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListArithVar().size(); i++) {

				int parenthesisnum = 0;
				HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
				RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapArithVar();
				parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i));
				if(parenthesisnum ==1 ){
					Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							CUPROG_DEFINE_MAXARRAYNUM_NAME);
					
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i),
							pMathRecurSize));
					
				}else if(1 < parenthesisnum){
					Math_times pMathTimes =
						(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
					for(int j = 0; j < parenthesisnum; j++){
						Math_ci pMathRecurSize =
							(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
									CUPROG_DEFINE_MAXARRAYNUM_NAME);
						pMathTimes.addFactor(pMathRecurSize);
					}
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListArithVar().get(i),
							pMathTimes));
				}
			}
			
			//OutputVar変数へのmallocによるメモリ割り当て
			for (int i = 0; i < m_pRecMLAnalyzer.getM_ArrayListOutputVar().size(); i++) {
				
				//[]の数を取得する
				int parenthesisnum = 0;
				HashMap<Math_ci, Integer> RecurVarHM_G = new HashMap<Math_ci, Integer>();
				RecurVarHM_G = m_pRecMLAnalyzer.getM_HashMapOutputVar();
				parenthesisnum = RecurVarHM_G.get(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i));

				if(parenthesisnum-1 == 1 ){
					Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							CUPROG_DEFINE_MAXARRAYNUM_NAME);
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i),
							pMathRecurSize));
					
				}else if(1 < parenthesisnum-1){
					Math_times pMathTimes =
						(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
					for(int j = 0; j < parenthesisnum; j++){
						Math_ci pMathRecurSize =
							(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
									CUPROG_DEFINE_MAXARRAYNUM_NAME);
						pMathTimes.addFactor(pMathRecurSize);
					}
					pSynMainFunc.addStatement(createMalloc(m_pRecMLAnalyzer.getM_ArrayListOutputVar().get(i),
							pMathTimes));
				}
			}
			*/
			
	

		//----------------------------------------------
		//cudaHoatAalloc呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);

			/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaHostAlloc(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostRecVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpHostVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostRecVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);
			
		/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaHostAlloc(pTmpHostVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostArithVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpHostVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostArithVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);
		/*cudaMalloc文を追加*/
			pSynMainFunc.addStatement(this.createCudaHostAlloc(pTmpHostVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			//コピー先変数インスタンス生成
			Math_ci pTmpHostVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//要素数を文字列化
			String strDataNumVar =
				//String.valueOf(m_pRecMLAnalyzer.getM_vecConstVar().size());
					String.valueOf(1);

			//データ数計算式の構築
			//Math_cn pDataNumVar =
			//	(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);


			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);
	
			//cudaMalloc文を追加
			pSynMainFunc.addStatement(this.createCudaHostAlloc(pTmpHostVar, pMathTimes1));
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
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostRecVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostRecVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pMathTimes1));
		}
		for (int i = 0; i < m_vecSynHostArithVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostArithVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*要素数を文字列化*/
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListArithVar().size());

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
//			pMathTimes1.addFactor(pDataNumVar);
			Math_ci pMathRecurSize =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
										CUPROG_DEFINE_MAXARRAYNUM_NAME);

			pMathTimes1.addFactor(pMathRecurSize);
			pMathTimes1.addFactor(pMathRecurSize);

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
					//String.valueOf(m_pRecMLAnalyzer.getM_ArrayListConstVar().size());
					String.valueOf(1);

			/*データ数計算式の構築*/
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			/*memset文を追加*/
			pSynMainFunc.addStatement(this.createZeroMemset(pTmpDstVar, pDataNumVar));
		}
		//----------------------------------------------
		// cudaHostGetDevicePointer呼び出しの追加
		//----------------------------------------------
	
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			//コピー元・コピー先変数インスタンス生成
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//要素数を文字列化
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			//データ数計算式の構築
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			//cudaMemcpy文を追加
			pSynMainFunc.addStatement(this.createCudaHostGetDevicePointer(pTmpDstVar, pTmpSrcVar));

		}
		for (int i = 0; i < m_vecSynHostRecVarDec.size(); i++) {

			//コピー元・コピー先変数インスタンス生成
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostRecVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevRecVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//要素数を文字列化
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListRecurVar().size());

			//データ数計算式の構築
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			//cudaMemcpy文を追加
			pSynMainFunc.addStatement(this.createCudaHostGetDevicePointer(pTmpDstVar, pTmpSrcVar));
		}
		for(int i = 0; i < m_vecSynHostArithVarDec.size(); i++) {

			//コピー元・コピー先変数インスタンス生成
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostArithVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevArithVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//要素数を文字列化
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListArithVar().size());

			//データ数計算式の構築
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);
			Math_times pMathTimes1 =
				(Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

			pMathTimes1.addFactor(m_pDefinedDataSizeVar);
			pMathTimes1.addFactor(pDataNumVar);

			//cudaMemcpy文を追加
			pSynMainFunc.addStatement(this.createCudaHostGetDevicePointer(pTmpDstVar, pTmpSrcVar));
		}
		for(int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			//コピー元・コピー先変数インスタンス生成
			Math_ci pTmpSrcVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//要素数を文字列化
			String strDataNumVar =
				String.valueOf(m_pRecMLAnalyzer.getM_ArrayListConstVar().size());

			//データ数計算式の構築
			Math_cn pDataNumVar =
				(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, strDataNumVar);

			//cudaMemcpy文を追加
			pSynMainFunc.addStatement(this.createCudaHostGetDevicePointer(pTmpDstVar, pTmpSrcVar));
		}


		/*Initialization*/
		SyntaxControl pSynAPLoop2D;
		{
			Math_ci pLoopIndexVarX = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"ix");
			Math_ci pLoopIndexVarY = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,"iy");
			/* Create the 1D array loop y*/ //*
			SyntaxControl pCellPotLoopX = createSyntaxDataNumLoop(m_pDefinedDataSizeVarX, pLoopIndexVarX);
			pSynAPLoop2D = createSyntaxDataNumLoop(m_pDefinedDataSizeVarY, pLoopIndexVarY);
	//		pSynFor1.addStatement(pCellPotLoopX);
			pCellPotLoopX.addStatement(pSynAPLoop2D);
		}
		int MaxLoopNumber = 1;
		pSynMainFunc.addStatement(this.MainFuncSyntaxStatementList(MaxLoopNumber));
	
		//pSynAPLoop2D.addStatement(pKernelCall);
	
		
		
		//----------------------------------------------
		//初期化カーネル呼び出し追加
		//----------------------------------------------
		/*初期カーネル呼び出しの追加*/
		//SyntaxCallFunction pInitKernelCall =
		//	new SyntaxCallFunction(CudaInitKernelGenerator.CUPROG_INIT_KERNEL_NAME);
		//pSynMainFunc.addStatement(pInitKernelCall);

		/*初期カーネル呼び出しパラメータ追加*/
		//pInitKernelCall.addKernelParam(pGridVar);
		//pInitKernelCall.addKernelParam(pBlockVar);

		/*引数の追加*/
		//pInitKernelCall.addArgFactor(m_pSynDevInputVarDec.getDeclaredVariable());

		//----------------------------------------------
		//ループ構文の生成
		//----------------------------------------------
		/*ループ構文生成・追加*/
	//	Math_ci pTimeVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pTimeVar().createCopy());
	//	Math_ci pDeltaVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pDeltaVar().createCopy());

	//	SyntaxControl pSynFor1 =
	//		createSyntaxTimeLoop(m_dStartTime, m_dEndTime, pTimeVariale, pDeltaVariale);
	//	pSynMainFunc.addStatement(pSynFor1);

		/* Create the double loop for computing the time evolution of CellML formulas */ //*
	
		//----------------------------------------------
		//計算カーネル呼び出し追加生成
		//----------------------------------------------
		/*計算カーネル呼び出しの追加*/
		
		//SyntaxCallFunction pKernelCall =	
			//	new SyntaxCallFunction(CudaCalcKernelGenerator.CUPROG_CALC_KERNEL_NAME);
	
		//計算カーネル呼び出しパラメータ追加
		//pKernelCall.addKernelParam(pGridVar);
		//pKernelCall.addKernelParam(pBlockVar);
/*
		//計算カーネル引数追加
		for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevRecVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevRecVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevArithVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevArithVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {
			Math_ci pParamVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());
			pKernelCall.addArgFactor(pParamVar);
		}
		*/
		//pKernelCall.addArgFactor(m_pSynTimeDec.getDeclaredVariable());
		//pKernelCall.addArgFactor(m_pSynDeltaDec.getDeclaredVariable());

		//pSynMainFunc.addStatement(pKernelCall);
		//----------------------------------------------
		//ホスト変数用free関数呼び出しの追加
		//----------------------------------------------
		for (int i = 0; i < m_vecSynHostTimeVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFreeHost(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynHostRecVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostRecVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFreeHost(pTmpDstVar));
		}
		for(int i = 0; i < m_vecSynHostArithVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostArithVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFreeHost(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynHostConstVarDec.size(); i++) {

			/*コピー先変数インスタンス生成*/
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynHostConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			/*cudaFree文を追加*/
			pSynMainFunc.addStatement(this.createCudaFreeHost(pTmpDstVar));
		}


		//----------------------------------------------
		//cudaFree呼び出しの追加
		//----------------------------------------------

		/*
		  for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {
		 

			//コピー先変数インスタンス生成
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//cudaFree文を追加
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevRecVarDec.size(); i++) {

			//コピー先変数インスタンス生成
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevRecVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//cudaFree文を追加
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevArithVarDec.size(); i++) {

			//コピー先変数インスタンス生成
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevArithVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//cudaFree文を追加
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {

			//コピー先変数インスタンス生成
			Math_ci pTmpDstVar =
				(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
						getDeclaredVariable().createCopy());

			//cudaFree文を追加
			pSynMainFunc.addStatement(this.createCudaFree(pTmpDstVar));
		}
		 */

		//----------------------------------------------
		//CUT_EXIT呼び出しの追加
		//----------------------------------------------
		pSynMainFunc.addStatement(this.createCutExit());

		return pSynMainFunc;
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
		Math_cn pThreadsVarY =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_THREADS_SIZE_Y_NAME);
		Math_cn pThreadsVarZ =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_THREADS_SIZE_Z_NAME);

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
		Math_cn pBlocksVarY =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
							   CUPROG_DEFINE_BLOCKS_SIZE_Y_NAME);

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
	 * CudaMalloc関数呼び出しを生成する.
	 * @param pHostVar メモリ割り当て先変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaHostAlloc(Math_ci pHostVar,
			MathFactor pDataNumFactor)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMallocCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAHOSTALLOC);

		/*第一引数の構築*/
		Math_ci pNewDstVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "( void** ) &" + pHostVar.toLegalString());
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

		/*第3引数の構築*/
		Math_ci pFlagVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUDA_CONST_CUDAHOSTALLOCMAPPED);
		//Syntax構文群が完成するまでの暫定処置

		/*第四引数の追加*/
		pSynCuMallocCall.addArgFactor(pFlagVar);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMallocCall;
	}

	
	/**
	 * CudaMemcpy関数呼び出し(Host to Device)を生成する.
	 * @param pHostVar コピー先変数インスタンス
	 * @param pDevVar コピー元変数インスタンス
	 * @param pDataNumFactor データ数を表す数式ファクタインスタンス
	 * @return 生成した関数呼び出しインスタンス
	 * @throws MathException
	 */
	protected SyntaxCallFunction createCudaHostGetDevicePointer(Math_ci pDevVar,
			Math_ci pHostVar)
	throws MathException {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynCuMemcpyCall =
			new SyntaxCallFunction(CUDA_FUNC_STR_CUDAHOSTGETDEVICEPOINTER);

		/*第一引数の構築*/
		Math_ci pNewDevVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "( void** ) &" + pDevVar.toLegalString());
		
		/*第一・第二引数追加*/
		pSynCuMemcpyCall.addArgFactor(pNewDevVar);
		/*第一引数の構築*/
		Math_ci pNewHostVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   "( void* )" + pHostVar.toLegalString());

		pSynCuMemcpyCall.addArgFactor(pNewHostVar);

		/*第３引数の構築*/
		Math_ci pFlagVar =
			(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
							   CUDA_CONST_CUDAHOSTALLOCDEFAULT);
		//Syntax構文群が完成するまでの暫定処置

		/*第３引数の追加*/
		pSynCuMemcpyCall.addArgFactor(pFlagVar);

		/*関数呼び出しインスタンスを戻す*/
		return pSynCuMemcpyCall;
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
	 * cudaFreeHost関数呼び出しを生成する.
	 * @param pDstVar メモリ解放対象変数インスタンス
	 * @return 生成した関数呼び出し構文インスタンス
	 */
	public SyntaxCallFunction createCudaFreeHost(Math_ci pDstVar) {
		/*関数呼び出しインスタンス生成*/
		SyntaxCallFunction pSynFreeCall = new SyntaxCallFunction(CUDA_FUNC_STR_CUDAFREEHOST);

		/*第一引数追加*/
		pSynFreeCall.addArgFactor(pDstVar);

		/*生成したインスタンスを返す*/
		return pSynFreeCall;
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
			String[] strAttr_inner = strAttr_Now.clone();
			strAttr_inner[LoopNumber] = "inner";
	
			SyntaxStatementList aStatementList_inner = new SyntaxStatementList();

						if (m_pRecMLAnalyzer.hasChild(strAttr_inner)) {
							// create loop structure 
							//----- create "tn" = 0 (loop index string = RecMLAnalyzer.getIndexString(LoopNumber)) -----
							aStatementList.addStatement(createInitEqu(LoopNumber));
							// for debug
							strNow = aStatementList.toLegalString();
							//System.out.println(strNow);

							//----- create loop condition -----
							SyntaxControl pSynDowhile = createSyntaxDowhile(LoopNumber, strAttr_Now);
							aStatementList.addStatement(pSynDowhile);
							// for debug
							strNow = aStatementList.toLegalString();
							//System.out.println(strNow);

							//----- create inner Statements and add to Do While loop -----
							
				//strAttr[LoopNumber] = "inner" has inner loop structure
				aStatementList_inner.addStatement(MakeDowhileLoop(MaxLoopNumber, m_pRecMLAnalyzer.nextChildLoopNumber(strAttr_inner), strAttr_inner));
				pSynDowhile.addStatement(aStatementList_inner);
				// for debug
				strNow = aStatementList.toLegalString();
				//System.out.println(strNow);

				//----- insert loop counter increment -----
				pSynDowhile.addStatement(createIndexIncrementEqu(LoopNumber));
				// for debug
				strNow = aStatementList.toLegalString();
				//System.out.println(strNow);
		
						} else {
				//strAttr[LoopNumber] = "inner" has no inner loop
				
				CudaRecurrenceCalcKernelGenerator.setVecExpressions(createExpressions(strAttr_inner));
				//----------------------------------------------
				//計算カーネル呼び出し追加生成
				//----------------------------------------------
							
							
							
				/*計算カーネル呼び出しの追加*/
				SyntaxCallFunction pKernelCall =
					new SyntaxCallFunction(CudaCalcKernelGenerator.CUPROG_CALC_KERNEL_NAME);
				/*グリッド・ブロック変数宣言を生成*/
				Math_ci pBlockVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, CUPROG_BLOCK_VAR_NAME);
				Math_ci pGridVar =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, CUPROG_GRID_VAR_NAME);

				/*計算カーネル呼び出しパラメータ追加*/
				pKernelCall.addKernelParam(pGridVar);
				pKernelCall.addKernelParam(pBlockVar);

				
				
					//計算カーネル呼び出しパラメータ追加
					pKernelCall.addKernelParam(pGridVar);
					pKernelCall.addKernelParam(pBlockVar);

					//計算カーネル引数追加
					for (int i = 0; i < m_vecSynDevTimeVarDec.size(); i++) {
						Math_ci pParamVar =
							(Math_ci)(((SyntaxDeclaration)m_vecSynDevTimeVarDec.get(i)).
									getDeclaredVariable().createCopy());
						pKernelCall.addArgFactor(pParamVar);
					}
					for (int i = 0; i < m_vecSynDevRecVarDec.size(); i++) {
						Math_ci pParamVar =
							(Math_ci)(((SyntaxDeclaration)m_vecSynDevRecVarDec.get(i)).
									getDeclaredVariable().createCopy());
						pKernelCall.addArgFactor(pParamVar);
					}
					for (int i = 0; i < m_vecSynDevArithVarDec.size(); i++) {
						Math_ci pParamVar =
							(Math_ci)(((SyntaxDeclaration)m_vecSynDevArithVarDec.get(i)).
									getDeclaredVariable().createCopy());
						pKernelCall.addArgFactor(pParamVar);
					}
					for (int i = 0; i < m_vecSynDevConstVarDec.size(); i++) {
						Math_ci pParamVar =
							(Math_ci)(((SyntaxDeclaration)m_vecSynDevConstVarDec.get(i)).
									getDeclaredVariable().createCopy());
						pKernelCall.addArgFactor(pParamVar);
					}
					//pKernelCall.addArgFactor(m_pSynTimeDec.getDeclaredVariable());
					//pKernelCall.addArgFactor(m_pSynDeltaDec.getDeclaredVariable());

				
				aStatementList_inner.addStatement(pKernelCall);
				aStatementList.addStatement(aStatementList_inner);
				// for debug
				strNow = aStatementList.toLegalString();
				//System.out.println(strNow);

				//----- insert loop counter increment -----
				//aStatementList.addStatement(createIndexIncrementEqu(LoopNumber));
				// for debug
				strNow = aStatementList.toLegalString();
				//System.out.println(strNow);
		
			}
				
			
			
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
//			System.out.println("loop1 = " + strAttr2[0] + "\n");
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
//	//---------------------------------------------
//	//出力変数から入力変数への代入式の追加
//	// (TecMLには記述されていない式を追加する)
//	//---------------------------------------------
//	for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
//		/*代入式の構成*/
//		Math_assign pMathAssign =
//			(Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
//		pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar().createCopy());
//		pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar().createCopy());
//		MathExpression pMathExp = new MathExpression(pMathAssign);
////
////		/*添え字の追加*/
////		this.addIndexToTecMLVariables(pMathExp, i);
//
//		/*数式ベクタに追加*/
//		SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
//		vecExpressions.add(pSyntaxExp);
//	}
	return vecExpressions;
}


// create "tn = 0"
protected SyntaxStatement createInitEqu(int LoopNumber) {
	MathExpression tnINIT = new MathExpression();
	Math_apply tnINITApply = new Math_apply();
	tnINIT.addOperator(tnINITApply);
	Math_assign tnINITAssign = new Math_assign();
	tnINITApply.addFactor(tnINITAssign);
//	Math_ci tnINITTn = new Math_ci("tn");
//	Math_ci tnINITTn = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
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
//	Math_ci tnpINNERTnL = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
	Math_ci tnpINNERTnL = new Math_ci(m_pRecMLAnalyzer.getIndexHashMap(LoopNumber));
	tnpINNERAssign.addFactor(tnpINNERTnL);
	Math_apply tnpINNERApplyR = new Math_apply();
	tnpINNERAssign.addFactor(tnpINNERApplyR);
	Math_plus tnpINNERPlusR = new Math_plus();
	tnpINNERApplyR.addFactor(tnpINNERPlusR);
	//Math_ci tnpINNERTnR = new Math_ci("tn");
//	Math_ci tnpINNERTnR = new Math_ci(m_pRecMLAnalyzer.getIndexString(LoopNumber));
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
