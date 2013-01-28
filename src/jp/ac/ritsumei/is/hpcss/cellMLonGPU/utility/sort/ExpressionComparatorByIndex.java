package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.MathCollections;

/**
 * Comparator for sorting expressions obey the expressions indexes and patterns
 * @author y-yamashita
 *
 */
public class ExpressionComparatorByIndex implements Comparator<MathExpression> {
	private int ASC =1;
	private int Desc = -1;
	private int sort = ASC;
	
	private int indexPosition =0;
	private int listSize=-1;
	
	private List<MathExpression> exprTypeList;
	
	
	public ExpressionComparatorByIndex(int pos,int listSize){
		indexPosition=pos;
		exprTypeList=new ArrayList<MathExpression>();
		this.listSize=listSize;
	}
	
	@Override
	public int compare(MathExpression o1, MathExpression o2) {
		MathExpression expr1=(MathExpression) o1;
		MathExpression expr2=(MathExpression) o2;
		
		MathExpression lhs1=expr1.getLeftExpression();
		MathExpression lhs2=expr2.getLeftExpression();
		
		Math_ci var1 = lhs1.getFirstVariable();
		Math_ci var2 = lhs2.getFirstVariable();
		
		
		
		int index1 = -listSize;
		int index2 = -listSize;
		
		if(var1.getIndexList().size() > indexPosition){
			//Calculate index ex. (1+2) -> Math_cn(3)
			Math_cn index1_cn = MathCollections.calculate(var1.getIndexList().get(indexPosition));  
			//Decode to Integer
			index1 = index1_cn.decode();
		}
		if(var2.getIndexList().size() > indexPosition){	
			//Calculate index ex. (1+2) -> Math_cn(3)
			Math_cn index2_cn = MathCollections.calculate(var2.getIndexList().get(indexPosition)); 
			//Decode to Integer
			index2 = index2_cn.decode();
		}
		//exprPattern has expression pattern number. 
		//In case of success then exprPattern >0, failed -1
		int expr1Pattern=-1;
		int expr2Pattern=-1;
		
		for(MathExpression exprType:this.exprTypeList){
			//If found registered pattern
			if(exprType.isSamePattern(expr1)){
				//Set pattern number
				expr1Pattern=listSize*exprTypeList.indexOf(exprType);
				break;
			}
		}
		//Not found a pattern
		if(expr1Pattern==-1){
			//Set pattern number
			expr1Pattern=listSize*exprTypeList.size();
			
			//Register new pattern
			exprTypeList.add(expr1);
		}
		
		
		for(MathExpression exprType:this.exprTypeList){
			//If found a registered pattern
			if(exprType.isSamePattern(expr2)){
				//Set pattern number
				expr2Pattern=listSize*exprTypeList.indexOf(exprType);
				break;
			}
		}
		//Not found a pattern
		if(expr2Pattern==-1){
			//Set pattern number
			expr2Pattern=listSize*exprTypeList.size();

			//Register new pattern
			exprTypeList.add(expr2);
		}
		
		//Add expression pattern number to index number
		index1 += expr1Pattern;
		index2 += expr2Pattern;
		
		//return compare result
		if(index1 == index2){
			return 0;
		}else if(index1 < index2){
			return -1 * sort;
		}else{
			return 1 * sort;
		}
	}

	
}
