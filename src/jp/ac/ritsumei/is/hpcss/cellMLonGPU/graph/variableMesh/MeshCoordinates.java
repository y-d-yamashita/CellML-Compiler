package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.variableMesh;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;

public class MeshCoordinates {

	/* Declare the coordinates storage of the mesh (N-dimensional) */
	private int x,y,z;
	private int coordinateLength;
	
	public MeshCoordinates() {
		coordinateLength = 3;
		x = 0;
		y = 0;
		z = 0;
	}
	
	public MeshCoordinates(int newCoordLength) {
		coordinateLength = newCoordLength;
		x = 0;
		y = 0;
		z = 0;
	}
	
	public void setCoordinatePoint(int[] intNewCoordinates) 
	throws GraphException {
		if(intNewCoordinates.length != coordinateLength) {
			throw new GraphException("MeshCoordinate:setCoordinates: " +
					"mesh size does not match with coordinates: " + Integer.toString(coordinateLength));
		}
		
		if(intNewCoordinates.length > 3) {
			throw new GraphException("MeshCoordinate:setCoordinates: " +
					"mesh coordinates exceeds # of cartesian coodinates (3)");
		}
		
		if (coordinateLength == 2) {
			x = intNewCoordinates[0];
			y = intNewCoordinates[1];
		} else if (coordinateLength == 3) {
			x = intNewCoordinates[0];
			y = intNewCoordinates[1];
			z = intNewCoordinates[2];
		}
	}
	
	public void setCoordinatePoint(int intNewCoordinate) {
		x = intNewCoordinate;
	}
	
	public void setCoordinatePoint(String strNewCoordinates) 
	throws GraphException {
		
		String delims = "[,]";
		String[] tokens = strNewCoordinates.split(delims);
		
		if(tokens.length != coordinateLength) {
			throw new GraphException("MeshCoordinate:setCoordinates: " +
					"mesh size does not match with coordinate length: " + Integer.toString(coordinateLength));
		}
		
		if (tokens.length > 3) {
			throw new GraphException("MeshCoordinate:setCoordinates: " +
					"mesh coordinates exceeds # of cartesian coodinates (3)");
		}
		
		if (coordinateLength == 1) {
			x = Integer.parseInt(tokens[0]);
		} else if (coordinateLength == 2) {
			x = Integer.parseInt(tokens[0]);
			y = Integer.parseInt(tokens[1]);
		} else if (coordinateLength == 3) {
			x = Integer.parseInt(tokens[0]);
			y = Integer.parseInt(tokens[1]);
			z = Integer.parseInt(tokens[2]);
		}

	}
	
	
	public void setCoordinates(int index, int newCoordinate) {
		if (index == 0) {
			x = newCoordinate;
		} else if (index == 1) {
			y = newCoordinate;
		} else if (index == 2) {
			z = newCoordinate;
		}
	}
	
	public int[] getCoordinates() {
		int[] cartesianCoor = new int[3];
		if (coordinateLength == 1) {
			cartesianCoor[0] = x;
		} else if (coordinateLength == 2) {
			cartesianCoor[0] = x;
			cartesianCoor[1] = y;
		} else if (coordinateLength == 3) {
			cartesianCoor[0] = x;
			cartesianCoor[1] = y;
			cartesianCoor[1] = z;
		}
		return cartesianCoor;
	}
	
	public int getCoordinate(int index) {
		int nCoorValue = 0;
		if (index == 0) {
			nCoorValue = x;
		} else if (index == 1) {
			nCoorValue = y;
		} else if (index == 2) {
			nCoorValue = z;
		}
		return nCoorValue;
	}
	
	
	public int getMeshSize() {
		return coordinateLength;
	}
	
	public String toString() {
		String strCoordinates = "";
		
		if (coordinateLength == 1) {
			 strCoordinates = Integer.toString(x);
		} else if (coordinateLength == 2) {
			strCoordinates = Integer.toString(x) +","+ Integer.toString(y);
		} else if (coordinateLength == 3) {
			strCoordinates = Integer.toString(x) +","+ Integer.toString(y) +","+ Integer.toString(z);
		}
		
		return strCoordinates;
	}
	
	/* override hashCode make objects of that MeshCoordinates work as hash map keys
	 * return single integer value of (x,y,z) */
	public int hashCode() {
		return (x * 31) ^ y + z;
	}
	
	/* override equals of HashMap to make objects of that MeshCoordinates work as hash map keys */
	public boolean equals(Object o) {
		 if (o instanceof MeshCoordinates) {
		    	MeshCoordinates other = (MeshCoordinates) o;
		      return (x == other.x && y == other.y && z == other.z);
		 }
		 return false;
	}
	   
}
