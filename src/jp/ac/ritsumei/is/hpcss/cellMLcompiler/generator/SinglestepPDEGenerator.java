package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Vector;
import java.util.List;

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
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_selector;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.pdesML.Mapper.ParameterMapper;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.writer.SimpleRecMLWriter;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PrimeFactors;

/**
 * テスト用プログラム構文生成クラス.
 */
public class SinglestepPDEGenerator extends ProgramGenerator {

	private static final String strNumAttr = "num";

	StringBuilder m_strMathExpInstances;
	/* return the mathml string of equation instances */
	public StringBuilder getM_strMathMLExp() {
		return m_strMathExpInstances;
	}
	
	/* the index of the boundary id and location attribute */
	private int nIDIndex;
	private int nBoundLocIndex;
	
	private int maxTime;
	public void setMaxTime(int maxtime) {
		maxTime = maxtime;
	}
	
	private int nEquNumber;
	public int getCurrentEquNumber() {
		return nEquNumber;
	}
	public void incrementEquNum() {
		nEquNumber = nEquNumber + 1;
	}
	public void resetEquNumber() {
		nEquNumber = 0;
	}
	
	Vector<MathExpression> m_vecDiscreteModelExp;
	/* return discrete model expressions */
	public Vector<MathExpression> getM_vecDiscreteModelExp() {
		return m_vecDiscreteModelExp;
	}

	HashMap<MeshCoordinates, String> MorphologyHMap;
	/* return the morphology hash map from RelML */
	public HashMap<MeshCoordinates, String> getM_strMorphologyHMap() {
		return MorphologyHMap;
	}
	HashMap<String, MathExpression> BoundaryExpHMap;
	/* return boundary condition expressions */
	public HashMap<String, MathExpression> getM_vecBoundaryExpHMap() {
		return BoundaryExpHMap;
	}
	
	HashMap<String, MathExpression> DistrParamExpHMap;
	/* return distributed parameter expressions */
	public HashMap<String, MathExpression> getM_vecDistrParamExpHMap() {
		return DistrParamExpHMap;
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
		m_vecDiscreteModelExp = new Vector<MathExpression>();
		MorphologyHMap = new HashMap<MeshCoordinates, String>();
		BoundaryExpHMap = new HashMap<String, MathExpression>();
		DistrParamExpHMap = new HashMap<String, MathExpression>();
		nIDIndex = 0;
		nBoundLocIndex = 1;
		nEquNumber = 0;
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
		
		ParameterMapper discreteEquMapper = new ParameterMapper();
		discreteEquMapper.readParameterCSVFile(m_pRelMLAnalyzer.getMorphologyFileName());

		/* generate the all instance points for the morphology */
		MorphologyHMap = discreteEquMapper.getParameterHMap();
		
		/* Store the CellML model equations for discretization */
		for(int i=0; i<m_pCellMLAnalyzer.getExpressionCount(); i++) {
			MathExpression pNewExp = m_pCellMLAnalyzer.getExpression(i);
			m_vecDiscreteModelExp.add(pNewExp);
		}

		/* create discrete model equations */
		this.createDiscreteExpressions(m_vecDiscreteModelExp);
		
		/* create the boundary conditions */
		this.createDiscreteBoundaryConditions();
		
		/* create the distributed parameters */
		this.createDiscreteDistrParams();
	
	}	
	
	/**
	 * Discretize the target single MathExpression using the TecML file
	 * strAttr are used for the boundary conditions
	 * @throws MathException
	 * @throws TranslateException
	 *
	 */
//	public void createDiscreteSingleExpression(MathExpression targetExpression, String strAttr)
//	throws MathException, TranslateException {
//		
//		for (int i = 0; i < m_pTecMLAnalyzer.getExpressionCount(); i++) {
//			
//			/* Get the current TecML expression boundary location attribute */
//			String strCurrentTecExpBoundLocAttr = m_pTecMLAnalyzer.getAttribute(i)[nBoundLocIndex];
//			
//			/*Get the left and right expression of each equation in TecML*/
//			MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);
//			MathExpression pLeftExp = pMathExp.getLeftExpression();
//			MathExpression pRightExp = pMathExp.getRightExpression();
//			
//			if (pLeftExp == null || pRightExp == null) {
//				throw new TranslateException("SingleStepPDEGenerator","addIndexToVariables",
//							     "failed to parse expression");
//			}
//			
//			/*Convert expression to MathOperand and assume both sides are variables*/
//			MathOperand pLeftVar = (MathOperand)((Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pLeftExp.toLegalString()));
//			
//			/* Check if diffvar and then do differential variable replacement (add index)*/
//			if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {
//				/* Copy TecML diffvar and replace with all the diffvars in RelML */
//				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecDiffVar().size(); j++) {
//					MathOperand pRelMLDiffVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDiffVar().get(j).toLegalString()); //remove "null." part and return MathOperand											
//					MathFactor pIndexedDiffVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLDiffVar);	
//					
//					/* Replace the differential variables in the target equation */
//					targetExpression.replace(pRelMLDiffVar, pIndexedDiffVar);
//				}
//			}
//			
//			/* Check if arithvar and then do differential variable replacement (add index)*/
//			if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
//				/* Copy TecML arithvar and replace with all the arithvars in RelML */
//				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecArithVar().size(); j++) {	
//					MathOperand pRelMLArithVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecArithVar().get(j).toLegalString());					
//					MathFactor pIndexedArithVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLArithVar);	
//				
//					/* Replace the arithmetic variables in the target equation */
//					targetExpression.replace(pRelMLArithVar, pIndexedArithVar);
//				}
//			}
//			
//			/* Check if constvar and then do differential variable replacement (add index)*/
//			if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
//				/* Copy TecML constvar and replace with all the constvars in RelML */
//				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecConstVar().size(); j++) {
//					MathOperand pRelMLConstVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecConstVar().get(j).toLegalString());
//					
//					/* check if the constant is a distributed parameter and if it is, apply discretization else do not change constvar */
//					HashMap<Math_ci, String> DistrParamHMap = m_pRelMLAnalyzer.getM_HashMapDistributedParam();
//					boolean isDistrParam = false;
//					for (Math_ci pVariable: DistrParamHMap.keySet()) {
//						if (pVariable.equals(m_pRelMLAnalyzer.getM_vecConstVar().get(j)))
//							isDistrParam = true;
//					}
//					
//					MathFactor pIndexedConstVar = pRelMLConstVar;
//					if (isDistrParam) {
//						pIndexedConstVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLConstVar);
//					} 
//
//					/* Replace the constants in the target equation */
//					targetExpression.replace(pRelMLConstVar, pIndexedConstVar);
//				}
//			}
//			
//			if (m_pTecMLAnalyzer.isDimensionVar(pLeftVar)) {
//				/* TODO: Insert propagation of equations */
//			}
//			
//			if (m_pTecMLAnalyzer.isDiffOperator(pLeftVar)) {
//				
//				int nDimensionVarNum = m_pRelMLAnalyzer.getM_vecDimensionVar().size();
//
//				/* Copy TecML diffvar and replace with all the diffvars in the differential operator */
//				for (int j = 0; j < m_pRelMLAnalyzer.getM_vecDiffVar().size(); j++) {
//					MathOperand pRelDiffVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDiffVar().get(j).toLegalString());
//					MathOperand pTecDiffVar = (MathOperand)m_pTecMLAnalyzer.getM_vecDiffVar().get(0);
//					
//					MathExpression newLeftExp = pLeftExp.createCopy();
//					MathExpression newRightExp = pRightExp.createCopy();					
//					MathOperator pLeftOptr = (MathOperator)newLeftExp.getRootFactor();
//					MathOperator pRightOptr = (MathOperator)newRightExp.getRootFactor();
//					pLeftOptr.replaceDiffOptrVar(pTecDiffVar, pRelDiffVar);
//					pRightOptr.replace(pTecDiffVar, pRelDiffVar);
//					
//					/* Replace the dimension variables in the TecML differential equations */
//					for (int k = 0; k < nDimensionVarNum; k++) {
//						MathOperand pRelDimVar = (MathOperand)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pRelMLAnalyzer.getM_vecDimensionVar().get(k).toLegalString());
//						MathOperand pTecDimVar = (MathOperand)m_pTecMLAnalyzer.getM_vecDimensionVar().get(k);		
//						pLeftOptr.replaceDiffOptrVar(pTecDimVar, pRelDimVar);
//					}
//					
//					/* Replace the differential operators in the equation (check for boundary attributes) */
//					if (strAttr != null && strCurrentTecExpBoundLocAttr != null) {
//						if (strAttr != null && (strAttr.equals(strCurrentTecExpBoundLocAttr))) {
//							targetExpression.replace(pLeftOptr, pRightOptr);
//						}	
//					} else {
//						targetExpression.replace(pLeftOptr, pRightOptr);
//					}
//				}
//			}
//		}
//	}
	
	/* override for single expression discretization */
//	public void createDiscreteSingleExpression (MathExpression targetExpression)
//	throws MathException, TranslateException {
//	
//		this.createDiscreteSingleExpression(targetExpression, null);
//	}
	
	/**
	 * Discretize the input multiple MathExpressions using the TecML file
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

					/* check if the constant is a distributed parameter and if it is, apply discretization else do not change constvar */
					HashMap<Math_ci, String> DistrParamHMap = m_pRelMLAnalyzer.getM_HashMapDistributedParam();
					boolean isDistrParam = false;
					for (Math_ci pVariable: DistrParamHMap.keySet()) {
						if (pVariable.equals(m_pRelMLAnalyzer.getM_vecConstVar().get(j)))
							isDistrParam = true;
					}
					
					MathFactor pIndexedConstVar = pRelMLConstVar;
					if (isDistrParam) {
						pIndexedConstVar = this.discretizeVariable(pRightExp, pLeftVar, pRelMLConstVar);
					} 
					
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
		Vector<MathExpression> m_vecRelMLBoundExp = new Vector<MathExpression>();
		
		/* if the attribute has a location, means its a boundary equation,
		 * else the equation is a distributed parameter equation */
		for (int i=0; i<m_pRelMLAnalyzer.getExpressionCount(); i++) {
			String[] strRelMLExpAttr = m_pRelMLAnalyzer.getAttribute(i);
//			System.out.println(Arrays.toString(strRelMLExpAttr));
			strBoundLocAttr[i] = strRelMLExpAttr[nBoundLocIndex];
			if (strBoundLocAttr[i] != null) {
				m_vecRelMLBoundExp.add(m_pRelMLAnalyzer.getExpression(i));
			} else {
				DistrParamExpHMap.put(m_pRelMLAnalyzer.getAttribute(i)[nIDIndex], m_pRelMLAnalyzer.getExpression(i));
//				System.out.println(m_pRelMLAnalyzer.getExpression(i).getLeftExpression().toLegalString());
			}
		}
		
		/* discretize the Boundary equations */
		this.createDiscreteExpressions(m_vecRelMLBoundExp, strBoundLocAttr);
		
		
		for (int i=0; i<m_vecRelMLBoundExp.size(); i++) {
			String strBoundIDAttr = m_pRelMLAnalyzer.getAttribute(i)[nIDIndex];
			BoundaryExpHMap.put(strBoundIDAttr, m_vecRelMLBoundExp.get(i));
		}
	}
	
	/**
	 * Add the distributed parameters from RelML
	 * @return:
	 */
	public void createDiscreteDistrParams() 
	throws MathException, TranslateException {
		
		HashMap<Math_ci, String> DistrParamHMap = m_pRelMLAnalyzer.getM_HashMapDistributedParam();
		Vector<MathExpression> m_vecDistrParamExp = new Vector<MathExpression>();
		
		for (Math_ci pVariable: DistrParamHMap.keySet()) {
			String strDistrParamKey = DistrParamHMap.get(pVariable);
			MathFactor pDistrParamFactor = this.addSpatialIndexToVariable(pVariable);
			Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
			pMathEq.addFactor(pDistrParamFactor);
			pMathEq.addFactor(pDistrParamFactor);
			MathExpression pNewExp = new MathExpression(pMathEq);
			
			/* if the distributed parameter variable does contains a relml equation, discretize that equation. else, add new parameter expression to hashmap */
			if (DistrParamExpHMap.containsKey(strDistrParamKey)){
//				this.createDiscreteSingleExpression(DistrParamExpHMap.get(strDistrParamKey));
				m_vecDistrParamExp.add(DistrParamExpHMap.get(strDistrParamKey));
			} else{
				DistrParamExpHMap.put(DistrParamHMap.get(pVariable), pNewExp);
			}

		}
		
		this.createDiscreteExpressions(m_vecDistrParamExp);
		
	}
	
	/*****
	 * Generate the discretized equations without expansion 
	 * 
	 * *****/
	public void generateRecMLDiscreteEquations(String strFileName) 
	throws MathException, IOException, GraphException {
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);
		
		/* add the discrete model equations */
		for (int i=0; i<m_vecDiscreteModelExp.size(); i++) {
			/* insert equation number attribute in equation */
			MathExpression pNewModelExpInstance = m_vecDiscreteModelExp.get(i).createCopy();
			HashMap<String, String> HMapEquNum = new HashMap<String, String>();
			HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
			pNewModelExpInstance.addAttribute(HMapEquNum);
			this.incrementEquNum();
			
			pw.print(pNewModelExpInstance.toMathMLString() + "\n");
			System.out.println(pNewModelExpInstance.toLegalString());
		}
		
		/* add the instances for boundary conditions */
		System.out.println("\n --------------------------- Spatial Boundary locations and instances:");
		this.generateBoundaryExpSpatialInstances(pw);
		pw.flush();
		
		/* add the instances for distributed parameters */
		System.out.println("\n --------------------------- Distributed parameter instances:");
		this.generateDistrParamExpInstances(pw);
		
		//Close the Print Writer
		pw.close();
	       
		//Close the File Writer
		fw.close();   
	}
	

	/*****
	 * Generate the equations for instances of space only
	 * using the csv files
	 * *****/
	public void generateRecMLSpatialExpansionEquations(String strFileName) 
	throws MathException, GraphException, IOException, TranslateException {
		
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);

		   
		/* add the instances for model equations */		
		System.out.println("\n --------------------------- Equation Spatial instances:");
		this.generateDiscreteExpSpatialInstances(pw);
		pw.flush();
		
		/* add the instances for boundary conditions */
		System.out.println("\n --------------------------- Boundary Spatial locations and instances:");
		this.generateBoundaryExpSpatialInstances(pw);
		pw.flush();
		
		/* add the instances for distributed parameters */
		System.out.println("\n --------------------------- Distributed parameter instances:");
		this.generateDistrParamExpInstances(pw);
		
	   //Close the Print Writer
	   pw.close();
	       
	   //Close the File Writer
	   fw.close();   
		
	}
	
	/*****
	 * Generate the equations for all instances of time and space
	 * using the csv files
	 * *****/
	public void generateRecMLAllExpansionEquations(String strFileName) 
	throws MathException, GraphException, IOException, TranslateException {
		
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);

		   
		/* add the instances for model equations */		
		System.out.println("\n --------------------------- Equation instances:");
		this.generateDiscreteExpInstances(pw);
		pw.flush();
		
		/* add the instances for boundary conditions */
		System.out.println("\n --------------------------- All Boundary locations and instances:");
		this.generateBoundaryExpInstances(pw);
		pw.flush();
		
		/* add the instances for distributed parameters */
		System.out.println("\n --------------------------- Distributed parameter instances:");
		this.generateDistrParamExpInstances(pw);
		
	   //Close the Print Writer
	   pw.close();
	       
	   //Close the File Writer
	   fw.close();   
		
	}
	
	/*****
	 * Generate the model equations for all instances of time and space
	 * and append the MathML equations to StringBuilder
	 * 
	 * @return: m_strMathExpInstances.append(boundaryMathMLString)
	 * *****/
	public void generateDiscreteExpInstances(PrintWriter pw) 
	throws MathException, GraphException {
		
		/* Combine the time steps with the spatial coordinates*/
		int nTimeSteps = maxTime;
		int[] morphologyInstance = new int[m_pRelMLAnalyzer.getM_vecIndexVar().size()];
		for (int i=0; i<nTimeSteps; i++) {
			morphologyInstance[0] = i;
			
			/* add the spatial coordinates to index instance */
			for (MeshCoordinates coor: MorphologyHMap.keySet()) {
				
				for (int j=1; j<morphologyInstance.length; j++) {
					morphologyInstance[j] = coor.getCoordinate(j-1);
				}
				if (Integer.parseInt(MorphologyHMap.get(coor)) != 0) {
					for (int k=0; k<m_vecDiscreteModelExp.size(); k++) {
						MathExpression pNewModelExpInstance = m_vecDiscreteModelExp.get(k).createCopy();
						this.generateExpInstance(pNewModelExpInstance, morphologyInstance, true);
						
						/* insert equation number attribute in equation */
						HashMap<String, String> HMapEquNum = new HashMap<String, String>();
						HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
						pNewModelExpInstance.addAttribute(HMapEquNum);
						this.incrementEquNum();
						
						pw.print(pNewModelExpInstance.toMathMLString() + "\n");
						System.out.println(pNewModelExpInstance.toLegalString());
					}
				}
			}
		}

	}
	
	/*****
	 * Generate the model equations for all instances except time
	 * and append the MathML equations to StringBuilder
	 * 
	 * @return: m_strMathExpInstances.append(boundaryMathMLString)
	 * *****/
	public void generateDiscreteExpSpatialInstances(PrintWriter pw) 
	throws MathException, GraphException {
		
		/* Combine the time steps with the spatial coordinates*/
		int[] morphologyInstance = new int[m_pRelMLAnalyzer.getM_vecIndexVar().size() - 1];
			
		/* add the spatial coordinates to index instance */
		for (MeshCoordinates coor: MorphologyHMap.keySet()) {
			
			for (int j=0; j<morphologyInstance.length; j++) {
				morphologyInstance[j] = coor.getCoordinate(j);
			}
			if (Integer.parseInt(MorphologyHMap.get(coor)) != 0) {
				for (int k=0; k<m_vecDiscreteModelExp.size(); k++) {
					MathExpression pNewModelExpInstance = m_vecDiscreteModelExp.get(k).createCopy();
					this.generateExpInstance(pNewModelExpInstance, morphologyInstance, false);
					
					/* insert equation number attribute in equation */
					HashMap<String, String> HMapEquNum = new HashMap<String, String>();
					HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
					pNewModelExpInstance.addAttribute(HMapEquNum);
					this.incrementEquNum();
					
					pw.print(pNewModelExpInstance.toMathMLString() + "\n");
					System.out.println(pNewModelExpInstance.toLegalString());
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
	public void generateBoundaryExpInstances(PrintWriter pw)
	throws MathException, GraphException {
		
		/* get the boundary file and locations */ 
		int nTimeSteps = maxTime;
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
					int nBoundaryID = Integer.parseInt(BoundedVarHMap.get(coor));
					if (nBoundaryID != 0) {	
						PrimeFactors primeBoundID = new PrimeFactors();
						if (primeBoundID.isPrime(nBoundaryID)) {
							MathExpression pNewBoundaryInstance = BoundaryExpHMap.get(BoundedVarHMap.get(coor)).createCopy();
							this.generateExpInstance(pNewBoundaryInstance, boundInstance, true);
							
							/* insert equation number attribute in equation */
							HashMap<String, String> HMapEquNum = new HashMap<String, String>();
							HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
							pNewBoundaryInstance.addAttribute(HMapEquNum);
							this.incrementEquNum();
							
							pw.print(pNewBoundaryInstance.toMathMLString() + "\n");
//							System.out.println(pNewBoundaryInstance.toLegalString());
						} else {
							List<Integer> boundaryIDsList = primeBoundID.primeFactors(nBoundaryID);
							for (int l=0; l<boundaryIDsList.size(); l++) {
								MathExpression pNewBoundaryInstance = BoundaryExpHMap.get(Integer.toString(boundaryIDsList.get(l))).createCopy();
								this.generateExpInstance(pNewBoundaryInstance, boundInstance, true);

								/* insert equation number attribute in equation */
								HashMap<String, String> HMapEquNum = new HashMap<String, String>();
								HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
								pNewBoundaryInstance.addAttribute(HMapEquNum);
								this.incrementEquNum();
								
								pw.print(pNewBoundaryInstance.toMathMLString() + "\n");
//								System.out.println(pNewBoundaryInstance.toLegalString());
							}
						}
					}
					
				}
					
			}

		}

	}
	
	/*****
	  * Generate the equations of boundary locations for all instances except time 
	  * indicated in the boundary maps and append the MathML equations to StringBuilder
	  * 
	  * @return: m_strMathExpInstances.append(boundaryMathMLString)
	 * *****/
	public void generateBoundaryExpSpatialInstances(PrintWriter pw)
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
			int[] boundInstance = new int[m_pRelMLAnalyzer.getM_vecIndexVar().size() - 1];
			
			/* add the spatial coordinates to index instance */
			/* check if the boundary-id is a prime id (has one boundary) or a combination of 2 or more boundaries */
			for (MeshCoordinates coor : BoundedVarHMap.keySet()) {
				for (int j=0; j<boundInstance.length; j++) {
					boundInstance[j] = coor.getCoordinate(j);
				}
				int nBoundaryID = Integer.parseInt(BoundedVarHMap.get(coor));
				if (nBoundaryID != 0) {					
					PrimeFactors primeBoundID = new PrimeFactors();
					if (primeBoundID.isPrime(nBoundaryID)) {
						MathExpression pNewBoundaryInstance = BoundaryExpHMap.get(BoundedVarHMap.get(coor)).createCopy();
						this.generateExpInstance(pNewBoundaryInstance, boundInstance, false);

						/* insert equation number attribute in equation */
						HashMap<String, String> HMapEquNum = new HashMap<String, String>();
						HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
						pNewBoundaryInstance.addAttribute(HMapEquNum);
						this.incrementEquNum();
						
						pw.print(pNewBoundaryInstance.toMathMLString() + "\n");
//						System.out.println(pNewBoundaryInstance.toLegalString());
					} else {
						List<Integer> boundaryIDsList = primeBoundID.primeFactors(nBoundaryID);
						for (int l=0; l<boundaryIDsList.size(); l++) {
							MathExpression pNewBoundaryInstance = BoundaryExpHMap.get(Integer.toString(boundaryIDsList.get(l))).createCopy();
							this.generateExpInstance(pNewBoundaryInstance, boundInstance, false);

							/* insert equation number attribute in equation */
							HashMap<String, String> HMapEquNum = new HashMap<String, String>();
							HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
							pNewBoundaryInstance.addAttribute(HMapEquNum);
							this.incrementEquNum();
							
							pw.print(pNewBoundaryInstance.toMathMLString() + "\n");
//							System.out.println(pNewBoundaryInstance.toLegalString());
						}
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
	public void generateDistrParamExpInstances(PrintWriter pw)
	throws MathException, GraphException {

		/* set the number of the parameter indices = number of spatial indices */
		int[] coordInstance = new int[(m_pRelMLAnalyzer.getM_vecIndexVar().size() - 1)];
		
		/* get the distributed parameter file and/or parameter equations */
		for (String strParamKey: DistrParamExpHMap.keySet()) {
			
			/* check if the variable has a parameter-id (parsable) or csv parameter file (unparsable) */
			boolean parsable = true;
			try{
			Integer.parseInt(strParamKey);

			}catch(NumberFormatException e){
			parsable = false;
			}
			
			if (parsable) {
									
				/* add the spatial coordinates to index instance */
				for (MeshCoordinates coor: MorphologyHMap.keySet()) {
					
					for (int j=0; j<coordInstance.length; j++) {
						coordInstance[j] = coor.getCoordinate(j);
					}
					/* replace all parameter variables with instance in the distributed parameter equation */
					if (Integer.parseInt(MorphologyHMap.get(coor)) != 0) {
							MathExpression pNewParamExpInstance = DistrParamExpHMap.get(strParamKey).createCopy();
							this.generateExpSpatialInstance(pNewParamExpInstance, coordInstance);	

							/* insert equation number attribute in equation */
							HashMap<String, String> HMapEquNum = new HashMap<String, String>();
							HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
							pNewParamExpInstance.addAttribute(HMapEquNum);
							this.incrementEquNum();
							
							pw.print(pNewParamExpInstance.toMathMLString() + "\n");
							System.out.println(pNewParamExpInstance.toLegalString());
					}
				}

			} else {
				
				ParameterMapper distrParamMapper = new ParameterMapper();
				distrParamMapper.readParameterCSVFile(strParamKey);
				
				/* generate the distributed parameter values for all instances */
				HashMap<MeshCoordinates, String> DistributedVarHMap = new HashMap<MeshCoordinates, String>();
				DistributedVarHMap = distrParamMapper.getParameterHMap();
				
				/* add the spatial coordinates to index instance */
				for (MeshCoordinates coor : DistributedVarHMap.keySet()) {
					for (int j=0; j<coordInstance.length; j++) {
						coordInstance[j] = coor.getCoordinate(j);
					}
					MathExpression pLeftExp =  DistrParamExpHMap.get(strParamKey).createCopy().getLeftExpression();
					
					/* replace all parameter variables with instance value */
					Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
					Math_cn paramValue = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, DistributedVarHMap.get(coor));
					pMathEq.addFactor(pLeftExp.getRootFactor());
					pMathEq.addFactor(paramValue);
					MathExpression pNewParamExpInstance = new MathExpression(pMathEq);
					this.generateExpSpatialInstance(pLeftExp, coordInstance);

					/* insert equation number attribute in equation */
					HashMap<String, String> HMapEquNum = new HashMap<String, String>();
					HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
					pNewParamExpInstance.addAttribute(HMapEquNum);
					this.incrementEquNum();
					
					pw.print(pNewParamExpInstance.toMathMLString() + "\n");
					System.out.println(pNewParamExpInstance.toLegalString());
					
				}
			}
		}
		
	}
	
	/*****
	 * Generate an instance of the equation for index values (n,j,l...)
	 * input: MathExpression with index
	 * output: MathExpression with index instance
	 * *****/
	public void generateExpInstance(MathExpression pExp, int[] pIndexValues, boolean bWithTimeIndex)
	throws MathException {
		
		if (bWithTimeIndex) {		
			if (m_pRelMLAnalyzer.getM_vecIndexVar().size() != pIndexValues.length) {
				throw new MathException("SingleStepPDEGenerator","generateExpressionInstance",
							     "number of indices and indices instance does not match");
			}
			
			for (int i=0; i<pIndexValues.length; i++) {	
				Math_cn pIndexInstance = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, Integer.toString(pIndexValues[i]));
				pIndexInstance.changeType();
				pExp.replace(m_pRelMLAnalyzer.getM_vecIndexVar().get(i), (MathOperand)pIndexInstance);
			}
		} else {
			if ((m_pRelMLAnalyzer.getM_vecIndexVar().size() - 1) != pIndexValues.length) {
				throw new MathException("SingleStepPDEGenerator","generateExpInstance",
							     "number of spatial indices does not match");
			}
			
			for (int i=0; i<pIndexValues.length; i++) {	
				Math_cn pIndexInstance = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, Integer.toString(pIndexValues[i]));
				pIndexInstance.changeType();
				pExp.replace(m_pRelMLAnalyzer.getM_vecIndexVar().get(i+1), (MathOperand)pIndexInstance);
			}
		}
			
	}
	
	public void generateExpSpatialInstance(MathExpression pExp, int[] pIndexValues)
	throws MathException {

		if ((m_pRelMLAnalyzer.getM_vecIndexVar().size() - 1) != pIndexValues.length) {
			throw new MathException("SingleStepPDEGenerator","generateExpSpatialInstance",
						     "number of spatail indices does not match");
		}
		
		for (int i=0; i<pIndexValues.length; i++) {	
			Math_cn pIndexInstance = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, Integer.toString(pIndexValues[i]));
			pIndexInstance.changeType();
			pExp.replace(m_pRelMLAnalyzer.getM_vecIndexVar().get(i+1), (MathOperand)pIndexInstance);
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
	
	/**
	 * Add spatial indices to variable
	 * @return Math_ci: discretized variable
	 */
	public MathFactor addSpatialIndexToVariable(Math_ci pVariable) 
	throws MathException {
		
		Math_selector pMathSelector = (Math_selector)MathFactory.createOperator(eMathOperator.MOP_SELECTOR);
		pMathSelector.addFactor(pVariable);
		for (int i=1; i<m_pRelMLAnalyzer.getM_vecIndexVar().size(); i++) {
			Math_ci pIndexVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecIndexVar().get(i).toLegalString());
			pMathSelector.addFactor(pIndexVar);
		}
		
		return pMathSelector;
	}
	
	/* TODO: */
	public void writeToSimpleRecMLFile(String strRecMLFileName, int nExpandOption) 
	throws MathException, GraphException, TranslateException, IOException {
		SimpleRecMLWriter pSimpleRecMLWriter = new SimpleRecMLWriter();
		
		int nExpansionType = 0;

		/* create the SimpleRecML file and insert variable declarations */
		pSimpleRecMLWriter.createSimpleRecMLFile(strRecMLFileName);
		pSimpleRecMLWriter.appendSimpleRecMLDeclarations(strRecMLFileName, m_pRelMLAnalyzer);
		
		/* insert the initial value conditions of diffvars and consts */
//		System.out.println("\n --------------------------- Initial values:");
//		HashMap<Math_ci, String> m_HMapInitValues= new HashMap<Math_ci, String>(); 		
//		m_HMapInitValues = m_pRelMLAnalyzer.getM_HashMapInitialValues();
//		this.insertInitDeclarations(strRecMLFileName, m_HMapInitValues);
		
		/* generate the equation instances and append to simple recml file (check the type of expansion*/
		nExpansionType = nExpandOption;
		if (nExpansionType == 0) { // no expansion
			this.generateRecMLDiscreteEquations(strRecMLFileName);
		} else if (nExpansionType == 1) { // spatial expansion
			this.generateRecMLSpatialExpansionEquations(strRecMLFileName);
		} else if (nExpansionType == 2) { // all expansion
			this.generateRecMLAllExpansionEquations(strRecMLFileName);	
		}
		
		/* change the variable names in the equations to remove the dot (from component name) */
		pSimpleRecMLWriter.changeRecMLVarNames(strRecMLFileName);
		pSimpleRecMLWriter.closeSimpleRecMLFile(strRecMLFileName);
	}
	
	/*****
	 * Insert the initial value declarations
	 * @return: mathml text of differential variables and constants and their respective initial value.
	 * *****/
	public void insertInitDeclarations(String strFileName, HashMap<Math_ci, String> HashMapInitValueList) 
	throws IOException, MathException {
		FileWriter fw = new FileWriter(strFileName, true);
		PrintWriter pw = new PrintWriter(fw);
		
		for (Math_ci varName: HashMapInitValueList.keySet()) {
			if (HashMapInitValueList.get(varName) != null) {  
				Math_cn pInitValue = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, HashMapInitValueList.get(varName));
				Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
				pMathEq.addFactor(varName);
				pMathEq.addFactor(pInitValue);
				
				MathExpression pNewInitExp = new MathExpression(pMathEq);
				
				/* insert equation number attribute in equation */
				HashMap<String, String> HMapEquNum = new HashMap<String, String>();
				HMapEquNum.put(strNumAttr, Integer.toString(nEquNumber));
				pNewInitExp.addAttribute(HMapEquNum);
				this.incrementEquNum();
				
				pw.print(pNewInitExp.toMathMLString() + "\n");
				System.out.println(pNewInitExp.toLegalString());
			}
		}
		
		pw.flush();
		//Close the Print Writer
		pw.close();
		//Close the File Writer
		fw.close();   
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
		System.out.println("[Discrete Model and Boundary Condition Equations]------------------------------------");

		System.out.println("\n --------------------------- Model equations:");
		for(int k=0; k<m_vecDiscreteModelExp.size(); k++) {
			System.out.println(m_vecDiscreteModelExp.get(k).toLegalString());
		}
		
		System.out.println("\n --------------------------- Boundary conditions:");
		for (String strBoundaryLoc : BoundaryExpHMap.keySet()) {
			System.out.println("Loc " + strBoundaryLoc + " Exp: " + BoundaryExpHMap.get(strBoundaryLoc).toLegalString());
		}
		
		System.out.println("\n --------------------------- Distributed parameters:");
		for (String strDistrParamID : DistrParamExpHMap.keySet()) {
			System.out.println("Key " + strDistrParamID + " Exp: " + DistrParamExpHMap.get(strDistrParamID).toLegalString());
		}

		/*改行*/
		System.out.println();
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
	

	/***** Main program for testing *****/
	public static void main(String[] args) 
	throws GraphException, MathException {
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
			e.printStackTrace();
		} catch (SAXException e) {
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
		
		int[] spaceDimension = pRelMLAnalyzer.getDimensions();
//		int[] spacing = pRelMLAnalyzer.getSpacing();
//		System.out.println(Arrays.toString(spaceDimension));
//		System.out.println(Arrays.toString(spacing));
		
		
		int nExpressionNum = pCellMLAnalyzer.getExpressionCount();
		System.out.println("CellML Exp count: " + nExpressionNum);
		
		/* get the variables of the CellML expressions and store it in a hashset (avoid duplicates)*/
//		HashSet<String> modelVarSet = new HashSet<String>();	
//		for (int i=0; i<nExpressionNum; i++) {
//			Vector<Math_ci> expVars = new Vector<Math_ci>();
//			pCellMLAnalyzer.getExpression(i).getAllVariables(expVars);
//			String varList = "";
//			for (int j=0; j<expVars.size(); j++) {
//				modelVarSet.add(expVars.get(j).toLegalString());
//				varList += expVars.get(j).toLegalString() + ", ";
//			}
//			System.out.println(varList);
//		}
//		VariableListPrinter printVars = new VariableListPrinter();
//		printVars.printRelMLVarList(modelVarSet);
		
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
		
		
		//Variables for testing the instantiation of simulation equations with time and a 2D array
		int maxTime = 2;
		

//		String strRecMLFileName = "FHN_FTCS_SimpleRecML" + Integer.toString(maxTime) + "x" + Integer.toString(spaceDimension[0]) + "x" + Integer.toString(spaceDimension[1]) +".recml";
		String strRecMLFileName = "FHN_FTCS_SimpleRecML_DiscreteOnly" + Integer.toString(spaceDimension[0]) + "x" + Integer.toString(spaceDimension[1]) +".recml";
		long start = System.currentTimeMillis();
		StringBuilder strRecMLMathExp = new StringBuilder();
		try {
			pSingleStepGenerator.setMaxTime(maxTime);
			pSingleStepGenerator.discretizeCellML();
			
			System.out.println("[output]------------------------------------");
			/* print the model, boundary and distributed parameter equations */
//			pSingleStepGenerator.printContents();
				
		} catch (MathException e1) {
			e1.printStackTrace();
		} catch (CellMLException e1) {
			e1.printStackTrace();
		} catch (RelMLException e1) {
			e1.printStackTrace();
		}  catch (TranslateException e1) {
			e1.printStackTrace();
		} 
		
		
		try {
			/* Generate the SimpleRecML file */
//			pSingleStepGenerator.generateAllInstanceEquations(strRecMLFileName);
			pSingleStepGenerator.writeToSimpleRecMLFile(strRecMLFileName, 0);
		
			
		} catch (MathException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (TranslateException e1) {
			e1.printStackTrace();
		}
		
		
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
//		System.out.println("Execution time: "+ (end-start) +" ms.");
		

//		try {
//			pRelMLAnalyzer.printContents();
//		} catch (MathException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			pTecMLAnalyzer.printContents();
//		} catch (MathException e) {
//			e.printStackTrace();
//		}
		
	}

}
