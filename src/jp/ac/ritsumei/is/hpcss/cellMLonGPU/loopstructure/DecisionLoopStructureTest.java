package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

/**
 * This class is the test to decide Loop Structure.
 *  
 * @author n-washio
 *
 */


import java.util.*;

public class DecisionLoopStructureTest {
	public static void main(String[] args) {
		//---------------------------------------------------
		//テスト　main　クラス
		//入力：	属性情報リストHashMap<Integer,HashMap<String,String>> AttrList
		//---------------------------------------------------		

		HashMap<Integer,HashMap<Integer,String>> AttrList = new HashMap<Integer,HashMap<Integer,String>>();
		HashMap<Integer,String> loopAttrList;
		
		loopAttrList = new HashMap<Integer,String>();
		
		//sample0
	/*
		AttrList.put(1, loopAttrList);
		loopAttrList.put("n1", "init");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(2, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(3, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(4, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(5, loopAttrList);
		loopAttrList.put("n1", "final");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(6, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "init");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(7, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(8, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(9, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(10, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "final");loopAttrList.put("n3", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(11, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "init");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(12, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(13, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");

		loopAttrList = new HashMap<String,String>();
		AttrList.put(14, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(15, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "final");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(16, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "init");loopAttrList.put("n3", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(17, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(18, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(19, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(20, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "final");loopAttrList.put("n3", "null");
	*/
		
		//sample1
	/*
		AttrList.put(1, loopAttrList);
		loopAttrList.put("n1", "init");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(2, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(3, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(4, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(5, loopAttrList);
		loopAttrList.put("n1", "final");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(6, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "init");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(7, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(8, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(9, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(10, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "final");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(11, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "init");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(12, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(13, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");

		loopAttrList = new HashMap<String,String>();
		AttrList.put(14, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(15, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "final");loopAttrList.put("n4", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(16, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "init");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(17, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(18, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(19, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(20, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "final");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(21, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "init");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(22, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(23, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(24, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(25, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "final");
	*/	
	
	
		//sample2
		AttrList.put(1, loopAttrList);
		loopAttrList.put(0, "init");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "pre");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(2, loopAttrList);
		loopAttrList.put(0, "inner");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "pre");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(3, loopAttrList);
		loopAttrList.put(0, "inner");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "pre");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(4, loopAttrList);
		loopAttrList.put(0, "inner");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "pre");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(5, loopAttrList);
		loopAttrList.put(0, "final");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "pre");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(6, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "init");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(7, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(8, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "post");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(9, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "post");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(10, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "final");loopAttrList.put(2, "post");loopAttrList.put(3, "post");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(11, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "init");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(12, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "inner");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(13, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "inner");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");

		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(14, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "inner");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(15, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "final");loopAttrList.put(3, "pre");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(16, loopAttrList);
		loopAttrList.put(0, "null");loopAttrList.put(1, "init");loopAttrList.put(2, "null");loopAttrList.put(3, "null");loopAttrList.put(4, "null");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(17, loopAttrList);
		loopAttrList.put(0, "null");loopAttrList.put(1, "inner");loopAttrList.put(2, "null");loopAttrList.put(3, "null");loopAttrList.put(4, "null");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(18, loopAttrList);
		loopAttrList.put(0, "null");loopAttrList.put(1, "inner");loopAttrList.put(2, "null");loopAttrList.put(3, "null");loopAttrList.put(4, "null");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(19, loopAttrList);
		loopAttrList.put(0, "null");loopAttrList.put(1, "inner");loopAttrList.put(2, "null");loopAttrList.put(3, "null");loopAttrList.put(4, "null");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(20, loopAttrList);
		loopAttrList.put(0, "null");loopAttrList.put(1, "final");loopAttrList.put(2, "null");loopAttrList.put(3, "null");loopAttrList.put(4, "null");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(21, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "init");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(22, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "inner");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(23, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "inner");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(24, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "inner");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(25, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "inner");loopAttrList.put(2, "post");loopAttrList.put(3, "final");loopAttrList.put(4, "post");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(26, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "init");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(27, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "inner");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(28, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "inner");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(29, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "inner");
		
		loopAttrList = new HashMap<Integer,String>();
		AttrList.put(30, loopAttrList);
		loopAttrList.put(0, "post");loopAttrList.put(1, "pre");loopAttrList.put(2, "pre");loopAttrList.put(3, "pre");loopAttrList.put(4, "final");
		
	
	/*
		//sample3
		AttrList.put(1, loopAttrList);
		loopAttrList.put("n1", "init");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(2, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(3, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(4, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(5, loopAttrList);
		loopAttrList.put("n1", "final");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(6, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "init");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(7, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(8, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(9, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(10, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "final");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(11, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "init");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(12, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(13, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");

		loopAttrList = new HashMap<String,String>();
		AttrList.put(14, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(15, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "final");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(16, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "init");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(17, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(18, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(19, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(20, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "final");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(21, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "init");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(22, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(23, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(24, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(25, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "final");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(26, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "init");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(27, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(28, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(29, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(30, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "final");loopAttrList.put("n6", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(31, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "init");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(32, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(33, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(34, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(35, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "final");
	*/	
	/*	
		//sample4
		AttrList.put(1, loopAttrList);
		loopAttrList.put("n1", "init");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(2, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(3, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(4, loopAttrList);
		loopAttrList.put("n1", "inner");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(5, loopAttrList);
		loopAttrList.put("n1", "final");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "pre");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(6, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "init");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(7, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(8, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(9, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(10, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "final");loopAttrList.put("n3", "post");loopAttrList.put("n4", "post");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(11, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "init");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(12, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(13, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");

		loopAttrList = new HashMap<String,String>();
		AttrList.put(14, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "inner");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(15, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "final");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(16, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "init");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");loopAttrList.put("n7", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(17, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");loopAttrList.put("n7", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(18, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");loopAttrList.put("n7", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(19, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");loopAttrList.put("n7", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(20, loopAttrList);
		loopAttrList.put("n1", "null");loopAttrList.put("n2", "final");loopAttrList.put("n3", "null");loopAttrList.put("n4", "null");loopAttrList.put("n5", "null");loopAttrList.put("n6", "null");loopAttrList.put("n7", "null");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(21, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "init");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(22, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(23, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(24, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(25, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "final");loopAttrList.put("n5", "post");loopAttrList.put("n6", "post");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(26, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "init");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(27, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(28, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(29, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "inner");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(30, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "pre");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "final");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "pre");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(31, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "init");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(32, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(33, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(34, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "inner");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(35, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "post");loopAttrList.put("n4", "inner");loopAttrList.put("n5", "post");loopAttrList.put("n6", "final");loopAttrList.put("n7", "post");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(36, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "init");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(37, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(38, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(39, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "inner");
		
		loopAttrList = new HashMap<String,String>();
		AttrList.put(40, loopAttrList);
		loopAttrList.put("n1", "post");loopAttrList.put("n2", "inner");loopAttrList.put("n3", "pre");loopAttrList.put("n4", "pre");loopAttrList.put("n5", "post");loopAttrList.put("n6", "pre");loopAttrList.put("n7", "final");
	*/
		int loop_num = loopAttrList.size();
		
		
		//リストから名前を取得
		Set<Integer> loop_nameSet = loopAttrList.keySet();
		Integer[] loop_name = loop_nameSet.toArray(new Integer[0]);
		
		
	/*
		System.out.println("入力リストの確認（AttrList）----------------");
		System.out.print("EquNum"+"\t"+"n1"+"\t");
		System.out.print("n2"+"\t");
		System.out.print("n3"+"\t");
		System.out.print("n4"+"\t");
		System.out.print("n5"+"\t");
		System.out.print("n6"+"\t");
		System.out.print("n7"+"\t");
		System.out.println();
	
		for(int i=1;i<AttrList.size()+1;i++){
			System.out.print("Eq"+ i+":\t"+AttrList.get(i).get("n1")+"\t");
			System.out.print(AttrList.get(i).get("n2")+"\t");
			System.out.print(AttrList.get(i).get("n3")+"\t");
			System.out.print(AttrList.get(i).get("n4")+"\t");
			System.out.print(AttrList.get(i).get("n5")+"\t");
			System.out.print(AttrList.get(i).get("n6")+"\t");
			System.out.print(AttrList.get(i).get("n7")+"\t");
			System.out.println();
		}
		System.out.println();
	*/
		//---------------------------------------------------
		//入力情報作成メソッド
		//inputListを作成する。
		//---------------------------------------------------
		
		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		inputList = make_inputList(AttrList, loopAttrList,loop_name);
		/*
		System.out.println("入力関係情報の確認（inputList）----------------");
		for(int i=0;i<inputList.size();i++){
			inputList.get(i).printContents();
		}
		System.out.println();
		*/
		
		//---------------------------------------------------
		//入力情報に対するnull処理
		//---------------------------------------------------
		
		inputList=removeNull(inputList);
		
		//---------------------------------------------------
		//関係情報の全パターン列挙
		//---------------------------------------------------
		ArrayList<RelationPattern> patternList = new ArrayList<RelationPattern>();
		patternList = get_patternList(loop_num,loop_name);
	/*
		System.out.println("関係パターンの確認（patternList）----------------");
		for(int i=0;i<patternList.size();i++){
			patternList.get(i).printContents();
		}
		System.out.println();
	*/	
		//---------------------------------------------------
		//関係情報の組み合わせパターン列挙
		//関係リストのパターンからループ数-1を選ぶ組み合わせをリスト化する
		//---------------------------------------------------
		
		ArrayList<ArrayList<RelationPattern>> combinationList = new ArrayList<ArrayList<RelationPattern>>();	
		combinationList = get_combinationList(patternList,loop_num);
		

		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（１）
		//ループの種類を網羅していない要素を削除する
		//---------------------------------------------------
		
		combinationList = remove_combinationList(combinationList,loop_num);

		
		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（２）
		//異なる関係の複数の同じ子を親がもつ組み合わせを削除
		//---------------------------------------------------
		
		combinationList = remove_combinationList_2nd(combinationList,loop_num);		
		
		
		
		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（３）
		//複数の同じ属性を親がもつ組み合わせを削除
		//---------------------------------------------------
				
		combinationList = remove_combinationList_3rd(combinationList,loop_num);	

		
		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（４）
		//複数の異なる親をもつ子が存在するものを削除
		//---------------------------------------------------
				
		combinationList = remove_combinationList_4th(combinationList,loop_num);

/*	
		System.out.println("関係パターン組み合わせの確認（combList）----------------");
		for(int i=0; i<combinationList.size();i++){
			System.out.println("Combination"+ (i+1) +"\t");
			for(int j=0;j<loop_num-1;j++){
				combinationList.get(i).get(j).printContents();
			}
			System.out.println();
		}
*/	
		
		//---------------------------------------------------
		//ループ構造の決定
		//孫以降へ継承する情報を入力情報と比較し、矛盾があれば削除
		//---------------------------------------------------

		ArrayList<RelationPattern> LoopStructure = new ArrayList<RelationPattern>();
		LoopStructure = get_LoopStructure(combinationList,loop_num, inputList);	
		
		
		//テスト用にループ構造を複雑化
		//n1の子要素にinner n6
		//n5の子要素にinner n7
		//RelationPattern test1 = new RelationPattern("n1","n6","inner");
		//RelationPattern test2 = new RelationPattern("n5","n7","inner");
		//LoopStructure.add(test1);
		//LoopStructure.add(test2);
		
		System.out.println("決定されたループ構造（Loop_Structure）----------------");
		if(LoopStructure!=null){
			for(int i=0;i<LoopStructure.size();i++){
				LoopStructure.get(i).printContents();
			}
		}
		System.out.println();
		
		
		//RecMLの先頭に記述する形式に変換（ここでは標準出力）
		toRecMLheader(LoopStructure);
		//mainメソッド終了
		//System.out.println("finish");
		
	}	
	
	public static void toRecMLheader(ArrayList<RelationPattern> LoopStructure){
		
		
		//rootの探索
		ArrayList<Integer> root_num = new ArrayList<Integer>();
		for(int i=0;i<LoopStructure.size();i++){
			//各親要素の名前について子要素の中を探索し、見つからなければrootである
			int flag=0;
			for(int j=0;j<LoopStructure.size();j++){
				if(LoopStructure.get(i).Parent_name.equals(LoopStructure.get(j).Child_name)){
					flag=1;
				}
			}
			if(flag==0)	root_num.add(i);//rootとなる関係情報を記録していく
		}
		
		int tab_count=2;//まず2回tabが入った状態で子要素は呼ばれる。
		System.out.println("<loopstruct name="+ "\""+LoopStructure.get(root_num.get(0)).Parent_name+"\""+">");
		for(int i=0;i<root_num.size();i++){
			System.out.println("\t"+"<position name="+ "\""+LoopStructure.get(root_num.get(i)).Attribute_name+ "\""+">");
			System.out.println("\t\t"+"<loopstruct name="+ "\""+LoopStructure.get(root_num.get(i)).Child_name+ "\""+">");
			call_child(LoopStructure, LoopStructure.get(root_num.get(i)).Child_name, tab_count);
		}
		System.out.println("</loopstruct name>");
	}
	
	public static void call_child(ArrayList<RelationPattern> LoopStructure,Integer My_name, int tab_count){
		//子要素の名前を受け取り、親になっていれば子要素を表示する動作を再帰的に行う。
		int flag=0;
		
		for(int i=0;i<LoopStructure.size();i++){
			if(My_name.equals(LoopStructure.get(i).Parent_name)){
				if(flag==0)tab_count++;
				for(int j=0;j<tab_count;j++) System.out.print("\t");
				System.out.println("<position name="+ "\""+LoopStructure.get(i).Attribute_name+ "\""+">");
				
				tab_count++;
				for(int j=0;j<tab_count;j++) System.out.print("\t");
				System.out.println("<loopstruct name="+ "\""+LoopStructure.get(i).Child_name+ "\""+">");
				call_child(LoopStructure,LoopStructure.get(i).Child_name, tab_count);
				tab_count--;
				flag=1;//子要素ありのフラグ
			}
		}
		if(flag==1){
			//子要素を持つ場合の処理（tab++が必要）
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</loopstruct name>");
		
		
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</position name>");
			
		}
		else{
			//子要素を持たない場合
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</position name>");
			
		}

	}
	
	
	public static ArrayList<RelationPattern> get_LoopStructure(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num, ArrayList<RelationPattern>  inputList){
		
		//孫以降へ継承する情報を含めて入力情報と比較し、矛盾があれば削除
		
		//全組み合わせについて各親に対する子の名前を記録し、その名前の親があるかどうか探索
		//子が親になっている場合（すなわち孫が存在する時）、その孫に親の親がもつ属性を付加した関係パターンを作成
		//作成した関係パターンまたはその親子関係がnullであるものが入力情報（inputList）に含まれるかどうかを探索
		//パターンが存在しないとき、その組み合わせを削除
		
		ArrayList<RelationPattern> LoopStructure = new ArrayList<RelationPattern>();
		ArrayList<RelationPattern> pattern_set;

		for(int i=0;i<combinationList.size();i++){
			pattern_set = new ArrayList<RelationPattern>();

			for(int j=0;j<loop_num-1;j++){
				pattern_set.add(combinationList.get(i).get(j));	//継承以外の情報も格納する
				Integer c_name = combinationList.get(i).get(j).Child_name;
				for(int k=0;k<loop_num-1;k++){
					if(j!=k){
						if(c_name.equals(combinationList.get(i).get(k).Parent_name)){
							
							int flag=0;
							for(int m=0;m<loop_num-1;m++){	
								if(combinationList.get(i).get(m).Attribute_name == combinationList.get(i).get(j).Attribute_name){
									if(combinationList.get(i).get(m).Child_name == combinationList.get(i).get(k).Child_name){
										if(combinationList.get(i).get(m).Child_name == combinationList.get(i).get(k).Child_name){
											flag=1;
										}	
									}										
								}
							}
							
							if(flag==0){
								Integer p = combinationList.get(i).get(j).Parent_name;
								Integer c = combinationList.get(i).get(k).Child_name;
								String a = new String(combinationList.get(i).get(j).Attribute_name);
								RelationPattern pattern = new RelationPattern(p,c,a);
								pattern_set.add(pattern);
							}
						}
					}
				}
			}
			
			
			for(int x=0;x<loop_num-3;x++){//ループ数4以上では曾孫以降の継承情報も必要
				
				int setSize = pattern_set.size(); //初期サイズを記録して探索に使用
				for(int j=0;j<setSize;j++){
					Integer c_name = pattern_set.get(j).Child_name;
					for(int k=0;k<setSize;k++){
						if(j!=k){
							if(c_name.equals(pattern_set.get(k).Parent_name)){
								
								int flag=0;
								for(int m=0;m<setSize;m++){	
									if(pattern_set.get(m).Attribute_name == pattern_set.get(j).Attribute_name){
										if(pattern_set.get(m).Child_name == pattern_set.get(k).Child_name){
											if(pattern_set.get(m).Child_name == pattern_set.get(k).Child_name){
												flag=1;
											}	
										}										
									}
								}
								
								if(flag==0){
									Integer p = pattern_set.get(j).Parent_name;
									Integer c = pattern_set.get(k).Child_name;
									String a = new String(pattern_set.get(j).Attribute_name);
									RelationPattern pattern = new RelationPattern(p,c,a);
									pattern_set.add(pattern);
								}
							}
						}
					}
				}
			}
			
			//以降、組み合わせパターンと継承されたパターンが入力情報に含まれているかどうかを探索
			int count=0;
			for(int j=0; j<pattern_set.size();j++){
				for(int k=0;k<inputList.size();k++){
					if(pattern_set.get(j).Parent_name.equals(inputList.get(k).Parent_name)){
						if(pattern_set.get(j).Child_name.equals(inputList.get(k).Child_name)){
							if(pattern_set.get(j).Attribute_name.equals(inputList.get(k).Attribute_name)){
								count++;//完全に一致でカウント
							}
							if(inputList.get(k).Attribute_name.equals("null")){
								count++;//親子が一緒でnullのものがあればカウント(nullは任意の属性と見なすことができる)
							}
						}
					}
				}
			}
			
			if(count==pattern_set.size()){
				//全ての関係が含まれている場合、適切な構造と見なす。
				LoopStructure = combinationList.get(i);//１つ見つかった段階で終了
				break;
			}
		}
		return LoopStructure;
		
	}
	
	public static ArrayList<RelationPattern> get_LoopStructureVer2(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num, ArrayList<RelationPattern>  inputList){
		
		//孫以降へ継承する情報を含めて入力情報と比較し、矛盾があれば削除
		
		//全組み合わせについて各親に対する子の名前を記録し、その名前の親があるかどうか探索
		//子が親になっている場合（すなわち孫が存在する時）、その孫に親の親がもつ属性を付加した関係パターンを作成
		//作成した関係パターンまたはその親子関係がnullであるものが入力情報（inputList）に含まれるかどうかを探索
		//パターンが存在しないとき、その組み合わせを削除
		
		ArrayList<RelationPattern> LoopStructure = new ArrayList<RelationPattern>();
		ArrayList<RelationPattern> pattern_set;

		for(int i=0;i<combinationList.size();i++){
			pattern_set = new ArrayList<RelationPattern>();

			for(int j=0;j<loop_num-1;j++){
				pattern_set.add(combinationList.get(i).get(j));	//継承以外の情報も格納する
				Integer c_name = combinationList.get(i).get(j).Child_name;
				for(int k=0;k<loop_num-1;k++){
					if(j!=k){
						if(c_name.equals(combinationList.get(i).get(k).Parent_name)){
							
							int flag=0;
							for(int m=0;m<loop_num-1;m++){	
								if(combinationList.get(i).get(m).Attribute_name == combinationList.get(i).get(j).Attribute_name){
									if(combinationList.get(i).get(m).Child_name == combinationList.get(i).get(k).Child_name){
										if(combinationList.get(i).get(m).Child_name == combinationList.get(i).get(k).Child_name){
											flag=1;
										}	
									}										
								}
							}
							
							if(flag==0){
								Integer p = combinationList.get(i).get(j).Parent_name;
								Integer c = combinationList.get(i).get(k).Child_name;
								String a = new String(combinationList.get(i).get(j).Attribute_name);
								RelationPattern pattern = new RelationPattern(p,c,a);
								pattern_set.add(pattern);
							}
						}
					}
				}
			}
			
			
			for(int x=0;x<loop_num-3;x++){//ループ数4以上では曾孫以降の継承情報も必要
				
				int setSize = pattern_set.size(); //初期サイズを記録して探索に使用
				for(int j=0;j<setSize;j++){
					Integer c_name = pattern_set.get(j).Child_name;
					for(int k=0;k<setSize;k++){
						if(j!=k){
							if(c_name.equals(pattern_set.get(k).Parent_name)){
								
								int flag=0;
								for(int m=0;m<setSize;m++){	
									if(pattern_set.get(m).Attribute_name == pattern_set.get(j).Attribute_name){
										if(pattern_set.get(m).Child_name == pattern_set.get(k).Child_name){
											if(pattern_set.get(m).Child_name == pattern_set.get(k).Child_name){
												flag=1;
											}	
										}										
									}
								}
								
								if(flag==0){
									Integer p = pattern_set.get(j).Parent_name;
									Integer c = pattern_set.get(k).Child_name;
									String a = new String(pattern_set.get(j).Attribute_name);
									RelationPattern pattern = new RelationPattern(p,c,a);
									pattern_set.add(pattern);
								}
							}
						}
					}
				}
			}
			
			//以降、組み合わせパターンと継承されたパターンが入力情報に含まれているかどうかを探索
			int count=0;
			for(int j=0; j<pattern_set.size();j++){
				for(int k=0;k<inputList.size();k++){
					if(pattern_set.get(j).Parent_name.equals(inputList.get(k).Parent_name)){
						if(pattern_set.get(j).Child_name.equals(inputList.get(k).Child_name)){
							if(pattern_set.get(j).Attribute_name.equals(inputList.get(k).Attribute_name)){
								count++;//完全に一致でカウント
							}
							if(inputList.get(k).Attribute_name.equals("null")){
								count++;//親子が一緒でnullのものがあればカウント(nullは任意の属性と見なすことができる)
							}
						}
					}
				}
			}
			
			if(count==pattern_set.size()){
				//全ての関係が含まれている場合、適切な構造と見なす。
				LoopStructure = combinationList.get(i);//１つ見つかった段階で終了
				break;
			}
		}
		return LoopStructure;
		
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_4th(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の異なる親をもつ子が存在するものを削除
		//親が複数あるかどうか＝子要素の名前の重複をチェックすれば十分
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> c_nameList;
		//各組み合わせの子の名前を重複チェックしながら探索
		//各子に対して親を探索し、親が複数あればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			c_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//子の名前を重複しないように登録
				int match =0;
				for(int x=0;x<c_nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(c_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) c_nameList.add(combinationList.get(i).get(j).Child_name);
			}
			if(c_nameList.size()==loop_num-1) combList_new.add(combinationList.get(i));
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_4thVer2(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の異なる親をもつ子が存在するものを削除
		//親が複数あるかどうか＝子要素の名前の重複をチェックすれば十分
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> c_nameList;
		//各組み合わせの子の名前を重複チェックしながら探索
		//各子に対して親を探索し、親が複数あればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			c_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//子の名前を重複しないように登録
				int match =0;
				for(int x=0;x<c_nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(c_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) c_nameList.add(combinationList.get(i).get(j).Child_name);
			}
			if(c_nameList.size()==loop_num-1) combList_new.add(combinationList.get(i));
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_3rd(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の同じ属性を親がもつ組み合わせを削除
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> p_nameList;
		ArrayList<String> a_nameList;
		//各組み合わせの親の名前を重複チェックしながら探索
		//各親に対して属性を探索し、属性の重複があればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			p_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<p_nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(p_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) p_nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			
			
			int flag=0;
			for(int k=0;k<p_nameList.size();k++){
				//各親の名ごとに探索し、属性要素が重複していたらフラグを立てる。
				
				a_nameList= new ArrayList<String>();
				for(int m=0;m<loop_num-1;m++){
					if(combinationList.get(i).get(m).Parent_name.equals(p_nameList.get(k))){
						int match=0;
						for(int x=0;x<a_nameList.size();x++){
							if(combinationList.get(i).get(m).Attribute_name.equals(a_nameList.get(x))){
								match=1;
							}
						}
						if(match==0)a_nameList.add(combinationList.get(i).get(m).Attribute_name);
						else flag=1;
					}
				}
			}
			if(flag!=1)combList_new.add(combinationList.get(i));
			
		}
		
		
		return combList_new;
	}

	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_3rdVer2(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の同じ属性を親がもつ組み合わせを削除
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> p_nameList;
		ArrayList<String> a_nameList;
		//各組み合わせの親の名前を重複チェックしながら探索
		//各親に対して属性を探索し、属性の重複があればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			p_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<p_nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(p_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) p_nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			
			
			int flag=0;
			for(int k=0;k<p_nameList.size();k++){
				//各親の名ごとに探索し、属性要素が重複していたらフラグを立てる。
				
				a_nameList= new ArrayList<String>();
				for(int m=0;m<loop_num-1;m++){
					if(combinationList.get(i).get(m).Parent_name.equals(p_nameList.get(k))){
						int match=0;
						for(int x=0;x<a_nameList.size();x++){
							if(combinationList.get(i).get(m).Attribute_name.equals(a_nameList.get(x))){
								match=1;
							}
						}
						if(match==0)a_nameList.add(combinationList.get(i).get(m).Attribute_name);
						else flag=1;
					}
				}
			}
			if(flag!=1)combList_new.add(combinationList.get(i));
			
		}
		
		
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_2nd(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の同じ子を親がもつ組み合わせを削除
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> p_nameList;
		ArrayList<Integer> c_nameList;
		//各組み合わせの親の名前を重複チェックしながら探索
		//各親に対して子を探索し、名前の重複があればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			p_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<p_nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(p_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) p_nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			
			
			int flag=0;
			for(int k=0;k<p_nameList.size();k++){
				//各親の名ごとに探索し、子要素が重複していたらフラグを立てる。
				
				c_nameList= new ArrayList<Integer>();
				for(int m=0;m<loop_num-1;m++){
					if(combinationList.get(i).get(m).Parent_name.equals(p_nameList.get(k))){
						int match=0;
						for(int x=0;x<c_nameList.size();x++){
							if(combinationList.get(i).get(m).Child_name.equals(c_nameList.get(x))){
								match=1;
							}
						}
						if(match==0)c_nameList.add(combinationList.get(i).get(m).Child_name);
						else flag=1;
					}
				}
			}
			if(flag!=1)combList_new.add(combinationList.get(i));
			
		}
		
		
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_2ndVer2(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//複数の同じ子を親がもつ組み合わせを削除
		
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> p_nameList;
		ArrayList<Integer> c_nameList;
		//各組み合わせの親の名前を重複チェックしながら探索
		//各親に対して子を探索し、名前の重複があればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			p_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<p_nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(p_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) p_nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			
			
			int flag=0;
			for(int k=0;k<p_nameList.size();k++){
				//各親の名ごとに探索し、子要素が重複していたらフラグを立てる。
				
				c_nameList= new ArrayList<Integer>();
				for(int m=0;m<loop_num-1;m++){
					if(combinationList.get(i).get(m).Parent_name.equals(p_nameList.get(k))){
						int match=0;
						for(int x=0;x<c_nameList.size();x++){
							if(combinationList.get(i).get(m).Child_name.equals(c_nameList.get(x))){
								match=1;
							}
						}
						if(match==0)c_nameList.add(combinationList.get(i).get(m).Child_name);
						else flag=1;
					}
				}
			}
			if(flag!=1)combList_new.add(combinationList.get(i));
			
		}
		
		
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList(ArrayList<ArrayList<RelationPattern>> combinationList, int loop_num){
		
		//ループの種類を網羅していない組み合わせを削除
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> nameList;
		for(int i=0;i<combinationList.size();i++){
			
			nameList = new ArrayList<Integer>();
			
			for(int j=0;j<loop_num-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			for(int j=0;j<combinationList.get(i).size();j++){
				//子の名前を重複しないように登録
				int match=0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Child_name);
			}
			
			if(nameList.size()==loop_num){
				combList_new.add(combinationList.get(i));
			}
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> remove_combinationList_1stVer2(ArrayList<ArrayList<RelationPattern>> combinationList, int indexListsize){
		
		//ループの種類を網羅していない組み合わせを削除
		ArrayList<ArrayList<RelationPattern>> combList_new = new ArrayList<ArrayList<RelationPattern>>();
		ArrayList<Integer> nameList;
		for(int i=0;i<combinationList.size();i++){
			
			nameList = new ArrayList<Integer>();
			
			for(int j=0;j<indexListsize-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			for(int j=0;j<combinationList.get(i).size();j++){
				//子の名前を重複しないように登録
				int match=0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Child_name);
			}
			
			if(nameList.size() == indexListsize){
				combList_new.add(combinationList.get(i));
			}
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> get_combinationList(ArrayList<RelationPattern> patternList,int loop_num){
		
		//pattenList.sizeから(loop_num-1)選ぶ組み合わせを列挙するメソッド
		//patternList.sizeからloop_num-1個を選ぶ
		int n = patternList.size();
		int r = loop_num-1;
	    int[] c = new int[10];
		for ( int i=0; i<10; i++ ){
			c[i] = 0;
	    }
		ArrayList<ArrayList<RelationPattern>> combinationList = new ArrayList<ArrayList<RelationPattern>>();
		combine_pattern(1,n,r,c,patternList, combinationList);
		
		return  combinationList;
	}
	
	public static ArrayList<ArrayList<RelationPattern>> get_combinationListVer2(ArrayList<RelationPattern> patternList,HashMap<Integer, String> IndexList){
		
		//pattenList.sizeから(loop_num-1)選ぶ組み合わせを列挙するメソッド
		//patternList.sizeからloop_num-1個を選ぶ
		int n = patternList.size();
		int r = IndexList.size() - 1;
	    int[] c = new int[10];
		for ( int i=0; i<10; i++ ){
			c[i] = 0;
	    }
		ArrayList<ArrayList<RelationPattern>> combinationList = new ArrayList<ArrayList<RelationPattern>>();
		combine_pattern(1,n,r,c,patternList, combinationList);
		
		return  combinationList;
	}
	
    public static void combine_pattern( int m ,int n,int r,int[] c,ArrayList<RelationPattern> patternList,ArrayList<ArrayList<RelationPattern>> combinationList) {
    	
    	if ( m <= r ) {
          for ( int i=c[m-1]+1; i<=n-r+m; i++ ){
             c[m] = i;
             combine_pattern(m+1,n,r,c,patternList,combinationList);
          }
        }
        else {
           ArrayList<RelationPattern> pattern = new ArrayList<RelationPattern>();
           for ( int i=1; i<=r; i++ ){
        	  pattern.add(patternList.get((c[i]-1)));
        	  //組み合わせ番号は正数の組み合わせなのでリストの格納番号（0スタート）に置き換えて取得
          }
          combinationList.add(pattern);
        }
    }
	
	
	public static ArrayList<RelationPattern> get_patternList(int loop_num,Integer[] loop_name){
		
		//関係パターンの列挙を取得するメソッド
		//列挙数を計算し、その数だけstruPatt[]を返す
		
		int attr_num = 3;
		String[] attr_name ={"pre","inner","post"};
		
		ArrayList<RelationPattern> patternList = new ArrayList<RelationPattern>();
		for(int i=0; i<loop_num;i++){	
			for(int j=0; j<loop_num;j++){
				for(int k=0; k<attr_num;k++){
					if(i!=j){
						RelationPattern pattern = new RelationPattern(loop_name[i],loop_name[j],attr_name[k]);
						patternList.add(pattern);
					}
				}
			}
		}
		return patternList;
	}
	
	public static ArrayList<RelationPattern> get_patternListVer2(HashMap<Integer, String> IndexList){
		
		//関係パターンの列挙を取得するメソッド
		//列挙数を計算し、その数だけstruPatt[]を返す
		
		int attr_num = 3;
		String[] attr_name ={"pre","inner","post"};
		
		ArrayList<RelationPattern> patternList = new ArrayList<RelationPattern>();
		for(int i=0; i<IndexList.size();i++){	
			for(int j=0; j<IndexList.size();j++){
				for(int k=0; k<attr_num;k++){
					if(i!=j){
						RelationPattern pattern = new RelationPattern(i,j,attr_name[k]);
						patternList.add(pattern);
					}
				}
			}
		}
		return patternList;
	}
	
	public static ArrayList<RelationPattern> removeNull(ArrayList<RelationPattern> inputList){
		//nullをもつ親子のペアに対して、null以外をもつものが見つかればnullをもつ情報は削除
 		
		ArrayList<RelationPattern> inputList_new= new ArrayList<RelationPattern>();
		for(int i=0;i<inputList.size();i++){
			
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}else{
				Integer Parent = inputList.get(i).Parent_name;
				Integer Child = inputList.get(i).Child_name;
			
				int flag = 0;
				
				for(int j=0;j<inputList.size();j++){
					if(i!=j){
						if(inputList.get(j).Parent_name.equals(Child) && inputList.get(j).Child_name.equals(Parent)||
								inputList.get(j).Parent_name.equals(Parent) && inputList.get(j).Child_name.equals(Child)){
							if(!inputList.get(j).Attribute_name.equals("null")){
								flag=1;
							}
						}
					}
				}
				if(flag!=1) inputList_new.add(inputList.get(i));
			}
		}
		
		return inputList_new;
	}
	
	public static ArrayList<RelationPattern> removeNullVer2(ArrayList<RelationPattern> inputList){
		//nullをもつ親子のペアに対して、null以外をもつものが見つかればnullをもつ情報は削除
 		
		ArrayList<RelationPattern> inputList_new= new ArrayList<RelationPattern>();
		for(int i=0;i<inputList.size();i++){
			
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}else{
				Integer Parent = inputList.get(i).Parent_name;
				Integer Child = inputList.get(i).Child_name;
			
				int flag = 0;
				
				for(int j=0;j<inputList.size();j++){
					if(i!=j){
						if(inputList.get(j).Parent_name.equals(Child) && inputList.get(j).Child_name.equals(Parent)||
								inputList.get(j).Parent_name.equals(Parent) && inputList.get(j).Child_name.equals(Child)){
							if(!inputList.get(j).Attribute_name.equals("null")){
								flag=1;
							}
						}
					}
				}
				if(flag!=1) inputList_new.add(inputList.get(i));
			}
		}
		
		return inputList_new;
	}
	
	public static ArrayList<RelationPattern> make_inputList(HashMap<Integer,HashMap<Integer,String>> AttrList,
			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int val_num= AttrList.size();		//変数の数
		int loop_num= loopAttrList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		
		for(int i=0;i<loop_num;i++){
			for(int j=1;j<val_num+1;j++){
				if(AttrList.get(j).get(loop_name[i]).equals("init")){
					for(int k=0;k<loop_num;k++){
						
						if(k!=i){
							String init_attr = new String(AttrList.get(j).get(loop_name[k]));
							int init_count=0;
							int inner_count=0;
							int final_count=0;
							for(int m=1;m<val_num+1;m++){
								if(AttrList.get(m).get(loop_name[k]).equals(init_attr)){
									if(AttrList.get(m).get(loop_name[i]).equals("init")) init_count++;
									if(AttrList.get(m).get(loop_name[i]).equals("inner")) inner_count++;
									if(AttrList.get(m).get(loop_name[i]).equals("final")) final_count++;
								}		
							}
							if(init_count!=0 &&  inner_count!=0 && final_count!=0){								
								inputList.add(new RelationPattern(loop_name[k],loop_name[i],init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}

	public static ArrayList<RelationPattern> make_inputListVer2(HashMap<Integer,HashMap<Integer,String>> AttrList,
			Vector<Integer> equOderVec, HashMap<Integer, String> IndexList){
//			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int val_num= AttrList.size();		//変数の数
		int loop_num= IndexList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		
		for(int i=0;i<loop_num;i++){
			for(int j=0;j<equOderVec.size();j++){
				int equNum1 = equOderVec.get(j);
				if(AttrList.get(equNum1).get(i).equals("init")){
					for(int k=0;k<loop_num;k++){
						
						if(k!=i){
							String init_attr = new String(AttrList.get(equNum1).get(k));
							int init_count=0;
							int inner_count=0;
							int final_count=0;
							for(int m=0;m<equOderVec.size();m++){
								int equNum2 = equOderVec.get(m);
								if(AttrList.get(equNum2).get(k).equals(init_attr)){
									if(AttrList.get(equNum2).get(i).equals("init")) init_count++;
									if(AttrList.get(equNum2).get(i).equals("inner")) inner_count++;
									if(AttrList.get(equNum2).get(i).equals("final")) final_count++;
								}		
							}
							if(init_count!=0 &&  inner_count!=0 && final_count!=0){								
								inputList.add(new RelationPattern(k,i,init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}	
}

	
	

