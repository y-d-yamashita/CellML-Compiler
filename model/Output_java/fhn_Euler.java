import java.lang.Math;
public class fhn_Euler{


public static void main ( String args[] ) {

	int __DATA_NUM = 100000;
	double Main_x_x[];
	double Main_time_t[];
	double Main_y_x[];
	double Main_ky_k[];
	double y;
	double t;
	double x;
	double Main_kx_k[];
	double Main_r_i[];
	double Main_epsilon_z;
	double Main_beta_z;
	double Main_gamma_z;
	double d;
	double Main_zz_z;
	int n;

	Main_x_x = new double[__DATA_NUM];
	Main_time_t = new double[__DATA_NUM];
	Main_y_x = new double[__DATA_NUM];
	Main_ky_k = new double[__DATA_NUM];
	Main_kx_k = new double[__DATA_NUM];
	Main_r_i = new double[__DATA_NUM];
	Main_epsilon_z = (double)0.03;
	Main_beta_z = (double)1.2;
	Main_gamma_z = (double)0.3;
	d = (double)0.01;
	Main_zz_z = (double)0.0;
	
	
	Main_time_t[0] = (double)0.0;
	Main_y_x[0] = (double)0.0;
	Main_x_x[0] = (double)0.0;
	
	
	n = 0;
	do{

		Main_time_t[ ( n + 1 ) ] =  ( Main_time_t[n] + d ) ;
		Main_ky_k[n] =  ( Main_epsilon_z *  (  ( Main_x_x[n] + Main_beta_z )  -  ( Main_gamma_z * Main_y_x[n] )  )  ) ;
		Main_y_x[ ( n + 1 ) ] =  ( Main_y_x[n] +  ( Main_ky_k[n] * d )  ) ;
		Main_r_i[n] =  ( Main_x_x[n] * Main_x_x[n] * Main_x_x[n] ) ;
		Main_kx_k[n] =  (  (  ( Main_x_x[n] -  ( Main_r_i[n] / (double)3 )  )  - Main_y_x[n] )  + Main_zz_z ) ;
		Main_x_x[ ( n + 1 ) ] =  ( Main_x_x[n] +  ( Main_kx_k[n] * d )  ) ;
		
		
		n =  ( n + 1 ) ;

	}while( ( Main_time_t[n] < 400 ) );

	t = Main_time_t[ ( n + 1 ) ];
	y = Main_y_x[ ( n + 1 ) ];
	x = Main_x_x[ ( n + 1 ) ];
	
	
	
	
	
}


}
