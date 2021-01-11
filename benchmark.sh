#!/usr/bin/env bash
#BSUB -P paperless 
#BSUB -J ycsb 
#BSUB -R "rusage[scratch=5120] span[hosts=1]"
#BSUB -o out.ycsb.o%J
#BSUB -W 04:00
#BSUB -n 48

# On cluster run: bsub < benchmark.sh

KILO=1024
MEGA=$((1024 * KILO))
GIGA=$((1024 * MEGA))

RANKS=(1 2)
N_RUNS=1
DBs=( "papyrus" "paperless" "mdhim" )
# Paperless parameters.

export MAX_LOCAL_MEMTABLE_SIZE=$GIGA
# MAX_REMOTE_MEMTABLE_SIZE has to be set to MAX_LOCAL_MEMTABLE_SIZE because papyrus uses
# one param to set both sizes.
export MAX_REMOTE_MEMTABLE_SIZE=$MAX_LOCAL_MEMTABLE_SIZE
export MAX_LOCAL_CACHE_SIZE=$GIGA
# Same as for memtable size.
export MAX_REMOTE_CACHE_SIZE=$MAX_LOCAL_CACHE_SIZE
export DISPATCH_IN_CHUNKS=1

# Papyrus parameters.
# Enable usage of caches.
export PAPYRUSKV_CACHE_LOCAL=1
export PAPYRUSKV_CACHE_REMOTE=1
export PAPYRUSKV_MEMTABLE_SIZE=$MAX_LOCAL_MEMTABLE_SIZE
# Remote buffer size is kept default (128KB), as Paperless doesn't have such a thing.
# We are benchmarking with 128KB values, so the KV pairs are quite big already and with
# the default settings the remote buffer gets disabled anyway.
# export PAPYRUSKV_REMOTE_BUFFER_SIZE=...
# PAPYRUSKV_REMOTE_BUFFER_ENTRY_MAX -- max size of value in remote buffer, if value size is specified. Default 4KB
export PAPYRUSKV_CACHE_SIZE=$MAX_LOCAL_CACHE_SIZE
export PAPYRUSKV_DESTROY_REPOSITORY=1 # Gets rid of the data afterwards.

# General
# Add mdhim, paperless and papyrus java wrappers to lib path.
export LD_LIBRARY_PATH=$HOME/usr/lib:$LD_LIBRARY_PATH
EXPERIMENT=ycsb
export STORAGE_BASE=/scratch/$EXPERIMENT

mpiexec --version
MPIEXEC_FLAGS=("--report-bindings" "--map-by" "node:pe=2")

rm -rf ${STORAGE_BASE}
for k in $(seq $N_RUNS); do
  for i in "${RANKS[@]}"; do
    for db in "${DBs[@]}"; do
      echo "run$k/ranks$i/db-$db"
      export STORAGE_LOCATION=$STORAGE_BASE/$db/storage
      export CHECKPOINT_PATH=$STORAGE_BASE/$db/checkpoint
      mkdir -p $STORAGE_LOCATION $CHECKPOINT_PATH

      # Load
      export SHOULD_RESTART=0
      echo "mpiexec -np $i ${MPIEXEC_FLAGS[@]} ./bin/ycsb load $db -P workloads/workloada -P benchsetup.dat"
      mpiexec -np $i ${MPIEXEC_FLAGS[@]} ./bin/ycsb load $db -P workloads/workloada -P benchsetup.dat

      # Run
      export SHOULD_RESTART=1
      echo "mpiexec -np $i ${MPIEXEC_FLAGS[@]} ./bin/ycsb run $db -P workloads/workloada -P benchsetup.dat"
      mpiexec -np $i ${MPIEXEC_FLAGS[@]} ./bin/ycsb run $db -P workloads/workloada -P benchsetup.dat
      rm -rf ${STORAGE_BASE}
    done
  done
done
