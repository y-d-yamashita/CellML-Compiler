package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.JavaBigDecimalProgramGeneratorSingleton;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * 宣言構文クラス.
 */
public class SyntaxDeclaration extends Syntax {

	//========================================================
	//DEFINE
	//========================================================
	/**宣言指定子 static*/
	public static final String DS_STR_STATIC = "static";
	/**宣言指定子 const*/
	public static final String DS_STR_CONST = "const";
	/**宣言指定子 __host__*/
	public static final String DS_STR_HOST = "__host__";
	/**宣言指定子 __device__*/
	public static final String DS_STR_DEVICE = "__device__";
	/**宣言指定子 __global__*/
	public static final String DS_STR_GLOBAL = "__global__";
	/**宣言指定子 __constant__*/
	public static final String DS_STR_CONSTANT = "__constant__";

	//========================================================
	//ENUM
	//========================================================
	/**宣言時修飾子列挙型*/
	public enum eDeclarationSpecifier {
		DS_NONE,
		DS_STATIC,
		DS_CONST,
		DS_CUDA_HOST,
		DS_CUDA_DEVICE,
		DS_CUDA_GLOBAL,
		DS_CUDA_CONSTANT,
	};

	/**内部の構文*/
	protected SyntaxDataType m_pSynDataType;
	protected MathOperand m_pMathOperand;

	/**変数宣言時修飾子*/
	protected eDeclarationSpecifier m_decSpecifier;

	/**初期化式*/
	MathExpression m_pInitExpression;

	/**コンストラクタ引数*/
	Vector<MathExpression> m_vecConstructArgExpression;

	/**
	 * 宣言構文インスタンスを作成する.
	 * @param pSynDataType データ型構文インスタンス
	 * @param pMathOperand 変数インスタンス
	 */
	public SyntaxDeclaration(SyntaxDataType pSynDataType, MathOperand pMathOperand) {
		super(eSyntaxClassification.SYN_DECLARATION);
		m_pSynDataType = pSynDataType;
		m_pMathOperand = pMathOperand;
		m_decSpecifier = eDeclarationSpecifier.DS_NONE;
		m_pInitExpression = null;
		m_vecConstructArgExpression = new Vector<MathExpression>();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalString()
	 */
	public String toLegalString()
	throws MathException, SyntaxException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*宣言指定子追加*/
		switch (m_decSpecifier) {

		case DS_STATIC:
			strPresentText.append(DS_STR_STATIC + " ");
			break;

		case DS_CONST:
			strPresentText.append(DS_STR_CONST + " ");
			break;

		case DS_CUDA_HOST:
			strPresentText.append(DS_STR_HOST + " ");
			break;

		case DS_CUDA_DEVICE:
			strPresentText.append(DS_STR_DEVICE + " ");
			break;

		case DS_CUDA_GLOBAL:
			strPresentText.append(DS_STR_GLOBAL + " ");
			break;

		case DS_CUDA_CONSTANT:
			strPresentText.append(DS_STR_CONSTANT + " ");
			break;
		}

		/*宣言文追加*/
		strPresentText.append(m_pSynDataType.toLegalString() + " "
				+ m_pMathOperand.toLegalString() + m_pSynDataType.toStringSuffix());

		/*初期化式追加1*/
		if (m_pInitExpression != null) {
			JavaBigDecimalProgramGeneratorSingleton pInstance =
				JavaBigDecimalProgramGeneratorSingleton.GetInstance();

			if (pInstance.getMode() &&
					m_pSynDataType.toLegalString().equals("BigDecimal")) {
				strPresentText.append(" = new BigDecimal( "
						+ m_pInitExpression.toLegalString()+ ")");
			} else {
				strPresentText.append(" = " + m_pInitExpression.toLegalString());
			}
		}
		else if (m_vecConstructArgExpression.size() > 0) {

			/*開始括弧追加*/
			strPresentText.append(" ( ");

			/*順次追加*/
			boolean first = true;
			for (MathExpression it: m_vecConstructArgExpression) {

				/*はじめの引数以外の前に,を追加*/
				if (first) {
					first = false;
				} else {
					strPresentText.append(" , ");
				}

				strPresentText.append(it.toLegalString());
			}

			/*終了括弧追加*/
			strPresentText.append(" ) ");
		}

		/*終端セミコロン追加*/
		strPresentText.append(";");

		/*結果を戻す*/
		return strPresentText.toString();
	}

	/**
	 * 引数宣言用文字列型に変換する.
	 * @return 引数宣言用文字列
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public String toStringParam()
	throws MathException, SyntaxException {
		return m_pSynDataType.toLegalString() + " "
			+ m_pMathOperand.toLegalString() + m_pSynDataType.toStringSuffix();
	}

	/**
	 * 宣言指定子を追加する.
	 * @param decSpecifier 宣言指定子列挙型
	 */
	public void addDeclarationSpecifier(eDeclarationSpecifier decSpecifier) {
		/*宣言指定子追加*/
		m_decSpecifier = decSpecifier;
	}

	/**
	 * 初期化式を追加する.
	 * @param pExpression 初期化式インスタンス
	 */
	public void addInitExpression(MathExpression pExpression) {
		m_pInitExpression = pExpression;
	}

	/**
	 * コンストラクタ引数を追加する.
	 * @param pExpression 引数インスタンス
	 */
	public void addConstructArgExpression(MathExpression pExpression) {
		m_vecConstructArgExpression.add(pExpression);
	}

	/**
	 * 宣言より変数を取り出す.
	 * @return 宣言より取り出された変数インスタンス.
	 * （Syntax構文クラス群が完成すれば差し替えられる）
	 * @throws SyntaxException
	 */
	public Math_ci getDeclaredVariable()
	throws SyntaxException {
		/*Math_ciでない場合は例外を投げる*/
		if (!m_pMathOperand.matches(eMathOperand.MOPD_CI)) {
			throw new SyntaxException("SyntaxDeclaration","getDeclaredVariable",
						  "invalid variable");
		}

		/*変数インスタンスを返す*/
		return (Math_ci)m_pMathOperand;
	}

	/**
	 * インスタンスを照合する.
	 * @param pDeclaration 比較対象インスタンス
	 * @return 一致判定
	 */
	public boolean matches(SyntaxDeclaration pDeclaration) {
		/*宣言変数の照合結果を返す*/
		return m_pMathOperand.matches(pDeclaration.m_pMathOperand);
	}

}
