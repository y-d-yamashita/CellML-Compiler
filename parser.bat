@echo off

rem �N���X�p�X�ݒ�
SET CLASS_bin=./bin

SET CLASS_lib=./lib/xercesImpl.jar
SET CLASS_lib=%CLASS_lib%;./lib/xml-apis.jar

SET CLASS_PATH=%CLASS_bin%;%CLASS_lib%

rem �R�}���h���C������
rem SET ARGS=.\model\relml\luo_rudy_1991\luo_rudy_1991_Euler.relml
rem SET ARGS=.\model\relml\luo_rudy_1991\luo_rudy_1991_MEuler.relml
rem SET ARGS=.\model\optest\fhn_Euler.relml
SET ARGS=%*

java -cp %CLASS_PATH% jp.ac.ritsumei.is.hpcss.cellMLonGPU.app.CellMLonGPUMain %ARGS%
