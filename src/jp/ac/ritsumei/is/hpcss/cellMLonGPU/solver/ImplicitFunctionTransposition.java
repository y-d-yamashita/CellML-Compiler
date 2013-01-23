package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * Implicit Function Transposition class for code generator.
 * 
 * Implicit Function means f(x) = 0
 * 
 * input:  
 * 	MathExpression
 * 	Math_ci (derived variable)
 * 
 * 
 * @author n-washio
 * 
 */

//構造情報対応
public class ImplicitFunctionTransposition {
	
	public MathExpression transporseExpression(MathExpression expression, Math_ci derivedVariable) throws MathException {
			
			MathExpression pNewExpression = new MathExpression();
			pNewExpression.setDerivedVariable(expression.getDerivedVariable());
			//属性情報を取得
			String[] strAttr=((MathOperator)expression.getRootFactor()).getAttribute();
			
			//構造情報取得
			HashMap<String, String> info = ((Math_apply) expression.getRootFactor()).get_hashExpInfo();
			HashMap<Integer, String> attr = ((Math_apply) expression.getRootFactor()).get_hashAttr();
			
			MathFactor leftSide = null;
			MathFactor rightSide = null;
			
			//両辺取得.
			if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				leftSide =(MathOperator) expression.getLeftExpression().getRootFactor();
			}else{
				leftSide =(MathOperand) expression.getLeftExpression().getRootFactor();
			}
			if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				rightSide =(MathOperator) expression.getRightExpression().getRootFactor();
			}else{
				rightSide =(MathOperand) expression.getRightExpression().getRootFactor();
			}
			
			pNewExpression=implicit_transposition(expression,strAttr,leftSide,rightSide);
			
			//構造情報付与
			((Math_apply)pNewExpression.getRootFactor()).set_hashExpInfo(info);
			((Math_apply)pNewExpression.getRootFactor()).set_hashAttr(attr);
			
		return pNewExpression;
	}
	

	public MathExpression implicit_transposition(
			MathExpression expression,String[] strAttr,MathFactor leftSide,MathFactor rightSide) throws MathException{
		
		//f(x)=0移項処理メソッド
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setDerivedVariable(expression.getDerivedVariable());
		
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		
		pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
			//左辺追加
			pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
			
			if(leftSide.matches(eMathMLClassification.MML_OPERATOR)){
				pNewExpression.addOperator((MathOperator) leftSide);
				pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			} else{
				pNewExpression.addOperand((MathOperand) leftSide);
			}
			
			if(rightSide.matches(eMathMLClassification.MML_OPERATOR)){
				pNewExpression.addOperator((MathOperator) rightSide);
				pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			} else{
				pNewExpression.addOperand((MathOperand) rightSide);
			}
			pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
			//右辺追加
			pNewExpression.addOperand(num);
		
		pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		return pNewExpression;
	}
	
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
		val2.addArrayIndexToFront(0);
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val3.addArrayIndexToFront(1);
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_ci val7=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "F");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "5");
		
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

		
		
		System.out.println("Input Expression : ");
		System.out.println(pNewExpression.toLegalString());
		System.out.println("");
		
		//テスト
		MathExpression pNewExpression2 = new MathExpression();
		ImplicitFunctionTransposition ift2 = new ImplicitFunctionTransposition();
		pNewExpression2 = ift2.transporseExpression(pNewExpression,val2);
		System.out.println("Implicit Function Form : ");
		System.out.println(pNewExpression2.toLegalString());
		System.out.println("");
	}
}
