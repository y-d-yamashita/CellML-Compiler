#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double** Main_a_xr;
	double* Main_d_x;
	double xend;
	double* Main_a;
	double** Main_ka_kr;
	double* Main_kd_k;
	double* Main_e_i;
	double Main_c_zr;
	double Main_b_zr;
	double d;
	int m;
	int n;
	int i0;
	int i1;

	Main_a_xr = (double**)malloc (  ( sizeof(double *) * __MAX_ARRAY_NUM )  ) ; ;
	Main_d_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_a = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ka_kr = (double**)malloc (  ( sizeof(double *) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kd_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_e_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	for(i0 = 0; ( i0 < __MAX_ARRAY_NUM ) ;i0++){

		Main_a_xr[i0] = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
		Main_ka_kr[i0] = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;

	}

	Main_c_zr = (double)0.0;
	Main_b_zr = (double)0.0;
	d = (double)0.01;
	
	
	Main_d_x[0] = (double)0.0;
	
	
	m = 0;
	do{

		
		
		Main_a_xr[m][0] = Main_d_x[m];
		
		
		n = 0;
		do{

			Main_ka_kr[m][n] =  ( (double)0 -  ( Main_b_zr * Main_c_zr )  ) ;
			Main_a_xr[m][ ( n + 1 ) ] = Main_ka_kr[m][n];
			
			
			n =  ( n + 1 ) ;

		}while(!( ( n > 100 ) ));

		Main_a[m] = Main_a_xr[m][ ( n + 1 ) ];
		
		
		Main_e_i[m] = Main_a[m];
		Main_kd_k[m] =  ( (double)3 * Main_e_i[m] ) ;
		Main_d_x[ ( m + 1 ) ] =  ( Main_d_x[m] +  ( Main_kd_k[m] * d )  ) ;
		
		
		
		
		m =  ( m + 1 ) ;

	}while(!( ( m > 300 ) ));

	xend = Main_d_x[ ( m + 1 ) ];
	
	
	
	
	
	{

		free ( Main_d_x ) ; 
		free ( Main_a ) ; 
		free ( Main_kd_k ) ; 
		free ( Main_e_i ) ; 

	}

	for(i0 = 0; ( i0 < __MAX_ARRAY_NUM ) ;i0++){

		free ( Main_a_xr[i0] ) ; 
		free ( Main_ka_kr[i0] ) ; 

	}

	return 0;
}



