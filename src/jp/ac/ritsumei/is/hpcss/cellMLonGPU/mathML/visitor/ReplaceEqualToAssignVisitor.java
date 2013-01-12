package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;

public class ReplaceEqualToAssignVisitor implements Visitor {

	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_apply){
			Math_apply apply = (Math_apply) factor;
				if(apply.getFactorVector().get(0) instanceof Math_eq){
					Math_eq eq = (Math_eq) apply.getFactorVector().get(0);
					Math_assign assign = new Math_assign();
					for(MathFactor eqFactor : eq.getFactorVector()){
						assign.addFactor(eqFactor);
					}
					apply.replace(0, assign);
				}
		}
		
	}

}
