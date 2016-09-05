#for k in `git branch -r | perl -pe 's/^..(.*?)( ->.*)?$/\1/'`; do echo -e `git show --pretty=format:"%Cgreen%ci %Cblue%cr%Creset" $k -- | head -n 1`\\t$k; done | sort -r
cd /Users/yusufsoysal/Development/dmall
for k in `cat ~/scripts/branchList.txt | sed -e 's/.*origin\/\(.*\)/\1/g'`; 
   do `git push origin --delete $k`; 
done
#cat ~/branchList.txt | sed -e 's/.*origin\/\(.*\)/\1/g' | git push origin --delete 
