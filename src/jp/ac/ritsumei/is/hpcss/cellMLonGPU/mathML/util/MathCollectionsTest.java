package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util;

import static org.junit.Assert.*;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.features.Calculator;

import org.junit.Test;

public class MathCollectionsTest {

	@Test
	public void testCalculate1() {
		System.out.println(new Thread().currentThread().getStackTrace()[1].getMethodName()+">>>");
		
		System.out.println("Test:3+2=?");
		String actual=null;
		String expected="5";
		String str_a = "3";
		String str_b = "2";
		
		Math_plus plus = new Math_plus();
		Math_cn a = new Math_cn(str_a);
		Math_cn b = new Math_cn(str_b);
		
		plus.addFactor(a);
		plus.addFactor(b);
		
		MathFactor computation = MathCollections.calculate(plus);
		
		
		if(computation instanceof Math_cn){
			Math_cn cn = (Math_cn) computation;
			cn.autoChangeType();
			try {
				actual=cn.toLegalString();
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(actual);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculate2() {
		System.out.println(new Thread().currentThread().getStackTrace()[1].getMethodName()+">>>");
		
		System.out.println("Test:1.1-2.2=?");
		String actual=null;
		String expected="(double)-1.1";
		String str_a = "1.1";
		String str_b = "2.2";
		
		Math_minus minus = new Math_minus();
		Math_cn a = new Math_cn(str_a);
		Math_cn b = new Math_cn(str_b);
		
		minus.addFactor(a);
		minus.addFactor(b);
		
		MathFactor computation = MathCollections.calculate(minus,3);
		
		
		if(computation instanceof Math_cn){
			Math_cn cn = (Math_cn) computation;
			cn.autoChangeType();
			try {
				actual=cn.toLegalString();
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(actual);
		assertEquals(expected,actual);
	}

}
