package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.variableMesh;

import java.util.ArrayList;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Vertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.variableMesh.MeshCoordinates;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;;

public class MeshNode extends Vertex {
	
	/* Node coordinates */
	private MeshCoordinates nodeLocation;
	public MeshCoordinates getNodeCoordinates() {
		return nodeLocation;
	}
	
	public int numInEdges; 	//Number of incoming mesh edges
	public int numOutEdges; //Number of outgoing mesh edges
	public ArrayList<MeshEdge> inEdge; //Array of incoming mesh edges
	public ArrayList<MeshEdge> outEdge; //Array of outgoing mesh edges
	
	public MeshNode(){
		nodeLocation = new MeshCoordinates();
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
