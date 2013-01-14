#include<stdio.h>
#include<stdlib.h>
#include<math.h>


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* X1;
	double* t1;
	double* X2[__MAX_ARRAY_NUM];
	double* Y2[__MAX_ARRAY_NUM];
	double* kX2[__MAX_ARRAY_NUM];
	double* X1end;
	double* Y1;
	double* t1end;
	double* kX1;
	double* Z1;
	double* X2end;
	double X2init;
	double delt1;
	double t1init;
	double X1init;
	double delt2;

	X1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	t1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	Y2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	kX2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	Y1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	Z1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2end = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	X1[0] = X1init;
	t1[0] = t1init;
	
	
	n1 = 0;
	do{

		Y1[n1] =  ( Z1[n1] +  (  ( - (double)1 )  * X1[n1] )  ) ;
		Z1[n1] =  (  (  ( - (double)1 )  * Y1[n1] )  + (double)1 ) ;
		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		
		
		X2[n1][0] = Y1[n1];
		
		
		n2 = 0;
		do{

			Y2[n1][n2] =  (  ( - (double)1 )  * X2[n1][n2] ) ;
			kX2[n1][n2] = Y2[n1][n2];
			X2[n1][ ( n2 + 1 ) ] =  ( X2[n1][n2] +  ( kX2[n1][n2] * delt2 )  ) ;
			
			
			n2 =  ( n2 + 1 ) ;

		}while(!(n2 == 100));

		X2end[n1] = X2[n1][ ( n2 + 1 ) ];
		
		
		kX1[n1] =  ( Y1[n1] + X2end[n1] ) ;
		X1[ ( n1 + 1 ) ] =  ( X1[n1] +  ( kX1[n1] * delt1 )  ) ;
		
		
		
		
		n1 =  ( n1 + 1 ) ;

	}while(!(n1 == 100));

	X1end = X1[ ( n1 + 1 ) ];
	t1end = t1[ ( n1 + 1 ) ];
	
	
	
	
	
	free ( X1 ) ; 
	free ( t1 ) ; 
	free ( X2 ) ; 
	free ( Y2 ) ; 
	free ( kX2 ) ; 
	free ( X1end ) ; 
	free ( Y1 ) ; 
	free ( t1end ) ; 
	free ( kX1 ) ; 
	free ( Z1 ) ; 
	free ( X2end ) ; 
null}



