#!/bin/bash

freeglut_version='2.8.1'
#freeglut_version='3.0.0'

if [ ! -x freeglut-${freeglut_version} ]; then
	tar xfz freeglut-${freeglut_version}.tar.gz
fi