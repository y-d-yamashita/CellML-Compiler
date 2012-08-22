@echo off

rem %1 は simple, common, cuda, java, java_bigdecimal のいずれか
SET DSTDIR=out_%1_gen

rem クラスパス設定
SET CLASS_bin=./bin
SET CLASS_lib=./lib/xercesImpl.jar
SET CLASS_lib=%CLASS_lib%;./lib/xml-apis.jar
SET CLASS_PATH=%CLASS_bin%;%CLASS_lib%

SET PARSER=java -cp %CLASS_PATH% jp.ac.ritsumei.is.hpcss.cellMLonGPU.app.CellMLonGPUMain -g %1

SET SRC=iyer_mazhari_winslow_2004\iyer_mazhari_winslow_2004_Euler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=iyer_mazhari_winslow_2004\iyer_mazhari_winslow_2004_MEuler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=iyer_mazhari_winslow_2004\iyer_mazhari_winslow_2004_RungeKutta
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=luo_rudy_1991\luo_rudy_1991_Euler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=luo_rudy_1991\luo_rudy_1991_MEuler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=luo_rudy_1991\luo_rudy_1991_RungeKutta
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=matsuoka_sarai_kuratomi_ono_noma_2003R\matsuoka_sarai_kuratomi_ono_noma_2003R_Euler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=matsuoka_sarai_kuratomi_ono_noma_2003R\matsuoka_sarai_kuratomi_ono_noma_2003R_MEuler
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=matsuoka_sarai_kuratomi_ono_noma_2003R\matsuoka_sarai_kuratomi_ono_noma_2003R_RungeKutta
%PARSER% .\model\relml\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=fhn2\fhn_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=fhn2\fhn_ModifiedEuler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=fhn2\fhn_RungeKutta
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=luo_rudy_1994\luo_rudy_1994_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=sample\sample_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%

SET SRC=relml2\matsuoka_sarai_kuratomi_ono_noma_2003R_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\fhn_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\fhn_MEuler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\fhn_RungeKutta2
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\fhn_RungeKutta4
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\luo_rudy_1991_RungeKutta4
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\luo_rudy_1994_MEuler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\luo_rudy_1994_RungeKutta4
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\simpleout_Euler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\simpleout_MEuler
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\simpleout_RungeKutta2
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\simpleout_RungeKutta3
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
SET SRC=relml2\simpleout_RungeKutta4
%PARSER% .\model\%SRC%.relml >out.txt
copy out.txt %DSTDIR%\%SRC%
copy relation.txt %DSTDIR%\%SRC%
copy initialize.txt %DSTDIR%\%SRC%
