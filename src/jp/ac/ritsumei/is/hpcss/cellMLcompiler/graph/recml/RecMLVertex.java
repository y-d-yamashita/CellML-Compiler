package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml;

import com.sun.org.apache.bcel.internal.classfile.SourceFile;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Vertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;


/**
 * Vertex class for RecML variable and expression
 * @author y-yamashita
 *
 */
public class RecMLVertex extends Vertex {
	/** RecML variable*/
	private Math_ci recVar;

	/** RecML expression*/
	private MathExpression recExpr;
	
	/** Flag to identify source vertex for maximum matching*/
	private boolean sourceFlag;

	/** Flag to identify sink vertex for maximum matching*/
	private boolean sinkFlag;

	/** Flag visited or not */
	private boolean visited;

	/** Used in tarjan's algorithm*/
	public int lowpt = -1;
	/** Used in tarjan's algorithm*/
	public int number = -1;
	/** Used in tarjan's algorithm*/
	public int lowvine=-1;

	/**
	 * Constructor
	 */
	public RecMLVertex(){
		recVar = null;
		recExpr = null;
		sourceFlag=false;
		sinkFlag=false;
		visited= false;
	}
	/**
	 * Constructor
	 * @param var RecML variable
	 */
	public RecMLVertex(Math_ci var){
		this();
		recVar = var;
	}
	/**
	 * Constructor
	 * @param expr RecML expression
	 */
	public RecMLVertex(MathExpression expr){
		this();
		recExpr = expr;
	}
	/**
	 * Constructor
	 * @param var RecMl variable
	 * @param expr RecMl expression
	 */
	public RecMLVertex(Math_ci var,MathExpression expr){
		recVar = var;
		recExpr = expr;
	}

	/**
	 * Get variable
	 * @return RecML variable
	 */
	public Math_ci getVariable(){
		return recVar;
	}
	
	/**
	 * Get expression
	 * @return RecML expression
	 */
	public MathExpression getExpression(){
		return recExpr;
	}
	
	/**
	 * equals method
	 * @param var RecML variable
	 * @return if var equals revVar return true, otherwise return false
	 */
	public boolean equals(Math_ci var){
		return var.equals(recVar);
	}

	/**
	 * equals method
	 * @param var RecML expression
	 * @return if expr equals revExpr return true, otherwise return false
	 */
	public boolean equals(MathExpression expr){
		return expr.equals(recExpr);
	}

	
	/**
	 * Set source flag
	 * @param flag
	 */
	public void  setSourceFlag(boolean flag){
		sourceFlag=flag;
	}
	
	/**
	 * Set sink flag
	 * @param flag
	 */
	public void  setSinkFlag(boolean flag){
		sinkFlag=flag;
	}
	
	/**
	 * Set visited flag
	 * @param flag
	 */
	public void setVisited(boolean flag){
		visited=flag;
	}
	
	/**
	 * If source vertex , return true: otherwise return false
	 * @return source flag
	 */
	public boolean isSource(){
		return sourceFlag;
	}
	
	/**
	 * If sink vertex , return true: otherwise return false
	 * @return sink flag
	 */
	public boolean isSink(){
		return sinkFlag;
	}
	
	/**
	 * Return visited flag
	 * @return visited flag
	 */
	public boolean isVisited(){
		return visited;
	}
	
	/**
	 * If having RecML variable, return true.Other case return false.
	 * @return has:true, not has:false
	 */
	public boolean hasVariable(){
		if(recVar!=null)
			return true;
		else
			return false;
	}

	/**
	 * If having RecML expression, return true.Other case return false.
	 * @return has:true, not has:false
	 */
	public boolean hasExpression(){
		if(recExpr!=null)
			return true;
		else
			return false;
	}

	
	/**
	 * toString method
	 */
    @Override
    public String toString(){
    if(isSink()){
    	return "Sink";
    }else if(isSource()){
    	return "Source";
    }else if(recVar!=null&&recExpr!=null){
    	try {
			return new StringBuilder().append("Var:"+recVar.toLegalString()+"&"+"Expr:"+recExpr.toLegalString()).toString();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }else if(recVar!=null){
    	try {
			return new StringBuilder().append("Var:"+recVar.toLegalString()).toString();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }else if(recExpr!=null){
    	try {
			return new StringBuilder().append("Expr:"+recExpr.toLegalString()).toString();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
			return "no string";
    }
}


