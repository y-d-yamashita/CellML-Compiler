package jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
public class SimpleRecMLWriter {

	HashMap<String, String> HMapRecMLVarList;
	public HashMap<String, String> get_HMapRecMLVarList() {
		return HMapRecMLVarList;
	}
	
	StringBuilder m_strSimpleRecML;
	/* return the simple recml string file */
	public StringBuilder getM_strSimpleRecMLFile() {
		return m_strSimpleRecML;
	}
	
	/** Constructor */
	public SimpleRecMLWriter () {
		HMapRecMLVarList = new HashMap<String, String>();
		m_strSimpleRecML = new StringBuilder();
	}
	
	/*****
	 * Create the Simple RecML file
	 * *****/
	public void createSimpleRecMLFile(String strFileName) 
	throws IOException {
		
		FileWriter fw = new FileWriter(strFileName);
		PrintWriter pw = new PrintWriter(fw);
		
		Calendar today = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("yyyy/MM/dd");
        System.out.println();
	       
		pw.print("<!--          " + strFileName + "            				--> \n");
		pw.print("<!--    Generated using SimpleRecMLWriter     			--> \n");
		pw.print("<!--    Generated on: " + df.format(today.getTime()) + "  --> \n\n");

		pw.flush();
		//Close the Print Writer
		pw.close();
		//Close the File Writer
		fw.close();   
		
	}
	
	/*****
	 * Return the Simple RecML file string
	 * *****/
	public void appendSimpleRecMLDeclarations(String strFileName, RelMLAnalyzer pRelMLAnalyzer) 
	throws MathException, IOException {
		
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.print("<recml>" + "\n");
		
		/* insert the loop index string */
		Vector<Math_ci> m_vecIndexVar = new Vector<Math_ci>();
		m_vecIndexVar = pRelMLAnalyzer.getM_vecIndexVar();
		this.insertLoopIndexDeclaration(pw, m_vecIndexVar);
		
		/* initial values HashMap*/
		HashMap<Math_ci, String> m_HMapInitValues= new HashMap<Math_ci, String>(); 		
		m_HMapInitValues = pRelMLAnalyzer.getM_HashMapInitialValues();
		String initVal = "";

		for (int i = 0; i<pRelMLAnalyzer.getM_vecDimensionVar().size(); i++) {
			initVal ="0";
			if (m_HMapInitValues.get(pRelMLAnalyzer.getM_vecIndexVar().get(i)) != null)
				initVal = m_HMapInitValues.get(pRelMLAnalyzer.getM_vecIndexVar().get(i));
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecIndexVar().get(i).toLegalString(), "stepvar", initVal);
		}	
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDimensionVar().size(); i++) {
			initVal ="0";
			if (m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDimensionVar().get(i)) != null)
				initVal = m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDimensionVar().get(i));
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecDimensionVar().get(i).toLegalString(), "recurvar", initVal);
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDiffVar().size(); i++) {
			initVal ="0";
			if (m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDiffVar().get(i)) != null)
				initVal = m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDiffVar().get(i));
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecDiffVar().get(i).toLegalString(), "recurvar", initVal);
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecArithVar().size(); i++) {
			initVal ="0";
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecArithVar().get(i).toLegalString(), "arithvar", initVal);
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecConstVar().size(); i++) {
			initVal ="0";
			if (m_HMapInitValues.get(pRelMLAnalyzer.getM_vecConstVar().get(i)) != null)
				initVal = m_HMapInitValues.get(pRelMLAnalyzer.getM_vecConstVar().get(i));
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecConstVar().get(i).toLegalString(), "constvar", initVal);
		}
		for (int i=0; i<pRelMLAnalyzer.getM_vecDeltaVar().size(); i++) {
			initVal ="0";
			if (m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDeltaVar().get(i)) != null)
				initVal = m_HMapInitValues.get(pRelMLAnalyzer.getM_vecDeltaVar().get(i));
			this.insertSimpleRecMLVarDeclaration(pw, pRelMLAnalyzer.getM_vecDeltaVar().get(i).toLegalString(), "constvar", initVal);
		}
		
		/* insert the model and boundary condition mathml equations */
//		this.insertMathMLEquations(m_strMathMLExp); 
		
		pw.print("\n<math>" + "\n\n");
		
		int equNum=1000000;
		/* insert the loop condition declarations */
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDimensionVar().size(); i++) {
			this.insertDummyLoopEquDeclaration(pw, pRelMLAnalyzer.getM_vecIndexVar().get(i).toLegalString(), equNum, i);
			equNum += 2;
		}	
		
//		this.insertMathMLEquations(pw, m_strbMathMLExp);
		
//		this.closeSimpleRecMLFile(strFileName);
		
		pw.flush();
		//Close the Print Writer
		pw.close();
		//Close the File Writer
		fw.close();   
	}

	
	public void closeSimpleRecMLFile(String strFileName) 
	throws IOException {
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.print("\n</math>" + "\n");
		pw.print("</recml>" + "\n");
		
		pw.flush();
		//Close the Print Writer
		pw.close();
		//Close the File Writer
		fw.close();   
	}
	
	protected void insertLoopIndexDeclaration(PrintWriter pw, Vector<Math_ci> m_vecIndexVar) 
	throws MathException {
		for (int i=0; i<m_vecIndexVar.size(); i++) {
			String loopDeclare = "<loopindex num=\"" + Integer.toString(i) + "\" ";
			loopDeclare += "name=\"" + m_vecIndexVar.get(i).toLegalString() + "\" />";
			
			pw.print(loopDeclare + "\n");
		}
	}
	
	protected void insertSimpleRecMLVarDeclaration(PrintWriter pw, String strRelMLVar, String varType, String initValue) {
		// remove the dot if ever from the variable name
		String newVarName = this.removeDotFromVarName(strRelMLVar);
		HMapRecMLVarList.put(strRelMLVar, newVarName);
		String varDeclare = "<variable name=\"" + newVarName + "\" ";
		varDeclare += "type=\"" + varType + "\" init=\"" + initValue + "\" />";
		pw.print(varDeclare + "\n");
	}
	
	protected void insertMathMLEquations(PrintWriter pw, StringBuilder m_strbMathMLExp) {
		pw.print(m_strbMathMLExp.toString());
	}
	
	protected void insertDummyLoopEquDeclaration(PrintWriter pw, String strRelMLIndexVar, int equNum, int loopNum) {
		int equNumAnd1 = equNum + 1;
		StringBuilder dummyEquations = new StringBuilder();
		dummyEquations.append("<apply num=\"" + Integer.toString(equNum) + "\" type=\"final\" condref=\"" + Integer.toString(equNumAnd1) + "\" loopnum=\"" + Integer.toString(loopNum) + "\" ><eq/>" + "\n");
		dummyEquations.append("   <ci>V" + Integer.toString(loopNum) + "end</ci>"+ "\n");
		dummyEquations.append("   <ci>V" + Integer.toString(loopNum) + "end</ci>"+ "\n");
		dummyEquations.append("</apply>"+ "\n");
		
		dummyEquations.append("<apply num=\"" + Integer.toString(equNumAnd1) + ">" + "\n");
		dummyEquations.append("   <ci>" + strRelMLIndexVar + "</ci>"+ "\n");
		dummyEquations.append("   <cn>100</cn>"+ "\n");
		dummyEquations.append("</apply>"+ "\n\n");
		pw.print(dummyEquations);
	}
	
	/**
	 * Write the simple recml string into a file
	 * @return: simpleRecML.recml 
	 */
	public void writeSimpleRecMLFile(String fileName)
	throws IOException {
		Writer output = null;
		File simpleRecMLFile = new File(fileName);
		output = new BufferedWriter(new FileWriter(simpleRecMLFile));
		output.append(m_strSimpleRecML);
		output.close();
	}
	
	/**
	 * Print the contents of the SimpleRecML StringBuilder
	 *  @throws MathException
	 */
	public void printContents() throws MathException {
		System.out.println(m_strSimpleRecML.toString());
	}
	
	public String removeDotFromVarName(String strVarName) {
		 String newVarName = strVarName.replace(".", "_");
		return newVarName;
	}
	
	/**
	 * Change the variable names in the equations if the variable names contain a dot (from component declaration)
	 * @args: inputFileName = input simpleRecML filename with incorrect variable names in the equations
	 * 
	 * */ 
	public void changeRecMLVarNames(String inputFileName) 
	throws IOException {
		File file = new File(inputFileName);
		File temp = File.createTempFile("file", ".txt", file.getParentFile());
		
		String charset = "UTF-8";
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
		
		for (String line; (line = reader.readLine()) != null;) {
			for (String oldVarName: HMapRecMLVarList.keySet()) {
				line = line.replaceAll(oldVarName, HMapRecMLVarList.get(oldVarName));
			}
			writer.println(line);
		}
		
		reader.close();
		writer.close();
		
		// delete the file and rename the temp
		file.delete();
		temp.renameTo(file);
	}

}
