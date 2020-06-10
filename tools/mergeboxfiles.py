# Created by Seltix
# 06-2020

import os
import glob
import re
import argparse

parser = argparse.ArgumentParser(
    formatter_class=argparse.RawDescriptionHelpFormatter,
    description='''\
description:
  This script will merge the content of all BOX files in the target folder.
  The result will be written to "all.box" in the same selected folder.''',
    epilog='''\
--------------------------------
Created by Seltix.''')
parser.add_argument("path", nargs='?', default="", help="Target folder. Current directory will be used if not set")
parser.add_argument("-q", "--quiet", action='store_true', dest='silent', help="Execute silently ( disable console output )")

args = parser.parse_args()


args.path = args.path.rstrip('\\')
if len(args.path) > 0:
    args.path = args.path + '\\'


if os.path.exists(args.path + "all.box"):
    os.remove(args.path + "all.box")


concat = ''
i = 0
for f in glob.glob(args.path + "*.box"):
    for line in open(f):
        concat = concat + re.sub(r'(.+) (\d+) (\d+) (\d+) (\d+) (\d+)', r"\1 \2 \3 \4 \5 " + str(i), line)
    i = i+1


if i > 0:
    with open(args.path + "all.box", "w") as fo:
        fo.write(concat)


if not args.silent:
    try:
        print(str(i) + " files merged!")
        input("Press Enter to continue...")
    except SyntaxError:
        pass