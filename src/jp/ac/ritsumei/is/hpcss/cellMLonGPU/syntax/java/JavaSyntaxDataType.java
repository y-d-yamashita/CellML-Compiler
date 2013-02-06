package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;

/**
 * Java　データ型構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxDataType {

	//========================================================
	//ENUM
	//========================================================

	/**
	 * データ型列挙
	 */
	public enum eDataType {
		DT_VOID		("void"),
		DT_CHAR		("char"),
		DT_UCHAR	("unsigned char"),
		DT_SHORT	("short"),
		DT_USHORT	("unsigned short"),
		DT_INT		("int"),
		DT_UINT		("unsigned int"),
		DT_FLOAT	("float"),
		DT_DOUBLE	("double"),
		DT_DIM3		("dim3"),
		DT_PUB		("public static void"),
		DT_PUBDouble("public static double"),
		DT_STRING	("String"),
		DT_BIGDECIMAL	("BigDecimal"),
			;
		private final String operatorStr;
		private eDataType(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}
	};

	/**データ型*/
	protected eDataType m_dataType;

	/**ポインタの数*/
	protected int m_byPointerNum;

	/**配列要素数ベクタ*/
	protected Vector<MathFactor> m_vecArrayElementFactor;

	/**
	 * データ型構文インスタンスを作成する.
	 * @param dataType データ型
	 * @param byPointerNum ポインタの数
	 */
	public JavaSyntaxDataType(eDataType dataType, int byPointerNum) {
		m_dataType = dataType;
		m_byPointerNum = byPointerNum;
		m_vecArrayElementFactor = new Vector<MathFactor>();
	}



	/**
	 * 配列要素を追加する.
	 * （整数引数オーバーロード）
	 * @param ulElementNum 配列要素数
	 * @throws MathException
	 */
	public void addArrayElementFactor(long ulElementNum)
	throws MathException {
		/*要素数をMath_cnに変換*/
		Math_cn pTmpConst =
			(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
					String.valueOf(ulElementNum));

		/*ベクタに配列要素数を追加*/
		m_vecArrayElementFactor.add(pTmpConst);
	}

	/**
	 * 配列要素を追加する.
	 * @param pFactor 追加要素ファクタ
	 */
	public void addArrayElementFactor(MathFactor pFactor) {
		/*ベクタに配列要素数を追加*/
		m_vecArrayElementFactor.add(pFactor);
	}

	/**
	 * 文字列に変換する.
	 * @return 変換した文字列
	 * @throws SyntaxException
	 */
	public String toLegalJavaString() throws SyntaxException {
		/*境界値チェック*/
		if (m_dataType == null) {
			throw new SyntaxException("SyntaxDataType","toLegalJavaString",
				"undefined data type set");
		}

		/*基本のデータ型文字列*/
		StringBuilder strPresentText =
			new StringBuilder(m_dataType.getOperatorStr());

		/*ポインタ演算子付加*/
		for (int i = 0; i < m_byPointerNum; i++) {
			strPresentText.append("*");
		}

		return strPresentText.toString();
	}

	/**
	 * 接尾文字列を変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 */
	public String toStringSuffix()
	throws MathException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		//------------------------------------------
		//配列要素を文字列変換していく
		//------------------------------------------
		/*配列要素を順次追加*/
		for (MathFactor it: m_vecArrayElementFactor) {

			/*NULLは空要素として扱う*/
			if(it == null){
				strPresentText.append("[]");
			}
			else {
				/*要素追加*/
				strPresentText.append("[" + it.toLegalJavaString() + "]");
			}
		}

		return strPresentText.toString();
	}

}
