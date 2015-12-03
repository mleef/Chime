import socket
import json
import time
import resource

#resource.setrlimit(resource.RLIMIT_NOFILE, (1000,-1))
registrationMessage = '{{ "registrationMessage" : {0} }}'
chimeMessage = '{{ "chimeMessage" : {0}}}'
chime = '{{"channel" : {0}, "television" : {1}, "message" : "{2}", "timeSent" : "{3}"}}'
registration = '{{"previousChannel" : {0}, "newChannel" : {1}, "television" : {2}}}'
television = '{{"id" : "{0}"}}'
channel = '{{"id" : "{0}"}}'

NUM_SOCKETS = 1

sockets = []

# Initialize new sockets
for i in range(0, NUM_SOCKETS):
	sock = socket.socket()
	sock.connect(('ec2-54-152-59-214.compute-1.amazonaws.com', 4444))
	sockets.append(sock)

# Send registration messages
for i in range(0, NUM_SOCKETS):
	sock = sockets[i]
	newTv = television.format(str(i))
	regMessage = registrationMessage.format(registration.format('null', channel.format(str(i)), newTv))
	#print(regMessage)
	sockets[i].send(regMessage)

time.sleep(100)

# Send chimes
for i in range(0, NUM_SOCKETS):
	sock = sockets[i]
	newTv = television.format(str(i))
	chMessage = chimeMessage.format(chime.format(channel.format(str(i)), television.format(str(i)), str(i), '1000'))
	#print(chMessage)
	sockets[i].send(chMessage)

time.sleep(30)
