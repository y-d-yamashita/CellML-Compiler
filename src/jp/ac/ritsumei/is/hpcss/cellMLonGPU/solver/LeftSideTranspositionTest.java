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
 * 数式ライブラリ対応 導出変数左辺移項テストクラス
 * @author n-washio
 */
//修正版 2012/10/29
public class LeftSideTranspositionTest {

	public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String strAttrLoop1 = "null";
		String strAttrLoop2 = "null";
		String strAttrLoop3 = "null";
		String strAttrLoop4 = "null";
		String strAttrLoop5 = "null";
		String[] strAttr = new String[] {strAttrLoop1, strAttrLoop2, strAttrLoop3, strAttrLoop4, strAttrLoop5};
	
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "F");
		Math_ci val7=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "G");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "2");
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
			
			
			//左辺追加
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			
				//plus第１要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
				pNewExpression.addOperand(val1);
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				pNewExpression.addOperand(val2);
				pNewExpression.addOperand(num);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression.addOperand(val3);
				pNewExpression.addOperand(val4);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
				
				//plus第3要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				pNewExpression.addOperand(val5);
				pNewExpression.addOperand(val6);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression.addOperand(val7);

		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
		System.out.println("Input Expression:");
		System.out.println(pNewExpression.toLegalString());
		System.out.println("");
		
		//クラステスト1
		MathExpression pNewExpression2 = new MathExpression();
		LeftSideTransposition lst2 = new LeftSideTransposition();
		pNewExpression2 = lst2.transporseExpression(pNewExpression,val2);
		System.out.println("Derived variable name : "+val2.toLegalString());
		System.out.println(pNewExpression2.toLegalString());
		System.out.println("");
		
		//クラステスト2
		MathExpression pNewExpression3 = new MathExpression();
		LeftSideTransposition lst3 = new LeftSideTransposition();
		pNewExpression3 = lst3.transporseExpression(pNewExpression,val3);
		System.out.println("Derived variable name : "+val3.toLegalString());
		System.out.println(pNewExpression3.toLegalString());
		System.out.println("");
		
		//クラステスト3
		MathExpression pNewExpression4 = new MathExpression();
		LeftSideTransposition lst4 = new LeftSideTransposition();
		pNewExpression4 = lst4.transporseExpression(pNewExpression,val4);
		System.out.println("Derived variable name : "+val4.toLegalString());
		System.out.println(pNewExpression4.toLegalString());
		System.out.println("");
		
		//クラステスト4
		MathExpression pNewExpression5 = new MathExpression();
		LeftSideTransposition lst5 = new LeftSideTransposition();
		pNewExpression5 = lst5.transporseExpression(pNewExpression,val5);
		System.out.println("Derived variable name : "+val5.toLegalString());
		System.out.println(pNewExpression5.toLegalString());
		System.out.println("");
		
		//クラステスト5
		MathExpression pNewExpression6 = new MathExpression();
		LeftSideTransposition lst6 = new LeftSideTransposition();
		pNewExpression6 = lst6.transporseExpression(pNewExpression,val6);
		System.out.println("Derived variable name : "+val6.toLegalString());
		System.out.println(pNewExpression6.toLegalString());
		System.out.println("");
		
		//クラステスト6
		MathExpression pNewExpression7 = new MathExpression();
		LeftSideTransposition lst7 = new LeftSideTransposition();
		pNewExpression7 = lst7.transporseExpression(pNewExpression,val7);
		System.out.println("Derived variable name : "+val7.toLegalString());
		System.out.println(pNewExpression7.toLegalString());
		System.out.println("");
		
	}
}
