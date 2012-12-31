package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.features;

import java.math.BigDecimal;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;

/**
 * Calculation before code generation
 * @author y-yamashita
 *
 */
public class Calculator {
	private Calculator(){}
	
	public static Math_cn calculate(MathFactor root,int scale){
		if(root instanceof Math_cn){
			return (Math_cn)root;
		}
		if(root instanceof MathOperator){
			MathOperator operator = (MathOperator) root;
			BigDecimal integerPart;
			BigDecimal fractionPart = null;
			BigDecimal computaion = null;
			try {
				computaion = new BigDecimal(operator.calculate()).setScale(scale,BigDecimal.ROUND_DOWN);
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			integerPart = new BigDecimal(computaion.intValue());
			fractionPart = computaion.subtract(integerPart).setScale(scale,BigDecimal.ROUND_DOWN);

			if(fractionPart.compareTo(new BigDecimal(0))==0){
				return new Math_cn(integerPart.toString());
			}else{
				return new Math_cn(computaion.toString());
			}
		}
		return null;
	}
	
}
