#Problem Fixes & Testing Steps

##Problems Encountered

Simply put, working with PlanetLab nodes is a bitch. Some of the problems encountered *do* actually have solutions though, I've listed them here for easy reference.

####Java won't install

If you attempt: `sudo yum install java`
And are presented with `Error: Cannot retrieve repository metadata (repomd.xml) for repository: fedora. Please verify its path and try again`
Then these steps worked for me (found on [stackoverflow](http://stackoverflow.com/a/28906973) and [fedora documentation](http://forums.fedoraforum.org/showpost.php?p=1225058&postcount=4))

1. `yum clean all`
2. `sudo rm -f /var/lib/rpm/__db*`
3. `sudo rpm --rebuilddb`
4. `cd /etc/yum.repos.d/`
5. `sudo perl -i -ape 's/https:/http:/g' *.repo`
6. `cd`
7. `sudo yum clean all`
8. `sudo yum repolist`
9. `sudo yum install java --nogpgcheck`

##Testing / Usage Checklist

Testing this program can be a pain. Sometimes you forget to change a file list somewhere or a port number and then nothing works. While these points of failue are being reduced, I've listed the steps I take after making changes to ensure everything will work.

1. Don't be Max
2. Seriously, don't be Max, you'll really fuck it all up
3. 
