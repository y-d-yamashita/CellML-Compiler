import java.lang.Math;
public class thesisdoubleloop{


public static void main ( String args[] ) {

	int __DATA_NUM = 100000;
	double Main_a_xr[][];
	double Main_d_x[];
	double xend;
	double Main_a[];
	double Main_ka_kr[][];
	double Main_kd_k[];
	double Main_e_i[];
	double Main_c_zr;
	double Main_b_zr;
	double d;
	int m;
	int n;

	Main_a_xr = new double[__DATA_NUM][__DATA_NUM];
	Main_d_x = new double[__DATA_NUM];
	Main_a = new double[__DATA_NUM];
	Main_ka_kr = new double[__DATA_NUM][__DATA_NUM];
	Main_kd_k = new double[__DATA_NUM];
	Main_e_i = new double[__DATA_NUM];
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

		}while( ( n < 100 ) );

		Main_a[m] = Main_a_xr[m][ ( n + 1 ) ];
		
		
		Main_e_i[m] = Main_a[m];
		Main_kd_k[m] =  ( (double)3 * Main_e_i[m] ) ;
		Main_d_x[ ( m + 1 ) ] =  ( Main_d_x[m] +  ( Main_kd_k[m] * d )  ) ;
		
		
		
		
		m =  ( m + 1 ) ;

	}while( ( m < 300 ) );

	xend = Main_d_x[ ( m + 1 ) ];
	
	
	
	
	
}


}
