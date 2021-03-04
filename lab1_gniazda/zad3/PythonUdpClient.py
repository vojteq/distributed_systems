import socket

serverIP = "127.0.0.1"
serverPort = 9008
mySocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
msg_bytes = (300).to_bytes(4, byteorder='little')

print('PYTHON UDP CLIENT')
mySocket.sendto(msg_bytes, (serverIP, serverPort))

buff, address = mySocket.recvfrom(4)
print('<CLIENT> RECEIVED: ' + str(int.from_bytes(buff, byteorder='little')))




