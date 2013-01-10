#!/bin/bash 

TARGET_ENV=py-local

wget https://raw.github.com/pypa/virtualenv/master/virtualenv.py
echo $TARGET_ENV
python virtualenv.py --system-site-packages $TARGET_ENV
source $TARGET_ENV/bin/activate
pip install -r requirements.txt
