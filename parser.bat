@echo off

SET CLASS_bin=./bin
SET CLASS_lib=./lib/xercesImpl.jar
SET CLASS_lib=%CLASS_lib%;./lib/xml-apis.jar
SET CLASS_PATH=%CLASS_bin%;%CLASS_lib%
SET ARGS=%*

java -cp %CLASS_PATH% jp.ac.ritsumei.is.hpcss.cellMLonGPU.app.CellCompilerMain %ARGS%
