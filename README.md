# Arduino-server


## Clients protocol

For simplicity Length Field approach used. Base idea : every message consists of 2 parts. 
Message length (2 bytes int) and message itself. For instance, the value of the length field in 
this example is 12 (0x000C) which represents the length of "HELLO, WORLD".

	 BEFORE DECODE (14 bytes)		 AFTER DECODE (12 bytes)
	+--------+----------------+		 +----------------+
	| Length | Actual Content |----->| Actual Content |
	| 0x000C | "HELLO, WORLD" |		 | "HELLO, WORLD" |
	+--------+----------------+		 +----------------+

So message is always "2 bytes + messageBody.length"; Max message length is (2^15)-1.

## COMMANDS
	register (usage "register pupkin@mail.ru pupkin")
	login (usage "login pupkin@mail.ru pupkin")
	saveProfile (usage "saveProfile {...}")
	loadProfile

## User Profile JSON structure
	UserProfile : { "dashBoards" : 
		[ 
			{
			 "id":1, "name":"My Dashboard", "active":true, 
			 "widgets"  : [...], 
			 "settings" : ["boardType":"UNO", ..., "someParam":"someValue"]
			}
		]
	}

## Widgets JSON structure

	Button				: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"BUTTON",         "pin":"D13", "value":"1"   } -- sends HIGH on digital pin 13. Possible values 1|0.
	Toggle Button ON	: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pin":"D18", "value":"1", "state":"ON"} -- sends 1 on digital pin 18
	Toggle Button OFF	: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pin":"D18", "value":"0", "state":"OFF"} -- sends 0 on digital pin 18
	Slider				: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"SLIDER",         "pin":"A18", "value":"244" } -- sends 244 on analog pin 18. Possible values -9999 to 9999
	Timer				: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"TIMER",          "pin":"D13", "value":"1", "startTime" : 1111111111, "stopTime" : 111111111} -- startTime is Unix Time.

	//pin reading widgets
	LED					: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"LED",            "pin":"D10"} - sends READ pin to server
	Digit Display		: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"DIGIT4_DISPLAY", "pin":"D10"} - sends READ pin to server
	Graph				: {"id":1, "x":1, "y":1, dashBoardId:1, "label":"Some Text", "type":"GRAPH",          "pin":"D10"} - sends READ pin to server

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

