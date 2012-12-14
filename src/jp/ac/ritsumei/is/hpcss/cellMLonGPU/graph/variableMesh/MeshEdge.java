package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.variableMesh;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.Edge;

public class MeshEdge extends Edge {

	/* initial vertex or node */
	private MeshCoordinates tail;
	public MeshCoordinates getEdgeTail() {
		return tail;
	}
	
	/* terminal vertex or node */
	private MeshCoordinates head;
	public MeshCoordinates getEdgeHead() {
		return head;
	}
	
	/* Constructor (empty) */
	public MeshEdge() {
		tail = null;
		head = null;
	}
	
	/* Constructor */
	public MeshEdge(MeshCoordinates nTail, MeshCoordinates nHead) {
		tail = nTail;
		head = nHead;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
