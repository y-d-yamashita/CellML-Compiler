package simulation;
public class Fhn_ModifiedEuler{


public static void main ( String args[] ) {

	int __DATA_NUM = 100000;
	double Main_time_t1[];
	double Main_x_x2[];
	double Main_y_x1[];
	double Main_x_x1[];
	double Main_y_x2[];
	double Main_time_t2[];
	double Main_ky_k1[];
	double Main_kx_k2[];
	double Main_r_i1[];
	double y;
	double Main_kx_k1[];
	double t;
	double Main_ky_k2[];
	double x;
	double Main_r_i2[];
	double Main_epsilon_z;
	double Main_beta_z;
	double Main_gamma_z;
	double d;
	double Main_zz_z;
	int n;
	
	/***** Added variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;	
	
	Main_time_t1 = new double[__DATA_NUM];
	Main_x_x2 = new double[__DATA_NUM];
	Main_y_x1 = new double[__DATA_NUM];
	Main_x_x1 = new double[__DATA_NUM];
	Main_y_x2 = new double[__DATA_NUM];
	Main_time_t2 = new double[__DATA_NUM];
	Main_ky_k1 = new double[__DATA_NUM];
	Main_kx_k2 = new double[__DATA_NUM];
	Main_r_i1 = new double[__DATA_NUM];
	Main_kx_k1 = new double[__DATA_NUM];
	Main_ky_k2 = new double[__DATA_NUM];
	Main_r_i2 = new double[__DATA_NUM];
	Main_epsilon_z = (double)0.03;
	Main_beta_z = (double)1.2;
	Main_gamma_z = (double)0.3;
	d = (double)0.01;
	Main_zz_z = (double)0.0;
	
	
	Main_time_t1[0] = (double)0.0;
	Main_y_x1[0] = (double)-0.37621367749846896;
	Main_x_x1[0] = (double)-1.501250563778375;
	
	
	n = 0;
	do{
		
		/***** Stimulation part *****/
		if ( ( ( Main_time_t1[n] >= stim_start )  &&  ( (Main_time_t1[n]- stim_start) <= stim_dur ) ) || ( ( Main_time_t1[n] >= stim_interval ) &&  ( (Main_time_t1[n] - stim_interval) <= stim_dur ) ) ) {
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}
		
		Main_time_t2[n] =  ( Main_time_t1[n] + d ) ;
		Main_time_t1[ ( n + 1 ) ] =  ( Main_time_t1[n] + d ) ;
		Main_ky_k1[n] =  ( Main_epsilon_z *  (  ( Main_x_x1[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x1[n] )  )  ) ;
		Main_y_x2[n] =  ( Main_y_x1[n] +  ( Main_ky_k1[n] * d )  ) ;
		Main_r_i1[n] =  ( Main_x_x1[n] * Main_x_x1[n] * Main_x_x1[n] ) ;
		Main_kx_k1[n] =  (  (  ( Main_x_x1[n] -  ( Main_r_i1[n] / (double)3 )  )  - Main_y_x1[n] )  + Main_zz_z ) ;
		Main_x_x2[n] =  ( Main_x_x1[n] +  ( Main_kx_k1[n] * d )  ) ;
		Main_ky_k2[n] =  ( Main_epsilon_z *  (  ( Main_x_x2[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x2[n] )  )  ) ;
		Main_y_x1[ ( n + 1 ) ] =  ( Main_y_x1[n] +  (  ( d / (double)2 )  *  ( Main_ky_k1[n] + Main_ky_k2[n] )  )  ) ;
		Main_r_i2[n] =  ( Main_x_x2[n] * Main_x_x2[n] * Main_x_x2[n] ) ;
		Main_kx_k2[n] =  (  (  ( Main_x_x2[n] -  ( Main_r_i2[n] / (double)3 )  )  - Main_y_x2[n] )  + Main_zz_z ) ;
		Main_x_x1[ ( n + 1 ) ] =  ( Main_x_x1[n] +  (  ( d / (double)2 )  *  ( Main_kx_k1[n] + Main_kx_k2[n] )  )  ) ;
		
		/***** Output part *****/
		System.out.print(Main_time_t1[n+1]+",");
		System.out.print(Main_x_x1[n+1]+",");
		System.out.print(Main_y_x1[n+1]);
		System.out.println();
		
		n =  ( n + 1 ) ;

	}while( ( Main_time_t1[n] < 400 ) );

	t = Main_time_t1[ ( n + 1 ) ];
	y = Main_y_x1[ ( n + 1 ) ];
	x = Main_x_x1[ ( n + 1 ) ];
	
	
	
	
	
}


}
