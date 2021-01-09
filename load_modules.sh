#!/usr/bin/env bash

# You need to run this script in the same subshell to not loose
# the loaded modules:
#    . load_modules.sh

# Use new software stack.
# env2lmod is an alias set somewhere, but I couldn't run it from inside of
# this script, so I used contents of "which env2lmod"  
. /cluster/apps/local/env2lmod.sh

module load openmpi/3.0.1 maven/3.5.0 openjdk/14.0.2 leveldb/1.20
module load zstd/1.3.0 snappy/1.1.7 
