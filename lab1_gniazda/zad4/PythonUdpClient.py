import socket

serverIP = "127.0.0.1"
serverPort = 9008
mySocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
msg = "msg from python client"

print('PYTHON UDP CLIENT')

mySocket.sendto(bytes(msg, "utf-8"), (serverIP, serverPort))

buff, address = mySocket.recvfrom(1024)
print('<CLIENT> RECEIVED: ' + str(buff))




