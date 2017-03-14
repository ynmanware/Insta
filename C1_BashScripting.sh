# This script accepts two parameters 
# param 1 - starting directory e.g. myData
# param 2 - true or false, true would include histogram of words in the final result

# The result is stored in wordCountResult.txt file in the current directory

startDir="$1/*";

includeHistogram=$2;

[ -e wordCountResult.txt ] && rm wordCountResult.txt
echo "WordCount FileName" > wordCountResult.txt
echo "------------------------------" >> wordCountResult.txt

function checkContent () {
   for entry in $@; do
    if [[ -d $entry ]] ; then
	 checkContent $entry/*
    elif [[ -f $entry ]] ; then
     wc -w $entry >> wordCountResult.txt
     if [ $includeHistogram == true ] ; then
        echo "    Histogram: " >> wordCountResult.txt
        echo "      Occurrences Word" >> wordCountResult.txt
        echo "      ............." >> wordCountResult.txt
        cat $entry | tr ' ' "\n" | sed '/^\s*$/d' | sort | uniq -c >> wordCountResult.txt  
     fi
     echo "------------------------------" >> wordCountResult.txt
    fi
   done
}

checkContent $startDir;