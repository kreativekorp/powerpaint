#!/usr/bin/env bash
set -e
rm -rf moccp

echo Building...
java -cp ./bin com.kreative.paint.material.MOCCPCurator

echo Packaging...
export COPYFILE_DISABLE=true
find moccp -name .DS_Store -delete
tar -zcvf moccp.tgz moccp
