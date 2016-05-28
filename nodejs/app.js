var express = require('express');
var app = express(),
		http = require('http'),
		server = http.createServer(app),
		io = require('socket.io').listen(server);
var moment = require('moment');
var crypto = require('crypto');

/* Start server */
var instance = server.listen(80, function(){
	var host = instance.address().address;
	var port = instance.address().port;
	console.log('App listening at http://%s:%s', host, port);
});

/* Socket io */
var gameUsers = [];

function userExists(name){
	for(var i=0; i < gameUsers.length; i++){
		if(gameUsers[i].userName == name){
			return true;
		}
	}
	return false;
}

function updateCoordinates(userId, x, y){
	for(var i=0; i < gameUsers.length; i++){
		if(gameUsers[i].userId == userId){
			//console.log('viene 1', coordinates.x);
			//console.log('viene 2', coordinates["x"]);
			//gameUsers[i].currentPosition.x = coordinates['x'];
			//gameUsers[i].currentPosition.y = coordinates['y'];
			gameUsers[i].currentPosition = {x: x, y: y};
			console.log('coordinates updated');
			break;
		}
	}
}

io.on('connection', function(socket){

	console.log('New user connected, total: '+gameUsers.length);

	// Welcome message
	io.emit('welcome', { message: 'be received by everyone'});

	// Register new user
	socket.on('new user', function(user){
		if(!userExists(user.userName)){
			var timeNow = new Date().getTime();
 			var userId = crypto.createHash('md5').update(""+timeNow).digest("hex");

			var positionX = Math.floor(Math.random() * (8 - 0 + 1)) + 1;
			var positionY = Math.floor(Math.random() * (8 - 0 + 1)) + 1;

			user.userId = userId;
			user.currentPosition = {x: positionX, y: positionY};
			gameUsers.push(user);

			// Update users
			socket.broadcast.emit('new user', user);

			// Login user
			console.log('Sending login user');
			socket.userId = user.userId;
			console.log(gameUsers);
			socket.emit('login user', userId, gameUsers);
		}else{
			console.log('Name already used');
		}
	});

	socket.on('message', function(data){
		io.emit('message', data.userId, data.message);
	});

	socket.on('move', function(move) {
		console.log('Moving user');
		updateCoordinates(move.userId, move.x, move.y);
		io.emit('move', move.userId, move.x, move.y);
	});

	// Disconnect socket
	socket.on('leave', function(user){
		if(!socket.userId) return;
		gameUsers.splice(socket.userId);
		console.log('User disconnected');
		io.emit('leave', socket.userId);
	});

	// Send message
	socket.on('message', function(user_id, token, msg){

  });
});
