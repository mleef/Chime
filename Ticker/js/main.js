function channelChangeCB(channelInfo, type) {
	chime = {
			chimeMessage : {
				channel : {id : channelInfo.channelName}, 
				television : {id : "2"},
				message : "Test message.",
				timeSent : "1000"
			}
		};
	socket.send(JSON.stringify(chime));
}

window.onload = function () {
    // TODO:: Do your initialization job

	console.log("Reached js");
	var registration = {
		registrationMessage: {
			previousChannel: null,
			newChannel: {id: "0"},
			television: {id: "2"}
		}
	};

	var chime = {
		chimeMessage : {
			channel : {id : "0"}, 
			television : {id : "2"},
			message : "Test message.",
			timeSent : "1000"
		}
	};
	
	if (navigator.onLine) {
		console.log("TV is online");
	} else {
		console.log("TV not online");
	}
	
	var socket = new WebSocket('ws://ec2-54-152-59-214.compute-1.amazonaws.com:4445');
	socket.onopen = function () {
		console.log("connection opened.");
		socket.send(JSON.stringify(registration));
		socket.send(JSON.stringify(chime));
	}
	
	socket.onmessage = function (message) {
		console.log("response received");
		console.log(message);
	}
	
	var channel = tizen.tvchannel.getCurrentChannel();
	console.log(channel);
	//var channelListenerID = tizen.tvchannel.addChannelChangeListener(channelChangeCB);

};
