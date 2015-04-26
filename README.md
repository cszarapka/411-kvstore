#Group 11 : Key-Value Distributed System
Monitoring Service: http://104.236.25.77:3000/


##Files
Each node contains the following files:
* kvs.jar             //Our project code
* monitor.jar         //The node's monitoring service
* javaCheck.jar       //Runs once every 3 minutes and reboots kvs.jar if it is not running
* monitorCheck.jar    //Runs once every 3 minutes and reboots monitor.jar if it is not running

##MESSAGE FORMAT
Commands are sent to the system, from any computer, by sending a message to *node-IP:port* using the *wire protocol*. The list of nodes running our code can be found [here](runningNodes.txt).

###Wire Protocol
Messages (requests) sent to the system must conform to the protocol below. The commands for these operations are shown in the next section: App-Layer Commands.


####Put
Command | Key       | Value-Length                      | Value
------- | --------- | --------------------------------- | -----
1 byte  | 1 byte    | 2 bytes   | up to 15,000 bytes

####Get and Remove
Command | Key 
------- | ---
1 byte  | 1 byte

####Get Response

Response Code | Value-Length                | Value
------------- | --------------------------- | -----
1 byte | Integer; 2 bytes; little endian    | up to 15,000 bytes

####Shutdown

Command |
------- |
1 byte  |



##App-Layer Commands

- **0x01:** put(*key*, *value*)
- Puts some value into the store. The value can be later retrieved using the key. If there is already a value corresponding to the key then the value is overwritten.
- **0x02:** get(*key*)
- Returns the value that is associated with the key. If there is no such key in our store, an error - not found - is returned.
- **0x03:** remove(*key*)
- Removes the value that is associated with the key. If there is no such key the store returns an error to the user.

## App-Layer Responses

Upon making a request to the system, you will *should* receive a response.

- **0x00:** The operation was successful
- **0x01:** Non-existent key requested in a get or remove operation
- **0x02:** Out of space (no room for a _put_)
- **0x03:** System overload
- **0x04:** Internal KVStore failure
- **0x05:** Unrecognized command

##In Depth 
Our DHT code is made up of 16 files:

###Main  
* Server.java         //Main code. Initializes datatypes and tries to join DHT.
* WDT.java            //WatchDogThread broadcasts isAlive messages to other nodes.

###Communication
* UDPSocket.java      //Creates UDP sockets for communication.
* Builder.java        //Builds byte array to be sent to other nodes.
* Message.java        //Converts received byte array into readable message.
* AppResponse.java    //Builds response to message.

###Handlers
* JoinHandler.java    //Thread handles requests from other nodes to join DHT.
* KVSHandler.java     //Thread handles forwarded GET and PUT requests.
* UpdateHandler.java  //Thread handles notifications from nodes regarding status of others.

###Structures 
* Node.java           //Node class containing host address and local timestamp.
* DHT.java            //DHT circle containing nodes.
* KVS.java            //Storage for key/value pair.

###Utilities
* Commands.java       //Determines the type of command being issued.
* HashFunction.java   //MD5 hash function.
* Protocols.java      //List of OPcodes and important values used throught execution.
* Utils.java          //Helper functions for byte level operations.






##Node-Layer Commands/Requests

These are messages sent between nodes, such as a lookup, in order to accomplish the functionality of a distributed KVStore.

_Todo:_ list commands here

##System Details

- **Scale:** this system will be deployed on 150-200 nodes when it is fully completed
- **Correctness:** tested through correct responses following sequences of gets, puts and removes
- **Robustness:** our service continues to operate in spite of node failures
- **Data durability:** our system does not lose data to node failures (_not yet implemented_)
- **Performance:** responsiveness (time to reply to requests) and throughput have been tested
- _Todo:_ list the results
- **Data availability:** if a request is not served in 5 seconds, it is considered failed
- **Persistence:** no data is persisted on disk
- **Memory usage:** the space on each node is limited by at most 100,000 key/value pairs _or_ 64MB of space (including any replication data)
