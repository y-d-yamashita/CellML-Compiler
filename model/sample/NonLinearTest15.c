#include<stdio.h>
#include<stdlib.h>
#include<math.h>


int main ( int argc , char** argv ) ;
double newton5 ( double kX1 , double Y1 ) ;
double func5 ( double kX1 , double Y1 ) ;
double dfunc5 ( double kX1 , double Y1 ) ;

int main ( int argc , char** argv ) {

	double* X1;
	double* t1;
	double* X1end;
	double* Y1;
	double* t1end;
	double* kX1;
	double delt1;
	double t1init;
	double X1init;

	X1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	t1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	Y1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	X1[0] = X1init;
	t1[0] = t1init;
	
	
	n1 = 0;
	do{

		Y1[n1] =  ( X1[n1] /  ( - (double)1 )  ) ;
		kX1[n1] = newton5 ( kX1[n1] , Y1[n1] ) ;
		X1[ ( n1 + 1 ) ] =  ( X1[n1] +  ( kX1[n1] * delt1 )  ) ;
		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		
		
		n1 =  ( n1 + 1 ) ;

	}while(!(n1 == 100));

	X1end = X1[ ( n1 + 1 ) ];
	
	
	
	
	
	free ( X1 ) ; 
	free ( t1 ) ; 
	free ( X1end ) ; 
	free ( Y1 ) ; 
	free ( t1end ) ; 
	free ( kX1 ) ; 
null}


double newton5 ( double kX1 , double Y1 ) {


	int max = 0;
	double eps;
	double kX1_next;

	do {

		max ++;
		if(max > 1000){
			printf("error:no convergence\n");break;
		}
		kX1_next = kX1 - ( func5(kX1,Y1) / 	dfunc5(kX1,Y1) );
		kX1 = kX1_next;
		eps = func5(kX1,Y1);

	} while( eps < -1.0E-50 || 1.0E-50 < eps );

	return kX1;
}


double func5 ( double kX1 , double Y1 ) {


	return  (  ( kX1 * kX1 )  - Y1 ) ;

}


double dfunc5 ( double kX1 , double Y1 ) {


	return  (  (  ( (double)1 * kX1 )  +  ( kX1 * (double)1 )  )  - (double)0 ) ;

}



