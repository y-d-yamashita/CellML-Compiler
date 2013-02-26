package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
 
 
public class CSVFileReader {
 
	String fileName;
	 
	ArrayList<String> lineValues;
	public ArrayList<String> getLineArray() {
		return lineValues;
	}
	
	public CSVFileReader()
	{
		fileName = null;
		lineValues = new ArrayList<String>();
	}
	 
	public void ReadFile(String FileName) {
		
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader(new FileReader(FileName)); 
			String sCurrentLine;
		 
			while( (sCurrentLine = br.readLine()) != null) {
				lineValues.add(sCurrentLine);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
 
	}
 
	//mutators and accessors
	public void setFileName(String newFileName) {
		this.fileName=newFileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public int getLineArraySize() {
		return lineValues.size();
	}
	
}
