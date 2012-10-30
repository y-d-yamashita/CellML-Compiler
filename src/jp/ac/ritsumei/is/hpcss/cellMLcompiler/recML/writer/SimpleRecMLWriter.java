package jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;

public class SimpleRecMLWriter {

	
	StringBuilder m_strSimpleRecML;
	/* return the simple recml string file */
	public StringBuilder getM_strSimpleRecMLFile() {
		return m_strSimpleRecML;
	}
	
	/** Constructor */
	public SimpleRecMLWriter () {
		m_strSimpleRecML = new StringBuilder();
	}
	
	
	/*****
	 * Return the Simple RecML file string
	 * *****/
	public void createSimpleRecMLString(RelMLAnalyzer pRelMLAnalyzer, StringBuilder m_strMathMLExp) 
	throws MathException {
		
		m_strSimpleRecML.append("<!--          SimpleRecML File                --> \n");
		m_strSimpleRecML.append("<!--    Generated using SimpleRecMLWriter     --> \n\n");
		m_strSimpleRecML.append("<recml>" + "\n");
		
		/* insert the loop index string */
		Vector<Math_ci> m_vecIndexVar = new Vector<Math_ci>();
		m_vecIndexVar = pRelMLAnalyzer.getM_vecIndexVar();
		this.insertLoopIndexDeclaration(m_vecIndexVar);
		
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDimensionVar().size(); i++) {
			this.insertSimpleRecMLVarDeclaration(pRelMLAnalyzer.getM_vecDimensionVar().get(i), "double", "0.0");
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDiffVar().size(); i++) {
			this.insertSimpleRecMLVarDeclaration(pRelMLAnalyzer.getM_vecDiffVar().get(i), "double", "0.0");
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecArithVar().size(); i++) {
			this.insertSimpleRecMLVarDeclaration(pRelMLAnalyzer.getM_vecArithVar().get(i), "double", "0.0");
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecConstVar().size(); i++) {
			this.insertSimpleRecMLVarDeclaration(pRelMLAnalyzer.getM_vecConstVar().get(i), "double", "0.0");
		}
		for (int i=0; i<pRelMLAnalyzer.getM_vecDeltaVar().size(); i++) {
			this.insertSimpleRecMLVarDeclaration(pRelMLAnalyzer.getM_vecDeltaVar().get(i), "double", "0.0");
		}
		
		/* insert the model and boundary condition mathml equations */
//		this.insertMathMLEquations(m_strMathMLExp); 
		
		m_strSimpleRecML.append("</recml>" + "\n");
	}
	
	protected void insertLoopIndexDeclaration(Vector<Math_ci> m_vecIndexVar) 
	throws MathException {
		for (int i=0; i<m_vecIndexVar.size(); i++) {
			String loopDeclare = "<loopindex num=\"" + Integer.toString(i) + "\" ";
			loopDeclare += "name=\"" + m_vecIndexVar.get(i).toLegalString() + "\" />";
			
			m_strSimpleRecML.append(loopDeclare + "\n");
		}
	}
	
	protected void insertSimpleRecMLVarDeclaration(Math_ci pRelMLVar, String varType, String initValue) 
	throws MathException {
		String varDeclare = "<variable name=\"" + pRelMLVar.toLegalString() + "\" ";
		varDeclare += "type=\"" + varType + "\" ";
		varDeclare += "initialvalue=\""+ initValue + "\" />";
		m_strSimpleRecML.append(varDeclare + "\n");
	}
	
	protected void insertMathMLEquations(StringBuilder m_strMathMLExp) 
	throws MathException{
		m_strSimpleRecML.append("\n" + "<math>" + "\n");
		m_strSimpleRecML.append(m_strMathMLExp + "\n");
		m_strSimpleRecML.append("</math>" + "\n");
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

}
