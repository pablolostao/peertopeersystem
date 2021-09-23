# Peer to peer file system
This repository implements a peer to peer file system in Java, which works with a central indexer server and a multitude of peers. 
It can be run both locally and over a network. The operation is simple, each time a peer is created it indicates which folder it wants to share, indicating the absolute path 
of this folder in its local file system. Automatically the system will register in the central server all the files (including subfolders) stored in the shared folder. 
A WatcherService is provided, which will automatically notify the central server of all changes (deletion, modification and creation) that occur in that folder. 

Peers can:

1. Look for a file. At this moment, only file name is used for matching. The indexer server responds with a set of peers that own that file.
2. Retrieve a file. They choose one peer of the list obtained in step 1. and it will download the file from the owner of the file and storing it in the shared folder (and the indexer will be notified)
3. Exit

# Prerequisites:
You must have JDK installed to compile this project and JRE to run it (tested with openjdk version "1.8.0_292")

Git

make

# Execution
Each instance of the program is either a indexer or a peer, so when it is executed, the user must choose.

1.  git clone https://github.com/pablolostao/peertopeersystem
2.  cd peertopeersystem
2.  make
3.  make run
