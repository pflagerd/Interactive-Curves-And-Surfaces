#!/usr/bin/python

import time
newFileName = time.strftime('%y%m%d%H%M%SZ.md', time.gmtime())

import glob
import shutil
shutil.copyfile(sorted(glob.glob('19*.md'))[-1], newFileName)
