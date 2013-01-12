package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.AttachVariableNameAndIndexVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.ReplaceEqualToAssignVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.ReplacePartOfVariableNameVisitor;


public class MathExpressionLoop{
	private Integer startLoopIndex;
	private Integer endLoopIndex;
	
	/*Selector追加要素*/
	private MathFactor indexFactor;
		
	private boolean isLoop;
	
	private List<MathExpression> mathExpressionList;
	private List<MathExpression> baseMathExpressionList;
	private List<MathExpressionLoop> mathExpressionLoopList;
	
	private int removeIndexPosition = 0;
	private AttachVariableNameAndIndexVisitor attachVisitor = new AttachVariableNameAndIndexVisitor(removeIndexPosition);
	private ReplacePartOfVariableNameVisitor replaceNameVisitor = new ReplacePartOfVariableNameVisitor("\\.", "_");
	private ReplaceEqualToAssignVisitor replaceEqualVisitor = new ReplaceEqualToAssignVisitor();
	
	public MathExpressionLoop() {
		this.startLoopIndex=null;
		this.endLoopIndex=null;
		indexFactor=null;
		setLoop(false);
		mathExpressionList=new ArrayList<MathExpression>();
		mathExpressionLoopList=new ArrayList<MathExpressionLoop>();
		baseMathExpressionList=new ArrayList<MathExpression>();
	}

	public Integer getSatrtLoopIndex() {
		return startLoopIndex;
	}

	public void setSatrtLoopIndex(Integer satrtLoopIndex) {
		this.startLoopIndex = satrtLoopIndex;
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
	public void addMathExpressionList(List<MathExpression> mathExpressionList){
		this.mathExpressionList.addAll(mathExpressionList);
	}
	public void addMathExpression(MathExpression mathExpression){
		this.mathExpressionList.add(mathExpression);
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
			if(this.startLoopIndex!=null){
			replaceIndexInExpressions(this.getAllExpressions(),this.startLoopIndex,indexFactor,indexPosition);
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
			expression.traverse(attachVisitor,replaceNameVisitor,replaceEqualVisitor);
			
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
		if(indexFactor==null||startLoopIndex==endLoopIndex){
			stringBuilder.append(indent+"//----------------------------  NO LOOP: start:"+startLoopIndex+" end:"+endLoopIndex+" ----------------------------//\n");
		}else{
			try {
				stringBuilder.append(indent+"//---------------------------- LOOP ----------------------------//\n");
				stringBuilder.append(indent+"for("+indexFactor.toLegalString()+"="+startLoopIndex+"; "+indexFactor.toLegalString()+"<="+endLoopIndex+"; "+indexFactor.toLegalString()+"++){\n");
			} catch (MathException e1) {
				e1.printStackTrace();
			}
		}
		for(MathExpressionLoop loop:this.mathExpressionLoopList){
			loop.toString(stringBuilder,indent+indent);
		}
		List<MathExpression> resultExpressionList=null;
		for(MathExpression expression:mathExpressionList){
			expression.traverse(attachVisitor,replaceNameVisitor,replaceEqualVisitor);
			//stringBuilder.append(indent+" /* Expr["+mathExpressionList.indexOf(expression)+"] */  ");
			stringBuilder.append("		");
			try {
				stringBuilder.append(expression.toLegalString()+";\n");
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
		if(indexFactor==null||startLoopIndex==endLoopIndex){
			stringBuilder.append(indent+"//------------------------------- END -------------------------------//\n");
		}else{
			stringBuilder.append(indent+"}\n");
			stringBuilder.append(indent+"//----------------------------- LOOP END -----------------------------//\n");
		}
		stringBuilder.append("\n");
	}
}
