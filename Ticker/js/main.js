var channel;
var socket;
var ip;
var incomingMessages = [];



function channelChangeCB(channelInfo, type) {
	setTimeout(function() {
		channel = tizen.tv.channel.getCurrentChannel().channelName;
		// Make sure the channel hasn't changed in the last 2 seconds.
		if (channel != channelInfo.channelName) {
			return;
		}
		chime = {
				registrationMessage : {
					previousChannel : {id : channel}, 
					newChannel : {id: channelInfo.channelName},
					television : {id : ip},
				}
			};
		socket.send(JSON.stringify(chime));
		channel = channelInfo.channelName;
	}, 2000);
}

window.onload = function () {
    // TODO:: Do your initialization job

	ip = webapis.network.getIp();
	

	$('#mainBG').show();
	
	var registration = {
		registrationMessage: {
			previousChannel: null,
			newChannel: {id: tizen.tv.channel.getCurrentChannel().channelName},
			television: {id: ip}
		}
	};

	console.log(tizen.time.getCurrentDateTime().toString());
	var chime = {
		chimeMessage : {
			channel : {id : tizen.tv.channel.getCurrentChannel().channelName}, 
			television : {id : ip},
			message : "Test message.",
			timeSent : tizen.time.getCurrentDateTime().toString()
		}
	};
		
	socket = new WebSocket('ws://ec2-54-152-59-214.compute-1.amazonaws.com:4445');
	socket.onopen = function () {
		console.log("connection opened.");
		//socket.send(JSON.stringify(registration));
		//socket.send(JSON.stringify(chime));
		channel = tizen.tv.channel.getCurrentChannel().channelName;
	}
	
	socket.onmessage = function (message) {
		console.log("response received");
		messageObj = JSON.parse(message.data);
		incomingMessages.push(messageObj);
	}
	
	var channelListenerID = tizen.tv.channel.addChannelChangeListener(channelChangeCB);
	
	setInterval(function() {
		if(incomingMessages.length > 0) {
			var messageObj = incomingMessages.shift();
			if (messageObj.channel.id === channel) {
				// Handle old messages based on their time
				console.log(messageObj.message);
				document.getElementById("mainBG").innerHTML = messageObj.message;
			}
		}
	}, 1000);
};
