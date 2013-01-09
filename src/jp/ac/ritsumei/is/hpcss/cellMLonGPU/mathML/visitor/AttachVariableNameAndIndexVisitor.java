package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

public class AttachVariableNameAndIndexVisitor implements Visitor {
	private int indexPosition;
	public AttachVariableNameAndIndexVisitor(int pos){
		indexPosition=pos;
	}
	
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			Vector<MathFactor> indexList = ci.getIndexList();
			if(indexList.size() > indexPosition){
				String indexString = "";
				try {
					 indexString = indexList.get(indexPosition).toLegalString();
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String attachString =indexString
						.replace("(","")
						.replace(")", "")
						.replace("+", "")
						.replace(" ", "");
				ci.setM_strPresentText(ci.getM_strPresentText()+"__"+attachString);
				indexList.remove(indexPosition);
			}
		}
		
	}

}
