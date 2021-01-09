#!/usr/bin/env bash
#BSUB -P paperlesss
#BSUB -J ycsb_bench 
#BSUB -R "rusage[scratch=5120] span[hosts=1]"
#BSUB -o out.ycsb_bench.o%J
#BSUB -W 04:00
#BSUB -n 48

# On cluster run: bsub < artificial_workload_benchmark.sh

KILO=1024
MEGA=$((1024 * KILO))
GIGA=$((1024 * MEGA))

RANKS=(4 8 16 24)
N_RUNS=3

export MAX_LOCAL_MEMTABLE_SIZE=$GIGA
export MAX_REMOTE_MEMTABLE_SIZE=$GIGA
export MAX_LOCAL_CACHE_SIZE=$GIGA
export MAX_REMOTE_CACHE_SIZE=$GIGA
export DISPATCH_IN_CHUNKS=1
EXPERIMENT=ycsb_bench_paperless
export STORAGE_LOCATION=/scratch/$EXPERIMENT/
DATA_LOCATION=/cluster/scratch/$USER/$EXPERIMENT
#export STORAGE_LOCATION=/home/julia/eth/dphpc/paperless/analytics/data/$EXPERIMENT/checkpoints
#DATA_LOCATION=/home/julia/eth/dphpc/paperless/analytics/data/$EXPERIMENT

mpirun --version
MPIRUN_FLAGS=("--report-bindings" "--map-by" "node:pe=2")

rm -rf ${STORAGE_LOCATION}*
for i in "${RANKS[@]}"; do
  for k in $(seq $N_RUNS); do
      echo "ranks$i/run$k"
      rm -rf ${STORAGE_LOCATION}*
  done
done
