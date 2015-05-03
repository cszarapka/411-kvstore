#Group 11 : Key-Value Distributed System
Cam Szarapka
Max Parker
Ryan Clarke
Stephan Bouthot

Monitoring Service: http://104.236.25.77:3000/

##File List
nodes.txt - list of nodes the service is running
kvStore.zip - source code
README.txt - this file!
test81nodesResults.txt - example output from our own tests
test45nodesResults.txt - example output from our own tests

##Design

###Overview
This is a distributed key-value store based on consistent hashing. The system is distributed across 100 planetlab nodes, whom exhibit lossy channels, bandwidth issues, and crash regularly. Our design goal was to maximize request servicing time while keeping bandwidth usage low, in a manner that took advantage of the scale of our system. With 100 nodes, it was reasonable to have each node be aware of every node in the network and communicate with each other. A node receiving a request will either satisfy the request, or only have to pass it to one node in order to satisfy it. To maintain the correctness of each node's view of the system, frequent "isAlive" messages are broadcasted. A timestamp based on the "isAlive" messages is used to determine when a node has died.

###Initialization
There are two modes of operation for a node. One node, chosen by us, will be the instigator. Passing this node the "create" command when launching the JAR will cause it to create a hash table with itself as node 255, and then listen for app commands, join requests, and "isAlive" messages. All other nodes will start without any specified command, which causes them to begin sending "join-requests" to a random node from the node_list.txt file.

###Key Partitioning
When a node boots, it sends join requests to random nodes from the node_list.txt file until one responds within a given timeout. To determine the node ID of the requesting node the node already in the table does the following:
- determine the node ID halfway in between itself and the closest CCW node
- offers this node ID to the requesting node, along with its current view of the system
Upon receival of this information the requesting node constructs its table, adds itself, and then broadcasts an "isAlive" message to all nodes it is aware of.

###Membership
Membership is managed through the use of "isAlive" and "isDead" messages.
Upon receipt of an "isAlive" message, a node will check if it already has this node in its table, if so, it will update a timestamp for said node, if not in our table, it will be added. If a node being added to the table is to become one of our neighbors, some replication will take place, which will be discussed later.
Upon receipt of an "isDead" message, a node will remove the node from its table, and perform replication if the dead node was an immediate neighbor.
Through the use of a WatchdogThread (WDT) we discover the death of a node and keep other nodes up-to-date on our liveliness. The WDT periodically broadcasts an "isAlive" message to all nodes, which will cause them to update their timestamp for said node. Then, the WDT scans through all known nodes, considers their timestamp against the current time, determining if it is greater than a predefined allowed max difference. If the difference is too great the node is assumed dead, and an isDead message is broadcasted to all nodes.

###Application Layer Requests (PUT, GET, REMOVE)
Upon receipt of an application layer request, the node will first determine if it should service this request. It should service this request if the specified key is in its range. If so, the request is performed and response sent to the requester. Should the key not fall in our range, the node refers to the local table and finds who is responsible for the key, and then "echos" the application request to the servicing node. The servicing node performs the requested operation, returns an "echoed response" to the node that echoed it the request, who then passes this "echoed response" back to the original requester. With this scheme we can achieve speedy lookups with minimal bandwidth usage.
We originally had the servicing node reply directly to the orginal requester, but those messages would not be received for some reason. We concluded responses must be received from the node that received the corresponding request.

###Replication
Replication offers two great benefits: greater load balancing and the safety of files in the face of node failures. Replication does create inconsitencies however, and this lead us to make a trade-off. We chose to sacrifice the load-balancing benefit of replication by only allowing nodes to service requests on keys in their own range. Replicated values a node would hold were solely to be shared with nodes on the death/replacement/addition of a neighboring node. This prevents any node from offering the user a stale, inconsistent value, and maintains key/value data in the face of node failures.
Our design has a node replicate all of the keys which it is responsible for to the nodes immediately CW and CCW of it, which we have colloquially referred to as our "neighbors." Upon receival of a PUT, the PUT is performed and also replicated to our neighbors. Upon death of a neighbor, files are replicated to the replacing neighbor. Upon arrival of a new neighbor via isAlive, keyValue pairs are once again replicated to the node.

###Testing

We ran two sets of nodes through the test suite. The outputs for these two sets are are located in test81nodesResults.txt and test45nodesResults.txt. 
The 45 node system was hand picked to be of the lowest latency and most reliable we could get: ultra reliable servers that are located in North America. We got our best throughput for catastrophic on this: up to 86 req/s with 67 req/s being successful, with a range of 214-614 ms, avg of 370 ms.
The 81 node system was created from trying every single node we could access on planetlab that could be connected to. These are located all over the world, most of them (roughly half) are located in North America, the rest are distributed throughout Europe, Eastern Asia, New Zealand, and Brazil. Our throughput was much lower for this set, with numbers approaching 20 req/s for catastrophic scenario. The latency for the operations range of 300-1000 ms, with an average of 560 ms.