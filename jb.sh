#!/bin/sh
mvn -U clean install
cd target
unzip jwat-tools-gui-*.zip
cd ..
