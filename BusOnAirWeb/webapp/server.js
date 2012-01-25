require(__dirname + "/lib/setup").ext(__dirname + "/lib").ext(
		__dirname + "/lib/express/support");
var connect = require('connect'), express = require('express'), 
sys = require('sys'), httpclient = require('wwwdude'), port = (8081);
var pub = __dirname + '/static';

var server = express.createServer();
server.configure(function() {
	server.set('views', __dirname + '/views');
	server.use(express.static(pub));
	server.use(express.errorHandler({
		dump : true,
		stack : true
	}));
	server.use(server.router);
	server.use(express.logger({
		format : ':method :url :status'
	}));
	server.set('view engine', 'jade');

});

server.error(function(err, req, res, next) {
	if (err instanceof NotFound) {
		res.render('404', {
			locals : {
				title : '404 - Not Found'
			},
			status : 404
		});
	} else {
		res.render('500', {
			locals : {
				title : 'The Server Encountered an Error',
				error : err
			},
			status : 500
		});
	}
});
server.listen(port);

server.get('/', function(req, res) {
	res.render('home');
});

var neodb = 'http://localhost:7474/plugin';
var neo = httpclient.createClient();

server.get('/routesearch', function(req, res) {
	neo.get( neodb + req.url ).addListener('complete', function(data, resp) {
		res.send(data);
	}).addListener('error', function(err) {
		res.send("Server Error");
	});
});

server.get('/directions', function(req, res) {
	neo.get( neodb + req.url ).addListener('complete', function(data, resp) {
		res.send(data);
	}).addListener('error', function(err) {
		res.send("Server Error");
	});
});

server.get('/500', function(req, res) {
	throw new Error('This is a 500 Error');
});

server.get('/*', function(req, res) {
	throw new NotFound;
});

function NotFound(msg) {
	this.name = 'NotFound';
	Error.call(this, msg);
	Error.captureStackTrace(this, arguments.callee);
}

console.log('Listening on http://0.0.0.0:' + port);
