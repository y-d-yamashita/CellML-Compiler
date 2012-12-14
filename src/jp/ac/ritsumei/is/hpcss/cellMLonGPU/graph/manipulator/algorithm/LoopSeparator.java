package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldVertexGroup;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldVertexGroupList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldVertex;

public class LoopSeparator {

	public FieldVertexGroupList separate(FieldGraph fg) {
		FieldVertexGroupList flg = new FieldVertexGroupList();
		List<FieldVertexGroup> timeLoop = separateTimeLoop(fg);
		flg.setTimeLoopGroupList(timeLoop);
		flg.setXAxisLoopGroupList(separateXAxisLoop(fg, timeLoop));
		return flg;
	}

	private List<FieldVertexGroup> separateTimeLoop(FieldGraph fg) {

		List<FieldVertexGroup> list = new ArrayList<FieldVertexGroup>();
		
		int maxIndex = fg.getMaxTimeIndex();
		int minIndex = fg.getMinTimeIndex();
		
		
		for(int curIndex=maxIndex;curIndex>=minIndex;curIndex--){
			int minIndependentIndex=curIndex;
			System.out.println(fg.getFieldVertexes(curIndex));
			for(FieldVertex srcV: fg.getFieldVertexes(curIndex)){
				int tmpMinIndex = getMinIndependentTimeIndex(srcV,curIndex,fg);

				if(tmpMinIndex < minIndependentIndex){
					minIndependentIndex=tmpMinIndex;
				}
			}
			
			System.out.println("minIndeIndex------------------------------"+minIndependentIndex);
			FieldVertexGroup group = new FieldVertexGroup();
			for(int i=curIndex;i>= minIndependentIndex;i--){
				group.addVertexList(fg.getFieldVertexes(i));
			}
			list.add(group);
		
		}
				
		Collections.reverse(list);
		return list;
	}

	
	
	
	private List<List<FieldVertexGroup>> separateXAxisLoop(FieldGraph fg,List<FieldVertexGroup> timeLoop) {

		List<List<FieldVertexGroup>> xLoopList = new ArrayList<List<FieldVertexGroup>>();
		
		for(FieldVertexGroup group:timeLoop){
			
			int timeIndex = group.getVertex(0).getTimeIndex();
			int maxIndex = fg.getMaxXAxisIndex(timeIndex);
			int minIndex = fg.getMinXAxisIndex(timeIndex);
		
			List<FieldVertexGroup> list= new ArrayList<FieldVertexGroup>();		
		for(int curIndex=maxIndex;curIndex>=minIndex;curIndex--){
			int minIndependentIndex=curIndex;
			
			System.out.println(this.getClass().getName()+" "+maxIndex+ " "+minIndex);
			for(FieldVertex srcV: fg.getFieldVertexes(timeIndex,curIndex)){
				int tmpMinIndex = getMinIndependentXAxisIndex(srcV,curIndex,fg,timeIndex);

				if(tmpMinIndex < minIndependentIndex){
					minIndependentIndex=tmpMinIndex;
				}
			}
			
			System.out.println("minIndeIndex------------------------------"+minIndependentIndex);
			FieldVertexGroup newGroup = new FieldVertexGroup();
			for(int i=curIndex;i>= minIndependentIndex;i--){
				newGroup.addVertexList(fg.getFieldVertexes(timeIndex,i));
			}
			list.add(newGroup);
		}
		Collections.reverse(list);
		
			xLoopList.add(list);
		}
		return xLoopList;
	}

	
	private int getMinIndependentTimeIndex(FieldVertex srcV, int cur, FieldGraph fg) {
		int minIndex=cur;
		
		for(FieldVertex destVertex:fg.getOutOfTimeIndexVertex(srcV)){
			System.out.println("Minindex>>>>>>>>>>>"+minIndex);
			System.out.println("SrcV:"+srcV.getTimeIndex()+" dest:"+destVertex.getTimeIndex()+" minIndex:"+minIndex);
			if(minIndex<destVertex.getTimeIndex()){
				minIndex = destVertex.getTimeIndex();
			}
		}
		return minIndex;
	}

	
	private int getMinIndependentXAxisIndex(FieldVertex srcV, int cur, FieldGraph fg,int timeIndex) {
		int minIndex=cur;
		
		for(FieldVertex destVertex:fg.getOutOfXAxisIndexVertex(srcV)){
			System.out.println("Minindex>>>>>>>>>>>"+minIndex);
			System.out.println("SrcV:"+srcV.getTimeIndex()+" dest:"+destVertex.getTimeIndex()+" minIndex:"+minIndex);
			if(minIndex<destVertex.getTimeIndex()){
				minIndex = destVertex.getTimeIndex();
			}
		}
		return minIndex;
	}

}
