#!/bin/sh

mkdir ./dist
mkdir ./dist/Evolvo

cp ../README ./dist/Evolvo/
cp ../HISTORY ./dist/Evolvo/
cp ../src/LICENSE ./dist/Evolvo/
cp -R ./evolvo/build/evolvo.app ./dist/Evolvo
