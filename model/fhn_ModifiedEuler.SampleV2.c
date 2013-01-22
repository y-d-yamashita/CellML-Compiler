#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* Main_time_t2;
	double* Main_x_x2;
	double* Main_y_x2;
	double* Main_y_x1;
	double* Main_time_t1;
	double* Main_x_x1;
	double* Main_ky_k2;
	double* Main_kx_k1;
	double* Main_r_i1;
	double t_t1end;
	double x_x1end;
	double* Main_kx_k2;
	double y_x1end;
	double* Main_ky_k1;
	double* Main_r_i2;
	double Main_epsilon_z;
	double Main_gamma_z;
	double Main_beta_z;
	double Main_zz_z;
	int n1;
	int i0;
	int i1;
	
	/***** Added variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;
	

	Main_time_t2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	Main_x_x1[0] = (double)-1.501250563778375;
	Main_y_x1[0] = (double)-0.37621367749846896;
	Main_time_t1[0] = (double)0.0;
	
	
	Main_epsilon_z = 0.03;
	Main_beta_z = 1.2;
	Main_gamma_z = 0.3;
	
	n1 = 0;
	do{
	
		/***** Stimulation part *****/
		if ( ( ( Main_time_t1[n1] >= stim_start )  &&  ( (Main_time_t1[n1]- stim_start) <= stim_dur ) ) || ( ( Main_time_t1[n1] >= stim_interval ) &&  ( (Main_time_t1[n1] - stim_interval) <= stim_dur ) ) ) { //
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}

		Main_r_i1[n1] =  ( Main_x_x1[n1] * Main_x_x1[n1] * Main_x_x1[n1] ) ;
		Main_kx_k1[n1] =  (  (  ( Main_x_x1[n1] -  ( Main_r_i1[n1] / (double)3 )  )  - Main_y_x1[n1] )  + Main_zz_z ) ;
		Main_ky_k1[n1] =  ( Main_epsilon_z *  (  ( Main_x_x1[n1] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x1[n1] )  )  ) ;
		Main_x_x2[n1] =  ( Main_x_x1[n1] +  ( Main_kx_k1[n1] * (double)0.01 )  ) ;
		Main_y_x2[n1] =  ( Main_y_x1[n1] +  ( Main_ky_k1[n1] * (double)0.01 )  ) ;
		Main_time_t2[n1] =  ( Main_time_t1[n1] + (double)0.01 ) ;
		Main_r_i2[n1] =  ( Main_x_x2[n1] * Main_x_x2[n1] * Main_x_x2[n1] ) ;
		Main_kx_k2[n1] =  (  (  ( Main_x_x2[n1] -  ( Main_r_i2[n1] / (double)3 )  )  - Main_y_x2[n1] )  + Main_zz_z ) ;
		Main_ky_k2[n1] =  ( Main_epsilon_z *  (  ( Main_x_x2[n1] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x2[n1] )  )  ) ;
		Main_x_x1[ ( n1 + 1 ) ] =  ( Main_x_x1[n1] +  (  ( (double)0.01 / (double)2 )  *  ( Main_kx_k1[n1] + Main_kx_k2[n1] )  )  ) ;
		Main_y_x1[ ( n1 + 1 ) ] =  ( Main_y_x1[n1] +  (  ( (double)0.01 / (double)2 )  *  ( Main_ky_k1[n1] + Main_ky_k2[n1] )  )  ) ;
		Main_time_t1[ ( n1 + 1 ) ] =  ( Main_time_t1[n1] + (double)0.01 ) ;
		
		printf("%lf,%lf,%lf\n",Main_time_t1[n1+1],Main_x_x1[n1+1],Main_y_x1[n1+1]);
		n1 =  ( n1 + 1 ) ;

	}while(!( ( Main_time_t1[n1] > 400 ) ));

	x_x1end = Main_x_x1[ ( n1 + 1 ) ];
	y_x1end = Main_y_x1[ ( n1 + 1 ) ];
	t_t1end = Main_time_t1[ ( n1 + 1 ) ];
	
	
	
	
	
	{

		free ( Main_time_t2 ) ; 
		free ( Main_x_x2 ) ; 
		free ( Main_y_x2 ) ; 
		free ( Main_y_x1 ) ; 
		free ( Main_time_t1 ) ; 
		free ( Main_x_x1 ) ; 
		free ( Main_ky_k2 ) ; 
		free ( Main_kx_k1 ) ; 
		free ( Main_r_i1 ) ; 
		free ( Main_kx_k2 ) ; 
		free ( Main_ky_k1 ) ; 
		free ( Main_r_i2 ) ; 

	}

	{


	}

	return 0;
}



