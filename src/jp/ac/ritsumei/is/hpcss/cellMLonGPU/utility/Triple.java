package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

/**
 * Pair class
 * @author y-yamashita
 * @param <FIRST> Type of First element
 * @param <SECOND> Type of Second element
 * @param <THIRD>
 */
public class Triple<FIRST,SECOND,THIRD> {
	private FIRST first;
	private SECOND second;
	private THIRD third;
	
	/**
	 * Constructor
	 * @param a
	 * @param b
	 */
	public Triple(){
		this.setFirst(null);
		this.setSecond(null);
		this.setThird(null);
	}

	/**
	 * Constructor
	 * @param a
	 * @param b
	 */
	public Triple(FIRST first,SECOND second,THIRD third){
		this.setFirst(first);
		this.setSecond(second);
		this.setThird(third);
	}
	
	/**
	 * equals method
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj){
		if(obj==null){return false;}
		if(obj instanceof Triple){
			Object[] that = getValues((Triple<FIRST,SECOND,THIRD>) obj);
			return java.util.Arrays.deepEquals(that,getValues(this));
		}
		return false;		
	}
	
	/**
	 * has code mehod
	 */
	public int hashCode(){
		return java.util.Arrays.deepHashCode(getValues(this));
	}
	
	/**
	 * Get values method
	 * @param v
	 * @return object array
	 */
	private static <FIRST,SECOND,THIRD> Object[] getValues(Triple<FIRST,SECOND,THIRD> v){
		return new Object[]	{v.getFirst(),v.getSecond(),v.getThird()};
	}
	
	/**
	 * toString method
	 */
	@Override 
	public String toString(){
		return new StringBuilder().append(this.getFirst()).append(",").
				append(this.getSecond()).append(",").
				append(this.getThird()).toString();
	}

	public FIRST getFirst() {
		return first;
	}

	public void setFirst(FIRST first) {
		this.first = first;
	}

	public SECOND getSecond() {
		return second;
	}

	public void setSecond(SECOND second) {
		this.second = second;
	}

	public THIRD getThird() {
		return third;
	}

	public void setThird(THIRD third) {
		this.third = third;
	}
	
}
