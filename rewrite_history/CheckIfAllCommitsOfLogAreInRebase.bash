#! /bin/bash

ERRORS=0

COMMITS=$(cut -d' ' -f1 $1)
for C in $COMMITS; do
	echo "[LOG] Checking for $C in $2"
	if grep -q $C $2; then
		grep $C $2
		echo "[LOG] Got $? - Found $C in $2"
	else
		echo "[ERROR] Got $? - Didn't find $C in $2"
		(( ERRORS++ ))
	fi
	echo ""
done

echo "[LOG] There are $ERRORS errors."

