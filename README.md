arduino-server
==============

For server launch:
java -jar server-jar-with-dependencies.jar 8080


For client launch
java -jar client-jar-with-dependencies.jar localhost 8080 auth_abc

You can run as many clients as you want. Last param 'auth_abc' is AUTH token.
Clients with same AUTH token will be grouped within one room/group. And can send messages to each other.

Every AUTH token should start from 'auth_'.

