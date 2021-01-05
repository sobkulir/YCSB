#!/usr/bin/env bash

# First argument is database path that needs to be created.
rm -rf $1
mkdir -p $1
./bin/ycsb load mdhim -P workloads/workloada
