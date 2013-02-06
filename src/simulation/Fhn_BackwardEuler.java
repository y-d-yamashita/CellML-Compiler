package simulation;
public class Fhn_BackwardEuler{


public static void main ( String args[] ) {

	int __DATA_NUM = 100000;
	double simulSet0[];
	double Main_y_x[];
	double Main_x_x[];
	double Main_time_t[];
	double Main_kx_k[];
	double yend;
	double Main_r_i[];
	double xend;
	double tend;
	double Main_ky_k[];
	double Main_zz_z;
	double Main_epsilon_z;
	double d;
	double Main_beta_z;
	double Main_gamma_z;
	int n;
	
	/***** variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;

	simulSet0 = new double[__DATA_NUM];
	Main_y_x = new double[__DATA_NUM];
	Main_x_x = new double[__DATA_NUM];
	Main_time_t = new double[__DATA_NUM];
	Main_kx_k = new double[__DATA_NUM];
	Main_r_i = new double[__DATA_NUM];
	Main_ky_k = new double[__DATA_NUM];
	Main_zz_z = (double)0.0;
	Main_epsilon_z = (double)0.03;
	d = (double)0.01;
	Main_beta_z = (double)1.2;
	Main_gamma_z = (double)0.3;
	
	
	Main_time_t[0] = (double)0.0;
	
	/***** initial values *****/
	Main_x_x[0] = (double)-1.501250563778375;
	Main_y_x[0] = (double)-0.37621367749846896;
	
	
	n = 0;
	do{
			
		/***** Stimulation part *****/
		if ( ( ( Main_time_t[n] >= stim_start )  &&  ( (Main_time_t[n] - stim_start) <= stim_dur ) ) || ( ( Main_time_t[n] >= stim_interval ) &&  ( (Main_time_t[n] - stim_interval) <= stim_dur ) ) ) { 
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}
		
		Main_time_t[ ( n + 1 ) ] =  ( Main_time_t[n] + d ) ;
		simulSet0[0] = Main_r_i[n];
		simulSet0[1] = Main_kx_k[n];
		simulSet0[2] = Main_ky_k[n];
		simulSet0[3] = Main_x_x[ ( n + 1 ) ];
		simulSet0[4] = Main_y_x[ ( n + 1 ) ];
		simulNewton0 ( simulSet0 , Main_zz_z , Main_epsilon_z , Main_beta_z , Main_gamma_z , Main_x_x[n] , d , Main_y_x[n] ) ;
		Main_r_i[n] = simulSet0[0];
		Main_kx_k[n] = simulSet0[1];
		Main_ky_k[n] = simulSet0[2];
		Main_x_x[ ( n + 1 ) ] = simulSet0[3];
		Main_y_x[ ( n + 1 ) ] = simulSet0[4];
		
		/***** Output part *****/
		System.out.print(Main_time_t[n+1]+",");
		System.out.print(Main_x_x[n+1]+",");
		System.out.print(Main_y_x[n+1]);
		System.out.println();
		
		n =  ( n + 1 ) ;

	}while( ( Main_time_t[n] < 400 ) );

	tend = Main_time_t[ ( n + 1 ) ];
	xend = Main_x_x[ ( n + 1 ) ];
	yend = Main_y_x[ ( n + 1 ) ];
	
	
	
	
	
}


public static void simulNewton0 ( double [] simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , double var11 ) {


	int max = 0;
	int i, j, k;
	double buf;
	double det;
	double pro;
	double eps;
	double[] f = new double[5];
	double[][] jac = new double[5][5];
	double[][] cpy = new double[5][5];
	double[][] inv = new double[5][5];
	double[] simulSet_next = new double[5];


	do {
		max ++;
		if(max > 1000){
			System.out.println("error:no convergence\n");break;
		}
		det = 1.0;
		eps = 0.0;

		for(i=0;i<5;i++){
			for(j=0;j<5;j++){
				jac[i][j] = jacobi0(simulSet,var4,var6,var7,var8,var9,var10,var11,i,j);
				cpy[i][j] = jacobi0(simulSet,var4,var6,var7,var8,var9,var10,var11,i,j);

			}
		}

		for(i=0;i<5;i++){
			for(j=0;j<5;j++){
				if(i<j){
					buf = cpy[j][i] / cpy[i][i];
					for(k=0;k<5;k++){
						cpy[j][k] -= cpy[i][k] * buf;
					}
				}
			}
		}
		for(i=0;i<5;i++){
			det *= cpy[i][i];
		}
		if(det == 0.0){
			System.out.println("error:det is zero\n");break;
		}
		for(i=0;i<5;i++){
			for(j=0;j<5;j++){
				if(i==j) inv[i][j] = 1.0;
				else inv[i][j] = 0.0;
			}
		}
		for(i=0;i<5;i++){
			buf = 1 / jac[i][i];
			for(j=0;j<5;j++){
				jac[i][j] *= buf;
				inv[i][j] *= buf;
			}
			for(j=0;j<5;j++){
				if(i!=j){
					buf = jac[j][i];
					for(k=0;k<5;k++){
						jac[j][k] -= jac[i][k]*buf;
						inv[j][k] -= inv[i][k]*buf;
					}
				}
			}
		}
		for(i=0;i<5;i++){
			pro = 0.0;
			for(j=0;j<5;j++){
				pro += inv[i][j] * simulFunc0(simulSet,var4,var6,var7,var8,var9,var10,var11,j);
			}
			simulSet_next[i] = simulSet[i] - pro;
		}
		for(i=0;i<5;i++){
			simulSet[i] = simulSet_next[i];
		}
		for(i=0;i<5;i++){
			f[i] = simulFunc0(simulSet,var4,var6,var7,var8,var9,var10,var11,i);
			eps += f[i]*f[i];
		}
	} while (1.0E-5 < Math.sqrt(eps));

}


public static double simulFunc0 ( double [] simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , double var11 , int i ) {



	double var0 = simulSet[0];
	double var2 = simulSet[1];
	double var5 = simulSet[2];
	double var1 = simulSet[3];
	double var3 = simulSet[4];

	if(i==0) return  ( var0 -  ( var1 * var1 * var1 )  ) ;
	if(i==1) return  ( var2 -  (  (  ( var1 -  ( var0 / (double)3 )  )  - var3 )  + var4 )  ) ;
	if(i==2) return  ( var5 -  ( var6 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  ) ;
	if(i==3) return  ( var1 -  ( var9 +  ( var2 * var10 )  )  ) ;
	if(i==4) return  ( var3 -  ( var11 +  ( var5 * var10 )  )  ) ;

	else return 0.0;
}


public static double jacobi0 ( double [] simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , double var11 , int i , int j ) {



	double var0 = simulSet[0];
	double var2 = simulSet[1];
	double var5 = simulSet[2];
	double var1 = simulSet[3];
	double var3 = simulSet[4];

	if(i==0 && j==0) return  (  ( (double)1 -  (  ( (double)0 *  ( var1 * var1 )  )  +  ( var1 *  (  ( (double)0 * var1 )  +  ( var1 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  ( var1 * var1 )  )  +  ( var1 *  (  ( (double)0 * var1 )  +  ( var1 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==2) return  (  ( (double)0 -  (  ( (double)0 *  ( var1 * var1 )  )  +  ( var1 *  (  ( (double)0 * var1 )  +  ( var1 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==3) return  (  ( (double)0 -  (  ( (double)1 *  ( var1 * var1 )  )  +  ( var1 *  (  ( (double)1 * var1 )  +  ( var1 * (double)1 )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==4) return  (  ( (double)0 -  (  ( (double)0 *  ( var1 * var1 )  )  +  ( var1 *  (  ( (double)0 * var1 )  +  ( var1 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==0) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)1 * (double)3 )  -  ( var0 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==1 && j==1) return  (  ( (double)1 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var0 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==1 && j==2) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var0 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==1 && j==3) return  (  ( (double)0 -  (  (  ( (double)1 -  (  (  ( (double)0 * (double)3 )  -  ( var0 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==1 && j==4) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var0 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)1 )  + (double)0 )  )  - (double)0 ) ;
	if(i==2 && j==0) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  +  ( var6 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var3 )  +  ( var8 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  +  ( var6 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var3 )  +  ( var8 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==2) return  (  ( (double)1 -  (  ( (double)0 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  +  ( var6 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var3 )  +  ( var8 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==3) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  +  ( var6 *  (  ( (double)1 + (double)0 )  -  (  ( (double)0 * var3 )  +  ( var8 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==4) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  +  ( var6 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var3 )  +  ( var8 * (double)1 )  )  )  )  )  )  - (double)0 ) ;
	if(i==3 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 * var10 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==3) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 * var10 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==4) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 * var10 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;

	else return 0.0;
}


}
