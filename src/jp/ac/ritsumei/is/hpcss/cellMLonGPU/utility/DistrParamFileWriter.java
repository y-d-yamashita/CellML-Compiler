package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


public class DistrParamFileWriter {

	@SuppressWarnings("unused")
	private int maxx, maxy, maxz;
	
	public DistrParamFileWriter() {
		maxx = 1;
		maxy = 1;
		maxz = 1;
	}
	
	/* set the size of the rectilinear mesh */
	public void setMeshSize(int sizex, int sizey, int sizez) {
		maxx = sizex;
		maxy = sizey;
		maxz = sizez;
	}
	
	public void generateDistrParamPoints(String strFileName) 
	throws IOException {
		StringBuilder strMorphologyMesh = new StringBuilder();
		double distrParamValue = 0.0;
		
		for (int i=0; i<maxx; i++) {
			for (int j=0; j<maxy; j++) {
				if ((i <= Math.round(maxx*0.15)) || (i > Math.round(maxx*0.65))) {
					distrParamValue = 0.03;
				} else {
					distrParamValue = 0.025;
				}
				
				strMorphologyMesh.append(Integer.toString(i) + "," + Integer.toString(j) + ",0," + Double.toString(distrParamValue) +"\n");
			}
		}
		
		Writer output = null;
		File strMorpFile = new File(strFileName);
		output = new BufferedWriter(new FileWriter(strMorpFile));
		output.append(strMorphologyMesh);
		output.close();
	}
	
	public static void main(String[] args) {
		
		DistrParamFileWriter pCSVFileGenerator = new DistrParamFileWriter();
		int maxx = 9;
		int maxy = 9;
		
		pCSVFileGenerator.setMeshSize(maxx, maxy, 1);
		try {
			
//			pCSVFileGenerator.generateMeshPoints("morphology_RMesh_100x100.csv");
//			pCSVFileGenerator.generateBoundaryPoints("boundary_RMesh_100x100.csv");
			pCSVFileGenerator.generateDistrParamPoints("./csv/DP_epsilon_9x9.csv");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
}
