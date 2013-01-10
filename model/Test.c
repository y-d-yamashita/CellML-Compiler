#include<stdio.h>
#include<stdlib.h>
#include<math.h>


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* X1;
	double* t1;
	double* X1end;
	double* kX1;
	double* t1end;
	double* Y1;
	double X1init;
	double delt1;
	double t1init;

	X1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	t1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	Y1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	X1[0] = X1init;
	t1[0] = t1init;
	
	
	n1 = 0;
	do{

		Y1[n1] =  (  ( - (double)1 )  * X1[n1] ) ;
		kX1[n1] = Y1[n1];
		X1[ ( n1 + 1 ) ] =  ( X1[n1] +  ( kX1[n1] * delt1 )  ) ;
		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		
		
		n1 =  ( n1 + 1 ) ;

	}while(!(n1 == 100));

	X1end = X1[ ( n1 + 1 ) ];
	
	
	
	
	
	free ( X1 ) ; 
	free ( t1 ) ; 
	free ( X1end ) ; 
	free ( kX1 ) ; 
	free ( t1end ) ; 
	free ( Y1 ) ; 
}



