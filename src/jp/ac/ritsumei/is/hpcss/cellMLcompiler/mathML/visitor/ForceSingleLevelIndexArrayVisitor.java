package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_times;

public class ForceSingleLevelIndexArrayVisitor implements Visitor {

	private List<String> strIndexMaxSizeList;
	
	public ForceSingleLevelIndexArrayVisitor(){
		strIndexMaxSizeList=null;
	}
	public ForceSingleLevelIndexArrayVisitor(List<String> indexMaxSizeList){
		this.strIndexMaxSizeList=indexMaxSizeList;
	}
	public void setIndexMaxSizeList(List<String> indexMaxSizeList) {
		this.strIndexMaxSizeList = indexMaxSizeList;
	}

	
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			if(ci.getM_vecIndexListFactor().size()>1){  //Already single level array
				List<MathFactor> indexFactorList = ci.getM_vecIndexListFactor();
				Vector<MathFactor> singleLevelIndex = new Vector<MathFactor>();
			Math_apply topApply = new Math_apply();
			Math_plus  plus = new Math_plus();
			singleLevelIndex.add(topApply);
			topApply.addFactor(plus);
			
			// Create "index * INDEX_MAXSIZE" expression
			for(int i = 0;i < indexFactorList.size()-1;i++){ 
				Math_apply secondApply = new Math_apply();
				Math_times times = new Math_times();
				secondApply.addFactor(times);
				times.addFactor(indexFactorList.get(i));
				for(int j=indexFactorList.size()-1;j>i;j--){
				times.addFactor(new Math_ci(strIndexMaxSizeList.get(j)));
				}
				plus.addFactor(secondApply);
				}
				plus.addFactor(indexFactorList.get(indexFactorList.size()-1));
			ci.setM_vecIndexListFactor(singleLevelIndex);
			}
		}
		
	}

}
