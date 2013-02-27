About CellCompiler2012

<Abstruct>
This system generates C or Java program code.
Any one of RelML, SimpleRecML, and StructuredRecML is 
considered as an input file in it. 
 
<GUI>
jp.ac.ritsumei.is.hpcss.cellMLonGPU.app
CellCompilerGUI.java

<Main>
jp.ac.ritsumei.is.hpcss.cellMLonGPU.app
CellCompilerMain.java

  Arguments Information  
  1. -J or -C
  2. RelML or SimpleRecML or StructuredRecML
  3. Input file directory	(ex; ./model/RelML/fhn_BackwardEuler.relml)
  4. Output directory 		(ex; ./model)
  5. Output file name 		(ex; fhn_BackwardEulerSimulation.java) 


<SubMain>
jp.ac.ritsumei.is.hpcss.cellMLonGPU.app 
Code Generator for Non-linear Simultaneous Equation system
-ProgramGeneratorMain.java @author n-washio

jp.ac.ritsumei.is.hpcss.cellMLonGPU.app
Equation Expansion for Recurrence Relation system
-SimpleExpansionMain.java @author m-ara
