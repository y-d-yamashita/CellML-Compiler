package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_selector;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.AttachVariableNameAndIndexVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.ReplaceEqualToAssignVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.ReplacePartOfVariableNameVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;


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
	
	private boolean dummyExpressionFlag;
	private List<Integer> dummpyExpressionNumList;
	
	private Integer idOfLoopHasDummy;
	private Integer shortestCalOrderNum;
	
	public MathExpressionLoop() {
		this.startLoopIndex=null;
		this.endLoopIndex=null;
		indexFactor=null;
		setLoop(false);
		mathExpressionList=new ArrayList<MathExpression>();
		mathExpressionLoopList=new ArrayList<MathExpressionLoop>();
		baseMathExpressionList=new ArrayList<MathExpression>();
		setDummyExpressionFlag(false);
		shortestCalOrderNum=null;
		
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

	public void addSimpleDumpyExpr(int nextLoopStartIndex) throws MathException{
		if(isDummyExpressionFlag() == false){ //Not setted dummy expression
			setDummpyExpressionNumList(new ArrayList<Integer>());
			List<MathExpression> newExpressionList = new ArrayList<MathExpression>();
		for(MathExpression expr : this.getAllExpressions()){
			MathExpression lhs = expr.getLeftExpression();
			MathExpression lhs_copy = lhs.createCopy();
			MathExpression rhs = expr.getRightExpression();
			Math_apply rootFactor =  new Math_apply();
			Math_eq eq = new Math_eq();
			rootFactor.addFactor(eq);
			eq.addFactor(lhs.getRootFactor());
			Math_apply applyOfPlus = new Math_apply();
			Math_plus plus = new Math_plus();
			eq.addFactor(applyOfPlus);
			applyOfPlus.addFactor(plus);
			
			//Create an expression with dummy
			//ex. x=y ->> x=y*f[0] + x*f[1]
			
			//Flag variable creation
			//__flag(id)[n][i-startLoopIndex]
			Math_ci d_ci1 = new Math_ci("__flag"+this.idOfLoopHasDummy);
			d_ci1.addIndexList(new Math_ci("n"));
				//Create i - startLoopIndex
			Math_apply applyOfIndex1 = new Math_apply();
			Math_minus minusOfIndex1 = new Math_minus();
			Math_ci ci1_index1 = new Math_ci("i");
			Math_cn offsetOfIndex1 = new Math_cn(startLoopIndex.toString());
			offsetOfIndex1.autoChangeType();
			applyOfIndex1.addFactor(minusOfIndex1);
			minusOfIndex1.addFactor(ci1_index1);
			minusOfIndex1.addFactor(offsetOfIndex1);
			d_ci1.addIndexList(applyOfIndex1);
			
			//(1 - __flag(id)[n][i-startLoopIndex])
			Math_ci d_ci2 = new Math_ci("__flag"+this.idOfLoopHasDummy);
			d_ci2.addIndexList(new Math_ci("n"));
				//Create i - startLoopIndex
			Math_apply applyOfIndex2 = new Math_apply();
			Math_minus minusOfIndex2 = new Math_minus();
			Math_ci ci2_index2 = new Math_ci("i");
			Math_cn offsetOfIndex2 = new Math_cn(startLoopIndex.toString());
			offsetOfIndex2.autoChangeType();
			applyOfIndex2.addFactor(minusOfIndex2);
			minusOfIndex2.addFactor(ci2_index2);
			minusOfIndex2.addFactor(offsetOfIndex2);
			d_ci2.addIndexList(applyOfIndex2);
				//Create 1 - __flag(id)[n][i-startLoopIndex
			Math_apply d_negate_apply = new Math_apply();
			Math_minus d_minus = new Math_minus();
			d_negate_apply.addFactor(d_minus);
			Math_cn one = new Math_cn("1");
			one.autoChangeType();
			d_minus.addFactor(one);
			d_minus.addFactor(d_ci2);
			
			
			//f(x)* __flag
			Math_apply apply1 = new Math_apply();
			Math_times times1 = new Math_times();
			apply1.addFactor(times1);
			times1.addFactor(rhs.getRootFactor());
			times1.addFactor(d_ci1);
			
			//Add dummy expr
			//x * (1 - __flag)
			Math_apply apply2 = new Math_apply();
			Math_times times2 = new Math_times();
			apply2.addFactor(times2);
			times2.addFactor(lhs_copy.getRootFactor());
			times2.addFactor(d_negate_apply);
			
			plus.addFactor(apply1);
			plus.addFactor(apply2);
			
			newExpressionList.add(new MathExpression(rootFactor));
		}
			this.setMathExpressionList(newExpressionList);
			for(MathExpression expr:this.getMathExpressionList()){
				try {
					CCLogger.log(expr.toLegalString());
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			setDummyExpressionFlag(true);
		}
		for(int i=this.endLoopIndex+1; i<nextLoopStartIndex;i++){
			getDummpyExpressionNumList().add(i);
		}

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
		stringBuilder.append(indent+"//Shortest Calculation Order:"+this.getShortestCalOrderNum()+"\n");
		
		if(isDummyExpressionFlag()){
			stringBuilder.append(indent+"/*** Zero flag *****\n");
			for(Integer f: this.getDummpyExpressionNumList()){
					stringBuilder.append(indent+"__flag"+this.idOfLoopHasDummy+"__n["+f+"-"+this.startLoopIndex+"] = 0; \n ");
			}
			stringBuilder.append(indent+" *******************/\n");
		}

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

	public Integer getIDofLoopHasDummy() {
		return idOfLoopHasDummy;
	}

	public void setIDofLoopHasDummy(Integer loopID) {
		this.idOfLoopHasDummy = loopID;
	}

	public Integer getShortestCalOrderNum() {
		return shortestCalOrderNum;
	}

	public void setShortestCalOrderNum(Integer shortestCalOrderNum) {
		this.shortestCalOrderNum = shortestCalOrderNum;
	}

	public boolean isDummyExpressionFlag() {
		return dummyExpressionFlag;
	}

	public void setDummyExpressionFlag(boolean dummyExpressionFlag) {
		this.dummyExpressionFlag = dummyExpressionFlag;
	}

	public List<Integer> getDummpyExpressionNumList() {
		return dummpyExpressionNumList;
	}

	public void setDummpyExpressionNumList(List<Integer> dummpyExpressionNumList) {
		this.dummpyExpressionNumList = dummpyExpressionNumList;
	}
}
