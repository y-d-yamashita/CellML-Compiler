package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm.comparator;

import java.util.Comparator;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;

public class SortVertexDistanceComparator implements Comparator<RecMLVertex> {

	private RecMLVertex goal;
	private BipartiteGraph<RecMLVertex, RecMLEdge> graph;
	public SortVertexDistanceComparator(
			BipartiteGraph<RecMLVertex, RecMLEdge>graph
			,RecMLVertex goal){
		this.graph = graph;
		this.goal = goal;
	}
	
	/**
	 * if o equals goal, the distance is 0.
	 * if next vertex of o is goal, the distance is 1.
	 * In other case is distance is 2 (default).
	 */
	@Override
	public int compare(RecMLVertex o1, RecMLVertex o2) {
		int o1_distance = 2;
		int o2_distance = 2;
		
		//Get distance of o1
		if(o1 == goal){
			o1_distance = 0;
		}else{
			for(RecMLVertex next:graph.getOutOfVertex(o1)){
				if(next == goal){
					o1_distance = 1;
					break;
				}
			}
		}
		
		//Get distance of o2
		if(o2 == goal){
			o2_distance = 0;
		}else{
			for(RecMLVertex next:graph.getOutOfVertex(o2)){
				if(next == goal){
					o2_distance = 1;
					break;
				}
			}
		}

		
		if(o1_distance == o2_distance){
			return 0;
		}else if(o1_distance < o2_distance){
			return -1;
		}else{
			return 1;
		}
	}

}
