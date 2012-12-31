package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.MathCollections;

public class IndexReplacingVisitor implements Visitor {

	private MathFactor replaceIndexFactor;
	private int baseIndex;
	private int indexPosition;
	public IndexReplacingVisitor(MathFactor replaceIndexFactor, int baseIndex,
			int indexPosition){
		this.replaceIndexFactor=replaceIndexFactor;
		this.baseIndex=baseIndex;
		this.indexPosition=indexPosition;
	}
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			if(ci.getM_vecIndexListFactor().size()>indexPosition){
				MathFactor indexFactor=ci.getM_vecIndexListFactor().get(indexPosition);
				Integer indexInteger=null;
				indexInteger = MathCollections.calculate(indexFactor).decode();
				Integer diffValue = indexInteger-baseIndex;
				if(diffValue.equals(0)){
					ci.getM_vecIndexListFactor().set(indexPosition, replaceIndexFactor);
				}else if(diffValue>0){

					Math_apply apply = new Math_apply();
					Math_plus plus = new Math_plus();
					Math_ci diffValueCi = new Math_ci(diffValue.toString());
				
					apply.addFactor(plus);
					plus.addFactor(replaceIndexFactor);
					plus.addFactor(diffValueCi);
					ci.getM_vecIndexListFactor().set(indexPosition, apply);
				}else{
					Integer negatedDiffValue=diffValue*-1;
					Math_apply apply1 = new Math_apply();
					Math_plus plus = new Math_plus();
					Math_apply apply2 = new Math_apply();
					Math_minus minus = new Math_minus();
					Math_ci diffValueCi = new Math_ci(negatedDiffValue.toString());
				
					apply1.addFactor(plus);
						plus.addFactor(replaceIndexFactor);
						plus.addFactor(apply2);
							apply2.addFactor(minus);
								minus.addFactor(diffValueCi);
					ci.getM_vecIndexListFactor().set(indexPosition, apply1);
					
				}
			}
		}
	}

	/**
	 * Translate String to Integer  
	 * @param str
	 * @return Translated Integer
	 */
	private Integer decode(String str){
		return Integer.decode(str.replace(" ","").replace("(","").replace(")", "").replace("double",""));
	}

}
