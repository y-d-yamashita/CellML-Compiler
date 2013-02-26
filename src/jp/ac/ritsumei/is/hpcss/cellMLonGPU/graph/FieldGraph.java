package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldGraphIndexMap;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldVertex;

public class FieldGraph extends DirectedGraph<FieldVertex,FieldEdge> {
	
	private FieldGraphIndexMap indexMap;
	
	/* Minimum index cash */
	private Integer minTimeIndex;
	private Map<Integer,Integer> minXAxisIndex; //<timeIndex, xAxisIndex>
	private Integer minYAxisIndex;
	private Integer minZAxisIndex;
	
	/* Max index cash */
	private Integer maxTimeIndex;
	private Map<Integer,Integer> maxXAxisIndex; //<timeIndex,xAxisIndex>
	private Integer maxYAxisIndex;
	private Integer maxZAxisIndex;

	private boolean updateFlag;
	public FieldGraph() {
		minTimeIndex=null;
		minXAxisIndex=null;
		minYAxisIndex=null;
		minZAxisIndex=null;
		maxTimeIndex=null;
		maxXAxisIndex=null;
		maxYAxisIndex=null;
		maxZAxisIndex=null;
		
			
		indexMap = new FieldGraphIndexMap();	
		updateFlag=false;
	}
	
	 /**
	  * Add new vertex
	  * @param v
	  * @throws GraphException
	  */
	@Override
	public void addVertex(FieldVertex v) throws GraphException{
		super.addVertex(v);
		indexMap.add(v.getTimeIndex(),
				v.getXAxisIndex(),
				v.getYAxisIndex(),
				v.getZAxisIndex(),
				v);
		updateFlag=true;
	}
	
	/**
	 * Remove a vertex from graph
	 * @param v
	 */
	@Override
	public void removeVertex(FieldVertex v) {
		super.removeVertex(v);
		indexMap.remove(v.getTimeIndex(),
				v.getXAxisIndex(),
				v.getYAxisIndex(),
				v.getZAxisIndex());
		updateFlag=true;
	}
	
	
	
	
	private void updateGraphInformation() {
		if(updateFlag==true){
			updateMinMaxTimeIndexes();
			updateMinMaxXAxisIndexes();
			updateFlag=false;
		}
	}

	
	
	private void updateMinMaxTimeIndexes() {
			Set<Integer> timeIndexes = indexMap.getTimeIndexes();
			minTimeIndex=Collections.min(timeIndexes);
			maxTimeIndex=Collections.max(timeIndexes);
	}

	private void updateMinMaxXAxisIndexes() {
		if(minXAxisIndex==null){
			minXAxisIndex = new TreeMap<Integer, Integer>();
		}
		if(maxXAxisIndex==null){
			maxXAxisIndex=new TreeMap<Integer, Integer>();
		}
		for(Integer timeIndex:indexMap.getTimeIndexes()){
			minXAxisIndex.put(timeIndex, Collections.min(indexMap.getXAxisIndexes(timeIndex)));
			maxXAxisIndex.put(timeIndex, Collections.max(indexMap.getXAxisIndexes(timeIndex)));
		}
	}

	public Integer getMaxTimeIndex(){
		updateGraphInformation();		
		return maxTimeIndex;
	}

	public Integer getMinTimeIndex(){
		updateGraphInformation();
		return minTimeIndex;
	}
	
	public Integer getMaxXAxisIndex(int timeIndex){
		updateGraphInformation();		
		return maxXAxisIndex.get(timeIndex);
	}

	public Integer getMinXAxisIndex(int timeIndex){
		updateGraphInformation();
		return minXAxisIndex.get(timeIndex);
	}

	public Integer getMaxYAxisIndex(){
		updateGraphInformation();		
		return maxYAxisIndex;
	}

	public Integer getMinYAxisIndex(){
		updateGraphInformation();
		return minYAxisIndex;
	}
	
	public Integer getMaxZAxisIndex(){
		updateGraphInformation();		
		return maxZAxisIndex;
	}

	public Integer getMinZAxisIndex(){
		updateGraphInformation();
		return minZAxisIndex;
	}

	public List<FieldVertex> getFieldVertexes(int timeIndex){
		return indexMap.getVertexList(timeIndex);
	}

	public List<FieldVertex> getFieldVertexes(int timeIndex, int xAxisIndex){
		return indexMap.getVertexList(timeIndex, xAxisIndex);
	}

	
	public Set<FieldVertex> getOutOfTimeIndexVertex(FieldVertex v){
		Set<FieldVertex> outOfVertexes=new TreeSet<FieldVertex>();
		for(FieldEdge e:getOutEdges(v)){
			FieldVertex destVertex = getDestVertex(e);
			if(!destVertex.getTimeIndex().equals(v.getTimeIndex())){
				outOfVertexes.add(destVertex);
			}
		}
		return outOfVertexes;
	}
	
	
	
	public Set<FieldVertex> getOutOfXAxisIndexVertex(FieldVertex v){
		Set<FieldVertex> outOfVertexes=new TreeSet<FieldVertex>();
		for(FieldEdge e:getOutEdges(v)){
			FieldVertex destVertex = getDestVertex(e);
			if(destVertex.getTimeIndex().equals(v.getTimeIndex())){
				if(!destVertex.getXAxisIndex().equals(v.getXAxisIndex())){
						outOfVertexes.add(destVertex);
				}
			}
		}
		return outOfVertexes;
	}

}
