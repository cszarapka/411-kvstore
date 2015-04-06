<?php

if(sizeof($argv) == 1) {
    shell_exec("scp kvStore.jar testingNodes.txt ubc_eece411_5@plab3.eece.ksu.edu:; scp kvStore.jar testingNodes.txt ubc_eece411_5@pl2.cs.yale.edu:; scp kvStore.jar testingNodes.txt ubc_eece411_5@pl1.pku.edu.cn:; scp kvStore.jar testingNodes.txt ubc_eece411_5@pl2.6test.edu.cn:; scp kvStore.jar testingNodes.txt ubc_eece411_5@planetlab2.cs.ubc.ca:; scp testingClient.jar ubc_eece411_5@planet-lab2.cs.ucr.edu:");
}

shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;0;0t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@planet-lab2.cs.ucr.edu -t 'killall -9 java; sleep 10; java -jar testingClient.jar; bash'\"");
shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;0;280t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@plab3.eece.ksu.edu -t 'killall -9 java; java -jar kvStore.jar; bash'\"");
shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;0;565t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@pl2.cs.yale.edu -t 'killall -9 java; java -jar kvStore.jar; bash'\"");
shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;685;0t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@pl1.pku.edu.cn -t 'killall -9 java; java -jar kvStore.jar; bash'\"");
//shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;685;280t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@pl2.6test.edu.cn -t 'killall -9 java; java -jar kvStore.jar; bash'\"");
shell_exec("sh term.sh \"printf '\\e[8;18;95t';printf '\\e[3;685;565t';ssh -i ~/.ssh/id_rsa ubc_eece411_5@planetlab2.cs.ubc.ca -t 'killall -9 java; java -jar kvStore.jar; bash'\"");

?>