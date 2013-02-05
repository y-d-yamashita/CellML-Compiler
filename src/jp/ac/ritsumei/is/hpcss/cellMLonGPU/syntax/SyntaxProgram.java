package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.JavaBigDecimalProgramGeneratorSingleton;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.JavaProgramGeneratorSingleton;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * プログラム構文クラス.
 */
public class SyntaxProgram extends Syntax {

	/**内部の構文*/
	protected Vector<SyntaxPreprocessor> m_vecSynPreprocessor;
	protected Vector<SyntaxDeclaration> m_vecSynDeclaration;
	protected Vector<SyntaxFunction> m_vecSynFunction;

	/**
	 * プログラム構文インスタンスを作成する.
	 */
	public SyntaxProgram() {
		super(eSyntaxClassification.SYN_PROGRAM);
		m_vecSynPreprocessor = new Vector<SyntaxPreprocessor>();
		m_vecSynDeclaration = new Vector<SyntaxDeclaration>();
		m_vecSynFunction = new Vector<SyntaxFunction>();
	}
	
	/**java構文(プロトタイプ宣言不要)*/
	protected boolean javaFlag = false;
	
	public void setJavaFlag(){
		javaFlag=true;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalString()
	 */
	public String toLegalString()
	throws MathException, SyntaxException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		//------------------------------------------
		//プリプロセッサ構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxPreprocessor it: m_vecSynPreprocessor) {
				/*文字列追加*/
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//宣言構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for(SyntaxDeclaration it: m_vecSynDeclaration) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);
		
		if(!this.javaFlag){
			//------------------------------------------
			//関数プロトタイプ構文追加
			//------------------------------------------
			{
				/*順次追加*/
				for (SyntaxFunction it: m_vecSynFunction) {
					strPresentText.append(it.toStringPrototype() + ";" + StringUtil.lineSep);
				}
			}

			/*改行*/
			strPresentText.append(StringUtil.lineSep);
		}
		

		//------------------------------------------
		//関数構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxFunction it: m_vecSynFunction) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		return strPresentText.toString();
	}

	/**
	 * Javaプログラムの文字列に変換する.
	 * @return 変換した文字列
	 * @throws SyntaxException
	 * @throws MathException
	 */
	public String toJavaString()
	throws SyntaxException, MathException {
		//JAVA_MODE=1;
		JavaProgramGeneratorSingleton pInstance = JavaProgramGeneratorSingleton.GetInstance();
		pInstance.setMode(true);
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");


		//------------------------------------------
		//プリプロセッサ構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxPreprocessor it: m_vecSynPreprocessor) {
				/*文字列追加*/
				strPresentText.append(it.toJavaString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//宣言構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for(SyntaxDeclaration it: m_vecSynDeclaration) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//関数構文追加
		//------------------------------------------
		{
			strPresentText.append("public final class JavaProgramGenerator{"
					+ StringUtil.lineSep);

			/*順次追加*/
			for (SyntaxFunction it: m_vecSynFunction) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		pInstance.DeleteInstance();
		pInstance = null;
		//JAVA_MODE=0;
		strPresentText.append("}" + StringUtil.lineSep);

		return strPresentText.toString();

	}

	/**
	 * Java Bigdecimalプログラムの文字列に変換する.
	 * @return 変換した文字列
	 * @throws SyntaxException
	 * @throws MathException
	 */
	public String toJavaBigDecimalString()
	throws SyntaxException, MathException {
		JavaBigDecimalProgramGeneratorSingleton pInstance =
			JavaBigDecimalProgramGeneratorSingleton.GetInstance();
		pInstance.setMode(true);
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		//------------------------------------------
		//プリプロセッサ構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxPreprocessor it: m_vecSynPreprocessor) {
				/*文字列追加*/
				strPresentText.append(it.toJavaString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//宣言構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for(SyntaxDeclaration it: m_vecSynDeclaration) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//関数構文追加
		//------------------------------------------
		{
			strPresentText.append("public final class JavaBigDecimalProgram{"
					+ StringUtil.lineSep);

			/*順次追加*/
			for (SyntaxFunction it: m_vecSynFunction) {
				strPresentText.append(it.toLegalString() + StringUtil.lineSep);
			}
		}
		pInstance.DeleteInstance();
		pInstance = null;

		/*
	  	*Add calculation methods text for BigDecimal calculation
		*
	  	*/

		strPresentText.append(""
	    + "/**" + StringUtil.lineSep
	    + "* Compute x^exponent to a given scale.  Uses the same" + StringUtil.lineSep
	    + "* algorithm as class numbercruncher.mathutils.IntPower." + StringUtil.lineSep
	    + "* @param x the value x" + StringUtil.lineSep
	    + "* @param exponent the exponent value" + StringUtil.lineSep
	    + "* @param scale the desired scale of the result" + StringUtil.lineSep
	    + "* @return the result value" + StringUtil.lineSep
	    + "*/" + StringUtil.lineSep
	    + "public static BigDecimal intPower(BigDecimal x, long exponent,int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   if (exponent < 0) {" + StringUtil.lineSep
	    + "      return BigDecimal.valueOf(1).divide(intPower(x, -exponent, scale), scale,BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   BigDecimal power = BigDecimal.valueOf(1);" + StringUtil.lineSep

	    + "   // Loop to compute value^exponent." + StringUtil.lineSep
	    + "   while (exponent > 0) {" + StringUtil.lineSep

	    + "       // Is the rightmost bit a 1?" + StringUtil.lineSep
	    + "       if ((exponent & 1) == 1) {" + StringUtil.lineSep
	    + "           power = power.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "       }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // Square x and shift exponent 1 bit to the right." + StringUtil.lineSep
	    + "	x = x.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "       exponent >>= 1;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   return power;" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep

	    + "/**" + StringUtil.lineSep
	    + " * Compute the integral root of x to a given scale, x >= 0." + StringUtil.lineSep
	    + " * Use Newton's algorithm." + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param index the integral root value" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result" + StringUtil.lineSep
	    + " * @return the result value" + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "public static BigDecimal intRoot(BigDecimal x, long index,int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   // Check that x >= 0." + StringUtil.lineSep
	    + "   if (x.signum() < 0) {" + StringUtil.lineSep
	    + "       throw new IllegalArgumentException(\"x < 0\");" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep

	    + "   int        sp1 = scale + 1;" + StringUtil.lineSep
	    + "   BigDecimal n   = x;" + StringUtil.lineSep
	    + "   BigDecimal i   = BigDecimal.valueOf(index);" + StringUtil.lineSep
	    + "   BigDecimal im1 = BigDecimal.valueOf(index-1);" + StringUtil.lineSep
	    + "   BigDecimal tolerance = BigDecimal.valueOf(5).movePointLeft(sp1);" + StringUtil.lineSep
	    + "   BigDecimal xPrev;" + StringUtil.lineSep

	    + "   // The initial approximation is x/index." + StringUtil.lineSep
	    + "   x = x.divide(i, scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep

	    + "   // Loop until the approximations converge" + StringUtil.lineSep
	    + "   // (two successive approximations are equal after rounding)." + StringUtil.lineSep
	    + "   do {" + StringUtil.lineSep
	    + "       // x^(index-1)" + StringUtil.lineSep
	    + "       BigDecimal xToIm1 = intPower(x, index-1, sp1);" + StringUtil.lineSep

	    + "       // x^index" + StringUtil.lineSep
	    + "       BigDecimal xToI = x.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep

	    + "       // n + (index-1)*(x^index)" + StringUtil.lineSep
	    + "       BigDecimal numerator = n.add(im1.multiply(xToI)).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep

	    + "       // (index*(x^(index-1))" + StringUtil.lineSep
	    + "       BigDecimal denominator = i.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // x = (n + (index-1)*(x^index)) / (index*(x^(index-1)))" + StringUtil.lineSep
	    + "       xPrev = x;" + StringUtil.lineSep
	    + "       x = numerator.divide(denominator, sp1, BigDecimal.ROUND_DOWN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   } while (x.subtract(xPrev).abs().compareTo(tolerance) > 0);" + StringUtil.lineSep
	    + "   return x;" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute e^x to a given scale." + StringUtil.lineSep
	    + " * Break x into its whole and fraction parts and" + StringUtil.lineSep
	    + " * compute (e^(1 + fraction/whole))^whole using Taylor's formula." + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result" + StringUtil.lineSep
	    + " * @return the result value" + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "public static BigDecimal exp(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   // e^0 = 1" + StringUtil.lineSep
	    + "   if (x.signum() == 0) {" + StringUtil.lineSep
	    + "       return BigDecimal.valueOf(1);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep

	    + "   // If x is negative, return 1/(e^-x)." + StringUtil.lineSep
	    + "   else if (x.signum() == -1) {" + StringUtil.lineSep
	    + "       return BigDecimal.valueOf(1).divide(exp(x.negate(), scale), scale,BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Compute the whole part of x." + StringUtil.lineSep
	    + "   BigDecimal xWhole = x.setScale(0, BigDecimal.ROUND_DOWN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // If there isn't a whole part, compute and return e^x." + StringUtil.lineSep
	    + "   if (xWhole.signum() == 0) return expTaylor(x, scale);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Compute the fraction part of x." + StringUtil.lineSep
	    + "   BigDecimal xFraction = x.subtract(xWhole);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // z = 1 + fraction/whole" + StringUtil.lineSep
	    + "   BigDecimal z = BigDecimal.valueOf(1).add(xFraction.divide(" + StringUtil.lineSep
	    + "                               xWhole, scale," + StringUtil.lineSep
	    + "                               BigDecimal.ROUND_HALF_EVEN));" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // t = e^z" + StringUtil.lineSep
	    + "   BigDecimal t = expTaylor(z, scale);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   BigDecimal maxLong = BigDecimal.valueOf(Long.MAX_VALUE);" + StringUtil.lineSep
	    + "   BigDecimal result  = BigDecimal.valueOf(1);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Compute and return t^whole using intPower()." + StringUtil.lineSep
	    + "   // If whole > Long.MAX_VALUE, then first compute products" + StringUtil.lineSep
	    + "   // of e^Long.MAX_VALUE." + StringUtil.lineSep
	    + "   while (xWhole.compareTo(maxLong) >= 0) {" + StringUtil.lineSep
	    + "       result = result.multiply(" + StringUtil.lineSep
	    + "                           intPower(t, Long.MAX_VALUE, scale))" + StringUtil.lineSep
	    + "                   .setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "       xWhole = xWhole.subtract(maxLong);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "   return result.multiply(intPower(t, xWhole.longValue(), scale))" + StringUtil.lineSep
	    + "                   .setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute e^x to a given scale by the Taylor series." + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result" + StringUtil.lineSep
	    + " * @return the result value" + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "private static BigDecimal expTaylor(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   BigDecimal factorial = BigDecimal.valueOf(1);" + StringUtil.lineSep
	    + "   BigDecimal xPower    = x;" + StringUtil.lineSep
	    + "   BigDecimal sumPrev;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // 1 + x" + StringUtil.lineSep
	    + "   BigDecimal sum  = x.add(BigDecimal.valueOf(1));" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Loop until the sums converge" + StringUtil.lineSep
	    + "   // (two successive sums are equal after rounding)." + StringUtil.lineSep
	    + "   int i = 2;" + StringUtil.lineSep
	    + "   do {" + StringUtil.lineSep
	    + "       // x^i" + StringUtil.lineSep
	    + "       xPower = xPower.multiply(x)" + StringUtil.lineSep
	    + "                   .setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // i!" + StringUtil.lineSep
	    + "       factorial = factorial.multiply(BigDecimal.valueOf(i));" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // x^i/i!" + StringUtil.lineSep
	    + "       BigDecimal term = xPower" + StringUtil.lineSep
	    + "                           .divide(factorial, scale," + StringUtil.lineSep
	    + "                                   BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // sum = sum + x^i/i!" + StringUtil.lineSep
	    + "       sumPrev = sum;" + StringUtil.lineSep
	    + "       sum = sum.add(term);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       ++i;" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   } while (sum.compareTo(sumPrev) != 0);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   return sum;" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    +  "/**" + StringUtil.lineSep
	    + " * Compute the natural logarithm of x to a given scale, x > 0." + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "public static BigDecimal ln(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   // Check that x > 0." + StringUtil.lineSep
	    + "   if (x.signum() <= 0) {" + StringUtil.lineSep
	    + "       throw new IllegalArgumentException(\"x <= 0\");" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // The number of digits to the left of the decimal point." + StringUtil.lineSep
	    + "   int magnitude = x.toString().length() - x.scale() - 1;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   if (magnitude < 3) {" + StringUtil.lineSep
	    + "       return lnNewton(x, scale);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Compute magnitude*ln(x^(1/magnitude))." + StringUtil.lineSep
	    + "   else {" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // x^(1/magnitude)" + StringUtil.lineSep
	    + "       BigDecimal root = intRoot(x, magnitude, scale);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // ln(x^(1/magnitude))" + StringUtil.lineSep
	    + "       BigDecimal lnRoot = lnNewton(root, scale);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // magnitude*ln(x^(1/magnitude))" + StringUtil.lineSep
	    + "       return BigDecimal.valueOf(magnitude).multiply(lnRoot)" + StringUtil.lineSep
	    + "                   .setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute the natural logarithm of x to a given scale, x > 0." + StringUtil.lineSep
	    + " * Use Newton's algorithm." + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "private static BigDecimal lnNewton(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   int        sp1 = scale + 1;" + StringUtil.lineSep
	    + "   BigDecimal n   = x;" + StringUtil.lineSep
	    + "   BigDecimal term;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Convergence tolerance = 5*(10^-(scale+1))" + StringUtil.lineSep
	    + "   BigDecimal tolerance = BigDecimal.valueOf(5)" + StringUtil.lineSep
	    + "                                       .movePointLeft(sp1);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Loop until the approximations converge" + StringUtil.lineSep
	    + "   // (two successive approximations are within the tolerance)." + StringUtil.lineSep
	    + "   do {" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // e^x" + StringUtil.lineSep
	    + "       BigDecimal eToX = exp(x, sp1);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // (e^x - n)/e^x" + StringUtil.lineSep
	    + "       term = eToX.subtract(n)" + StringUtil.lineSep
	    + "                   .divide(eToX, sp1, BigDecimal.ROUND_DOWN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // x - (e^x - n)/e^x" + StringUtil.lineSep
	    + "       x = x.subtract(term);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   } while (term.compareTo(tolerance) > 0);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   return x.setScale(scale, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute the arctangent of x to a given scale, |x| < 1" + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result" + StringUtil.lineSep
	    + " * @return the result value" + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "public static BigDecimal arctan(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   // Check that |x| < 1." + StringUtil.lineSep
	    + "   if (x.abs().compareTo(BigDecimal.valueOf(1)) >= 0) {" + StringUtil.lineSep
	    + "       throw new IllegalArgumentException(\"|x| >= 1\");" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // If x is negative, return -arctan(-x)." + StringUtil.lineSep
	    + "   if (x.signum() == -1) {" + StringUtil.lineSep
	    + "       return arctan(x.negate(), scale).negate();" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "   else {" + StringUtil.lineSep
	    + "       return arctanTaylor(x, scale);" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute the arctangent of x to a given scale" + StringUtil.lineSep
	    + " * by the Taylor series, |x| < 1 " + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result" + StringUtil.lineSep
	    + " * @return the result value " + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "private static BigDecimal arctanTaylor(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   int     sp1     = scale + 1;" + StringUtil.lineSep
	    + "   int     i       = 3;" + StringUtil.lineSep
	    + "   boolean addFlag = false;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   BigDecimal power = x;" + StringUtil.lineSep
	    + "   BigDecimal sum   = x;" + StringUtil.lineSep
	    + "   BigDecimal term;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Convergence tolerance = 5*(10^-(scale+1))" + StringUtil.lineSep
	    + "   BigDecimal tolerance = BigDecimal.valueOf(5)" + StringUtil.lineSep
	    + "                                       .movePointLeft(sp1);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Loop until the approximations converge" + StringUtil.lineSep
	    + "   // (two successive approximations are within the tolerance)." + StringUtil.lineSep
	    + "   do {" + StringUtil.lineSep
	    + "       // x^i" + StringUtil.lineSep
	    + "       power = power.multiply(x).multiply(x)" + StringUtil.lineSep
	    + "                   .setScale(sp1, BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // (x^i)/i" + StringUtil.lineSep
	    + "       term = power.divide(BigDecimal.valueOf(i), sp1," + StringUtil.lineSep
	    + "                            BigDecimal.ROUND_HALF_EVEN);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // sum = sum +- (x^i)/i" + StringUtil.lineSep
	    + "       sum = addFlag ? sum.add(term)" + StringUtil.lineSep
	    + "                     : sum.subtract(term);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       i += 2;" + StringUtil.lineSep
	    + "       addFlag = !addFlag;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   } while (term.compareTo(tolerance) > 0);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   return sum;" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "/**" + StringUtil.lineSep
	    + " * Compute the square root of x to a given scale, x >= 0." + StringUtil.lineSep
	    + " * Use Newton's algorithm." + StringUtil.lineSep
	    + " * @param x the value of x" + StringUtil.lineSep
	    + " * @param scale the desired scale of the result2" + StringUtil.lineSep
	    + " * @return the result value" + StringUtil.lineSep
	    + " */" + StringUtil.lineSep
	    + "public static BigDecimal sqrt(BigDecimal x, int scale)" + StringUtil.lineSep
	    + "{" + StringUtil.lineSep
	    + "   // Check that x >= 0." + StringUtil.lineSep
	    + "   if (x.signum() < 0) {" + StringUtil.lineSep
	    + "       throw new IllegalArgumentException(\"x < 0\");" + StringUtil.lineSep
	    + "   }" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // n = x*(10^(2*scale))" + StringUtil.lineSep
	    + "   BigInteger n = x.movePointRight(scale << 1).toBigInteger();" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "  // The first approximation is the upper half of n." + StringUtil.lineSep
	    + "   int bits = (n.bitLength() + 1) >> 1;" + StringUtil.lineSep
	    + "   BigInteger ix = n.shiftRight(bits);" + StringUtil.lineSep
	    + "   BigInteger ixPrev;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   // Loop until the approximations converge" + StringUtil.lineSep
	    + "   // (two successive approximations are equal after rounding)." + StringUtil.lineSep
	    + "   do {" + StringUtil.lineSep
	    + "       ixPrev = ix;" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       // x = (x + n/x)/2" + StringUtil.lineSep
	    + "       ix = ix.add(n.divide(ix)).shiftRight(1);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "       Thread.yield();" + StringUtil.lineSep
	    + "   } while (ix.compareTo(ixPrev) != 0);" + StringUtil.lineSep
	    + "" + StringUtil.lineSep
	    + "   return new BigDecimal(ix, scale);" + StringUtil.lineSep
	    + "}" + StringUtil.lineSep
	    + "");
		strPresentText.append("}" + StringUtil.lineSep);
		return strPresentText.toString();
	}

	/**
	 * プリプロセッサ構文を追加する.
	 * @param pPreprocessor 追加する構文インスタンス
	 */
	public void addPreprocessor(SyntaxPreprocessor pPreprocessor) {
		//------------------------------------------
		//重複チェック
		//------------------------------------------

		/*先頭から照合*/
		for (SyntaxPreprocessor it: m_vecSynPreprocessor) {

			/*照合*/
			if (it.matches(pPreprocessor)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		//------------------------------------------
		//ベクタに追加
		//------------------------------------------
		/*要素追加*/
		m_vecSynPreprocessor.add(pPreprocessor);
	}

	/**
	 * 宣言構文を追加する.
	 * @param pDeclaration 追加する構文インスタンス
	 */
	public void addDeclaration(SyntaxDeclaration pDeclaration) {
		//------------------------------------------
		//重複チェック
		//------------------------------------------

		/*先頭から照合*/
		for (SyntaxDeclaration it: m_vecSynDeclaration) {

			/*照合*/
			if (it.matches(pDeclaration)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		//------------------------------------------
		//ベクタに追加
		//------------------------------------------
		/*要素追加*/
		m_vecSynDeclaration.add(pDeclaration);
	}

	/**
	 * 関数構文を追加する.
	 * @param pFunction 追加する構文インスタンス
	 */
	public void addFunction(SyntaxFunction pFunction) {
		/*要素追加*/
		m_vecSynFunction.add(pFunction);
	}

}
