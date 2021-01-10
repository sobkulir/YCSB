#!/usr/bin/env bash
export LD_LIBRARY_PATH=$HOME/usr/lib:$LD_LIBRARY_PATH
export STORAGE_LOCATION="/scratch/hihi"

./bin/ycsb load paperless -P workloads/workloada -P benchsetup.dat
./bin/ycsb run paperless -P workloads/workloada -P benchsetup.dat

