package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.MathCollections;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;

public class CalculateVariableIndexVisitor implements Visitor {

	
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			Vector<MathFactor> indexList = ci.getIndexList();
			for(MathFactor indexRootFactor:indexList){
				Math_cn result = MathCollections.calculate(indexRootFactor);
				if(result != null){
					result.autoChangeType();
					ci.addIndexList(result,indexList.indexOf(indexRootFactor));
				}
			}
		}
	
	}

}
