package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class FieldGraphIndexMap{
	private Map<Integer,Map<Integer,Map<Integer,Map<Integer,FieldVertex>>>> indexMap;
	public FieldGraphIndexMap(){
		indexMap = new HashMap<Integer, Map<Integer,Map<Integer,Map<Integer,FieldVertex>>>>();
	}
	
	public void add(
			Integer timeIndex,
			Integer xAxisIndex,
			Integer yAxisIndex,
			Integer zAxisIndex,
			FieldVertex vertex){
		
		Map<Integer,Map<Integer, Map<Integer,FieldVertex>>> xAxisIndexMap=null;
		if(indexMap.containsKey(timeIndex)){
			xAxisIndexMap=indexMap.get(timeIndex);
		}else{
			xAxisIndexMap = new HashMap<Integer,Map<Integer, Map<Integer,FieldVertex>>>();
			indexMap.put(timeIndex, xAxisIndexMap);
		}

		Map<Integer, Map<Integer,FieldVertex>> yAxisIndexMap=null;
		if(xAxisIndexMap.containsKey(xAxisIndex)){
			yAxisIndexMap=xAxisIndexMap.get(xAxisIndex);
		}else{
			yAxisIndexMap = new HashMap<Integer, Map<Integer,FieldVertex>>();
			xAxisIndexMap.put(xAxisIndex, yAxisIndexMap);
		}
	
		System.out.println(timeIndex);
		System.out.println(xAxisIndex);
		System.out.println(yAxisIndex);
		Map<Integer,FieldVertex> zAxisIndexMap=null;
		if(yAxisIndexMap.containsKey(yAxisIndex)){
			zAxisIndexMap=yAxisIndexMap.get(yAxisIndex);
		}else{
			zAxisIndexMap = new HashMap<Integer, FieldVertex>();
			yAxisIndexMap.put(yAxisIndex, zAxisIndexMap);
		}
		
		zAxisIndexMap.put(zAxisIndex, vertex);
		}
	
	public void remove(
			Integer timeIndex,
			Integer xAxisIndex,
			Integer yAxisIndex,
			Integer zAxisIndex){
		indexMap.get(timeIndex).get(xAxisIndex).get(yAxisIndex).remove(zAxisIndex);
	}
		
	public FieldVertex get(
			Integer timeIndex,
			Integer xAxisIndex,
			Integer yAxisIndex,
			Integer zAxisIndex){
		return indexMap.get(timeIndex).get(xAxisIndex).get(yAxisIndex).get(zAxisIndex);
	}
	
	
	public List<FieldVertex> getVertexList(Integer timeIndex){
		List<FieldVertex> list = new ArrayList<FieldVertex>();
		Map<Integer,Map<Integer, Map<Integer,FieldVertex>>> xAxisIndexMap = indexMap.get(timeIndex);
		System.out.println(this.getClass().getName()+xAxisIndexMap+ " index"+timeIndex);
		for(Map<Integer,Map<Integer,FieldVertex>> yAxisMap:xAxisIndexMap.values()){
			for(Map<Integer,FieldVertex> zAxisMap:yAxisMap.values()){
				list.addAll(zAxisMap.values());
			}
		
		}
		return list;
	}
	
	
	public List<FieldVertex> getVertexList(Integer timeIndex,int xAxisIndex){
/*
		List<FieldVertex> list = new ArrayList<FieldVertex>();
		Map<Integer,Map<Integer, Map<Integer,FieldVertex>>> xAxisIndexMap = indexMap.get(timeIndex);
		Map<Integer,Map<Integer,FieldVertex>> yAxisIndexMap=xAxisIndexMap.get(xAxisIndexMap);

			System.out.println(getClass().getName()+": "+timeIndex+ " "+xAxisIndex+" "+indexMap.get(timeIndex).get(xAxisIndex));
		for(Map<Integer,FieldVertex> zAxisMap:indexMap.get(timeIndex).get(xAxisIndexMap).values()){
				list.addAll(zAxisMap.values());
			}
	
		
		return list;

	*/	
		List<FieldVertex> list = new ArrayList<FieldVertex>();
		Map<Integer, Map<Integer,FieldVertex>> yAxisIndexMap = indexMap.get(timeIndex).get(xAxisIndex);
		if(yAxisIndexMap!=null){
			for(Map<Integer,FieldVertex> zAxisMap:yAxisIndexMap.values()){
				list.addAll(zAxisMap.values());
			}
		}
		return list;

	}
	
	
	public Set<Integer> getTimeIndexes(){
		return indexMap.keySet();
	}
	
	public Set<Integer> getXAxisIndexes(int timeIndex){
		return indexMap.get(timeIndex).keySet();
	}
	

}
