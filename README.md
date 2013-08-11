arduino-server
==============

For server launch:
java -jar server.jar 8080


For client launch
java -jar client.jar localhost 8080

You can run as many clients as you want.

First command for client should be "register pupkin@mail.ua pupkin_pass".
Next command is "login pupkin@mail.ua pupkin_pass".

Where pupkin@mail.ua your email and pupkin_pass your pass. Now you are ready to send messages.

Clients with same username token will be grouped within one room/group. And can send messages to each other.

