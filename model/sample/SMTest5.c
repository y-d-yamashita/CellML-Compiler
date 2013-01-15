#include<stdio.h>
#include<stdlib.h>
#include<math.h>


int main ( int argc , char** argv ) ;
void simulNewton0 ( double* simulSet , double var2 ) ;
double simulFunc0 ( double* simulSet , double var2 , int i ) ;
double jacobi0 ( double* simulSet , double var2 , int i , int j ) ;

int main ( int argc , char** argv ) {

	double* t1;
	double* X1;
	double* X2[__MAX_ARRAY_NUM];
	double* Y1;
	double* kX1;
	double* Y2[__MAX_ARRAY_NUM];
	double* kX2[__MAX_ARRAY_NUM];
	double* Z1;
	double* t1end;
	double* X1end;
	double* X2end;
	double t1init;
	double X1init;
	double delt2;
	double X2init;
	double delt1;

	t1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	X1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	Y1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	kX1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	Y2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	kX2 = malloc (  ( sizeof( double ) *  ( __MAX_ARRAY_NUM * __MAX_ARRAY_NUM )  )  ) ; ;
	Z1 = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	X2end = malloc (  ( sizeof( double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	X1[0] = X1init;
	t1[0] = t1init;
	
	
	n1 = 0;
	do{

		simulNewton0 ( simulSet , X1[n1] ) ;
		Y1[n1] = simulSet[0];
		Z1[n1] = simulSet[1];
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
	
	
	
	
	
	free ( t1 ) ; 
	free ( X1 ) ; 
	free ( X2 ) ; 
	free ( Y1 ) ; 
	free ( kX1 ) ; 
	free ( Y2 ) ; 
	free ( kX2 ) ; 
	free ( Z1 ) ; 
	free ( t1end ) ; 
	free ( X1end ) ; 
	free ( X2end ) ; 
null}


void simulNewton0 ( double* simulSet , double var2 ) {


	int max = 0;
	int i, j, k;
	double buf;
	double det;
	double pro;
	double eps;
	double f[2];
	double jac[2][2];
	double cpy[2][2];
	double inv[2][2];
	double simulSet_next[2];


	do {
		max ++;
		if(max > 1000){
			printf("error:no convergence\n");break;
		}
		det = 1.0;
		eps = 0.0;

		for(i=0;i<2;i++){
			for(j=0;j<2;j++){
				jac[i][j] = jacobi(simulSet,var2,i,j);
				cpy[i][j] = jacobi(simulSet,var2,i,j);

			}
		}

		for(i=0;i<2;i++){
			for(j=0;j<2;j++){
				if(i<j){
					buf = cpy[j][i] / cpy[i][i];
					for(k=0;k<2;k++){
						cpy[j][k] -= cpy[i][k] * buf;
					}
				}
			}
		}
		for(i=0;i<2;i++){
			det *= cpy[i][i];
		}
		if(det == 0.0){
			printf("error:det is zero\n");break;
		}
		for(i=0;i<2;i++){
			for(j=0;j<2;j++){
				if(i==j) inv[i][j] = 1.0;
				else inv[i][j] = 0.0;
			}
		}
		for(i=0;i<2;i++){
			buf = 1 / jac[i][i];
			for(j=0;j<2;j++){
				jac[i][j] *= buf;
				inv[i][j] *= buf;
			}
			for(j=0;j<2;j++){
				if(i!=j){
					buf = jac[j][i];
					for(k=0;k<2;k++){
						jac[j][k] -= jac[i][k]*buf;
						inv[j][k] -= inv[i][k]*buf;
					}
				}
			}
		}
		for(i=0;i<2;i++){
			pro = 0.0;
			for(j=0;j<2;j++){
				pro += inv[i][j] * func(simulSet,var2,j);
			}
			simulSet_next[i] = simulSet[i] - pro;
		}
		for(i=0;i<2;i++){
			simulSet[i] = simulSet_next[i];
		}
		for(i=0;i<2;i++){
			f[i] = func(simulSet,var2,i);
			eps += f[i]*f[i];
		}
	} while (1.0E-50 < sqrt(eps));

}


double simulFunc0 ( double* simulSet , double var2 , int i ) {



	double var0 = simulSet[0];
	double var1 = simulSet[1];

	if(i==0) return  ( var0 -  ( var1 +  (  ( - (double)1 )  * var2 )  )  ) ;
	if(i==1) return  ( var1 -  (  (  ( - (double)1 )  * var0 )  + (double)1 )  ) ;

}


double jacobi0 ( double* simulSet , double var2 , int i , int j ) {



	double var0 = simulSet[0];
	double var1 = simulSet[1];

	if(i==0 && j==0) return  (  ( (double)1 -  ( (double)0 +  (  (  ( - (double)0 )  * var2 )  +  (  ( - (double)1 )  * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==0 && j==1) return  (  ( (double)0 -  ( (double)1 +  (  (  ( - (double)0 )  * var2 )  +  (  ( - (double)1 )  * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==1 && j==0) return  (  ( (double)0 -  (  (  (  ( - (double)0 )  * var0 )  +  (  ( - (double)1 )  * (double)1 )  )  + (double)0 )  )  - (double)0 ) ;
	if(i==1 && j==1) return  (  ( (double)1 -  (  (  (  ( - (double)0 )  * var0 )  +  (  ( - (double)1 )  * (double)0 )  )  + (double)0 )  )  - (double)0 ) ;

}



