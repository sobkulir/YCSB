#!/usr/bin/env bash
export LD_LIBRARY_PATH=$HOME/usr/lib:$LD_LIBRARY_PATH
export STORAGE_LOCATION="/scratch/hihi"
export CHECKPOINT_PATH="/scratch/alevole/"

export SHOULD_RESTART=0
./bin/ycsb load papyrus -P workloads/workloada -P benchsetup.dat
export SHOULD_RESTART=1
./bin/ycsb run papyrus -P workloads/workloada -P benchsetup.dat

