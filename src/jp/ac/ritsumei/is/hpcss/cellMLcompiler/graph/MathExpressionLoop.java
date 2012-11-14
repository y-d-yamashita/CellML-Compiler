package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;


public class MathExpressionLoop{
	private Integer satrtLoopIndex;
	private Integer endLoopIndex;
	
	/*Selector追加要素*/
	private MathFactor indexFactor;
		
	private boolean isLoop;
	
	private List<MathExpression> mathExpressionList;
	private List<MathExpression> replacedIndexMathExpressionList;
	private List<MathExpressionLoop> mathExpressionLoopList;
	
	public MathExpressionLoop() {
		this.satrtLoopIndex=null;
		this.endLoopIndex=null;
		indexFactor=null;
		setLoop(false);
		mathExpressionList=new ArrayList<MathExpression>();
		mathExpressionLoopList=new ArrayList<MathExpressionLoop>();
		replacedIndexMathExpressionList=new ArrayList<MathExpression>();
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
		for(MathExpression expr1:this.mathExpressionList){
			findFlag=false;
			Integer firstCompareResult=null;
			for(MathExpression expr2:loop.getMathExpressionList()){
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

	public void merge(MathExpressionLoop loop,MathFactor replaceIndexFactor,int indexPosition) throws MathException {
		if(this.isLoop){
			this.endLoopIndex=loop.getEndLoopIndex();
		}else{
			this.indexFactor=replaceIndexFactor;
			cloneExpressionList(this.mathExpressionList,this.replacedIndexMathExpressionList);
			replaceIndexInExpressions(this.replacedIndexMathExpressionList,this.satrtLoopIndex,indexFactor,indexPosition);
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
		if(replacedIndexMathExpressionList!=null){
			
		}
		for(MathExpressionLoop loop:this.mathExpressionLoopList){
			loop.toString(stringBuilder,indet);
		}
		List<MathExpression> resultExpressionList=null;
		if(this.replacedIndexMathExpressionList.size()>0){
			resultExpressionList=replacedIndexMathExpressionList;
		}else{
			resultExpressionList=mathExpressionList;
		}
		for(MathExpression expression:resultExpressionList){
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
		stringBuilder.append(indent+"LOOP: start:"+satrtLoopIndex+" end:"+endLoopIndex+"\n");
		for(MathExpressionLoop loop:this.mathExpressionLoopList){
			loop.toString(stringBuilder,indent+indent);
		}
		List<MathExpression> resultExpressionList=null;
		if(this.replacedIndexMathExpressionList.size()>0){
			resultExpressionList=replacedIndexMathExpressionList;
		}else{
			resultExpressionList=mathExpressionList;
		}
		for(MathExpression expression:resultExpressionList){
			stringBuilder.append(indent+" Expr["+mathExpressionList.indexOf(expression)+"]:");
			try {
				stringBuilder.append(expression.toLegalString()+"\n");
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
		
	}
}
