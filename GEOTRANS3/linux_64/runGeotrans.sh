#!/bin/csh -f
# CLASSIFICATION: UNCLASSIFIED

set dir=`dirname $0`

setenv LD_LIBRARY_PATH ${dir}:${dir}/../../CCS/linux_64
setenv MSPCCS_DATA ${dir}/../../data
setenv JAVA_HOME /usr/jre1.8.0_191

${JAVA_HOME}/bin/java -Xss1024k -jar ${dir}/MSPCCS.jar >& /dev/null
