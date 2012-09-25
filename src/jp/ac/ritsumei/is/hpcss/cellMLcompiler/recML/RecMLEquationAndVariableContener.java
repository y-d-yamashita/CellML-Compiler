package jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;

/**
 * Get variables and equations id to create graph
 * @author 
 *
 */
public class RecMLEquationAndVariableContener {

	private RecMLVariableTable recMLVariableTable;
	private RecMLAnalyzer recMLAnalyzer;
	
	public RecMLEquationAndVariableContener(RecMLAnalyzer analyzer,RecMLVariableTable table){
		recMLVariableTable=table;
		recMLAnalyzer=analyzer;
	}
	/**
	 * Get equations IDs
	 * @return equation ID list
	 */
	public List<Integer> getEquationIDs(){
		List<Integer> idList = new ArrayList<Integer>();
		
		for(int i =0;i<recMLAnalyzer.getExpressionCount();i++)
			idList.add(i);
			
		return idList;
	}
	
	
	/**
	 * Get variable IDs
	 * @return variable ID list
	 */
	public List<Integer> getVariableIDs(){
		List<Integer> idList = new ArrayList<Integer>();
		
		for(int i=0;i<recMLVariableTable.getVariableCount();i++)
			idList.add(i);
		
		return idList;
	}
	
	
	/**
	 * Get variable IDs used in a equation
	 * @param equationID
	 * @return variable ID list
	 */
	public List<List<Integer>> getVariableIDsOfEquation(int equationID){
		List<List<Integer>> idListList = new ArrayList<List<Integer>>();
		List<RecMLVariableReference> m_mapList = new ArrayList<RecMLVariableReference>();
		m_mapList.addAll(recMLVariableTable.values());
		Collections.sort( m_mapList);
		for(RecMLVariableReference r : m_mapList){
			List<Integer> idList = new ArrayList<Integer>();
	
		}
		
		return idList;		
	}

}
