#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;
double newton5 ( double var0 , double var1 ) ;
double func5 ( double var0 , double var1 ) ;
double dfunc5 ( double var0 , double var1 ) ;

int main ( int argc , char** argv ) {

	double* t1;
	double* X1;
	double* Y1;
	double* kX1;
	double t1end;
	double X1end;
	double t1init;
	double X1init;
	double delt1;
	int n1;
	int i0;
	int i1;

	t1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	X1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Y1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	t1init = (double)0.0;
	X1init = (double)0.0;
	delt1 = (double)0.0;
	
	
	t1[0] = t1init;
	X1[0] = X1init;
	
	
	n1 = 0;
	do{

		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		Y1[n1] =  ( X1[n1] /  ( - (double)1 )  ) ;
		kX1[n1] = newton5 ( kX1[n1] , Y1[n1] ) ;
		X1[ ( n1 + 1 ) ] =  ( X1[n1] +  ( kX1[n1] * delt1 )  ) ;
		
		
		n1 =  ( n1 + 1 ) ;

	}while( (n1 < 100) );

	X1end = X1[ ( n1 + 1 ) ];
	
	
	
	
	
	{

		free ( t1 ) ; 
		free ( X1 ) ; 
		free ( Y1 ) ; 
		free ( kX1 ) ; 

	}

	return 0;
}


double newton5 ( double var0 , double var1 ) {


	int max = 0;
	double eps;
	double var0_next;

	do {

		max ++;
		if(max > 1000){
			printf("error:no convergence\n");break;
		}
		var0_next = var0 - ( func5(var0,var1) / dfunc5(var0,var1) );
		var0 = var0_next;
		eps = func5(var0,var1);

	} while( eps < -1.0E-5 || 1.0E-5 < eps );

	return var0;
}


double func5 ( double var0 , double var1 ) {


	return  (  ( var0 * var0 )  - var1 ) ;

}


double dfunc5 ( double var0 , double var1 ) {


	return  (  (  ( (double)1 * var0 )  +  ( var0 * (double)1 )  )  - (double)0 ) ;

}



