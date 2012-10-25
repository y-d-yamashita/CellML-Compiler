package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;

public class FieldLoopGroupList {
	private List<FieldLoopGroup> timeLoopGroupList;
	private List<List<FieldLoopGroup>> xAxisLoopGroupList;
//	private Map<Integer,Map<Integer,List<FieldLoopGroup>>> yAxisLoopGroupList;
//	private Map<Integer,Map<Integer,Map<Integer,List<FieldLoopGroup>>>> zAxisLoopGroupList;
	
	public FieldLoopGroupList(){
		timeLoopGroupList= null;
		xAxisLoopGroupList= null;
//		yAxisLoopGroupList= null;
//		zAxisLoopGroupList= null;
	}
	
	
	public void addTimeLoopGroup(FieldLoopGroup group){
		timeLoopGroupList.add(group);
	}
	public void setTimeLoopGroupList(List<FieldLoopGroup> list){
		timeLoopGroupList = list;
	}

	public List<FieldLoopGroup> getTimeLoopGroupList(){
		return timeLoopGroupList;
	}
	public FieldLoopGroup getTimeLoopGroup(int i){
		return timeLoopGroupList.get(i);
	}


	public List<List<FieldLoopGroup>> getXAxisLoopGroupList(){
		return xAxisLoopGroupList;
	}
	public void setXAxisLoopGroupList(List<List<FieldLoopGroup>> xAxisLoopList){
		xAxisLoopGroupList = xAxisLoopList;
	}

	
	
	
/*
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

*/
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String indent = "	";
/*
		sb.append("< Z Axis Index Loop >-----------------------\n");
		for(FieldLoopGroup group:zAxisLoopGroupList){
			sb.append("loop["+zAxisLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
		}

		sb.append("< Y Axis Index Loop >-----------------------\n");
		for(FieldLoopGroup group:yAxisLoopGroupList){
			sb.append("loop["+yAxisLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
		}
*/
		if(xAxisLoopGroupList!=null){
			sb.append("< X Axis Index Loop >-----------------------\n");
			for(List<FieldLoopGroup> xLoop:xAxisLoopGroupList){
				sb.append("time loop["+xAxisLoopGroupList.indexOf(xLoop)+"]").append("\n");
				for(FieldLoopGroup group:xLoop){
					sb.append(indent+"x loop["+xLoop.indexOf(group)+"]").append(group.toString()).append("\n");
				}
			}
		}else{
			sb.append("< TimeIndexLoop >-----------------------\n");
			for(FieldLoopGroup group:timeLoopGroupList){
				sb.append("loop["+timeLoopGroupList.indexOf(group)+"]").append(group.toString()).append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
