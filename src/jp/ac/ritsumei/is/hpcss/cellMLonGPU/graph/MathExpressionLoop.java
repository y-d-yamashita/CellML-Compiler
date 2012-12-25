package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;


public class MathExpressionLoop{
	private Integer satrtLoopIndex;
	private Integer endLoopIndex;
	
	/*Selector追加要素*/
	private MathFactor indexFactor;
		
	private boolean isLoop;
	
	private List<MathExpression> mathExpressionList;
	private List<MathExpression> baseMathExpressionList;
	private List<MathExpressionLoop> mathExpressionLoopList;
	
	public MathExpressionLoop() {
		this.satrtLoopIndex=null;
		this.endLoopIndex=null;
		indexFactor=null;
		setLoop(false);
		mathExpressionList=new ArrayList<MathExpression>();
		mathExpressionLoopList=new ArrayList<MathExpressionLoop>();
		baseMathExpressionList=new ArrayList<MathExpression>();
	}

	public Integer getSatrtLoopIndex() {
		return satrtLoopIndex;
	}

	public void setSatrtLoopIndex(Integer satrtLoopIndex) {
		this.satrtLoopIndex = satrtLoopIndex;
	}

	public Integer getEndLoopIndex() {
		return endLoopIndex;
	}

	public void setEndLoopIndex(Integer endLoopIndex) {
		this.endLoopIndex = endLoopIndex;
	}

	public MathFactor getIndexFactor() {
		return indexFactor;
	}

	public void setIndexFactor(MathFactor indexFactor) {
		this.indexFactor = indexFactor;
	}

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public List<MathExpression> getMathExpressionList() {
		return mathExpressionList;
	}

	public void setMathExpressionList(List<MathExpression> mathExpressionList) {
		this.mathExpressionList = mathExpressionList;
	}

	public List<MathExpressionLoop> getMathExpressionLoopList() {
		return mathExpressionLoopList;
	}

	public void setMathExpressionLoopList(List<MathExpressionLoop> mathExpressionLoopList) {
		this.mathExpressionLoopList = mathExpressionLoopList;
	}
	
	public void addLoop(MathExpressionLoop loop){
		this.mathExpressionLoopList.add(loop);
	}

	public MathExpressionLoop getLoop(int index) {
		return this.mathExpressionLoopList.get(index);
	}

	public boolean isPossibleMerge(MathExpressionLoop loop,int indexPosition) throws MathException {
		boolean findFlag=false;
		if(baseMathExpressionList.isEmpty()){
			cloneExpressionList(this.getAllExpressions(),this.baseMathExpressionList);
		}
		List<MathExpression> expr2List = loop.getAllExpressions();
		for(MathExpression expr1:baseMathExpressionList){
			findFlag=false;
			Integer firstCompareResult=null;
			for(MathExpression expr2:expr2List){
				Integer compareResult = expr1.compareFocusOnVariableIndex(expr2, indexPosition);
				if(firstCompareResult==null){
						firstCompareResult = expr1.compareFocusOnVariableIndex(expr2, indexPosition);	
				}
				if((compareResult!=null)&&(compareResult.equals(firstCompareResult))){ // Index(a[3]) - Index(a[2]) == 1
					findFlag=true;
					break;
				}
			}
			if(!findFlag) return false;
		}
		return true;
	}

	private List<MathExpression> getAllExpressions() {
		List<MathExpression> list = new ArrayList<MathExpression>();
		list.addAll(this.getMathExpressionList());
		for(MathExpressionLoop child:this.getMathExpressionLoopList()){
			child.getAllExpressions(list);
		}
		return list;
	}

	private void getAllExpressions(List<MathExpression> list) {
		list.addAll(this.getMathExpressionList());
		for(MathExpressionLoop child:this.getMathExpressionLoopList()){
			child.getAllExpressions(list);
		}
	}
	public void merge(MathExpressionLoop loop,MathFactor replaceIndexFactor,int indexPosition) throws MathException {
		if(this.isLoop){
			this.endLoopIndex=loop.getEndLoopIndex();
		}else{
			this.indexFactor=replaceIndexFactor;
//			cloneExpressionList(this.getAllExpressions(),this.baseMathExpressionList);
//			cloneExpressionList(this.mathExpressionList,this.replacedIndexMathExpressionList);
			//System.out.println(this+","+this.getAllExpressions()+","+this.satrtLoopIndex+","+indexFactor+","+indexPosition);
			if(this.satrtLoopIndex!=null){
			replaceIndexInExpressions(this.getAllExpressions(),this.satrtLoopIndex,indexFactor,indexPosition);
			}
			//replaceIndexInExpressions(this.getAllExpressions(),this.satrtLoopIndex,indexFactor,indexPosition);
			this.endLoopIndex=loop.getEndLoopIndex();
			this.isLoop=true;
			
		}
	}
	
	
	private void replaceIndexInExpressions(
			List<MathExpression> expressionList,
			int baseIndex,
			MathFactor replaceIndexFactor,
			int indexPosition) {
			for(MathExpression expr:expressionList){
				expr.replaceIndex(replaceIndexFactor,baseIndex,indexPosition);
			}
	}

	private void cloneExpressionList(List<MathExpression> expressionList1,
			List<MathExpression> expressionList2) throws MathException {
		for(MathExpression expr:expressionList1){
			expressionList2.add(expr.createCopy());
		}
		
	}

	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
		String indet ="	";	
		stringBuilder.append("<"+this.getClass()+">--------------\n");
		
		stringBuilder.append("ROOT:\n");
		
		for(MathExpressionLoop loop:this.mathExpressionLoopList){
			loop.toString(stringBuilder,indet);
		}
		for(MathExpression expression:mathExpressionList){
			stringBuilder.append("Expr["+mathExpressionList.indexOf(expression)+"]:");
			try {
				stringBuilder.append(expression.toLegalString()+"\n");
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
		return stringBuilder.toString();
	}
	
	private void toString(StringBuilder stringBuilder,String indent){
		if(indexFactor==null){
			stringBuilder.append(indent+"NO LOOP: start:"+satrtLoopIndex+" end:"+endLoopIndex+"\n");
		}else{
			try {
				stringBuilder.append(indent+"LOOP: "+indexFactor.toLegalString()+"="+satrtLoopIndex+", "+indexFactor.toLegalString()+"<="+endLoopIndex+", "+indexFactor.toLegalString()+"++)\n");
			} catch (MathException e1) {
				e1.printStackTrace();
			}
		}
		for(MathExpressionLoop loop:this.mathExpressionLoopList){
			loop.toString(stringBuilder,indent+indent);
		}
		List<MathExpression> resultExpressionList=null;
		for(MathExpression expression:mathExpressionList){
			stringBuilder.append(indent+" Expr["+mathExpressionList.indexOf(expression)+"]:");
			try {
				stringBuilder.append(expression.toLegalString()+"\n");
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
		
		stringBuilder.append("\n");
	}
}
