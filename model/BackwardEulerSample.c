#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100


int main ( int argc , char** argv ) ;
void simulNewton0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 ) ;
double simulFunc0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , int i ) ;
double jacobi0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , int i , int j ) ;

int main ( int argc , char** argv ) {

	double* simulSet0;
	double* Main.y_x;
	double* Main.time_t;
	double* Main.x_x;
	double x_xend;
	double t_tend;
	double* Main.kx_k;
	double* Main.ky_k;
	double r_iend;
	double y_xend;
	double* Main.r_i;
	double Main.epsilon_z;
	double Main.gamma_z;
	double Main.beta_z;
	double Main.zz_z;
	int n1;
	int i0;
	int i1;

	simulSet0 = (double*)malloc (  ( sizeof(double ) * (double)5 )  ) ; ;
	Main.y_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main.time_t = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main.x_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main.kx_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main.ky_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main.r_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	Main.x_x[0] = (double)0.0;
	Main.y_x[0] = (double)0.0;
	Main.time_t[0] = (double)0.0;
	
	
	n1 = 0;
	do{

		simulSet0[0] = Main.r_i[n1];
		simulSet0[1] = Main.kx_k[n1];
		simulSet0[2] = Main.ky_k[n1];
		simulSet0[3] = Main.x_x[ ( n1 + 1 ) ];
		simulSet0[4] = Main.y_x[ ( n1 + 1 ) ];
		simulNewton0 ( simulSet0 , Main.zz_z , Main.epsilon_z , Main.beta_z , Main.gamma_z , Main.x_x[n1] , Main.y_x[n1] ) ;
		Main.r_i[n1] = simulSet0[0];
		Main.kx_k[n1] = simulSet0[1];
		Main.ky_k[n1] = simulSet0[2];
		Main.x_x[ ( n1 + 1 ) ] = simulSet0[3];
		Main.y_x[ ( n1 + 1 ) ] = simulSet0[4];
		Main.time_t[ ( n1 + 1 ) ] =  ( Main.time_t[n1] + (double)0.01 ) ;
		
		
		n1 =  ( n1 + 1 ) ;

	}while(!( ( t[n1] < 400 ) ));

	x_xend = Main.x_x[n1];
	y_xend = Main.y_x[n1];
	r_iend = Main.r_i[n1];
	t_tend = Main.time_t[n1];
	
	
	
	
	
	{

		free ( Main.y_x ) ; 
		free ( Main.time_t ) ; 
		free ( Main.x_x ) ; 
		free ( Main.kx_k ) ; 
		free ( Main.ky_k ) ; 
		free ( Main.r_i ) ; 

	}

	{

		free ( simulSet0 ) ; 

	}

	return 0;
}


void simulNewton0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 ) {


	int max = 0;
	int i, j, k;
	double buf;
	double det;
	double pro;
	double eps;
	double f[5];
	double jac[5][5];
	double cpy[5][5];
	double inv[5][5];
	double simulSet_next[5];


	do {
		max ++;
		if(max > 1000){
			printf("error:no convergence\n");break;
		}
		det = 1.0;
		eps = 0.0;

		for(i=0;i<5;i++){
			for(j=0;j<5;j++){
				jac[i][j] = jacobi0(simulSet,var4,var6,var7,var8,var9,var10,i,j);
				cpy[i][j] = jacobi0(simulSet,var4,var6,var7,var8,var9,var10,i,j);

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
			printf("error:det is zero\n");break;
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
				pro += inv[i][j] * simulFunc0(simulSet,var4,var6,var7,var8,var9,var10,j);
			}
			simulSet_next[i] = simulSet[i] - pro;
		}
		for(i=0;i<5;i++){
			simulSet[i] = simulSet_next[i];
		}
		for(i=0;i<5;i++){
			f[i] = simulFunc0(simulSet,var4,var6,var7,var8,var9,var10,i);
			eps += f[i]*f[i];
		}
	} while (1.0E-50 < sqrt(eps));

}


double simulFunc0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , int i ) {



	double var0 = simulSet[0];
	double var2 = simulSet[1];
	double var5 = simulSet[2];
	double var1 = simulSet[3];
	double var3 = simulSet[4];

	if(i==0) return  ( var0 -  ( var1 * var1 * var1 )  ) ;
	if(i==1) return  ( var2 -  (  (  ( var1 -  ( var0 / (double)3 )  )  - var3 )  + var4 )  ) ;
	if(i==2) return  ( var5 -  ( var6 *  (  ( var1 + var7 )  -  ( var8 * var3 )  )  )  ) ;
	if(i==3) return  ( var1 -  ( var9 +  ( var2 * (double)0.01 )  )  ) ;
	if(i==4) return  ( var3 -  ( var10 +  ( var5 * (double)0.01 )  )  ) ;

}


double jacobi0 ( double* simulSet , double var4 , double var6 , double var7 , double var8 , double var9 , double var10 , int i , int j ) {



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
	if(i==3 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 * (double)0.01 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==3) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==3 && j==4) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var2 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==0) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==1) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==2) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)1 * (double)0.01 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==3) return  (  ( (double)0 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;
	if(i==4 && j==4) return  (  ( (double)1 -  ( (double)0 +  (  ( (double)0 * (double)0.01 )  +  ( var5 * (double)0 )  )  )  )  - (double)0 ) ;

}



