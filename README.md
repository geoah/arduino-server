# Arduino-server


## Clients protocol

For simplicity Length Field approach used. Base idea : every message consists of 4 parts. 
Message length (2 bytes int), message id (2 bytes int), command number (1 byte int) and message itself. For instance, the value of the length field in this example is 15 (0x000F) which represents the length of "HELLO, WORLD" (12 bytes), messageID field length (2 bytes) and command field length (1 byte).

	         BEFORE DECODE (17 bytes)                       AFTER DECODE (12 bytes)
	+--------+-----------+---------+----------------+
	| 2bytes |  2 bytes  |  1 byte |                |
	+--------+-----------+---------+----------------+         +----------------+
	| Length | MessageID | Command + Actual Content |----->   | Actual Content |
	| 0x000F |   0x0001  |   0x01  + "HELLO, WORLD" |         | "HELLO, WORLD" |
	+--------+-----------+---------+----------------+         +----------------+

So message is always "2 bytes + 2 bytes + 1 byte + messageBody.length"; Max message length is (2^15)-4 bytes.

## Client COMMANDS codes
So every message is "Length field + MessageId field + Command field + Content field".

        1 - register; Must have 2 space-separated params as content string : "a@a.ua a"
        2 - login;    Must have 2 space-separated params as content string : "a@a.ua a"
        3 - save profile; Must have 1 param as content string : "{...}"
        4 - load profile; Don't have any params

        10 - digital write; Must have 2 space-separated params as content string : "13 0" (Arduino digitalWrite(13, LOW)))
        11 - digital read; Must have 1 param as content string : "13"

        20 - analog write; Must have 2 space-separated params as content string : "9 0" (Arduino analogWrite(9, 0)))
        21 - analog read; Must have 1 param as content string : "9"

## Response Codes
Every command will return json object. It will be either requested info (like loadProfile) either response code message in case of error or in case of command that doesn't return anything (like saveProfile):
Response object -
	{"code":1}

    1 - message was successfully processed/passed to arduino board
    
    2 - command is bad formed, check syntax and passed params
    3 - user not registered
    4 - user with such name already registered
    5 - user havn't made login command
    6 - user not allowed to perfrom this operation (most probably not logged or socket was closed)
    7 - arduino board not in network
    8 - command not supported

## User Profile JSON structure
	{ "dashBoards" : 
		[ 
			{
			 "id":1, "name":"My Dashboard", "isActive":true, 
			 "widgets"  : [...], 
			 "settings" : {"boardType":"UNO", ..., "someParam":"someValue"}
			}
		]
	}

## Widgets JSON structure

	Button				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"BUTTON",         "pin":"D13", "value":"1"   } -- sends HIGH on digital pin 13. Possible values 1|0.
	Toggle Button ON	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pin":"D18", "value":"1", "state":"ON"} -- sends 1 on digital pin 18
	Toggle Button OFF	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pin":"D18", "value":"0", "state":"OFF"} -- sends 0 on digital pin 18
	Slider				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"SLIDER",         "pin":"A18", "value":"244" } -- sends 244 on analog pin 18. Possible values -9999 to 9999
	Timer				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TIMER",          "pin":"D13", "value":"1", "startTime" : 1111111111, "stopTime" : 111111111} -- startTime is Unix Time.

	//pin reading widgets
	LED					: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"LED",            "pin":"D10"} - sends READ pin to server
	Digit Display		: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"DIGIT4_DISPLAY", "pin":"D10"} - sends READ pin to server
	Graph				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"GRAPH",          "pin":"D10"} - sends READ pin to server

## START

For server launch:
java -jar server.jar 8080

For client launch
java -jar client.jar localhost 8080

You can run as many clients as you want.

First command for client should be "register pupkin@mail.ua pupkin_pass".
Next command is "login pupkin@mail.ua pupkin_pass".

Where pupkin@mail.ua your email and pupkin_pass your pass. Now you are ready to send messages.

When registering email is send to provided login name with code snippet required for start.

Clients with same username token will be grouped within one room/group. And can send messages to each other.

Registered users are stored locally in TMP dir of your system in file "user.db". So after restart you don't have to register again.

