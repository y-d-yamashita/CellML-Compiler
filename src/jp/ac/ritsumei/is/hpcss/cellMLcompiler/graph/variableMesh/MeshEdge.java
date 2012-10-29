package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.variableMesh;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Edge;

public class MeshEdge extends Edge {

	/* initial vertex or node */
	private MeshNode tail;
	public MeshNode getEdgeTail() {
		return tail;
	}
	
	/* terminal vertex or node */
	private MeshNode head;
	public MeshNode getEdgeHead() {
		return head;
	}
	
	/* Constructor (empty) */
	public MeshEdge() {
		tail = null;
		head = null;
	}
	
	/* Constructor */
	public MeshEdge(MeshNode nTail, MeshNode nHead) {
		tail = nTail;
		head = nHead;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
