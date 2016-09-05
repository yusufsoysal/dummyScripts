cd /Users/yusufsoysal/Development/dmall
for k in `git branch -r | perl -pe 's/^..(.*?)( ->.*)?$/\1/'`; do echo `git show --pretty=format:"%Cgreen%ci %Cblue%cr%Creset" $k -- | head -n 1` $k; done | sort -r
cd -
