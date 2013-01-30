#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#define __MAX_ARRAY_NUM 100000


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

	double* Main_m_x;
	double* Main_Cai_x;
	double* Main_time_t;
	double* Main_f_x;
	double* Main_d_x;
	double* Main_V_x;
	double* Main_X_x;
	double* Main_j_x;
	double* Main_h_x;
	double* Main_kf_k;
	double* Main_beta_X_i;
	double* Main_I_stim_i;
	double* Main_g_K_i;
	double* Main_beta_d_i;
	double* Main_kj_k;
	double* Main_kh_k;
	double* Main_kX_k;
	double t;
	double X;
	double j;
	double h;
	double* Main_alpha_K1_i;
	double* Main_E_K1_i;
	double* Main_i_K_i;
	double* Main_Xi_i;
	double* Main_kV_k;
	double* Main_kd_k;
	double* Main_alpha_X_i;
	double* Main_alpha_j_i;
	double* Main_i_b_i;
	double* Main_E_si_i;
	double* Main_alpha_m_i;
	double* Main_i_Kp_i;
	double* Main_beta_f_i;
	double m;
	double* Main_km_k;
	double* Main_K1_infinity_i;
	double* Main_i_si_i;
	double* Main_alpha_f_i;
	double* Main_kCai_k;
	double* Main_alpha_d_i;
	double* Main_E_K_i;
	double Cai;
	double* Main_i_Na_i;
	double d;
	double f;
	double* Main_alpha_h_i;
	double* Main_E_Kp_i;
	double* Main_beta_j_i;
	double* Main_beta_m_i;
	double V;
	double* Main_beta_h_i;
	double* Main_g_K1_i;
	double* Main_beta_K1_i;
	double* Main_Kp_i;
	double* Main_i_K1_i;
	double* Main_E_Na_i;
	double Main_g_Na_z = 23.0;
	double Main_g_b_z = 0.03921;
	double Main_Nai_z = 18.0;
	double Main_stim_period_z = 1000.0;
	double Main_Ko_z = 5.4;
	double Main_stim_duration_z = 2.0;
	double Main_g_Kp_z = 0.0183;
	double Main_F_z = 96484.6;
	double Main_Ki_z = 145.0;
	double Main_stim_start_z = 100.0;
	double Main_stim_end_z = 1.0E11;
	double Main_Nao_z = 140.0;
	double Main_E_b_z = -59.87;
	double Main_C_z = 1.0;
	double Main_stim_amplitude_z = -25.5;
	double Main_T_z = 310.0;
	double Main_R_z = 8314.0;
	double Main_PR_NaK_z = 0.01833;
	int n;
	int i0;
	int i1;

	Main_j_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_Cai_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_d_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_m_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_f_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_time_t = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_X_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_h_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_V_x = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_g_K_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kCai_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_si_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_E_K_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_Kp_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_I_stim_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_m_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kj_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_Na_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kd_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_Kp_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_E_K1_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_km_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_d_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kX_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_E_si_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_X_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_X_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_f_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_E_Kp_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kV_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_K1_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_K1_infinity_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_g_K1_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_d_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_m_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_K1_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_h_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_j_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_Xi_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_j_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_f_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_beta_K1_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kf_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_E_Na_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_kh_k = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_b_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_alpha_h_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	Main_i_K_i = (double*)malloc (  ( sizeof(double ) * __MAX_ARRAY_NUM )  ) ; ;
	
	
	Main_V_x[0] = (double)-83.853;
	Main_m_x[0] = (double)0.00187018;
	Main_h_x[0] = (double)0.9804713;
	Main_j_x[0] = (double)0.98767124;
	Main_d_x[0] = (double)0.00316354;
	Main_f_x[0] = (double)0.99427859;
	Main_X_x[0] = (double)0.16647703;
	Main_Cai_x[0] = (double)2.0E-4;
	Main_time_t[0] = (double)0.0;
	
	
	n = 0;
	do{

		Main_I_stim_i[n] =  (  (  ( Main_time_t[n] >= Main_stim_start_z )  &&  ( Main_time_t[n] <= Main_stim_end_z )  &&  (  (  ( Main_time_t[n] - Main_stim_start_z )  -  ( floor(  (  ( Main_time_t[n] - Main_stim_start_z )  / Main_stim_period_z )  ) * Main_stim_period_z )  )  <= Main_stim_duration_z )  )  ? Main_stim_amplitude_z : (double)0 ) ;
		Main_E_Na_i[n] =  (  (  ( Main_R_z * Main_T_z )  / Main_F_z )  * log(  ( Main_Nao_z / Main_Nai_z )  ) ) ;
		Main_i_Na_i[n] =  ( Main_g_Na_z * pow( Main_m_x[n] , (double)3 ) * Main_h_x[n] * Main_j_x[n] *  ( Main_V_x[n] - Main_E_Na_i[n] )  ) ;
		Main_alpha_m_i[n] =  (  ( (double)0.32 *  ( Main_V_x[n] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( Main_V_x[n] + (double)47.13 )  )  ) )  ) ;
		Main_beta_m_i[n] =  ( (double)0.08 * exp(  (  ( - Main_V_x[n] )  / (double)11 )  ) ) ;
		Main_alpha_h_i[n] =  (  ( Main_V_x[n] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + Main_V_x[n] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
		Main_beta_h_i[n] =  (  ( Main_V_x[n] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * Main_V_x[n] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * Main_V_x[n] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( Main_V_x[n] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
		Main_alpha_j_i[n] =  (  ( Main_V_x[n] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * Main_V_x[n] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * Main_V_x[n] )  ) )  )  *  ( Main_V_x[n] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( Main_V_x[n] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
		Main_beta_j_i[n] =  (  ( Main_V_x[n] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * Main_V_x[n] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( Main_V_x[n] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * Main_V_x[n] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( Main_V_x[n] + (double)32 )  )  ) )  )  ) ;
		Main_E_si_i[n] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( Main_Cai_x[n] / (double)1 )  ) )  ) ;
		Main_i_si_i[n] =  ( (double)0.09 * Main_d_x[n] * Main_f_x[n] *  ( Main_V_x[n] - Main_E_si_i[n] )  ) ;
		Main_alpha_d_i[n] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( Main_V_x[n] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( Main_V_x[n] - (double)5 )  )  ) )  ) ;
		Main_beta_d_i[n] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( Main_V_x[n] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( Main_V_x[n] + (double)44 )  )  ) )  ) ;
		Main_alpha_f_i[n] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( Main_V_x[n] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( Main_V_x[n] + (double)28 )  )  ) )  ) ;
		Main_beta_f_i[n] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( Main_V_x[n] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( Main_V_x[n] + (double)30 )  )  ) )  ) ;
		Main_g_K_i[n] =  ( (double)0.282 * sqrt(  ( Main_Ko_z / (double)5.4 )  ) ) ;
		Main_E_K_i[n] =  (  (  ( Main_R_z * Main_T_z )  / Main_F_z )  * log(  (  ( Main_Ko_z +  ( Main_PR_NaK_z * Main_Nao_z )  )  /  ( Main_Ki_z +  ( Main_PR_NaK_z * Main_Nai_z )  )  )  ) ) ;
		Main_i_K_i[n] =  ( Main_g_K_i[n] * Main_X_x[n] * Main_Xi_i[n] *  ( Main_V_x[n] - Main_E_K_i[n] )  ) ;
		Main_alpha_X_i[n] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( Main_V_x[n] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( Main_V_x[n] + (double)50 )  )  ) )  ) ;
		Main_beta_X_i[n] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( Main_V_x[n] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( Main_V_x[n] + (double)20 )  )  ) )  ) ;
		Main_Xi_i[n] =  (  ( Main_V_x[n] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( Main_V_x[n] + (double)77 )  )  ) - (double)1 )  )  /  (  ( Main_V_x[n] + (double)77 )  * exp(  ( (double)0.04 *  ( Main_V_x[n] + (double)35 )  )  ) )  )  : (double)1 ) ;
		Main_g_K1_i[n] =  ( (double)0.6047 * sqrt(  ( Main_Ko_z / (double)5.4 )  ) ) ;
		Main_E_K1_i[n] =  (  (  ( Main_R_z * Main_T_z )  / Main_F_z )  * log(  ( Main_Ko_z / Main_Ki_z )  ) ) ;
		Main_i_K1_i[n] =  ( Main_g_K1_i[n] * Main_K1_infinity_i[n] *  ( Main_V_x[n] - Main_E_K1_i[n] )  ) ;
		Main_alpha_K1_i[n] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( Main_V_x[n] - Main_E_K1_i[n] )  - (double)59.215 )  )  ) )  ) ;
		Main_beta_K1_i[n] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( Main_V_x[n] + (double)5.476 )  - Main_E_K1_i[n] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( Main_V_x[n] -  ( Main_E_K1_i[n] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( Main_V_x[n] - Main_E_K1_i[n] )  + (double)4.753 )  )  ) )  ) ;
		Main_K1_infinity_i[n] =  ( Main_alpha_K1_i[n] /  ( Main_alpha_K1_i[n] + Main_beta_K1_i[n] )  ) ;
		Main_E_Kp_i[n] = Main_E_K1_i[n];
		Main_Kp_i[n] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - Main_V_x[n] )  / (double)5.98 )  ) )  ) ;
		Main_i_Kp_i[n] =  ( Main_g_Kp_z * Main_Kp_i[n] *  ( Main_V_x[n] - Main_E_Kp_i[n] )  ) ;
		Main_i_b_i[n] =  ( Main_g_b_z *  ( Main_V_x[n] - Main_E_b_z )  ) ;
		Main_kV_k[n] =  (  (  ( - (double)1 )  / Main_C_z )  *  ( Main_I_stim_i[n] + Main_i_Na_i[n] + Main_i_si_i[n] + Main_i_K_i[n] + Main_i_K1_i[n] + Main_i_Kp_i[n] + Main_i_b_i[n] )  ) ;
		Main_km_k[n] =  (  ( Main_alpha_m_i[n] *  ( (double)1 - Main_m_x[n] )  )  -  ( Main_beta_m_i[n] * Main_m_x[n] )  ) ;
		Main_kh_k[n] =  (  ( Main_alpha_h_i[n] *  ( (double)1 - Main_h_x[n] )  )  -  ( Main_beta_h_i[n] * Main_h_x[n] )  ) ;
		Main_kj_k[n] =  (  ( Main_alpha_j_i[n] *  ( (double)1 - Main_j_x[n] )  )  -  ( Main_beta_j_i[n] * Main_j_x[n] )  ) ;
		Main_kd_k[n] =  (  ( Main_alpha_d_i[n] *  ( (double)1 - Main_d_x[n] )  )  -  ( Main_beta_d_i[n] * Main_d_x[n] )  ) ;
		Main_kf_k[n] =  (  ( Main_alpha_f_i[n] *  ( (double)1 - Main_f_x[n] )  )  -  ( Main_beta_f_i[n] * Main_f_x[n] )  ) ;
		Main_kX_k[n] =  (  ( Main_alpha_X_i[n] *  ( (double)1 - Main_X_x[n] )  )  -  ( Main_beta_X_i[n] * Main_X_x[n] )  ) ;
		Main_kCai_k[n] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * Main_i_si_i[n] )  +  ( (double)0.07 *  ( (double)0.0001 - Main_Cai_x[n] )  )  ) ;
		Main_V_x[ ( n + 1 ) ] =  ( Main_V_x[n] +  ( Main_kV_k[n] * (double)0.01 )  ) ;
		Main_m_x[ ( n + 1 ) ] =  ( Main_m_x[n] +  ( Main_km_k[n] * (double)0.01 )  ) ;
		Main_h_x[ ( n + 1 ) ] =  ( Main_h_x[n] +  ( Main_kh_k[n] * (double)0.01 )  ) ;
		Main_j_x[ ( n + 1 ) ] =  ( Main_j_x[n] +  ( Main_kj_k[n] * (double)0.01 )  ) ;
		Main_d_x[ ( n + 1 ) ] =  ( Main_d_x[n] +  ( Main_kd_k[n] * (double)0.01 )  ) ;
		Main_f_x[ ( n + 1 ) ] =  ( Main_f_x[n] +  ( Main_kf_k[n] * (double)0.01 )  ) ;
		Main_X_x[ ( n + 1 ) ] =  ( Main_X_x[n] +  ( Main_kX_k[n] * (double)0.01 )  ) ;
		Main_Cai_x[ ( n + 1 ) ] =  ( Main_Cai_x[n] +  ( Main_kCai_k[n] * (double)0.01 )  ) ;
		Main_time_t[ ( n + 1 ) ] =  ( Main_time_t[n] + (double)0.01 ) ;
		
		printf("%lf,%lf\n",Main_time_t[ ( n  ) ],Main_V_x[ ( n + 1 ) ]);
		n =  ( n + 1 ) ;

	}while(!( ( Main_time_t[n] > 800 ) ));

	V = Main_V_x[ ( n + 1 ) ];
	m = Main_m_x[ ( n + 1 ) ];
	h = Main_h_x[ ( n + 1 ) ];
	j = Main_j_x[ ( n + 1 ) ];
	d = Main_d_x[ ( n + 1 ) ];
	f = Main_f_x[ ( n + 1 ) ];
	X = Main_X_x[ ( n + 1 ) ];
	Cai = Main_Cai_x[ ( n + 1 ) ];
	t = Main_time_t[ ( n + 1 ) ];
	
	
	
	
	
	{

		free ( Main_j_x ) ; 
		free ( Main_Cai_x ) ; 
		free ( Main_d_x ) ; 
		free ( Main_m_x ) ; 
		free ( Main_f_x ) ; 
		free ( Main_time_t ) ; 
		free ( Main_X_x ) ; 
		free ( Main_h_x ) ; 
		free ( Main_V_x ) ; 
		free ( Main_g_K_i ) ; 
		free ( Main_kCai_k ) ; 
		free ( Main_i_si_i ) ; 
		free ( Main_E_K_i ) ; 
		free ( Main_i_Kp_i ) ; 
		free ( Main_I_stim_i ) ; 
		free ( Main_alpha_m_i ) ; 
		free ( Main_kj_k ) ; 
		free ( Main_i_Na_i ) ; 
		free ( Main_kd_k ) ; 
		free ( Main_Kp_i ) ; 
		free ( Main_E_K1_i ) ; 
		free ( Main_km_k ) ; 
		free ( Main_beta_d_i ) ; 
		free ( Main_kX_k ) ; 
		free ( Main_E_si_i ) ; 
		free ( Main_beta_X_i ) ; 
		free ( Main_alpha_X_i ) ; 
		free ( Main_alpha_f_i ) ; 
		free ( Main_E_Kp_i ) ; 
		free ( Main_kV_k ) ; 
		free ( Main_i_K1_i ) ; 
		free ( Main_K1_infinity_i ) ; 
		free ( Main_g_K1_i ) ; 
		free ( Main_alpha_d_i ) ; 
		free ( Main_beta_m_i ) ; 
		free ( Main_alpha_K1_i ) ; 
		free ( Main_beta_h_i ) ; 
		free ( Main_alpha_j_i ) ; 
		free ( Main_Xi_i ) ; 
		free ( Main_beta_j_i ) ; 
		free ( Main_beta_f_i ) ; 
		free ( Main_beta_K1_i ) ; 
		free ( Main_kf_k ) ; 
		free ( Main_E_Na_i ) ; 
		free ( Main_kh_k ) ; 
		free ( Main_i_b_i ) ; 
		free ( Main_alpha_h_i ) ; 
		free ( Main_i_K_i ) ; 

	}

	return 0;
}



