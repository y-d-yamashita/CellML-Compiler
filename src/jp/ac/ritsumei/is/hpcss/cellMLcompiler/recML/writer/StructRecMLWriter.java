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

public class StructRecMLWriter {
	
	
	StringBuilder m_strStructRecML;
	/* return the structured recml stringbuilder */
	public StringBuilder getM_strStructRecMLFile() {
		return m_strStructRecML;
	}

	/** Constructor */
	public StructRecMLWriter() {
		m_strStructRecML = new StringBuilder();
	}
	
	/*****
	 * Return the Structured RecML file string
	 * *****/
	public void createStructRecMLString(RelMLAnalyzer pRelMLAnalyzer, StringBuilder m_strMathMLExp) 
	throws MathException {
		
		m_strStructRecML.append("<!--          Structured RecML File           --> \n");
		m_strStructRecML.append("<!--    Generated using StructRecMLWriter     --> \n\n");
		m_strStructRecML.append("<recml>" + "\n");
		
		/* insert the loop structure string */
		this.insertLoopStructDeclaration();
		
		/* insert the loop index string */
		Vector<Math_ci> m_vecIndexVar = new Vector<Math_ci>();
		m_vecIndexVar = pRelMLAnalyzer.getM_vecIndexVar();
		this.insertLoopIndexDeclaration(m_vecIndexVar);
		
		/* insert the variable string */
		int indexNum = pRelMLAnalyzer.getM_vecDimensionVar().size();
		
		for (int i = 0; i<pRelMLAnalyzer.getM_vecDiffVar().size(); i++) {
			this.insertStructRecMLVarDeclaration(pRelMLAnalyzer.getM_vecDiffVar().get(i), "double", indexNum);
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecArithVar().size(); i++) {
			this.insertStructRecMLVarDeclaration(pRelMLAnalyzer.getM_vecArithVar().get(i), "double", indexNum);
		}
		for (int i = 0; i<pRelMLAnalyzer.getM_vecConstVar().size(); i++) {
			this.insertStructRecMLVarDeclaration(pRelMLAnalyzer.getM_vecConstVar().get(i), "double", indexNum);
		}
		for (int i=0; i<pRelMLAnalyzer.getM_vecDeltaVar().size(); i++) {
			this.insertStructRecMLVarDeclaration(pRelMLAnalyzer.getM_vecDeltaVar().get(i), "double", indexNum);
		}
		
		/* insert the model and boundary condition mathml equations */
		this.insertMathMLEquations(m_strMathMLExp); 

		m_strStructRecML.append("</recml>" + "\n");
	}
	
	/**
	 * Insert structured RecML loop structure
	 * TODO: implement loop structure declaration
	 */
	protected void insertLoopStructDeclaration() {
		
	}
	
	/**
	 * Insert RecML loop index declaration
	 * @return 
	 */
	protected void insertLoopIndexDeclaration(Vector<Math_ci> m_vecIndexVar) 
	throws MathException {
		for (int i=0; i<m_vecIndexVar.size(); i++) {
			String loopDeclare = "<loopindex num=\"" + Integer.toString(i) + "\" ";
			loopDeclare += "name=\"" + m_vecIndexVar.get(i).toLegalString() + "\" />";
			
			m_strStructRecML.append(loopDeclare + "\n");
		}
	}
	
	/**
	 * Insert the structured RecML variable list declaration
	 * @return 
	 */
	private void insertStructRecMLVarDeclaration(Math_ci pRelMLVar, String varType, int indexNum) 
	throws MathException {
		String strMLVar = "<variable name=\"" + pRelMLVar.toLegalString() + "\" ";
		strMLVar += "type=\"" + varType + "\" ";
		for (int i=0; i<indexNum; i++) {
			strMLVar += "loopcomponent"+ Integer.toString(i+1) + "=\"1\" ";
		}
		strMLVar += "/>";
		
		m_strStructRecML.append(strMLVar + "\n");
	}
	
	/**
	 * Insert the MathML equations
	 * @return 
	 */
	protected void insertMathMLEquations(StringBuilder m_strMathMLExp) 
	throws MathException{
		m_strStructRecML.append("\n" + "<math>" + "\n");
		m_strStructRecML.append(m_strMathMLExp + "\n");
		m_strStructRecML.append("</math>" + "\n");
	}
	
	/**
	 * Write the structured recml string into a file
	 * @return: structRecML.recml 
	 */
	public void writeStructRecMLFile(String fileName)
	throws IOException {
		Writer output = null;
		File structRecMLFile = new File(fileName);
		output = new BufferedWriter(new FileWriter(structRecMLFile));
		output.append(m_strStructRecML);
		output.close();
	}
	
	/**
	 * Print the contents of the StructRecML StringBuilder
	 *  @throws MathException
	 */
	public void printContents() throws MathException {
		System.out.println(m_strStructRecML.toString());
	}
}
