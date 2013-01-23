#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* Main_x_x;
	double* Main_time_t;
	double* Main_y_x;
	double* Main_ky_k;
	double x_xend;
	double* Main_kx_k;
	double r_iend;
	double y_xend;
	double t_tend;
	double* Main_r_i;
	double Main_beta_z;
	double Main_epsilon_z;
	double Main_gamma_z;
	double Main_zz_z;
	int n1;
	int i0;
	int i1;
	
	/***** variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;
	
	Main_x_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	/***** initial values *****/
	Main_x_x[0] = (double)-1.501250563778375;
	Main_y_x[0] = (double)-0.37621367749846896;
	Main_epsilon_z = 0.03;
	Main_beta_z = 1.2;
	Main_gamma_z = 0.3;
	
	Main_time_t[0] = (double)0.0;
	
	
	n1 = 0;
	do{
		
		/***** Stimulation part *****/
		if ( ( ( Main_time_t[n1] >= stim_start )  &&  ( (Main_time_t[n1] - stim_start) <= stim_dur ) ) || ( ( Main_time_t[n1] >= stim_interval ) &&  ( (Main_time_t[n1] - stim_interval) <= stim_dur ) ) ) { 
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}
		
		Main_r_i[n1] =  ( Main_x_x[n1] * Main_x_x[n1] * Main_x_x[n1] ) ;
		Main_kx_k[n1] =  (  (  ( Main_x_x[n1] -  ( Main_r_i[n1] / (double)3 )  )  - Main_y_x[n1] )  + Main_zz_z ) ;
		Main_ky_k[n1] =  ( Main_epsilon_z *  (  ( Main_x_x[n1] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x[n1] )  )  ) ;
		Main_x_x[ ( n1 + 1 ) ] =  ( Main_x_x[n1] +  ( Main_kx_k[n1] * (double)0.01 )  ) ;
		Main_y_x[ ( n1 + 1 ) ] =  ( Main_y_x[n1] +  ( Main_ky_k[n1] * (double)0.01 )  ) ;
		Main_time_t[ ( n1 + 1 ) ] =  ( Main_time_t[n1] + (double)0.01 ) ;
		
		/***** Output part *****/
		printf("%lf,%lf,%lf\n",Main_time_t[n1+1],Main_x_x[n1+1],Main_y_x[n1+1]);
		
		n1 =  ( n1 + 1 ) ;

	}while(!( ( Main_time_t[n1] > 400 ) ));

	x_xend = Main_x_x[ ( n1 + 1 ) ];
	y_xend = Main_y_x[ ( n1 + 1 ) ];
	r_iend = Main_r_i[ ( n1 + 1 ) ];
	t_tend = Main_time_t[ ( n1 + 1 ) ];
	
	
	
	
	
	{

		free ( Main_x_x ) ; 
		free ( Main_time_t ) ; 
		free ( Main_y_x ) ; 
		free ( Main_ky_k ) ; 
		free ( Main_kx_k ) ; 
		free ( Main_r_i ) ; 

	}

	return 0;
}



