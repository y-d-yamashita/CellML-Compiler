About CellCompiler2012

<Abstract>
This system generates C or Java program code.
Any one of RelML, SimpleRecML, and StructuredRecML is 
considered as an input file in it. 
 
<GUI>
location: jp.ac.ritsumei.is.hpcss.cellMLonGPU.app 
 CellCompilerGUI.java

<Main>
location: jp.ac.ritsumei.is.hpcss.cellMLonGPU.app
 CellCompilerMain.java

  Arguments Information  
  1. -C or -J
  2. RelML or SimpleRecML or StructuredRecML
  3. Input file directory	(example; ./model/RelML/fhn_BackwardEuler.relml)
  4. Output directory 		(example; ./model)
  5. Output file name 		(example;  fhn_BackwardEulerSimulation.java) 


<SubMain>
Code Generator for Non-linear Simultaneous Equation system
location: jp.ac.ritsumei.is.hpcss.cellMLonGPU.app 
 ProgramGeneratorMain.java @author n-washio

Equation Expansion for Recurrence Relation system
location: jp.ac.ritsumei.is.hpcss.cellMLonGPU.app 
 SimpleExpansionMain.java @author m-ara
