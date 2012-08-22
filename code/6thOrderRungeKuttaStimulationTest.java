
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.math.BigDecimal;


public final class 6thOrderRungeKuttaStimulationTest{
public static void main ( String args[] ) {

	int __i;
	double xi[];
	double xo[];
	double x0[];
	double x1[];
	double x2[];
	double x3[];
	double x4[];
	double x5[];
	double x6[];
	double x7[];
	double x8[];
	double k1[];
	double k2[];
	double k3[];
	double k4[];
	double k5[];
	double k6[];
	double k7[];
	double k8[];
	double y0[];
	double y1[];
	double y2[];
	double y3[];
	double y4[];
	double y5[];
	double y6[];
	double y7[];
	double z[] = {0,0.03,1.2,0.3};
	double t;
	double d = 0.010000;
	int __DATA_NUM = 1;

	xi = new double[ ( 2 * __DATA_NUM ) ];
	xo = new double[ ( 2 * __DATA_NUM ) ];
	x0 = new double[ ( 2 * __DATA_NUM ) ];
	x1 = new double[ ( 2 * __DATA_NUM ) ];
	x2 = new double[ ( 2 * __DATA_NUM ) ];
	x3 = new double[ ( 2 * __DATA_NUM ) ];
	x4 = new double[ ( 2 * __DATA_NUM ) ];
	x5 = new double[ ( 2 * __DATA_NUM ) ];
	x6 = new double[ ( 2 * __DATA_NUM ) ];
	x7 = new double[ ( 2 * __DATA_NUM ) ];
	x8 = new double[ ( 2 * __DATA_NUM ) ];
	y0 = new double[ ( 1 * __DATA_NUM ) ];
	y1 = new double[ ( 1 * __DATA_NUM ) ];
	y2 = new double[ ( 1 * __DATA_NUM ) ];
	y3 = new double[ ( 1 * __DATA_NUM ) ];
	y4 = new double[ ( 1 * __DATA_NUM ) ];
	y5 = new double[ ( 1 * __DATA_NUM ) ];
	y6 = new double[ ( 1 * __DATA_NUM ) ];
	y7 = new double[ ( 1 * __DATA_NUM ) ];
	k1 = new double[ ( 2 * __DATA_NUM ) ];
	k2 = new double[ ( 2 * __DATA_NUM ) ];
	k3 = new double[ ( 2 * __DATA_NUM ) ];
	k4 = new double[ ( 2 * __DATA_NUM ) ];
	k5 = new double[ ( 2 * __DATA_NUM ) ];
	k6 = new double[ ( 2 * __DATA_NUM ) ];
	k7 = new double[ ( 2 * __DATA_NUM ) ];
	k8 = new double[ ( 2 * __DATA_NUM ) ];
	
	/***** Added variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;	// 
	double stim_dur = 1;		//
	
	/***** Initial values of v and w (differential variables) *****/
	xi[ ( 0 * __DATA_NUM) ] = -1.501250563778375;	// v
	xi[ ( 1 * __DATA_NUM) ] = -0.37621367749846896;	// w
	
	/***** Write the results *****/
	int cntr = 0;
	int div = 100;
	try{
		FileWriter fw = new FileWriter("SampleData6thOrderRungeKuttaStimulationTest.csv");
		PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	
	
	for(t = 0.000000; ( t <= 400.000000 ) ;t =  ( t + d ) ){

		for(__i = 0; ( __i < __DATA_NUM ) ;__i++){

			/***** Stimulation part *****/
			if ( ( ( t >= stim_start )  &&  ( (t - stim_start) <= stim_dur ) ) || ( ( t >= stim_interval ) &&  ( (t - stim_interval) <= stim_dur ) ) ) { //
				z[0] = 1.0;
			}
			else{
				z[0] = 0.0;
			}
			
			x0[ (  ( 0 * __DATA_NUM )  + __i ) ] = xi[ (  ( 0 * __DATA_NUM )  + __i ) ];
			x0[ (  ( 1 * __DATA_NUM )  + __i ) ] = xi[ (  ( 1 * __DATA_NUM )  + __i ) ];
			y0[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] * x0[ (  ( 0 * __DATA_NUM )  + __i ) ] * x0[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k1[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y0[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x0[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k1[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x0[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x1[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] *  ( d / (double)18 )  )  ) ;
			x1[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] *  ( d / (double)18 )  )  ) ;
			y1[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k2[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y1[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x1[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k2[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x1[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x2[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] *  ( d / (double)12 )  )  )  +  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] *  ( d / (double)4 )  )  ) ;
			x2[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] *  ( d / (double)12 )  )  )  +  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] *  ( d / (double)4 )  )  ) ;
			y2[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x2[ (  ( 0 * __DATA_NUM )  + __i ) ] * x2[ (  ( 0 * __DATA_NUM )  + __i ) ] * x2[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k3[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x2[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y2[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x2[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k3[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x2[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x2[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x3[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)2 *  ( d / (double)81 )  )  )  +  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)4 *  ( d / (double)27 )  )  +  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)8 *  ( d / (double)81 )  )  ) ;
			x3[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)2 *  ( d / (double)81 )  )  )  +  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)4 *  ( d / (double)27 )  )  +  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)8 *  ( d / (double)81 )  )  ) ;
			y3[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x3[ (  ( 0 * __DATA_NUM )  + __i ) ] * x3[ (  ( 0 * __DATA_NUM )  + __i ) ] * x3[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k4[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x3[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y3[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x3[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k4[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x3[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x3[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x4[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)40 *  ( d / (double)33 )  )  +  ( -  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)4 *  ( d / (double)11 )  )  )  +  ( -  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)56 *  ( d / (double)11 )  )  )  +  ( k4[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)54 *  ( d / (double)11 )  )  ) ;
			x4[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)40 *  ( d / (double)33 )  )  +  ( -  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)4 *  ( d / (double)11 )  )  )  +  ( -  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)56 *  ( d / (double)11 )  )  )  +  ( k4[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)54 *  ( d / (double)11 )  )  ) ;
			y4[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x4[ (  ( 0 * __DATA_NUM )  + __i ) ] * x4[ (  ( 0 * __DATA_NUM )  + __i ) ] * x4[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k5[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x4[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y4[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x4[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k5[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x4[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x4[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x5[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)369 *  ( d / (double)73 )  )  )  +  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)72 *  ( d / (double)73 )  )  +  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)5380 *  ( d / (double)219 )  )  +  ( -  ( k4[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)12285 *  ( d / (double)584 )  )  )  +  ( k5[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)2695 *  ( d / (double)1752 )  )  ) ;
			x5[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)369 *  ( d / (double)73 )  )  )  +  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)72 *  ( d / (double)73 )  )  +  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)5380 *  ( d / (double)219 )  )  +  ( -  ( k4[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)12285 *  ( d / (double)584 )  )  )  +  ( k5[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)2695 *  ( d / (double)1752 )  )  ) ;
			y5[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x5[ (  ( 0 * __DATA_NUM )  + __i ) ] * x5[ (  ( 0 * __DATA_NUM )  + __i ) ] * x5[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k6[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x5[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y5[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x5[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k6[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x5[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x5[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x6[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)8716 *  ( d / (double)891 )  )  )  +  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)656 *  ( d / (double)297 )  )  +  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)39520 *  ( d / (double)891 )  )  +  ( -  ( k4[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)416 *  ( d / (double)11 )  )  )  +  ( k5[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)52 *  ( d / (double)27 )  )  ) ;
			x6[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( -  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)8716 *  ( d / (double)891 )  )  )  +  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)656 *  ( d / (double)297 )  )  +  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)39520 *  ( d / (double)891 )  )  +  ( -  ( k4[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)416 *  ( d / (double)11 )  )  )  +  ( k5[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)52 *  ( d / (double)27 )  )  ) ;
			y6[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x6[ (  ( 0 * __DATA_NUM )  + __i ) ] * x6[ (  ( 0 * __DATA_NUM )  + __i ) ] * x6[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k7[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x6[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y6[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x6[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k7[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x6[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x6[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x7[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)3015 *  ( d / (double)256 )  )  +  ( -  ( k2[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)9 *  ( d / (double)4 )  )  )  +  ( -  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)4219 *  ( d / (double)78 )  )  )  +  ( k4[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)5985 *  ( d / (double)128 )  )  +  ( -  ( k5[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)539 *  ( d / (double)384 )  )  )  +  ( k7[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)693 *  ( d / (double)3328 )  )  ) ;
			x7[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)3015 *  ( d / (double)256 )  )  +  ( -  ( k2[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)9 *  ( d / (double)4 )  )  )  +  ( -  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)4219 *  ( d / (double)78 )  )  )  +  ( k4[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)5985 *  ( d / (double)128 )  )  +  ( -  ( k5[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)539 *  ( d / (double)384 )  )  )  +  ( k7[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)693 *  ( d / (double)3328 )  )  ) ;
			y7[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x7[ (  ( 0 * __DATA_NUM )  + __i ) ] * x7[ (  ( 0 * __DATA_NUM )  + __i ) ] * x7[ (  ( 0 * __DATA_NUM )  + __i ) ] ) ;
			k8[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( x7[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y7[ (  ( 0 * __DATA_NUM )  + __i ) ] / (double)3 )  )  - x7[ (  ( 1 * __DATA_NUM )  + __i ) ] )  + z[0] ) ;
			k8[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( z[1] *  (  ( x7[ (  ( 0 * __DATA_NUM )  + __i ) ] + z[2] )  -  ( z[3] * x7[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  ) ;
			x8[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)57 *  ( d / (double)640 )  )  +  ( -  ( k3[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)16 *  ( d / (double)65 )  )  )  +  ( k4[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)1377 *  ( d / (double)2240 )  )  +  ( k5[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)121 *  ( d / (double)320 )  )  +  ( k7[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)891 *  ( d / (double)8320 )  )  +  ( k8[ (  ( 0 * __DATA_NUM )  + __i ) ] * (double)2 *  ( d / (double)35 )  )  ) ;
			x8[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x0[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)57 *  ( d / (double)640 )  )  +  ( -  ( k3[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)16 *  ( d / (double)65 )  )  )  +  ( k4[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)1377 *  ( d / (double)2240 )  )  +  ( k5[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)121 *  ( d / (double)320 )  )  +  ( k7[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)891 *  ( d / (double)8320 )  )  +  ( k8[ (  ( 1 * __DATA_NUM )  + __i ) ] * (double)2 *  ( d / (double)35 )  )  ) ;
			xo[ (  ( 0 * __DATA_NUM )  + __i ) ] = x8[ (  ( 0 * __DATA_NUM )  + __i ) ];
			xo[ (  ( 1 * __DATA_NUM )  + __i ) ] = x8[ (  ( 1 * __DATA_NUM )  + __i ) ];
			xi[ (  ( 0 * __DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __DATA_NUM )  + __i ) ];
			xi[ (  ( 1 * __DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __DATA_NUM )  + __i ) ];

			/***** Print the results *****/
			if((cntr % div) == 0 && t<400){
//				pw.println(t+","+xo[ (  ( 0 * __DATA_NUM )  + __i ) ] +", "+ xo[ (  ( 1 * __DATA_NUM )  + __i ) ]);
				System.out.println(xo[ (  ( 0 * __DATA_NUM + __i) ) ]);
			}
//			pw.println(t+","+xo[ (  ( 0 * __DATA_NUM )  + __i ) ] +", "+ xo[ (  ( 1 * __DATA_NUM )  + __i ) ]);
		}
		cntr = cntr + 1;
	}
	

}catch(FileNotFoundException e){
	e.printStackTrace();
}catch(IOException e){
	e.printStackTrace();
}
}
}


