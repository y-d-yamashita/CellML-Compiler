#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* Main_y_x2;
	double* Main_time_t4;
	double* Main_y_x1;
	double* Main_y_x3;
	double* Main_time_t3;
	double* Main_x_x1;
	double* Main_y_x4;
	double* Main_x_x3;
	double* Main_x_x2;
	double* Main_x_x4;
	double* Main_time_t2;
	double* Main_time_t1;
	double* Main_r_i4;
	double* Main_kx_k3;
	double* Main_ky_k2;
	double x;
	double* Main_kx_k2;
	double* Main_kx_k4;
	double y;
	double* Main_r_i1;
	double* Main_ky_k4;
	double* Main_r_i3;
	double* Main_ky_k3;
	double t;
	double* Main_ky_k1;
	double* Main_kx_k1;
	double* Main_r_i2;
	double Main_zz_z;
	double Main_epsilon_z;
	double d;
	double Main_gamma_z;
	double Main_beta_z;
	int n;
	int i0;
	int i1;
	
	/***** variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;

	Main_y_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k4 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k3 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_zz_z = (double)0.0;
	Main_epsilon_z = (double)0.03;
	d = (double)0.01;
	Main_gamma_z = (double)0.3;
	Main_beta_z = (double)1.2;
	
	
	Main_time_t1[0] = (double)0.0;
	Main_y_x1[0] = (double)-0.37621367749846896;
	Main_x_x1[0] = (double)-1.501250563778375;
	
	
	n = 0;
	do{
		
		/***** Stimulation part *****/
		if ( ( ( Main_time_t1[n] >= stim_start )  &&  ( (Main_time_t1[n] - stim_start) <= stim_dur ) ) || ( ( Main_time_t1[n] >= stim_interval ) &&  ( (Main_time_t1[n] - stim_interval) <= stim_dur ) ) ) {
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}
	
		Main_time_t1[ ( n + 1 ) ] =  ( Main_time_t1[n] + d ) ;
		Main_time_t2[n] =  ( Main_time_t1[n] +  ( d / (double)2 )  ) ;
		Main_time_t4[n] =  ( Main_time_t1[n] + d ) ;
		Main_time_t3[n] =  ( Main_time_t1[n] +  ( d / (double)2 )  ) ;
		Main_ky_k1[n] =  ( Main_epsilon_z *  (  ( Main_x_x1[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x1[n] )  )  ) ;
		Main_y_x2[n] =  ( Main_y_x1[n] +  ( Main_ky_k1[n] *  ( d / (double)2 )  )  ) ;
		Main_r_i1[n] =  ( Main_x_x1[n] * Main_x_x1[n] * Main_x_x1[n] ) ;
		Main_kx_k1[n] =  (  (  ( Main_x_x1[n] -  ( Main_r_i1[n] / (double)3 )  )  - Main_y_x1[n] )  + Main_zz_z ) ;
		Main_x_x2[n] =  ( Main_x_x1[n] +  ( Main_kx_k1[n] *  ( d / (double)2 )  )  ) ;
		Main_ky_k2[n] =  ( Main_epsilon_z *  (  ( Main_x_x2[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x2[n] )  )  ) ;
		Main_y_x3[n] =  ( Main_y_x1[n] +  ( Main_ky_k2[n] *  ( d / (double)2 )  )  ) ;
		Main_r_i2[n] =  ( Main_x_x2[n] * Main_x_x2[n] * Main_x_x2[n] ) ;
		Main_kx_k2[n] =  (  (  ( Main_x_x2[n] -  ( Main_r_i2[n] / (double)3 )  )  - Main_y_x2[n] )  + Main_zz_z ) ;
		Main_x_x3[n] =  ( Main_x_x1[n] +  ( Main_kx_k2[n] *  ( d / (double)2 )  )  ) ;
		Main_ky_k3[n] =  ( Main_epsilon_z *  (  ( Main_x_x3[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x3[n] )  )  ) ;
		Main_y_x4[n] =  ( Main_y_x1[n] +  ( Main_ky_k3[n] * d )  ) ;
		Main_r_i3[n] =  ( Main_x_x3[n] * Main_x_x3[n] * Main_x_x3[n] ) ;
		Main_kx_k3[n] =  (  (  ( Main_x_x3[n] -  ( Main_r_i3[n] / (double)3 )  )  - Main_y_x3[n] )  + Main_zz_z ) ;
		Main_x_x4[n] =  ( Main_x_x1[n] +  ( Main_kx_k3[n] * d )  ) ;
		Main_ky_k4[n] =  ( Main_epsilon_z *  (  ( Main_x_x4[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x4[n] )  )  ) ;
		Main_y_x1[ ( n + 1 ) ] =  ( Main_y_x1[n] +  (  ( d / (double)6 )  *  ( Main_ky_k1[n] +  ( (double)2 * Main_ky_k2[n] )  +  ( (double)2 * Main_ky_k3[n] )  + Main_ky_k4[n] )  )  ) ;
		Main_r_i4[n] =  ( Main_x_x4[n] * Main_x_x4[n] * Main_x_x4[n] ) ;
		Main_kx_k4[n] =  (  (  ( Main_x_x4[n] -  ( Main_r_i4[n] / (double)3 )  )  - Main_y_x4[n] )  + Main_zz_z ) ;
		Main_x_x1[ ( n + 1 ) ] =  ( Main_x_x1[n] +  (  ( d / (double)6 )  *  ( Main_kx_k1[n] +  ( (double)2 * Main_kx_k2[n] )  +  ( (double)2 * Main_kx_k3[n] )  + Main_kx_k4[n] )  )  ) ;
		
		/***** Output part *****/
		printf("%lf,%lf,%lf\n",Main_time_t1[n+1],Main_x_x1[n+1],Main_y_x1[n+1]);
				
		n =  ( n + 1 ) ;

	}while( (Main_time_t1[n] < 400) );

	t = Main_time_t1[ ( n + 1 ) ];
	y = Main_y_x1[ ( n + 1 ) ];
	x = Main_x_x1[ ( n + 1 ) ];
	
	
	
	
	
	{

		free ( Main_y_x2 ) ; 
		free ( Main_time_t4 ) ; 
		free ( Main_y_x1 ) ; 
		free ( Main_y_x3 ) ; 
		free ( Main_time_t3 ) ; 
		free ( Main_x_x1 ) ; 
		free ( Main_y_x4 ) ; 
		free ( Main_x_x3 ) ; 
		free ( Main_x_x2 ) ; 
		free ( Main_x_x4 ) ; 
		free ( Main_time_t2 ) ; 
		free ( Main_time_t1 ) ; 
		free ( Main_r_i4 ) ; 
		free ( Main_kx_k3 ) ; 
		free ( Main_ky_k2 ) ; 
		free ( Main_kx_k2 ) ; 
		free ( Main_kx_k4 ) ; 
		free ( Main_r_i1 ) ; 
		free ( Main_ky_k4 ) ; 
		free ( Main_r_i3 ) ; 
		free ( Main_ky_k3 ) ; 
		free ( Main_ky_k1 ) ; 
		free ( Main_kx_k1 ) ; 
		free ( Main_r_i2 ) ; 

	}

	return 0;
}



