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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * This class is Implicit Function Transposition for code generator.
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

public class ImplicitFunctionTransposition {
	
	public MathExpression transporseExpression(MathExpression expression, Math_ci derivedVariable) throws MathException {
			
			MathExpression pNewExpression = new MathExpression();
			//属性情報を取得
			String[] strAttr=((MathOperator)expression.getRootFactor()).getAttribute();
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
		
		return pNewExpression;
	}
	

	public MathExpression implicit_transposition(
			MathExpression expression,String[] strAttr,MathFactor leftSide,MathFactor rightSide) throws MathException{
		
		//f(x)=0移項処理メソッド
		MathExpression pNewExpression = new MathExpression();
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		
		pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
			//左辺追加
			pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
			
			if((leftSide.matches(eMathMLClassification.MML_OPERATOR))){
				pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator((MathOperator) leftSide);
				pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			} else{
				pNewExpression.addOperand((MathOperand) leftSide);
			}
			
			if((rightSide.matches(eMathMLClassification.MML_OPERATOR))){
				pNewExpression.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator((MathOperator) rightSide);
				pNewExpression.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
}
