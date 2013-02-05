import java.lang.Math


public static void main ( String args[] ) {

	int __DATA_NUM = 1;
	double X1[];
	double t1[];
	double X1end;
	double Y1[];
	double t1end;
	double kX1[];
	double delt1;
	double t1init;
	double X1init;
	int n1;

	X1 = new double[__DATA_NUM];
	t1 = new double[__DATA_NUM];
	Y1 = new double[__DATA_NUM];
	kX1 = new double[__DATA_NUM];
	delt1 = (double)0.0;
	t1init = (double)0.0;
	X1init = (double)0.0;
	
	
	t1[0] = t1init;
	X1[0] = X1init;
	
	
	n1 = 0;
	do{

		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		Y1[n1] =  ( X1[n1] /  ( - (double)1 )  ) ;
		kX1[n1] = newton5 ( kX1[n1] , Y1[n1] ) ;
		X1[ ( n1 + 1 ) ] =  ( X1[n1] +  ( kX1[n1] * delt1 )  ) ;
		
		
		n1 =  ( n1 + 1 ) ;

	}while(!(n1 == 100));

	X1end = X1[ ( n1 + 1 ) ];
	
	
	
	
	
}


public static void newton5 ( double var0 , double var1 ) {


	int max = 0;
	double eps;
	double var0_next;

	do {

		max ++;
		if(max > 1000){
			System.out.println("error:no convergence\n");break;
		}
		var0_next = var0 - ( func5(var0,var1) / dfunc5(var0,var1) );
		var0 = var0_next;
		eps = func5(var0,var1);

	} while( eps < -1.0E-5 || 1.0E-5 < eps );

	return var0;
}


public static double func5 ( double var0 , double var1 ) {


	return  (  ( var0 * var0 )  - var1 ) ;

}


public static double dfunc5 ( double var0 , double var1 ) {


	return  (  (  ( (double)1 * var0 )  +  ( var0 * (double)1 )  )  - (double)0 ) ;

}



