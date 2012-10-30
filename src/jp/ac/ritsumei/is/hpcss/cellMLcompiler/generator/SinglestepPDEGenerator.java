package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.variableMesh.MeshCoordinates;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.pdesML.Mapper.ParameterMapper;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.writer.SimpleRecMLWriter;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;
/**
 * テスト用プログラム構文生成クラス.
 */
public class SinglestepPDEGenerator extends ProgramGenerator {


	StringBuilder m_strMathExpInstances;
	/* return the mathml string of equation instances */
	public StringBuilder getM_strMathMLExp() {
		return m_strMathExpInstances;
	}
	
	/* the index of the boundary id and location attribute */
	private int nBoundIDIndex;
	private int nBoundLocIndex;
	
	HashMap<String, MathExpression> BoundaryExpHMap;
	/* return boundary condition expressions */
	public HashMap<String, MathExpression> getM_vecBoundaryExp() {
		return BoundaryExpHMap;
	}

	Vector<MathExpression> m_vecDiscreteModelExp;
	/* return discrete model expressions */
	public Vector<MathExpression> getM_vecDiscreteModelExp() {
		return m_vecDiscreteModelExp;
	}
	
	Vector<MathOperand> m_vecRelMLIndices;
	/* return boundary condition expressions */
	public Vector<MathOperand> getM_vecRelMLIndices() {
		return m_vecRelMLIndices;
	}
	

	public SinglestepPDEGenerator() {
		// TODO Remove after testing as in ProgramGenerator
	}

	/**
	 * テスト用プログラム構文生成インスタンスを作成する.
	 * @param pCellMLAnalyzer CellML解析器
	 * @param pRelMLAnalyzer RelML解析器
	 * @param pTecMLAnalyzer TecML解析器
	 */
	public SinglestepPDEGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer) {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
		
		m_strMathExpInstances = new StringBuilder();
		BoundaryExpHMap = new HashMap<String, MathExpression>();
		m_vecDiscreteModelExp = new Vector<MathExpression>();
		m_vecRelMLIndices = new Vector<MathOperand>();
		nBoundIDIndex = 0;
		nBoundLocIndex = 1;
	}

	/*-----プログラム構文取得メソッド-----*/
	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.ProgramGenerator#getSyntaxProgram()
	 */
	public SyntaxProgram getSyntaxProgram()
	throws MathException, CellMLException, RelMLException, TranslateException {
		System.out.println("[test]------------------------------------");
		/*プログラム構文生成*/
		SyntaxProgram pSynProgram = null;

		/*プログラム構文を返す*/
		return pSynProgram;
	}
	
	/**
	 * Discretize the CellML using the single-step equations in the TecML and variable list and 
	 * boundary conditions in the RelML.
	 * @throws CellMLException
	 * @throws RelMLException
	 * @throws MathException
	 * @throws TranslateException
	 *
	 */
	public void discretizeCellML() 
	throws CellMLException, RelMLException, MathException, TranslateException, GraphException {
		
		/* Store the CellML model equations for discretization */
		for(int i=0; i<m_pCellMLAnalyzer.getExpressionCount(); i++) {
			m_vecDiscreteModelExp.add(m_pCellMLAnalyzer.getExpression(i));
		}
		
		/* Store the RelML index variables used */
		for(int i=0; i<m_pRelMLAnalyzer.getM_vecIndexVar().size(); i++) {
			MathOperand pRelIndexVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecIndexVar().get(i).toLegalString());
			m_vecRelMLIndices.add(pRelIndexVar);
		}
		
		this.createDiscreteExpressions(m_vecDiscreteModelExp);

		/* Add the boundary conditions */
		this.createDiscreteBoundaryConditions();
		
		System.out.println("\n --------------------------- Boundary conditions from RelML:");
//		for (String strBoundaryLoc : BoundaryExpHMap.keySet()) {
//			System.out.println("Loc " + strBoundaryLoc + "Exp: " + BoundaryExpHMap.get(strBoundaryLoc).toLegalString());
//		}

		System.out.println("\n --------------------------- Single-step equations: partialdiff got working!");
//		for(int k=0; k<m_vecDiscreteModelExp.size(); k++) {
//			System.out.println(m_vecDiscreteModelExp.get(k).toLegalString());
//		}
		
	}	
	
	/**
	 * Discretize the input MathExpressions using the TecML file
	 * strAttr are used for the boundary conditions
	 * @throws MathException
	 * @throws TranslateException
	 *
	 */
	public void createDiscreteExpressions(Vector<MathExpression> targetExpressions, String[] strAttr)
	throws MathException, TranslateException {
		
		int nTargetExpNum = targetExpressions.size();
		
		for (int i = 0; i < m_pTecMLAnalyzer.getExpressionCount(); i++) {
			
			/* Get the current TecML expression boundary location attribute */
			String strCurrentTecExpBoundLocAttr = m_pTecMLAnalyzer.getAttribute(i)[nBoundLocIndex];
			
			/*Get the left and right expression of each equation in TecML*/
			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);
			MathExpression pLeftExp = pMathExp.getLeftExpression();
			MathExpression pRightExp = pMathExp.getRightExpression();
			
			if (pLeftExp == null || pRightExp == null) {
				throw new TranslateException("SingleStepPDEGenerator","addIndexToVariables",
							     "failed to parse expression");
			}
			
			/*Convert expression to MathOperand and assume both sides are variables*/
			MathOperand pLeftVar = (MathOperand)((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pLeftExp.toLegalString()));
			
			/* Check if diffvar and then do differential variable replacement (add index)*/
			if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {
				/* Copy TecML diffvar and replace with all the diffvars in RelML */
				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecDiffVar().size(); j++) {
					MathOperand pRelMLDiffVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDiffVar().get(j).toLegalString()); //remove "null." part and return MathOperand											
					MathFactor pIndexedDiffVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLDiffVar);	
//					System.out.println(pIndexedDiffVar.toLegalString());
					
					/* Replace the differential variables in the CellML equations */
					for(int k=0; k<nTargetExpNum; k++) {
						targetExpressions.get(k).replace(pRelMLDiffVar, pIndexedDiffVar);
					}
				}
			}
			
			/* Check if arithvar and then do differential variable replacement (add index)*/
			if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
				/* Copy TecML arithvar and replace with all the arithvars in RelML */
				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecArithVar().size(); j++) {	
					MathOperand pRelMLArithVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecArithVar().get(j).toLegalString());					
					MathFactor pIndexedArithVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLArithVar);	
//					System.out.println(pIndexedArithVar.toLegalString());
				
					/* Replace the arithmetic variables in the CellML equations */
					for(int k=0; k<nTargetExpNum; k++) {
						targetExpressions.get(k).replace(pRelMLArithVar, pIndexedArithVar);
					}
				}
			}
			
			/* Check if constvar and then do differential variable replacement (add index)*/
			if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
				/* Copy TecML constvar and replace with all the constvars in RelML */
				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecConstVar().size(); j++) {
					MathOperand pRelMLConstVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecConstVar().get(j).toLegalString());
					MathFactor pIndexedConstVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLConstVar);	
//					System.out.println(pIndexedConstVar.toLegalString());

					/* Replace the constants in the CellML equations */
					for(int k=0; k<nTargetExpNum; k++) {
						targetExpressions.get(k).replace(pRelMLConstVar, pIndexedConstVar);
					}
				}
			}
			
			if (m_pTecMLAnalyzer.isDimensionVar(pLeftVar)) {
				/* TODO: Insert propagation of equations */
			}
			
			if (m_pTecMLAnalyzer.isDiffOperator(pLeftVar)) {
				
				int nDimensionVarNum = m_pRelMLAnalyzer.getM_vecDimensionVar().size();

				/* Copy TecML diffvar and replace with all the diffvars in the differential operator */
				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecDiffVar().size(); j++) {
					MathOperand pRelDiffVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDiffVar().get(j).toLegalString());
					MathOperand pTecDiffVar = (MathOperand)m_pTecMLAnalyzer.getM_vecDiffVar().get(0);
					
					MathExpression newLeftExp = pLeftExp.createCopy();
					MathExpression newRightExp = pRightExp.createCopy();					
					MathOperator pLeftOptr = (MathOperator)newLeftExp.getRootFactor();
					MathOperator pRightOptr = (MathOperator)newRightExp.getRootFactor();
					pLeftOptr.replaceDiffOptrVar(pTecDiffVar, pRelDiffVar);
					pRightOptr.replace(pTecDiffVar, pRelDiffVar);
					
					/* Replace the dimension variables in the TecML differential equations */
					for (int k = 0; k < nDimensionVarNum; k++) {
						MathOperand pRelDimVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDimensionVar().get(k).toLegalString());
						MathOperand pTecDimVar = (MathOperand)m_pTecMLAnalyzer.getM_vecDimensionVar().get(k);		
						pLeftOptr.replaceDiffOptrVar(pTecDimVar, pRelDimVar);
					}
					
					/* Replace the differential operators in the CellML equations */
					for(int k=0; k<nTargetExpNum; k++) {		
						if (strAttr != null && strCurrentTecExpBoundLocAttr != null) {
							if (strAttr[k] != null && (strAttr[k].equals(strCurrentTecExpBoundLocAttr))) {
								targetExpressions.get(k).replace(pLeftOptr, pRightOptr);
							}	
						} else {
							targetExpressions.get(k).replace(pLeftOptr, pRightOptr);
						}
					}
				}
			}
		}

	}
	
	/**
	 * Discretize the input MathExpressions using the TecML file
	 * without attribute inputs
	 * @throws MathException
	 * @throws TranslateException
	 *
	 */
	public void createDiscreteExpressions(Vector<MathExpression> targetExpressions)
			throws MathException, TranslateException {
		this.createDiscreteExpressions(targetExpressions, null);
	}
	
	/**
	 * Add the boundary equations from RelML
	 * @return Math_ci: discretized variable
	 */
	private void createDiscreteBoundaryConditions() 
	throws MathException, TranslateException {
		
		String[] strBoundLocAttr = new String[m_pRelMLAnalyzer.getExpressionCount()];
		Vector<MathExpression> m_vecRelMLExp = new Vector<MathExpression>();
		
		for (int i=0; i<m_pRelMLAnalyzer.getExpressionCount(); i++) {
			m_vecRelMLExp.add(m_pRelMLAnalyzer.getExpression(i));
			String[] strRelMLExpAttr = m_pRelMLAnalyzer.getAttribute(i);
			System.out.println(Arrays.toString(strRelMLExpAttr));
			strBoundLocAttr[i] = strRelMLExpAttr[nBoundLocIndex];
			
		}
		/* discretize the RelML equations */
		this.createDiscreteExpressions(m_vecRelMLExp, strBoundLocAttr);
		
		/* if the attribute has a location, means its a boundary equation */
		for (int i=0; i<m_pRelMLAnalyzer.getExpressionCount(); i++) {
			if (strBoundLocAttr[i] != null) {
				String strBoundIDAttr = m_pRelMLAnalyzer.getAttribute(i)[nBoundIDIndex];
				BoundaryExpHMap.put(strBoundIDAttr, m_vecRelMLExp.get(i));
			}
		}
	}
	
	/*****
	 * Generate the equations for all instances of time and space
	 * using the csv files
	 * *****/
	public void generateAllInstanceEquations(int nTimeSteps) 
	throws MathException, GraphException {
		
		/* add the instances for model equations */		
		System.out.println("\n --------------------------- Equation instances:");
		this.generateDiscreteExpInstances(nTimeSteps);
		
		/* add the instances for boundary conditions */
		System.out.println("\n --------------------------- Boundary locations:");
		this.generateBoundaryExpInstances(nTimeSteps);
		
		//TODO: distributed parameters
		/* add the instances for distributed parameters */
		
	}
	
	/*****
	 * Generate the model equations for all instances of time and space
	 * and append the MathML equations to StringBuilder
	 * 
	 * @return: m_strMathExpInstances.append(boundaryMathMLString)
	 * *****/
	public void generateDiscreteExpInstances(int nTimeSteps) 
	throws MathException, GraphException {

		ParameterMapper discreteEquMapper = new ParameterMapper();
		discreteEquMapper.readParameterCSVFile(m_pRelMLAnalyzer.getMorphologyFileName());

		/* generate the all instances for all simulation points in the boundary */
		HashMap<MeshCoordinates, String> MorphologyHMap = new HashMap<MeshCoordinates, String>();
		MorphologyHMap = discreteEquMapper.getParameterHMap();
		
		/* Combine the time steps with the spatial coordinates*/
		int[] morphologyInstance = new int[m_pRelMLAnalyzer.getM_vecIndexVar().size()];
		for (int i=0; i<nTimeSteps; i++) {
			morphologyInstance[0] = i;
			
			/* add the spatial coordinates to index instance */
			for (MeshCoordinates coor: MorphologyHMap.keySet()) {
				
				for (int j=1; j<morphologyInstance.length; j++) {
					morphologyInstance[j] = coor.getCoordinate(j-1);
				}
				
//				System.out.println(Arrays.toString(morphologyInstance) + " morphology-state: " + MorphologyHMap.get(coor));
				if (Integer.parseInt(MorphologyHMap.get(coor)) != 0) {
					for (int k=0; k<m_vecDiscreteModelExp.size(); k++) {
						MathExpression pNewModelExpInstance = m_vecDiscreteModelExp.get(k).createCopy();
						this.generateExpInstance(pNewModelExpInstance, morphologyInstance);
						m_strMathExpInstances.append(pNewModelExpInstance.toMathMLString() + "\n");
//						System.out.println(pNewModelExpInstance.toLegalString());
					}
				}
			}
		}

	}
	
	/*****
	  * Generate the equations for all instances of boundary locations
	  * indicated in the boundary maps and append the MathML equations to StringBuilder
	  * 
	  * @return: m_strMathExpInstances.append(boundaryMathMLString)
	 * *****/
	public void generateBoundaryExpInstances(int nTimeSteps)
	throws MathException, GraphException {
		
		/* get the boundary file and locations */ 
		HashMap<Math_ci, String> BoundaryHMap = new HashMap<Math_ci, String>();
		BoundaryHMap = m_pRelMLAnalyzer.getM_HashMapBoundedVar();
		Vector<ParameterMapper> m_vecBoundaryMaps = new Vector<ParameterMapper>();

		/* Get all the bounded variables' boundary condition */
		for (Math_ci m_pBoundedVar : BoundaryHMap.keySet()) {
//		    System.out.println(m_pBoundedVar.toLegalString() + " filename: " + BoundaryHMap.get(m_pBoundedVar));
		    ParameterMapper boundaryMapper = new ParameterMapper();
			boundaryMapper.readParameterCSVFile(m_pBoundedVar, BoundaryHMap.get(m_pBoundedVar));
			m_vecBoundaryMaps.add(boundaryMapper);
			
			/* generate the all instances for bounded variable boundary equations */
			HashMap<MeshCoordinates, String> BoundedVarHMap = new HashMap<MeshCoordinates, String>();
			BoundedVarHMap = boundaryMapper.getParameterHMap();
			
			/* Combine the time steps with the spatial coordinates*/
			int[] boundInstance = new int[m_pRelMLAnalyzer.getM_vecIndexVar().size()];
			for (int i=0; i<nTimeSteps; i++) {
				boundInstance[0] = i;
			
				/* add the spatial coordinates to index instance */
				for (MeshCoordinates coor : BoundedVarHMap.keySet()) {
					for (int j=1; j<boundInstance.length; j++) {
						boundInstance[j] = coor.getCoordinate(j-1);
					}
//					System.out.println(Arrays.toString(boundInstance) + " boundary-id: " + BoundedVarHMap.get(coor));
					if (Integer.parseInt(BoundedVarHMap.get(coor)) != 0) {
						MathExpression pNewBoundaryInstance = BoundaryExpHMap.get(BoundedVarHMap.get(coor)).createCopy();
						this.generateExpInstance(pNewBoundaryInstance, boundInstance);
						m_strMathExpInstances.append(pNewBoundaryInstance.toMathMLString() + "\n");
//						System.out.println(pNewBoundaryInstance.toLegalString());
					}
					
				}
					
			}

		}

	}
	
	/*****
	 * Generate an instance of the equation for index values (n,j,l...)
	 * input: MathExpression with index
	 * output: MathExpression with index instance
	 * *****/
	public void generateExpInstance(MathExpression pExp, int[] pIndexValues)
	throws MathException {
		
		for (int i=0; i<m_vecRelMLIndices.size(); i++) {	
			if (m_vecRelMLIndices.size() != pIndexValues.length) {
				throw new MathException("SingleStepPDEGenerator","generateExpressionInstance",
							     "number of indices and indices instance does not match");
			}
			Math_cn pIndexInstance = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, Integer.toString(pIndexValues[i]));
			pIndexInstance.changeType();
			pExp.replace(m_vecRelMLIndices.get(i), (MathOperand)pIndexInstance);
		}
		
	}
	
	/**
	 * Discretize the equation variables in CellML
	 * @return MathFactor: discretized variable
	 */
	private MathFactor discretizeVariable(MathExpression pExpression, MathOperand pOldOprnd,  MathOperand pNewOprnd) 
	throws MathException {
		MathExpression pNewExpression = pExpression.createCopy();
		pNewExpression.replace(pOldOprnd, pNewOprnd); 		
		MathFactor pNewVarFactor = pNewExpression.getRootFactor();
		return pNewVarFactor;	
	}
	
		
	static boolean parseXMLFile(String strXMLFileName, XMLReader pParser,
			XMLAnalyzer pXMLAnalyzer) {

		try {

			/*ファイルの存在を確認*/
			if(!new File(strXMLFileName).canRead()){
				System.err.println("file can't open : " + strXMLFileName);
				return false;
			}

			/*パース処理*/
			XMLHandler handler = new XMLHandler(pXMLAnalyzer);
			pParser.setContentHandler(handler);
			pParser.parse(strXMLFileName);

		} catch (SAXParseException e) {
			/*エラー出力*/
			System.err.println("failed to parse file : " + strXMLFileName);
			return false;
		} catch (Exception e) {

			/*例外メッセージ出力*/
			System.err.println("error: Parse error occurred - "+e.getMessage());
			Exception se = e;
			if (e instanceof SAXException) {
				se = ((SAXException)e).getException();
			}
			if (se != null) {
				se.printStackTrace(System.err);
			} else {
				e.printStackTrace(System.err);
			}

			/*エラー出力*/
			System.err.println("failed to parse file : " + strXMLFileName);
			return false;
		}

		return true;
	}
	
	/**
	 * 解析内容を標準出力する.
	 * @throws MathException
	 */
	public void printContents() throws MathException {
		//-------------------------------------------------
		//出力
		//-------------------------------------------------
		/*開始線出力*/
		System.out.println("[Simple/Structured RecML]------------------------------------");

//		printContents("diffvar", m_vecDiffVarIndexed);
//		printContents("arithar", m_vecArithVarIndexed);
//		printContents("constvar", m_vecConstVarIndexed);

		/*print the math expressions*/
//		super.printExpressions();

		/*改行*/
		System.out.println();
	}

	/**
	 * TODO: just for generating mesh points
	 * @throws MathException
	 */
	public void generateMeshPoints(int maxx, int maxy) 
	throws IOException {
		StringBuilder strMorphologyMesh = new StringBuilder();
		for (int i=0; i<maxx; i++) {
			for (int j=0; j<maxy; j++) {
				strMorphologyMesh.append(Integer.toString(i) + "," + Integer.toString(j) + ",0,1\n");
			}
		}
		
		Writer output = null;
		File strMorpFile = new File("morphology_100x100.csv");
		output = new BufferedWriter(new FileWriter(strMorpFile));
		output.append(strMorphologyMesh);
		output.close();
	}
	
	public void generateBoundaryPoints(int maxx, int maxy) 
	throws IOException {
		StringBuilder strMorphologyMesh = new StringBuilder();
		int boundaryID = 0;
		
		for (int i=0; i<maxx; i++) {
			for (int j=0; j<maxy; j++) {
				if (i==0 && j==0) {
					boundaryID = 5;
				} else if(i==0 && j==(maxy-1)) {
					boundaryID = 6;
				} else if(i==(maxx-1) && j==0) {
					boundaryID = 7;
				} else if(i==(maxx-1) && j==(maxy-1)) {
					boundaryID = 8;
				} else if(i==0) {
					boundaryID = 1;
				} else if(i==(maxx-1)) {
					boundaryID = 2;
				} else if(j==0) {
					boundaryID = 3;
				} else if(j==(maxy-1)) {
					boundaryID = 4;
				} else {
					boundaryID = 0;
				}
				
				strMorphologyMesh.append(Integer.toString(i) + "," + Integer.toString(j) + ",0," + Integer.toString(boundaryID) +"\n");
			}
		}
		
		Writer output = null;
		File strMorpFile = new File("boundary_100x100.csv");
		output = new BufferedWriter(new FileWriter(strMorpFile));
		output.append(strMorphologyMesh);
		output.close();
	}

	/***** Main program for testing *****/
	public static void main(String[] args) 
	throws GraphException {
		RelMLAnalyzer pRelMLAnalyzer = new RelMLAnalyzer();
		CellMLAnalyzer pCellMLAnalyzer = new CellMLAnalyzer();
		TecMLAnalyzer pTecMLAnalyzer = new TecMLAnalyzer();
		CellMLVariableAnalyzer pCellMLVariableAnalyzer = new CellMLVariableAnalyzer();
		
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
					new Integer(16 * 0x1000));
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("
					+ "org.apache.xerces.parsers.SAXParser" + ")");
			System.exit(1);
		}

		XMLHandler handler = new XMLHandler(pRelMLAnalyzer);
		parser.setContentHandler(handler);
		try {
			parser.parse(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String strTecMLFileName;
		String strCellMLFileName;
		
		strTecMLFileName = pRelMLAnalyzer.getFileNameTecML();
		strCellMLFileName = pRelMLAnalyzer.getFileNameCellML();

		/*CellML変数部分の解析*/
		if(!parseXMLFile(strCellMLFileName, parser, pCellMLVariableAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* CellML変数 parse end");

		/*変数テーブルをCellML解析器に渡す*/
		pCellMLAnalyzer.setComponentTable(pCellMLVariableAnalyzer.getComponentTable());

		/*CellMLの解析*/
		if(!parseXMLFile(strCellMLFileName, parser, pCellMLAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* CellML parse end");

		/*TecMLの解析*/
		if(!parseXMLFile(strTecMLFileName,parser,pTecMLAnalyzer)){
			System.exit(1);
		}
		//	System.out.println("******* TecML parse end");
		
//		int[] spaceDimension = pRelMLAnalyzer.getDimensions();
//		int[] spacing = pRelMLAnalyzer.getSpacing();
//		System.out.println(Arrays.toString(spaceDimension));
//		System.out.println(Arrays.toString(spacing));
		
		/* Print the contents of the generator*/
		SinglestepPDEGenerator pSingleStepGenerator = null;
		
		try {
			pSingleStepGenerator = new SinglestepPDEGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
//			pSynProgram = pSingleStepGenerator.getSyntaxProgram();
		} catch (Exception e) {
			/*エラー出力*/
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.err.println("failed to translate program");
			System.exit(1);
		}
		
		int nExpressionNum = pCellMLAnalyzer.getExpressionCount();
		System.out.println("CellML Exp count: " + nExpressionNum);
		
		
		//Variables for testing the instantiation of simulation equations with time and a 2D array
		int maxTime = 2;
		int maxx = 75;
		int maxy = 75;
		long start = System.currentTimeMillis();
		StringBuilder strRecMLMathExp = new StringBuilder();
		try {
			pSingleStepGenerator.discretizeCellML();
			pSingleStepGenerator.generateAllInstanceEquations(maxTime);
			strRecMLMathExp = pSingleStepGenerator.getM_strMathMLExp();
			
//			pSingleStepGenerator.generateMeshPoints(maxx, maxy);
			pSingleStepGenerator.generateBoundaryPoints(maxx, maxy);
			System.out.println("[output]------------------------------------");
			
//			pSingleStepGenerator.createSimpleRecMLString(maxDimensions);
//			pSingleStepGenerator.createStructRecMLString(maxDimensions);
			
		} catch (MathException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (CellMLException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (RelMLException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (TranslateException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		/* Generate the SimpleRecML file */
		SimpleRecMLWriter pSimpleRecMLWriter = new SimpleRecMLWriter();
		try {
			pSimpleRecMLWriter.createSimpleRecMLString(pRelMLAnalyzer, strRecMLMathExp);
			pSimpleRecMLWriter.writeSimpleRecMLFile("FHN_FTCS_2x75x75.recml");
			
			Writer output = null;
			File strMorpFile = new File("mathExp_75x75.txt");
			output = new BufferedWriter(new FileWriter(strMorpFile));
			output.append(strRecMLMathExp);
			output.close();
//			pSimpleRecMLWriter.printContents();
		} catch (MathException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		
//		/* Number of instances for generating equations */
//		System.out.println(Arrays.toString(maxDimensions));
//		
//		/* Total number of processors or cores available to the JVM */
//		System.out.println("Available processors (cores): " + 
//		        Runtime.getRuntime().availableProcessors());
//
//		/* Total amount of free memory available to the JVM */
//	    System.out.println("Free memory (bytes): " + 
//	        Runtime.getRuntime().freeMemory());
//
//	    /* This will return Long.MAX_VALUE if there is no preset limit */
//	    long maxMemory = Runtime.getRuntime().maxMemory();
//	    /* Maximum amount of memory the JVM will attempt to use */
//	    System.out.println("Maximum memory (bytes): " + 
//	        (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
//
//	    /* Total memory currently in use by the JVM */
//	    System.out.println("Total memory (bytes): " + 
//	        Runtime.getRuntime().totalMemory());
//
//	    PerformanceMonitor runtimeMonitor = new PerformanceMonitor();
//	    // Get the Java runtime
//	    Runtime runtime = Runtime.getRuntime();
//	    // Run the garbage collector
//	    runtime.gc();
//	    // Calculate the used memory
//	    long memory = runtime.totalMemory() - runtime.freeMemory();
//	    System.out.println("Used memory in bytes: " + memory);
//	    System.out.println("Used memory in megabytes: "
//	        + runtimeMonitor.bytesToMegabytes(memory));
//
//		long end = System.currentTimeMillis();
//		System.out.println("Execution time: "+ (end-start)/1000 +" s.");
		
		/* Write the Simple RecML into a file*/
//		String recmlFileName = "FHN_FTCS_simple_" + Integer.toString(maxDimensions[0]) + "x" + Integer.toString(maxDimensions[1]) + "x" + Integer.toString(maxDimensions[1]) +".recml";
//		try {	
//			pSingleStepGenerator.writeSimpleRecMLFile(recmlFileName);
//		} catch (IOException io1) {
//			io1.printStackTrace();
//		}
		
		//print the simple recml file
//		System.out.println(pSingleStepGenerator.m_strSimpleRecML);

//		try {
//			pRelMLAnalyzer.printContents();
//		} catch (MathException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			pTecMLAnalyzer.printContents();
//		} catch (MathException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
