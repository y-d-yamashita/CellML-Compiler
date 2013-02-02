#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* X1;
	double* t1;
	double** X2;
	double** Y2;
	double** kX2;
	double X1end;
	double* Y1;
	double t1end;
	double* kX1;
	double* X2end;
	double X2init;
	double delt1;
	double t1init;
	double X1init;
	double delt2;
	int n1;
	int n2;
	int i0;
	int i1;

	X1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	t1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2 = (double**)malloc (  ( sizeof(double *) * __MAX_ARRAY_NUM )  ) ; ;
	Y2 = (double**)malloc (  ( sizeof(double *) * __MAX_ARRAY_NUM )  ) ; ;
	kX2 = (double**)malloc (  ( sizeof(double *) * __MAX_ARRAY_NUM )  ) ; ;
	Y1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2end = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	for(i0 = 0; ( i0 < __MAX_ARRAY_NUM ) ;i0++){

		X2[i0] = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
		Y2[i0] = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
		kX2[i0] = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;

	}

	
	
	t1[0] = t1init;
	X1[0] = X1init;
	
	
	n1 = 0;
	do{

		t1[ ( n1 + 1 ) ] =  ( t1[n1] + delt1 ) ;
		Y1[n1] =  (  ( - (double)1 )  * X1[n1] ) ;
		
		
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

	t1end = t1[ ( n1 + 1 ) ];
	X1end = X1[ ( n1 + 1 ) ];
	
	
	
	
	
	{

		free ( X1 ) ; 
		free ( t1 ) ; 
		free ( Y1 ) ; 
		free ( kX1 ) ; 
		free ( X2end ) ; 

	}

	for(i0 = 0; ( i0 < __MAX_ARRAY_NUM ) ;i0++){

		free ( X2[i0] ) ; 
		free ( Y2[i0] ) ; 
		free ( kX2[i0] ) ; 

	}

	return 0;
}



