package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * 数式ライブラリ対応 記号微分テストクラス
 * @author n-washio
 * 
 * plus(1,2項),plus(多項)
 * minus(1,2項),minus(多項)
 * times(2項)
 * divide
 * sin,cos,tan
 * inc,dec
 * power,root
 * 
 * -未対応
 * times(多項)
 * exp,ln
 * log
 */
public class DifferentiationTest {

	public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
	
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val2.addArrayIndexToFront(0);
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val3.addArrayIndexToFront(1);
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "5");
		
		MathExpression pExpression = new MathExpression();
		pExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
			
			
			//左辺追加
			pExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			
				//plus第１要素
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
				pExpression.addOperand(val1);
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				pExpression.addOperand(val2);
				pExpression.addOperand(num);
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pExpression.addOperand(val3);
				pExpression.addOperand(val4);
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
				
				//plus第3要素
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				pExpression.addOperand(val5);
				pExpression.addOperand(val6);
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
			pExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pExpression.addOperand(zero);

		pExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
		System.out.println("Input Function : ");
		System.out.println(pExpression.getLeftExpression().toLegalString());
		System.out.println("");
		Differentiation diff = new Differentiation();
		//導出変数を設定
		Math_ci derivedVal = val5;

		MathExpression pNewExpression = new MathExpression();
		//数式の左辺を微分して取得
		pNewExpression = diff.differentiate(pNewExpression ,derivedVal);
		
		System.out.println("Implicit Function Form : ");
		System.out.println(pNewExpression.getLeftExpression().toLegalString());
		System.out.println("");
	}
}
