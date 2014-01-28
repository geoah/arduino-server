# Arduino-server
Java 7 required.

# Clients protocol

Every message consists of 4 parts: 
Message length (2 bytes int), message id (2 bytes int), command number (1 byte int) and message itself. For instance, the value of the length field in this example is 15 (0x000F) which represents the length of "HELLO, WORLD" (12 bytes), messageID field length (2 bytes) and command field length (1 byte).

	             BEFORE DECODE (17 bytes)               
	+--------+-----------+---------+----------------+
	| 2bytes |  2 bytes  |  1 byte |    12 bytes    |
	+--------+-----------+---------+----------------+       
	| Length | MessageID | Command | Actual Content |
	| 0x000F |   0x0001  |   0x01  | "HELLO, WORLD" |         
	+--------+-----------+---------+----------------+
	         |                15 bytes              |
	         +--------------------------------------+

So message is always "2 bytes + 2 bytes + 1 byte + messageBody.length"; Max message length is (2^15)-4 bytes.

### Length field
Is 2 bytes field for defining message length.
Formula for length field is next : 2 bytes + 1 bytes + messageBody.length

### Message Id field
Is 2 bytes field for defining unique message identifier. Used in order to distinguish on mobile client how to manage responses from arduino. Message ID field should be generated by client side. 
Any read command (analogRead, digitalRead) should always have same messageId for same widget. Let's say, you have widget graph1 and widget graph2 that configured to read different analog pins. Commands will look like :

        length = 4, messageID = 1, command = 21, body = 1; for graph 1
        length = 4, messageID = 2, command = 21, body = 2; for graph 2
        
After you reconfigured graph1 to read another pin, load command will still look the same. So messageID will be an ID of widget to draw results on

IMPORTANT : ZERO VALUE FOR MESSAGE ID IS RESERVED AND SHOULDN'T BE USED BY CLIENTS.

### Command field
This is 1 byte field responsible for storing code of requested from [client command](https://github.com/doom369/arduino-server/blob/master/common/src/main/java/com/ddumanskiy/arduino/common/Command.java), like login, digitalWrite, etc...

#### Mobile client command codes

        1 - register; Must have 2 space-separated params as content field (username and pass) : "a@a.ua a"
        2 - login:
            a) For mobile client must have 2 space-separated params as content field (username and pass) : "a@a.ua a"
            b) For arduino client must have 1 param, user token : "6a7a3151cb044cd893a92033dd65f655"
        3 - save profile; Must have 1 param as content string : "{...}"
        4 - load profile; Don't have any params
        5 - getToken; Must have 1 int param, dash board id : "1"
        
#### Arduino client command codes

        10 - digital write; Must have 2 space-separated params as content string : "13 0" (Arduino digitalWrite(13, LOW))
        11 - digital read; Must have 1 param as content string : "13"

        20 - analog write; Must have 2 space-separated params as content string : "9 0" (Arduino analogWrite(9, 0))
        21 - analog read; Must have 1 param as content string : "9"

        30 - virtual pin write; Must have 2 space-separated params as content string : "9 0"
        31 - virtual pin read; Must have 1 param as content string : "9"

        40 - reset a reading pin on arduino (used when load widget removed, for instance graph was reading pin and now not needed).
        Use case for graph1 :
        	length = 4, messageID = 1, command = 21, body = 1
        	length = 3, messageID = 1, command = 40
        41 - reset all state info on arduino (not hardware reset)

## Response Codes
Every command will return json object. It will be either requested info (like loadProfile) either [response code](https://github.com/doom369/arduino-server/blob/master/server/src/main/java/com/ddumanskiy/arduino/response/ResponseCode.java) message in case of error or in case of command that doesn't return anything (like saveProfile):
Response object -
	{"code":200}

    200 - message was successfully processed/passed to arduino board
    
    2 - command is bad formed, check syntax and passed params
    3 - user not registered
    4 - user with such name already registered
    5 - user havn't made login command
    6 - user not allowed to perfrom this operation (most probably not logged or socket was closed)
    7 - arduino board not in network
    8 - command not supported
    9 - token not valid
    500 - server error. something went wrong on server

## User Profile JSON structure
	{ "dashBoards" : 
		[ 
			{
			 "id":1, "name":"My Dashboard", "isActive":true, "timestamp":333333,
			 "widgets"  : [...], 
			 "settings" : {"boardType":"UNO", ..., "someParam":"someValue"}
			}
		]
	}

## Widgets JSON structure

	Button				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"BUTTON",         "pinType":"NONE", "pin":13, "value":"1"   } -- sends HIGH on digital pin 13. Possible values 1|0.
	Toggle Button ON	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pinType":"DIGITAL", "pin":18, "value":"1", "state":"ON"} -- sends 1 on digital pin 18
	Toggle Button OFF	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pinType":"VIRTUAL", "pin":18, "value":"0", "state":"OFF"} -- sends 0 on digital pin 18
	Slider				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"SLIDER",         "pinType":"ANALOG",  "pin":18, "value":"244" } -- sends 244 on analog pin 18. Possible values -9999 to 9999
	Timer				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TIMER",          "pinType":"DIGITAL", "pin":13, "value":"1", "startTime" : 1111111111, "stopTime" : 111111111} -- startTime is Unix Time.

	//pin reading widgets
	LED					: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"LED",            "pinType":"DIGITAL", "pin":10} - sends READ pin to server
	Digit Display		: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"DIGIT4_DISPLAY", "pinType":"DIGITAL", "pin":10} - sends READ pin to server
	Graph				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"GRAPH",          "pinType":"DIGITAL", "pin":10, "readingFrequency":1000} - sends READ pin to server. Frequency in microseconds

## GETTING STARTED

1. Run the server
	java -jar server.jar 8080

2. Run the client (simulates smartphone client)		
	java -jar client.jar localhost 8080

3. In this client: register new user and login with the same credentials
	register yourEmail yourPassword
	login yourEmail yourPassword

4. Get the token for Arduino
	gettoken 1
	You will get smth. like this:
	00:05:18.086 INFO  - Sending : Message{messageId=30825, command=5, body='1'}
	00:05:18.100 INFO  - Getting : Message{messageId=30825, command=5,body='33bcbe756b994a6768494d55d1543c74'}

5. Start another client (simulates Arduino) and use received token to login
 	java -jar client.jar localhost 8080
	login 33bcbe756b994a6768494d55d1543c74

You can run as many clients as you want.

6. Clients with same credentials and token will be grouped within one room/group. And can send messages to each other.
All client commands are human-flriendly, so you don't have to remember codes. Examples:
"digitalWrite 1 1"
"digitalRead 1"
"analogWrite 1 1"
"analogRead 1"
"virtualWrite 1 1"
"virtualRead 1"


Registered users are stored locally in TMP dir of your system in file "user.db". So after restart you don't have to register again.
