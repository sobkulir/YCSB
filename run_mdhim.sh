#!/usr/bin/env bash
#BSUB -P ycsb
#BSUB -J mdhim
#BSUB -R "rusage[scratch=5120] span[hosts=1]"
#BSUB -o out.ycsb_mdhim.o%J
#BSUB -W 00:05
#BSUB -n 4

# On cluster run: bsub < run_mdhim.sh

# This path is also hardcoded in MDHIM code src/mdhim.c
export STORAGE_LOCATION=/scratch/rsobkuliak/hng

mpirun --version
MPIRUN_FLAGS=("--report-bindings")

      mpirun -np $i ${MPIRUN_FLAGS[@]} ./build/thegreatbenchmark_paperless $KEYLEN $VALLEN $COUNT $j $PAPERLESS_PATH
      rm -rf ${STORAGE_LOCATION}*
      echo mpirun -np $i ${MPIRUN_FLAGS[@]} ./build/thegreatbenchmark_papyrus $KEYLEN $VALLEN $COUNT $j $PAPYRUS_PATH
      mpirun -np $i ${MPIRUN_FLAGS[@]} ./build/thegreatbenchmark_papyrus $KEYLEN $VALLEN $COUNT $j $PAPYRUS_PATH
      rm -rf ${STORAGE_LOCATION}*
    done
  done
done
