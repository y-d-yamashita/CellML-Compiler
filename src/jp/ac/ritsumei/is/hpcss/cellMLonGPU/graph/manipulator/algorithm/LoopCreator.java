package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm;

import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldVertexGroup;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldVertexGroupList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.MathExpressionLoop;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

/**
 * 依存関係の無いグループからループグループを形成する
 * @author y-yamashita
 *
 */
public class LoopCreator {

	//private final int timeIndexPosition=0;
	private final int xAxisIndexPosition=1;
	//private final int yAxisIndexPosition=2;
	//private final int zAxisIndexPosition=3;
	
	//private final Math_ci timeIndexVariable=new Math_ci("i");
	private final Math_ci xAxisIndexVariable=new Math_ci("j");
	
	
	public LoopCreator(){
	}
	
	public MathExpressionLoop create(FieldVertexGroupList groupList) throws MathException{
		MathExpressionLoop loopList=expand(groupList);
		loopList=merge(loopList,groupList);
		
		
		return loopList;
	}

	private MathExpressionLoop expand(FieldVertexGroupList groupList) {
		MathExpressionLoop loopRoot=new MathExpressionLoop();
		if(groupList.getXAxisLoopGroupList()!=null){
			for(List<FieldVertexGroup> timeLevel:groupList.getXAxisLoopGroupList()){
				MathExpressionLoop timeLoop = new MathExpressionLoop();
				timeLoop.setSatrtLoopIndex(groupList.getXAxisLoopGroupList().indexOf(timeLevel));
				timeLoop.setEndLoopIndex(groupList.getXAxisLoopGroupList().indexOf(timeLevel));
				loopRoot.addLoop(timeLoop);
				for(FieldVertexGroup xAxisLevel:timeLevel){
					MathExpressionLoop xAxisLoop = new MathExpressionLoop();
					timeLoop.addLoop(xAxisLoop);
					xAxisLoop.setMathExpressionList(xAxisLevel.getVertex(0).getExpressionList());
					xAxisLoop.setSatrtLoopIndex(xAxisLevel.getVertex(0).getXAxisIndex());
					xAxisLoop.setEndLoopIndex(xAxisLevel.getVertex(0).getXAxisIndex());
				}
			}
			
		}else if(groupList.getTimeLoopGroupList()!=null){
			
		}
		return loopRoot;
	}

	
	private MathExpressionLoop merge(MathExpressionLoop loopList,FieldVertexGroupList groupList) throws MathException{
		MathExpressionLoop loopRoot=loopList;
		
		//Xループ化
		if(groupList.getXAxisLoopGroupList()!=null){
			for(MathExpressionLoop timeLoop:loopList.getMathExpressionLoopList()){
					for(int i=1;i<timeLoop.getMathExpressionLoopList().size();i++){
						if(timeLoop.getLoop(i-1).isPossibleMerge(timeLoop.getLoop(i),xAxisIndexPosition)){
							timeLoop.getLoop(i-1).merge(timeLoop.getLoop(i),xAxisIndexVariable,xAxisIndexPosition);
							timeLoop.getMathExpressionLoopList().remove(i);
							i--;							
					}
				}
			}
		}
		
		//Timeループ化
		if(groupList.getTimeLoopGroupList()!=null){
			
		}
		return loopRoot;
	}
	
	/*
	private MathExpressionLoop creteTimeLoopGroup(
			FieldVertexGroupList groupList) {
			MathExpressionLoop mathExpressionLoop=new MathExpressionLoop();
			MathExpressionLoop curLoop=new MathExpressionLoop();
			if(groupList.getTimeLoopGroupList().size()<2){
				for(List<FieldVertexGroup> fvgList:groupList.getXAxisLoopGroupList()){
					for(FieldVertexGroup fvg:fvgList){
						for(FieldVertex v:fvg.getVertexGroup()){
						mathExpressionLoop.setMathExpressionList(v.getExpressionList());
						}
					}
				}
			}else{
					for(FieldVertexGroup fvg:groupList.getXAxisLoopGroupList().get(0)){
						for(FieldVertex v:fvg.getVertexGroup()){
						curLoop.setMathExpressionList(v.getExpressionList());
						curLoop.setSatrtLoopIndex(0);
						curLoop.setEndLoopIndex(0);
						}
					}
				
				for(int i=0;i<groupList.getXAxisLoopGroupList().size()-1;i++){
					if(curLoop.isLoopable(groupList.getXAxisLoopGroupList().get(i+1))){
						curLoop.merge(i+1,timeIndexVariable,timeIndexPosition);
					}else{
						mathExpressionLoop.addLoop(curLoop);
						for(FieldVertexGroup fvg:groupList.getXAxisLoopGroupList().get(i+1)){
							for(FieldVertex v:fvg.getVertexGroup()){
							curLoop.setMathExpressionList(v.getExpressionList());
							curLoop.setSatrtLoopIndex(i+1);
							curLoop.setEndLoopIndex(i+1);
							}
						}

					}
				}
			}
		return mathExpressionLoop;
	}*/
}
