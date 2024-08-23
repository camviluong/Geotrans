#! /bin/csh

# CLASSIFICATION : UNCLASSIFIED

set dir=`dirname $0`

# set the library path for linux
setenv LD_LIBRARY_PATH	${dir}:../linux_64
setenv MSPCCS_DATA	../../data

./testCoordinateConversionSample
