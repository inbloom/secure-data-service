#!/bin/bash 

TARGET_ENV=py-local

wget https://raw.github.com/pypa/virtualenv/master/virtualenv.py
python virtualenv.py $TARGET_ENV
source $TARGET_ENV/bin/activate
pip install -r requirements.txt 
rm virtualenv.py*
