<?php
/*
 *	Jar files are automatically updated by Eclipse.
 *	We just need to send them to the nodes we will be testing on.
 */

// Create an array to store file names
$nodes = array();
$count = 0;

/*
 * Get the nodes we are testing on.
 * The first node in the file list will always be the "starting" node.
 * That is, the one who will receive the CREATE-DHT command
 */
$nodesFile = fopen("repTestNodes.txt", "r") or die("Unable to open file :(");
while(!feof($nodesFile)) {
	$nodes[$count] = trim(fgets($nodesFile));
	$count++;
}
fclose($nodesFile);

echo "Distributing kvStore JARs and node lists...\n";
// Iterate through all the nodes, SCPing all the kvStore.jar files to them
$counter = 0;
while($counter < $count) {
	echo shell_exec('scp /Users/cam/Dev/411-kvstore/JARs/In_Development/kvStore.jar ubc_eece411_5@'.$nodes[$counter].':');
	echo shell_exec('scp /Users/cam/Dev/411-kvstore/Testing/repTestNodes.txt ubc_eece411_5@'.$nodes[$counter].':');
	echo 'Success: '.$nodes[$counter]."\n";
	$counter++;
}

// Lastly, update the testingClient node, the node to test on is passed as the argument
echo "Distributing testingClient JAR...\n";
echo shell_exec('scp /Users/cam/Dev/411-kvstore/JARs/In_Development/testingClient.jar ubc_eece411_5@'.trim($argv[1]).':');
echo 'Success: '.$argv[1]."\n";
?>