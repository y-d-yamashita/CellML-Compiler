package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.variableMesh.MeshCoordinates;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.pdesML.Mapper.ParameterMapper;

public class CSVFileReaderMain {

	public static void main(String[] args) 
	throws GraphException, MathException {
		String boundaryFilename = "S:\\DataFiles\\csv\\boundary_RMesh_4x4.csv";
		
		Math_ci dummyVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "dummyVar");
		ParameterMapper boundaryMapper = new ParameterMapper();
		boundaryMapper.readParameterCSVFile(dummyVar, boundaryFilename);
		
		HashMap<MeshCoordinates, String> boundaryHMap = new HashMap<MeshCoordinates, String>();
		boundaryHMap = boundaryMapper.getParameterHMap();
		
		int[] coordinateValues = {0,1,0};	
		MeshCoordinates cNodeCoor = new MeshCoordinates();
		cNodeCoor.setCoordinatePoint(coordinateValues);
		String newValue = boundaryHMap.get(cNodeCoor);
		System.out.println("value of " + cNodeCoor.toString() + ":" + newValue);
	
		
		for (MeshCoordinates value : boundaryHMap.keySet()) {
			if (cNodeCoor.equals(value))
				System.out.println("same key");
		    System.out.println(value.toString());
		    System.out.println(boundaryHMap.get(value));
		}
		
		
	}

}