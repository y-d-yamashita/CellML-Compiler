package jp.ac.ritsumei.is.hpcss.cellMLcompiler.pdesML.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.variableMesh.MeshCoordinates;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.CSVFileReader;


/**
 * Class for storing the parameter / boundary values of mesh nodes.
 *  input: CSV file which has coordinate and parameter values in the form
 *  		{x,y,z,parameterValue}
 */
public class ParameterMapper {
	
	/** Variable name for the corresponding map */
	private Math_ci m_pMappedVariableName;
	public Math_ci getM_pMappedVariableName() {
		return m_pMappedVariableName;
	}
	
	/** Number of dimensions in the coordinate (default = 3 (x,y,z)) */
	private int coordinateLength;
	
	/** HashMap for storing the parameter values */
	private HashMap<MeshCoordinates, String> ParameterHMap;
	public HashMap<MeshCoordinates, String> getParameterHMap() {
		return ParameterHMap;
	}
	
	public ParameterMapper() {
		ParameterHMap = new HashMap<MeshCoordinates, String>();
		m_pMappedVariableName = null;
		coordinateLength = 3;
	}
	
	public void readParameterCSVFile(Math_ci m_pParameterName, String CSVFileName) 
	throws GraphException {
		
		m_pMappedVariableName = m_pParameterName;
		String delims = "[,]";
		CSVFileReader parameterReader = new CSVFileReader();
		parameterReader.ReadFile(CSVFileName);
		
		ArrayList<String> strNodeData = parameterReader.getLineArray();
		for (int i=0; i<strNodeData.size(); i++) {
			
			String[] tokens = strNodeData.get(i).split(delims);
			int[] coordinateValues = new int[coordinateLength];
			for (int j=0; j<coordinateLength; j++) {
				coordinateValues[j] = Integer.parseInt(tokens[j]);
			}
			MeshCoordinates nNodeCoordinate = new MeshCoordinates();
			nNodeCoordinate.setCoordinatePoint(coordinateValues);
			String pNodeValue = tokens[coordinateLength];
			ParameterHMap.put(nNodeCoordinate, pNodeValue);		
			
		}
	}
	
	public void readParameterCSVFile(String CSVFileName) 
	throws MathException, GraphException {
		
		Math_ci dummyVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "dummyVar");
		this.readParameterCSVFile(dummyVar, CSVFileName);
		
	}
	
	public void setCoordinateLength(int newCoordLength) {
		coordinateLength = newCoordLength;
	}
	
	/** print the contents of the HashMap */
	public void toLegalString() {
		for (MeshCoordinates nNodeCoordinate : ParameterHMap.keySet()) {
			System.out.println(nNodeCoordinate.toString() + ": " + ParameterHMap.get(nNodeCoordinate));
		}
		
	}
	
}
