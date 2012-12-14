package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.variableMesh;

import java.util.ArrayList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.Vertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.variableMesh.MeshCoordinates;

public class MeshNode extends Vertex {
	
	/* Node coordinates */
	private MeshCoordinates nodeLocation;
	public MeshCoordinates getNodeCoordinates() {
		return nodeLocation;
	}
	
	public int numInEdges; 	//Number of incoming mesh edges
	public int numOutEdges; //Number of outgoing mesh edges
	public ArrayList<MeshEdge> inEdgeList; //Array of incoming mesh edges
	public ArrayList<MeshEdge> outEdgeList; //Array of outgoing mesh edges
	
	public MeshNode(){
		nodeLocation = new MeshCoordinates();
		inEdgeList = new ArrayList<MeshEdge>();
	}
	
	public MeshNode(int numCoordinates){
		nodeLocation = new MeshCoordinates(numCoordinates);
	}
	
	public void setNodeCoordinates(int[] nodeCoordinates)
	throws GraphException {
		nodeLocation.setCoordinatePoint(nodeCoordinates);
	}
	
	
	/* Get the size of the mesh as the number of coordinates */
	public int getMeshSize() {
		return nodeLocation.getMeshSize(); 
	}
	
	/* Get the value of the node coordinates*/
	public MeshCoordinates getCoordinates() {
		return nodeLocation;
	}
	
	public void addIncomingEdge(MeshEdge newInEdge) {
		inEdgeList.add(newInEdge);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
