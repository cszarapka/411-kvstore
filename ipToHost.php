<?php

$fileContents = file_get_contents("nodes.txt");
$names = Array();
$ips = explode("\n", $fileContents);
for($i = 0; $i < sizeof($ips); $i++) {
    $output = shell_exec("nslookup ".$ips[$i]);
    $arr = explode("name = ", $output);
    $output = $arr[1];
    $arr = explode("\n", $output);
    $output = $arr[0];
    $output = substr($output, 0, $output.length - 1);
    array_push($names, $output."\n");
}

file_put_contents("nodeNames.txt",$names);
?>