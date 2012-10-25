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
	private Integer id;
	private Integer timeIndex;
	private Integer xAxisIndex;
	private Integer yAxisIndex;
	private Integer zAxisIndex;
	
	
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
		this.timeIndex=decode(timeIndex);
		this.xAxisIndex=decode(xAxisIndex);
		this.yAxisIndex=decode(yAxisIndex);
		this.zAxisIndex=decode(zAxisIndex);
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

	
	private Integer decode(String str){
		return Integer.decode(str.replace(" ","").replace("(","").replace(")", ""));
	}
	/**
	 * Get time index
	 * @return  time index
	 */
	public Integer getTimeIndex(){
		return timeIndex;
	}
	
	/**
	 * Get X axis index
	 * @return  x axis index 
	 */
	public Integer getXAxisIndex(){
		return xAxisIndex;
	}
	
	/**
	 * Get Y axis index
	 * @return String of y axis index 
	 */
	public Integer getYAxisIndex(){
		return yAxisIndex;
	}

	/**
	 * Get Z axis index
	 * @return String of z axis index 
	 */
	public Integer getZAxisIndex(){
		return zAxisIndex;
	}

	/**
	 * Set time index
	 */
	public void setTimeIndex(String timeIndex){
		this.timeIndex=decode(timeIndex);
	}

	

	/**
	 * Set x axis index
	 */
	public void setXAxisIndex(String xAsisIndex){
		this.xAxisIndex = decode(xAsisIndex);
	}


	/**
	 * Set y axis index
	 */
	public void setYAxisIndex(String yAsisIndex){
		this.yAxisIndex = decode(yAsisIndex);
	}

	/**
	 * Set z axis index
	 */
	public void setZAxisIndex(String zAsisIndex){
		this.zAxisIndex = decode(zAsisIndex);
	}
	
	
	/**
	 * Set z axis index
	 * @param indexString
	 * @param number of index type
	 * 
	 * 0: time
	 * 1: x axis
	 * 2: y axis
	 * 3: z axis
	 */
	public void setAxisIndex(String indexString,int type){
		switch(type){
			case 0:
				setTimeIndex(indexString);
				break;
			case 1:
				setXAxisIndex(indexString);
				break;
			case 2:
				setYAxisIndex(indexString);
				break;
			case 3:
				setZAxisIndex(indexString);
				break;
		}
	}
	
	
	/**
	 * equals method
	 * @param String of time index
	 * @return if having index equals arg index return true, otherwise return false
	 */
	public boolean equalsTimeIndex(String index){
		return timeIndex.equals(decode(index));
	}

	/**
	 * equals method
	 * @param String of time index
	 * @return if x axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsXAxisIndex(String index){
		return xAxisIndex.equals(decode(index));
	}

	/**
	 * equals method
	 * @param String of index
	 * @return if y axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsYAxisIndex(String index){
		return yAxisIndex.equals(decode(index));
	}

	/**
	 * equals method
	 * @param String of index
	 * @return if z axis index equals arg index return true, otherwise return false
	 */
	public boolean equalsZAxisIndex(String index){
		return zAxisIndex.equals(decode(index));
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
    public String toXMLString(String indent){
     	StringBuilder sb =  new StringBuilder().append(indent).append("<node id=\""+getId()+"\"");
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setId(String id){
		this.id=decode(id);
	}
}


