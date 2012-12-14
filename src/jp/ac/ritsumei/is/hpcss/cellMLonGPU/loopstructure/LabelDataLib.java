package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;


public class LabelDataLib extends SimpleRecMLAnalyzer{

	HashMap<Integer, HashMap<String, Integer>> m_HashMapNodeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapNodeList() {
		return  m_HashMapNodeList;
	}
	
	HashMap<Integer, HashMap<String, Integer>> m_HashMapEdgeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapEdgeList() {
		return  m_HashMapEdgeList;
	}	
	
	HashMap<Integer, HashMap<String, Integer>> m_HashMapCloneNodeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapCloneNodeList() {
		return  m_HashMapCloneNodeList;
	}
	public void setM_HashMapCloneNodeList(HashMap<Integer, HashMap<String, Integer>> list) {
		m_HashMapCloneNodeList = list;
	}
	
	HashMap<Integer, HashMap<String, Integer>> m_HashMapCloneEdgeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapCloneEdgeList() {
		return  m_HashMapCloneEdgeList;
	}
	public void setM_HashMapCloneEdgeList(HashMap<Integer, HashMap<String, Integer>> list) {
		m_HashMapCloneEdgeList = list;
	}
	
	Vector<Integer> m_VecEquOder;
	public Vector<Integer> getM_VecEquOder() {
		return  m_VecEquOder;
	}
	
	HashMap<Integer, Integer> m_finalAttrEquNumLists;
	public HashMap<Integer, Integer> getm_finalAttrEquNumLists() {
		return  m_finalAttrEquNumLists;
	}
	
	HashMap<Integer, Integer> m_initendAttrEquNumLists;
	public HashMap<Integer, Integer> getm_initendAttrEquNumLists() {
		return  m_initendAttrEquNumLists;
	}
	
	HashMap<Integer, Integer> m_condrefAttrEquNumLists;
	public HashMap<Integer, Integer> getm_condrefAttrEquNumLists() {
		return  m_condrefAttrEquNumLists;
	}
	
	HashMap<Integer, String> m_indexList;
	public HashMap<Integer, String> getM_IndexList() {
		return  m_indexList;
	}
	
	HashMap<Integer, HashMap<Integer, String>> m_AttrLists;
	public HashMap<Integer, HashMap<Integer, String>> getM_AttrLists() {
		return  m_AttrLists;
	}
	public void setM_AttrLists(HashMap<Integer, HashMap<Integer, String>> attr) {
		m_AttrLists = attr;
	}
	
	ArrayList<RelationPattern> m_LoopStructure;
	
	public LabelDataLib(
			HashMap<Integer, HashMap<String, Integer>> NodeHashMap,
			HashMap<Integer, HashMap<String, Integer>> EdgeHashMap,
			Vector<Integer> EquOderVec,
			HashMap<Integer, Integer> finalAttr,
			HashMap<Integer, Integer> initendAttr,
			HashMap<Integer, Integer> CondrefAttr,
			HashMap<Integer, String> indexList){

		m_HashMapNodeList = NodeHashMap;
		m_HashMapCloneNodeList = (HashMap<Integer, HashMap<String, Integer>>) NodeHashMap.clone();
		
		m_HashMapEdgeList = EdgeHashMap;
		m_HashMapCloneEdgeList = (HashMap<Integer, HashMap<String, Integer>>) EdgeHashMap.clone();
		
		m_VecEquOder = EquOderVec;
		
		m_finalAttrEquNumLists = finalAttr;
		m_initendAttrEquNumLists = initendAttr;
		m_condrefAttrEquNumLists = CondrefAttr;
		m_indexList = indexList;
		
		m_AttrLists = new HashMap<Integer, HashMap<Integer, String>>();
		
		m_LoopStructure = new ArrayList<RelationPattern>();

	}

	
	
	
}
