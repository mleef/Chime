var socket;
var ENDPOINT = 'http://ec2-54-152-59-214.compute-1.amazonaws.com:4567';
var GET_CHANNELS = ENDPOINT + '/channel/list';
var curChannel = null;
var prevChannel = null;
window.onload = function () {
    // TODO:: Do your initialization job

    var registration = {
        registrationMessage: {
            previousChannel: null,
            newChannel: {id: "1"},
            television: {id: "Web TV"}
        }
    };
    var chime1 = {
        chimeMessage : {
            channel : {id: "1"},
            television : {id : "Web TV"},
            message : "I tried so hard.",
            timeSent : "1000"
        }
    };

    var chime2 = {
        chimeMessage : {
            channel : {id: "1"},
            television : {id : "Web TV"},
            message : "And got so far",
            timeSent : "1001"
        }
    };


    socket = new WebSocket('ws://ec2-54-152-59-214.compute-1.amazonaws.com:4445');
    socket.onopen = function () {
        console.log("connection opened.");
        socket.send(JSON.stringify(registration));
        //socket.send(JSON.stringify(chime1));
        //socket.send(JSON.stringify(chime2));
    }

    socket.onmessage = function (message) {
        console.log("response received");
        messageObj = JSON.parse(message.data);
        console.log(messageObj);
        $("marquee").append(messageObj.message);
    }
    $.get( GET_CHANNELS, function( data ) {
        var list = JSON.parse(data);
        for(var i = 0; i < list.length; i++) {
            $('#channelList')
                .append($("<option></option>")
                    .text(list[i].id));
        }
    });

    setInterval(function() {
        $.get( GET_CHANNELS, function( data ) {
            var list = JSON.parse(data);
            for(var i = 0; i < list.length; i++) {
                $('#channelList')
                    .append($("<option></option>")
                        .text(list[i].id));
            }
        });
    }, 10000);

    $('#channelList').change(function() {
        curChannel = $('select[id=channelList]').val();
        var registration = {
            registrationMessage: {
                previousChannel: {id: prevChannel},
                newChannel: {id: curChannel},
                television: {id: "Web TV"}
            }
        };
        prevChannel = curChannel;
        socket.send(JSON.stringify(registration));
    })
};