package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.List;

/**
 * Grouping fildGraph vertexes for loop optimization
 * @author y-yamashita
 */
public class FieldVertexGroupList {
	/** Loop group of time level*/
	private List<FieldVertexGroup> timeLoopGroupList;

	/** Loop group of x axis and time level*/
	private List<List<FieldVertexGroup>> xAxisLoopGroupList;

	//	private Map<Integer,Map<Integer,List<FieldLoopGroup>>> yAxisLoopGroupList;
//	private Map<Integer,Map<Integer,Map<Integer,List<FieldLoopGroup>>>> zAxisLoopGroupList;

	/**
	 * Constructor
	 */
	public FieldVertexGroupList(){
		timeLoopGroupList= null;
		xAxisLoopGroupList= null;
//		yAxisLoopGroupList= null;
//		zAxisLoopGroupList= null;
	}
	
	/**
	 * Add time level loop group 
	 * @param group
	 */
	public void addTimeLoopGroup(FieldVertexGroup group){
		timeLoopGroupList.add(group);
	}
	
	/**
	 * Set time looop group list
	 * @param list
	 */
	public void setTimeLoopGroupList(List<FieldVertexGroup> list){
		timeLoopGroupList = list;
	}

	/**
	 * Get time loop group list
	 * @return list of time loop group 
	 */
	public List<FieldVertexGroup> getTimeLoopGroupList(){
		return timeLoopGroupList;
	}
	
	/**
	 * Get specified time loop group
	 * @param i
	 * @return time loop group
	 */
	public FieldVertexGroup getTimeLoopGroup(int i){
		return timeLoopGroupList.get(i);
	}

	/**
	 * Get list of list of x axis loop group
	 * @return
	 */
	public List<List<FieldVertexGroup>> getXAxisLoopGroupList(){
		return xAxisLoopGroupList;
	}
	
	
	/**
	 * Set list of list of x axis loop group
	 * @param xAxisLoopList
	 */
	public void setXAxisLoopGroupList(List<List<FieldVertexGroup>> xAxisLoopList){
		xAxisLoopGroupList = xAxisLoopList;
	}

	
	

/* Not implemented
  
  public void addYAxisLoopGroup(FieldLoopGroup group){
		yAxisLoopGroupList.add(group);
	}
	public List<FieldLoopGroup> getYAxisLoopGroupList(){
		return yAxisLoopGroupList;
	}
	public FieldLoopGroup getYAxisLoopGroup(int i){
		return yAxisLoopGroupList.get(i);
	}

	public void addYAxisLoopGroupList(List<FieldLoopGroup> list){
		yAxisLoopGroupList = list;
	}

	
	

	public void addZAxisLoopGroup(FieldLoopGroup group){
		zAxisLoopGroupList.add(group);
	}
	public List<FieldLoopGroup> getZAxisLoopGroupList(){
		return zAxisLoopGroupList;
	}
	public FieldLoopGroup getZAxisLoopGroup(int i){
		return zAxisLoopGroupList.get(i);
	}
	public void addZAxisLoopGroupList(List<FieldLoopGroup> list){
		zAxisLoopGroupList = list;
	}

 end */
	
	/**
	 * Return string
	 * @return string
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String indent = "	";
/* Not implemented 
		sb.append("< Z Axis Index Loop >-----------------------\n");
		for(FieldLoopGroup group:zAxisLoopGroupList){
			sb.append("loop["+zAxisLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
		}

		sb.append("< Y Axis Index Loop >-----------------------\n");
		for(FieldLoopGroup group:yAxisLoopGroupList){
			sb.append("loop["+yAxisLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
		}
end */

		//if(xAxisLoopGroupList!=null){
			sb.append("< X Axis Index Group >-----------------------\n");
			for(List<FieldVertexGroup> xLoop:xAxisLoopGroupList){
				sb.append("time group["+xAxisLoopGroupList.indexOf(xLoop)+"]").append("{\n");
				for(FieldVertexGroup group:xLoop){
					sb.append(indent+"x axis group["+xLoop.indexOf(group)+"]{\n");
					sb.append(indent+indent+group.toString()).append("\n");
					sb.append(indent+"}\n");
				}
				sb.append("}\n");
			}
		//}else{
			sb.append("< Time Index Group >-----------------------\n");
			for(FieldVertexGroup group:timeLoopGroupList){
				sb.append("time group["+timeLoopGroupList.indexOf(group)+"]").append("{\n");
				sb.append(indent+"time group["+timeLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
			}
			sb.append("}\n");
		//}
		return sb.toString();
	}
}
