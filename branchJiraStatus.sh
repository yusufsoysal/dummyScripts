export PATH=$PATH:Development/groovy-2.3.7/bin
echo $PATH

for k in `cat ~/branchList.txt | sed -e 's/.*origin\/\(.*\)/\1/g'`; do
   `groovy jira.groovy $k`
done
