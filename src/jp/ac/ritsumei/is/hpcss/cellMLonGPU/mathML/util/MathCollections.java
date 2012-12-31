package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.features.Calculator;

public class MathCollections {
	private MathCollections(){}

	/**
	 * Calculate on specified scale
	 * @param root
	 * @param scale
	 * @return
	 */
	public static Math_cn calculate(MathFactor root,int scale){
		return Calculator.calculate(root,scale);
	}
	
	/**
	 * calculate on default scale of 10
	 * @param root
	 * @param scale
	 * @return
	 */
	public static Math_cn calculate(MathFactor root){
		int defaultScale = 10;
		return Calculator.calculate(root,defaultScale);
	}

}
