<?php
echo 'It begins..';

// Testing client for a single node KVStore service

// The host running the Key Value Store
$host = 'planetlab1.cs.colorado.edu';

// The port the host is listening on
$port = 5000;

// Message commands
$PUT = 1;
$GET = 2;
$REMOVE = 3;
$SHUTDOWN = 4;

/*
 * Build 7 messages for testing correctness
 */

// Build a GET, we expect a KEY DNE Response
$messages[0] = new SendMessage($GET, generateRandomString());

// Build a REMOVE, we expect a KEY DNE Response
$messages[1] = new SendMessage($REMOVE, $messages[0]->key);

// Build a PUT, we expect a success response
$messages[2] = new SendMessage($PUT, $messages[0]->key, generateRandomString(50));

// Build a GET, we expect a success response
$messages[3] = $messages[0];

// Build a REMOVE, we expect a SUCCESS response
$messages[4] = $messages[1];

// Build a GET, we expect a KEY DNE resposne
$messages[5] = $messages[0];

// Build a SHUTDOWN command, we expect a SUCCESS
$messages[6] = new SendMessage($SHUTDOWN);

/*
 * Sockets
 */

// Create and bind the socket
$socket = socket_create(AF_INET, SOCK_DGRAM, 0) or die("Could not create socket");
socket_bind($socket, "0.0.0.0", $port);

// Send and receive messages
for($i = 0; $i < $messages->length; $i++) {
	socket_sendto($socket, $messages[$i]->data, strlen($messages[i]->data), 0x00, $host, $port) or die("Could not send data to KVStore");
	echo "It sent the message\n";
	socket_recvfrom($socket, $result, 15500, 0, $from, $port) or die("Could not read server response");
	parseMessage($messages[$i]->command, $result);
}

echo "It ends.. for now.\n";

function parseMessage($sentCmd, $data) {
	echo "\nMessage received!";
	echo "- - - - - - - - - - - - -\n";
	echo "Response Code: ".$data[16]."\n\n";
}

function determineString($sentCmd, $data) {
	$responseArray = unpack('H', $data);
	$responseCode = $responesArray[16];
	if ($command == $GET and $responseCode == 0) {
		// do something
	}
}


class SendMessage {
	public $uniqueID;
	public $command;
	public $key;
	public $valueLength;
	public $value;
	public $data;

	function __construct($command) {
		$this->uniqueID = generateRandomString(16);
		$this->command = $command;
		$this->data = $uniqueID.pack(I, $command);
	}

	function __construct1($command, $key) {
		$this->uniqueID = generateRandomString(16);
		$this->command = $command;
		$this->key = $key;
		$this->data = $uniqueID.pack(I, $command).$key;
	} 

	function __construct2($command, $key, $value) {
		$this->uniqueID = generateRandomString(16);
		$this->command = $command;
		$this->key = $key;
		$this->valueLength = strlen($value);
		$this->value = $value;
		$this->data = $uniqueID.pack(I, $command).$key.pack(I, $valueLength).$value;
	}
}

/*
 * Function to generate a random string of default length = 32
 */
function generateRandomString($length = 32) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

?>