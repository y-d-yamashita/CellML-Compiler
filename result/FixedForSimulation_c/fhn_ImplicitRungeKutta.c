#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;
void simulNewton0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 ) ;
double simulFunc0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 , int i ) ;
double jacobi0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 , int i , int j ) ;

int main ( int argc , char** argv ) {

	double* simulSet0;
	double* Main_x_x2;
	double* Main_time_t1;
	double* Main_x_x;
	double* Main_time_t;
	double* Main_y_x1;
	double* Main_x_x1;
	double* Main_y_x2;
	double* Main_time_t2;
	double* Main_y_x;
	double* Main_kx_k2;
	double* Main_ky_k1;
	double* Main_kx_k1;
	double y;
	double* Main_r_i1;
	double t;
	double* Main_ky_k2;
	double x;
	double* Main_r_i2;
	double Main_epsilon_z;
	double Main_beta_z;
	double Main_gamma_z;
	double d;
	double Main_zz_z;
	int n;
	int i0;
	int i1;
	
	/***** variables for stimulation *****/
	double stim_start = 50;
	double stim_interval= 200;
	double stim_dur = 1;

	simulSet0 = (double*)malloc (  ( sizeof(double ) * (double)10 )  ) ; ;
	Main_x_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_x_x1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_y_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kx_k1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i1 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_ky_k2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_r_i2 = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_epsilon_z = (double)0.03;
	Main_beta_z = (double)1.2;
	Main_gamma_z = (double)0.3;
	d = (double)0.01;
	Main_zz_z = (double)0.0;
	
	
	Main_time_t[0] = (double)0.0;
	Main_y_x[0] = (double)-0.37621367749846896;
	Main_x_x[0] = (double)-1.501250563778375;
	
	
	n = 0;
	do{
	
		/***** Stimulation part *****/
		if ( ( ( Main_time_t[n] >= stim_start )  &&  ( (Main_time_t[n] - stim_start) <= stim_dur ) ) || ( ( Main_time_t[n] >= stim_interval ) &&  ( (Main_time_t[n] - stim_interval) <= stim_dur ) ) ) {
			Main_zz_z = 1.0;
		} else{
			Main_zz_z = 0.0;
		}	
	
		Main_time_t2[n] =  ( Main_time_t[n] +  ( d *  (  ( (double)1 / (double)2 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  ) ;
		Main_time_t1[n] =  ( Main_time_t[n] +  ( d *  (  ( (double)1 / (double)2 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  ) ;
		Main_time_t[ ( n + 1 ) ] =  ( Main_time_t[n] + d ) ;
		simulSet0[0] = Main_x_x1[n];
		simulSet0[1] = Main_y_x1[n];
		simulSet0[2] = Main_r_i1[n];
		simulSet0[3] = Main_kx_k1[n];
		simulSet0[4] = Main_ky_k1[n];
		simulSet0[5] = Main_x_x2[n];
		simulSet0[6] = Main_y_x2[n];
		simulSet0[7] = Main_r_i2[n];
		simulSet0[8] = Main_kx_k2[n];
		simulSet0[9] = Main_ky_k2[n];
		simulNewton0 ( simulSet0 , Main_x_x[n] , d , Main_y_x[n] , Main_zz_z , Main_epsilon_z , Main_beta_z , Main_gamma_z ) ;
		Main_x_x1[n] = simulSet0[0];
		Main_y_x1[n] = simulSet0[1];
		Main_r_i1[n] = simulSet0[2];
		Main_kx_k1[n] = simulSet0[3];
		Main_ky_k1[n] = simulSet0[4];
		Main_x_x2[n] = simulSet0[5];
		Main_y_x2[n] = simulSet0[6];
		Main_r_i2[n] = simulSet0[7];
		Main_kx_k2[n] = simulSet0[8];
		Main_ky_k2[n] = simulSet0[9];
		Main_y_x[ ( n + 1 ) ] =  ( Main_y_x[n] +  (  ( d / (double)2 )  *  ( Main_ky_k1[n] + Main_ky_k2[n] )  )  ) ;
		Main_x_x[ ( n + 1 ) ] =  ( Main_x_x[n] +  (  ( d / (double)2 )  *  ( Main_kx_k1[n] + Main_kx_k2[n] )  )  ) ;
				
		/***** Output part *****/
		printf("%lf,%lf,%lf\n",Main_time_t[n+1],Main_x_x[n+1],Main_y_x[n+1]);
		
		
		n =  ( n + 1 ) ;

	}while( (Main_time_t[n] < 400) );

	t = Main_time_t[ ( n + 1 ) ];
	y = Main_y_x[ ( n + 1 ) ];
	x = Main_x_x[ ( n + 1 ) ];
	
	
	
	
	
	{

		free ( Main_x_x2 ) ; 
		free ( Main_time_t1 ) ; 
		free ( Main_x_x ) ; 
		free ( Main_time_t ) ; 
		free ( Main_y_x1 ) ; 
		free ( Main_x_x1 ) ; 
		free ( Main_y_x2 ) ; 
		free ( Main_time_t2 ) ; 
		free ( Main_y_x ) ; 
		free ( Main_kx_k2 ) ; 
		free ( Main_ky_k1 ) ; 
		free ( Main_kx_k1 ) ; 
		free ( Main_r_i1 ) ; 
		free ( Main_ky_k2 ) ; 
		free ( Main_r_i2 ) ; 

	}

	{

		free ( simulSet0 ) ; 

	}

	return 0;
}


void simulNewton0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 ) {


	int max = 0;
	int i, j, k;
	double buf;
	double det;
	double pro;
	double eps;
	double f[10];
	double jac[10][10];
	double cpy[10][10];
	double inv[10][10];
	double simulSet_next[10];


	do {
		max ++;
		if(max > 1000){
			printf("error:no convergence\n");break;
		}
		det = 1.0;
		eps = 0.0;

		for(i=0;i<10;i++){
			for(j=0;j<10;j++){
				jac[i][j] = jacobi0(simulSet,var1,var3,var6,var10,var11,var12,var13,i,j);
				cpy[i][j] = jacobi0(simulSet,var1,var3,var6,var10,var11,var12,var13,i,j);

			}
		}

		for(i=0;i<10;i++){
			for(j=0;j<10;j++){
				if(i<j){
					buf = cpy[j][i] / cpy[i][i];
					for(k=0;k<10;k++){
						cpy[j][k] -= cpy[i][k] * buf;
					}
				}
			}
		}
		for(i=0;i<10;i++){
			det *= cpy[i][i];
		}
		if(det == 0.0){
			printf("error:det is zero\n");break;
		}
		for(i=0;i<10;i++){
			for(j=0;j<10;j++){
				if(i==j) inv[i][j] = 1.0;
				else inv[i][j] = 0.0;
			}
		}
		for(i=0;i<10;i++){
			buf = 1 / jac[i][i];
			for(j=0;j<10;j++){
				jac[i][j] *= buf;
				inv[i][j] *= buf;
			}
			for(j=0;j<10;j++){
				if(i!=j){
					buf = jac[j][i];
					for(k=0;k<10;k++){
						jac[j][k] -= jac[i][k]*buf;
						inv[j][k] -= inv[i][k]*buf;
					}
				}
			}
		}
		for(i=0;i<10;i++){
			pro = 0.0;
			for(j=0;j<10;j++){
				pro += inv[i][j] * simulFunc0(simulSet,var1,var3,var6,var10,var11,var12,var13,j);
			}
			simulSet_next[i] = simulSet[i] - pro;
		}
		for(i=0;i<10;i++){
			simulSet[i] = simulSet_next[i];
		}
		for(i=0;i<10;i++){
			f[i] = simulFunc0(simulSet,var1,var3,var6,var10,var11,var12,var13,i);
			eps += f[i]*f[i];
		}
	} while (1.0E-5 < sqrt(eps));

}


double simulFunc0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 , int i ) {



	double var0 = simulSet[0];
	double var5 = simulSet[1];
	double var9 = simulSet[2];
	double var2 = simulSet[3];
	double var7 = simulSet[4];
	double var14 = simulSet[5];
	double var15 = simulSet[6];
	double var16 = simulSet[7];
	double var4 = simulSet[8];
	double var8 = simulSet[9];

	if(i==0) return  ( var0 -  ( var1 +  ( var2 *  ( var3 / (double)4 )  )  +  ( var4 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  )  ) ;
	if(i==1) return  ( var5 -  ( var6 +  ( var7 *  ( var3 / (double)4 )  )  +  ( var8 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  )  ) ;
	if(i==2) return  ( var9 -  ( var0 * var0 * var0 )  ) ;
	if(i==3) return  ( var2 -  (  (  ( var0 -  ( var9 / (double)3 )  )  - var5 )  + var10 )  ) ;
	if(i==4) return  ( var7 -  ( var11 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  ) ;
	if(i==5) return  ( var14 -  ( var1 +  ( var2 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  ( var3 / (double)4 )  )  )  ) ;
	if(i==6) return  ( var15 -  ( var6 +  ( var7 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  ( var3 / (double)4 )  )  )  ) ;
	if(i==7) return  ( var16 -  ( var14 * var14 * var14 )  ) ;
	if(i==8) return  ( var4 -  (  (  ( var14 -  ( var16 / (double)3 )  )  - var15 )  + var10 )  ) ;
	if(i==9) return  ( var8 -  ( var11 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  ) ;

}


double jacobi0 ( double* simulSet , double var1 , double var3 , double var6 , double var10 , double var11 , double var12 , double var13 , int i , int j ) {



	double var0 = simulSet[0];
	double var5 = simulSet[1];
	double var9 = simulSet[2];
	double var2 = simulSet[3];
	double var7 = simulSet[4];
	double var14 = simulSet[5];
	double var15 = simulSet[6];
	double var16 = simulSet[7];
	double var4 = simulSet[8];
	double var8 = simulSet[9];

	if(i==0 && j==0) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==5) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==6) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==7) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==8) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)1 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==0 && j==9) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var2 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var4 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==1) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==5) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==6) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==7) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==8) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==1 && j==9) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var7 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  +  (  ( (double)1 *  ( var3 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var8 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  -  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  -  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==0) return  (  ( (double)0 -  (  ( (double)1 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)1 * var0 )  +  ( var0 * (double)1 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==2) return  (  ( (double)1 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==3) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==4) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==5) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==6) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==7) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==8) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==2 && j==9) return  (  ( (double)0 -  (  ( (double)0 *  ( var0 * var0 )  )  +  ( var0 *  (  ( (double)0 * var0 )  +  ( var0 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==3 && j==0) return  (  ( (double)0 -  (  (  ( (double)1 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==1) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)1 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==2) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)1 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==3) return  (  ( (double)1 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==4) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==5) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==6) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==7) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==8) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==3 && j==9) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var9 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==4 && j==0) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)1 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)1 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==2) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==3) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==4) return  (  ( (double)1 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==5) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==6) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==7) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==8) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==4 && j==9) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var0 + var12 )  -  ( var13 * var5 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var5 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==5) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==6) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==7) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==8) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)1 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==5 && j==9) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var2 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var4 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==5) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==6) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==7) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==8) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)0 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==6 && j==9) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 *  ( var3 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  )  +  ( var7 *  (  ( (double)0 *  (  ( (double)1 / (double)4 )  +  ( sqrt( (double)3 ) / (double)6 )  )  )  +  ( var3 *  (  (  (  ( (double)0 * (double)4 )  -  ( (double)1 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  +  (  (  (  ( pow( (double)3 , (double)0.5 ) *  (  (  ( (double)0 * (double)0.5 )  / (double)3 )  +  ( log( (double)3 ) * (double)0 )  )  )  * (double)6 )  -  ( sqrt( (double)3 ) * (double)0 )  )  /  ( (double)6 * (double)6 )  )  )  )  )  )  )  +  (  ( (double)1 *  ( var3 / (double)4 )  )  +  ( var8 *  (  (  ( (double)0 * (double)4 )  -  ( var3 * (double)0 )  )  /  ( (double)4 * (double)4 )  )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==0) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==2) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==3) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==4) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==5) return  (  ( (double)0 -  (  ( (double)1 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)1 * var14 )  +  ( var14 * (double)1 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==6) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==7) return  (  ( (double)1 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==8) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==7 && j==9) return  (  ( (double)0 -  (  ( (double)0 *  ( var14 * var14 )  )  +  ( var14 *  (  ( (double)0 * var14 )  +  ( var14 * (double)0 )  )  )  )  )  - (double)0 ) ;
	if(i==8 && j==0) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==1) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==2) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==3) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==4) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==5) return  (  ( (double)0 -  (  (  ( (double)1 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==6) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)1 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==7) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)1 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==8) return  (  ( (double)1 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==8 && j==9) return  (  ( (double)0 -  (  (  ( (double)0 -  (  (  ( (double)0 * (double)3 )  -  ( var16 * (double)0 )  )  /  ( (double)3 * (double)3 )  )  )  - (double)0 )  + (double)0 )  )  - (double)0 ) ;
	if(i==9 && j==0) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==1) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==2) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==3) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==4) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==5) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)1 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==6) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)1 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==7) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==8) return  (  ( (double)0 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;
	if(i==9 && j==9) return  (  ( (double)1 -  (  ( (double)0 *  (  ( var14 + var12 )  -  ( var13 * var15 )  )  )  +  ( var11 *  (  ( (double)0 + (double)0 )  -  (  ( (double)0 * var15 )  +  ( var13 * (double)0 )  )  )  )  )  )  - (double)0 ) ;

}



