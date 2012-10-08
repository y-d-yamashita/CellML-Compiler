package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field;

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
public class FieldVertex extends Vertex {
	private String timeIndex;
	private String xAxisIndex;
	private String yAxisIndex;
	private String zAxisIndex;
	
	
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
	public FieldVertex(String timeIndex,String xAxisIndex,String yAxisIndex,String zAxisIndex){
		this();
		this.timeIndex=timeIndex;
		this.xAxisIndex=xAxisIndex;
		this.yAxisIndex=yAxisIndex;
		this.zAxisIndex=zAxisIndex;
	}
	/**
	 * Constructor
	 */
	public FieldVertex(){
		this.timeIndex=null;
		this.xAxisIndex=null;
		this.yAxisIndex=null;
		this.zAxisIndex=null;
		sourceFlag=false;
		sinkFlag=false;
		visited= false;
	}

	
	/**
	 * Get time index
	 * @return String of time index
	 */
	public String getTimeIndexString(){
		return timeIndex;
	}
	
	/**
	 * Get X axis index
	 * @return String of x axis index 
	 */
	public String getXAxisIndex(){
		return xAxisIndex;
	}
	
	/**
	 * Get Y axis index
	 * @return String of y axis index 
	 */
	public String getYAxisIndex(){
		return yAxisIndex;
	}

	/**
	 * Get Z axis index
	 * @return String of z axis index 
	 */
	public String getZAxisIndex(){
		return zAxisIndex;
	}

	/**
	 * Set time index
	 */
	public void setTimeIndex(String timeIndex){
		this.timeIndex=timeIndex;
	}

	

	/**
	 * Set x axis index
	 */
	public void setXAxisIndex(String xAsisIndex){
		this.xAxisIndex = xAsisIndex;
	}


	/**
	 * Set y axis index
	 */
	public void setYAxisIndex(String yAsisIndex){
		this.yAxisIndex = yAsisIndex;
	}

	/**
	 * Set z axis index
	 */
	public void setZAxisIndex(String zAsisIndex){
		this.zAxisIndex = zAsisIndex;
	}
	
	
	/**
	 * Set z axis index
	 * @param index
	 * @param number of index type
	 * 
	 * 0: time
	 * 1: x axis
	 * 2: y axis
	 * 3: z axis
	 */
	public void setAxisIndex(String index,int type){
		switch(type){
			case 0:
				setTimeIndex(index);
				break;
			case 1:
				setXAxisIndex(index);
				break;
			case 2:
				setYAxisIndex(index);
				break;
			case 3:
				setZAxisIndex(index);
				break;
		}
	}
	
	
	/**
	 * equals method
	 * @param String of time index
	 * @return if having index equals arg index return true, otherwise return false
	 */
	public boolean equalsTimeIndex(String index){
		return timeIndex.equals(index);
	}

	/**
	 * equals method
	 * @param String of time index
	 * @return if x axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsXAxisIndex(String index){
		return xAxisIndex.equals(index);
	}

	/**
	 * equals method
	 * @param String of index
	 * @return if y axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsYAxisIndex(String index){
		return yAxisIndex.equals(index);
	}

	/**
	 * equals method
	 * @param String of index
	 * @return if z axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsZAxisIndex(String index){
		return zAxisIndex.equals(index);
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
	 * toString method
	 */
    @Override
    public String toString(){
    if(isSink()){
    	return "Sink";
    }else if(isSource()){
    	return "Source";
    }else{
    	return "t["+timeIndex+"]"+
    			" x["+xAxisIndex+"]"+
    			" y["+yAxisIndex+"]"+
    			" z["+zAxisIndex+"]";
    }
    }
    
    /**
     * toXMLString method
     * @param indent
     * @return XML string
     */
    public String toXMLString(int id, String indent){
     	StringBuilder sb =  new StringBuilder().append(indent).append("<node id="+id);
     	if(timeIndex!=null){
     		sb.append(" time=\""+timeIndex+"\"");
     	}
     	if(xAxisIndex!=null){
     		sb.append(" x=\""+xAxisIndex+"\"");
     	}
     	if(yAxisIndex!=null){
     		sb.append(" y=\""+yAxisIndex+"\"");
     	}
     	if(zAxisIndex!=null){
     		sb.append(" z=\""+zAxisIndex+"\"");
     	}
     	sb.append(" />\n");
     	return sb.toString();
    }
}


