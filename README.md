---
title: Chime
description: Real Time Channel Based Messaging for Smart TVs
author: Marc Leef
created:  2015 November 15
modified: 2016 January 10
---

# Chime
Chime: Real Time Channel Based Messaging for Smart TVs

## Overview
Chime is a high-performance chat server designed specifically to be used in conjunction with Smart TV applications. Chime can be deployed as either a monolithic instance or as a group of worker instances reporting to a master as pictured below.

### Design
![alt text](https://raw.githubusercontent.com/mleef/Chime/master/backend/resources/images/prototype.png "Monolith" width="200" height="300")

![alt text](https://raw.githubusercontent.com/mleef/Chime/master/backend/resources/images/final.png "Worker/Master")

## Backend Usage

### Default Ports
1. **Socket Connections (Monolith/Worker):**
	4444
2. **Web Socket Connections (Monolith/Worker):**
	4445
3. **HTTP Requests (Monolith/Worker):**
	4567
4. **HTTP Requests (Master)**
	4500

### Monolithic Chime Instances

Constrained by the maximum number of TCP connections the host is capable of maintaining, but still fully functional.

To instantiate a monolithic Chime instance do:

```
java -jar chime-monolith.jar
```

### Chime Workers and Master

Uses HTTP messages to coordinate a set of load balanced workers that can be scaled with the number of users.

To instantiate a Chime Master instance do:

```
java -jar chime-master.jar [Optional port #]
```

To instantiate a Chime Worker instance do:

```
java -jar chime-worker.jar [Chime Master URL]
```

## Frontend Usage

Chime instances can be communicated with using JSON messages written to socket/web socket streams or sent via HTTP requests. Clients open and maintain
a singular socket connection which is then used to update the backend of channel changes and chime sending events. 


### Chime Monolith Communication

To connect to a monolithic Chime instance from a browser do:

```
socket = new WebSocket("ws://[Chime instance URL]"")
```

Then send a registration message like so:

```
// Channel change event
var registration = {
    registrationMessage : {
        previousChannel : null,
        newChannel : {id : "1"},
        television : {id : "marcs-tv"}
    }
};
// Send to server
socket.send(JSON.stringify(registration));
```

Chimes can be sent similarly:
```
var chime = {
    chimeMessage : {
        channel : {id : "1"},
        television : {id : "marcs-tv"},
        message : "This is amazing!.",
        timeSent : "1000"
    }
};
socket.send(JSON.stringify(chime));
```

### Chime Worker/Master Communication

To connect to a Chime Worker instance first send a POST request to the master to be assigned a worker:
```
[Chime Master URL]/worker/assignment
```

The response should be a success object that contains a worker URL to connect to (open socket with as above):
```
{
    success : "[Worker URL]"
}
```

