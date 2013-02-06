package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;

/**
 * Java　関数呼び出し構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxCallFunction extends JavaSyntaxStatement {

	/**関数名*/
	protected String m_strFuncName;

	/**引数*/
	protected Vector<MathFactor> m_vecArgFactor;	//引数
	protected Vector<MathFactor> m_vecKernelParam;	//ブロック・グリッドサイズ，(共有メモリサイズ)

	/**キャスト型*/
	protected JavaSyntaxDataType m_pCastDataType;
	
	/**ベクタサイズ*/
	protected int size;
	
	public void setSize(int n){
		this.size=n;
	}
	public int getSize(){
		return this.size;
	}
	
	/**
	 * 関数呼び出し構文インスタンスを作成する.
	 * @param strFuncName 関数名
	 */
	public JavaSyntaxCallFunction(String strFuncName) {
		super(eSyntaxClassification.SYN_JAVA_CALLFUNCTION);
		m_strFuncName = strFuncName;
		m_pCastDataType = null;
		m_vecArgFactor = new Vector<MathFactor>();
		m_vecKernelParam = new Vector<MathFactor>();
		size=0;
	}

	/**
	 * 関数呼び出しへの引数を追加する.
	 * @param pArgFactor 関数呼び出しの引数要素
	 */
	public void addArgFactor(MathFactor pArgFactor) {
		/*ベクタへの追加*/
		m_vecArgFactor.add(pArgFactor);
	}

	/**
	 * カーネル関数呼び出しへのパラメータを追加する.
	 * @param pParamFactor カーネル関数呼び出しのパラメータ要素(ブロックサイズなど)
	 */
	public void addKernelParam(MathFactor pParamFactor) {
		/*ベクタへの追加*/
		m_vecKernelParam.add(pParamFactor);
	}

	/**
	 * 関数呼び出しの戻り値キャスト型を追加する.
	 * @param pDataType キャスト先のデータ型
	 */
	public void addCastDataType(JavaSyntaxDataType pDataType) {
		/*キャスト先のデータ型を指定*/
		m_pCastDataType = pDataType;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalJavaString()
	 */
	public String toLegalJavaString()
	throws MathException, SyntaxException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*キャスト型が指定されていればキャスト構文追加*/
		if (m_pCastDataType != null) {
			strPresentText.append("(" + m_pCastDataType.toLegalJavaString() + ")");
		}

		/*カーネル関数名追加*/
		strPresentText.append(m_strFuncName);

		//------------------------------------------
		//カーネルパラメータ追加
		//------------------------------------------
		if (m_vecKernelParam.size() > 0) {
			/*開始括弧追加*/
			strPresentText.append(" <<< ");

			/*順次パラメータを追加*/
			boolean first = true;
			for (MathFactor it1: m_vecKernelParam) {

				/*コンマ追加*/
				if (first) {
					first = false;
				} else {
					strPresentText.append(" , ");
				}

				/*パラメータ追加*/
				strPresentText.append(it1.toLegalJavaString());
			}

			/*終了括弧追加*/
			strPresentText.append(" >>> ");
		}

		//------------------------------------------
		//関数引数追加
		//------------------------------------------
		/*開始括弧追加*/
		strPresentText.append(" ( ");

		/*順次引数を追加*/
		boolean first = true;
		for (MathFactor it2: m_vecArgFactor) {

			/*コンマ追加*/
			if (first) {
				first = false;
			} else {
				strPresentText.append(" , ");
			}

			/*引数追加*/
			strPresentText.append(it2.toLegalJavaString());
		}

		/*終了括弧追加*/
		strPresentText.append(" ) ; ");

		/*文字列を返す*/
		return strPresentText.toString();
	}

	/**
	 * Javaプログラムの文字列に変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public String toJavaString()
	throws MathException, SyntaxException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*カーネル関数名追加*/
		strPresentText.append(m_strFuncName);
		
		for(int i=0;i<this.size;i++){
		
			if(i==0){
				/*キャスト型が指定されていればキャスト構文追加*/
				if (m_pCastDataType != null) {
					strPresentText.append(" " + m_pCastDataType.toLegalJavaString() + "[");
				}
			}else{
				strPresentText.append("[");
			}
			
			//------------------------------------------
			//カーネルパラメータ追加
			//------------------------------------------
			if (m_vecKernelParam.size() > 0) {
				/*開始括弧追加*/
				strPresentText.append(" <<< ");

				/*順次パラメータを追加*/
				boolean first = true;
				for (MathFactor it1: m_vecKernelParam) {

					/*コンマ追加*/
					if (first) {
						first = false;
					} else {
						strPresentText.append(" , ");
					}

					/*パラメータ追加*/
					strPresentText.append(it1.toLegalJavaString());
				}

				/*終了括弧追加*/
				strPresentText.append(" >>> ");
			}
			
			//------------------------------------------
			//関数引数追加
			//------------------------------------------
			/*開始括弧追加*/

			/*順次引数を追加*/
			boolean first = true;
			for (MathFactor it2: m_vecArgFactor) {

				/*コンマ追加*/
				if (first) {
					first = false;
				} else {
					strPresentText.append(" , ");
				}

				/*引数追加*/
				strPresentText.append(it2.toLegalJavaString());
			}
			strPresentText.append("]");
		}
		

		/*文字列を返す*/
		return strPresentText.toString();
	}

	/**
	 * Java Bigdecimalプログラムの文字列に変換する.
	 * @return 変換した文字列
	 * @throws SyntaxException
	 * @throws MathException
	 */
	public String toJavaBigDecimalString()
	throws SyntaxException, MathException
	{
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*カーネル関数名追加*/
		strPresentText.append(m_strFuncName);

		/*キャスト型が指定されていればキャスト構文追加*/
		if (m_pCastDataType != null) {
			strPresentText.append(" " + m_pCastDataType.toLegalJavaString() + "[");
		}

		//------------------------------------------
		//カーネルパラメータ追加
		//------------------------------------------
		if (m_vecKernelParam.size() > 0) {
			/*開始括弧追加*/
			strPresentText.append(" <<< ");

			/*順次パラメータを追加*/
			boolean first = true;
			for (MathFactor it1: m_vecKernelParam) {

				/*コンマ追加*/
				if (first) {
					first = false;
				} else {
					strPresentText.append(" , ");
				}

				/*パラメータ追加*/
				strPresentText.append(it1.toLegalJavaString());
			}

			/*終了括弧追加*/
			strPresentText.append(" >>> ");
		}

		//------------------------------------------
		//関数引数追加
		//------------------------------------------
		/*開始括弧追加*/

		/*順次引数を追加*/
		boolean first = true;
		for (MathFactor it2: m_vecArgFactor) {

			/*コンマ追加*/
			if (first) {
				first = false;
			} else {
				strPresentText.append(" , ");
			}

			/*引数追加*/
			strPresentText.append(it2.toLegalJavaString());
		}
		strPresentText.append("]");

		/*文字列を返す*/
		return strPresentText.toString();
	}

}
