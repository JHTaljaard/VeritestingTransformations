#!/bin/bash

alias runSPF-ISSTAExample ='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/export/scratch/soha/pathFinder/VeritestingTransformations/lib TARGET_CLASSPATH_WALA=/export/scratch/soha/pathFinder/VeritestingTransformations/build/src/ java -Djava.library.path=/export/scratch/soha/pathFinder/VeritestingTransformations/lib -Xmx1024m -ea -Dfile.encoding=UTF-8 -jar /export/scratch/soha/pathFinder/jpf-core-veritesting/build/RunJPF.jar '
shopt -s expand_aliases
VERIDIR=/export/scratch/soha/pathFinder/VeritestingTransformations
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element5.mode1.jpf >& $VERIDIR/logs/ISSTAExample.element5.mode1.log
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element5.mode3.jpf >& $VERIDIR/logs/ISSTAExample.element5.mode3.log
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element5.mode4.jpf >& $VERIDIR/logs/ISSTAExample.element5.mode4.og
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element10.mode1.jpf >& $VERIDIR/logs/ISSTAExample.element10.mode1.log
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element10.mode3.jpf >& $VERIDIR/logs/ISSTAExample.element10.mode3.log
runSPF-PaperExample $VERIDIR/src/ISSTAExample/ISSTAExample.element10.mode4.jpf >& $VERIDIR/logs/ISSTAExample.element10.mode4.og
