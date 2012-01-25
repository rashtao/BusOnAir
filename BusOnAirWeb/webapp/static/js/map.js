/**
 * Google Map Variables
 */
var bounds;
var geocoder;
var infoWindow;
var Demo = {

	map : null,
	markers : [],
	directionMarkers : [],
	walkmarkers : [],
	routemarkers : [],
	points : [],
	polylines : [],
	lat1 : null,
	lng1 : null,
	lat2 : null,
	lng2 : null,
	counter : 0
};


Demo.clearOverlay = function() {
	
	if (Demo.directionMarkers) {
		for (i in Demo.directionMarkers) {
			Demo.directionMarkers[i].setMap(null);
		}
	}

	if (Demo.markers) {
		for (i in Demo.markers) {
			Demo.markers[i].setMap(null);
		}
	}

	if (Demo.polylines) {
		for (i in Demo.polylines) {
			Demo.polylines[i].setMap(null);
		}
	}
}


function createStartMarker(lat, lng) {

	Demo.lat1 = lat;
	Demo.lng1 = lng;
	var markerLatLon = new  google.maps.LatLng(lat, lng);
	var marker = new google.maps.Marker({
		position : markerLatLon,
		map : Demo.map,
		icon : 'images/start.png',
		draggable : true
	});

	if (Demo.directionMarkers[0] != null) {
		Demo.directionMarkers[0].setMap(null);
	}

	Demo.directionMarkers[0] = marker;

	google.maps.event.addListener(marker, 'dragend', function() {
		Demo.lat1 = marker.getPosition().lat();
		Demo.lng1 = marker.getPosition().lng();
		if (Demo.directionMarkers[1] != null) {
			getDirections();
		}
	});
	return marker;
}

function createEndMarker(lat, lng) {

	Demo.lat2 = lat;
	Demo.lng2 = lng;
	var markerLatLon = new  google.maps.LatLng(lat, lng);
	var marker = new google.maps.Marker({
		position : markerLatLon,
		map : Demo.map,
		draggable : true
	});

	if (Demo.directionMarkers[1] != null) {
		Demo.directionMarkers[1].setMap(null);
	}
	Demo.directionMarkers[1] = marker;
	google.maps.event.addListener(marker, 'dragend', function() {
		Demo.lat2 = marker.getPosition().lat();
		Demo.lng2 = marker.getPosition().lng();
		if (Demo.directionMarkers[0] != null) {
			getDirections();
		}
	});
	return marker;
}


function getDirections() {
	var time =dateToInt();

	$.getJSON(document.URL + "directions", {
		lat1 : Demo.lat1,
		lon1 : Demo.lng1,
		lat2 : Demo.lat2,
		lon2 : Demo.lng2,
		time : time
	}, function(data) {
		$('#directionsPanel').empty();
		if (data == null) {
			$("#statusPanel").html("<p>No Path Found</p>")
			$("#statusPanel").addClass("error");
			console.log("No Path Found");
		} else {
			showDirections(data);
		}

	});
}


function showDirections(json) {
	
	Demo.clearOverlay();
	var directions =$('#directionsPanel');
	directions.hide();
    directions.append("<h3>Directions</h3>");
	$("#statusPanel").empty();
	bounds = new google.maps.LatLngBounds();
	processWalks(json);
	processRoutes(json);
	directions.slideDown();

}

function displayPoint(marker, html){
	if (infowindow) infowindow.close();
	infowindow = new google.maps.InfoWindow({
		content : html
	});
	Demo.map.panTo(marker.getPosition());
	infowindow.open(Demo.map, marker);
}

function listenMarker (marker, infoWindow)
{
    // so marker is associated with the closure created for the listenMarker function call
	google.maps.event.addListener(marker, "click", function(event) {
	infoWindow.open(Demo.map, this);
	Demo.map.panTo(marker.getPosition());
	});
}
function processWalks(json) {

	var walks = json.directionslist[0].walks;
	var walktemplate = '<span class="directionsDisplayIcon directionsWalkIcon"> \
	<small> Walk to	<%= deptStation %></small></span><p> <%= distance %> mins</p>';//
	var infoWindowTemplate = ('<p>Walk <%= distance %> mins</p>');

	for (i = 0; i < walks.length; i++) {

		var pathCoordinates = new google.maps.MVCArray();
		var latLon1 = new google.maps.LatLng(
		walks[i].latLon[0].lat, walks[i].latLon[0].lon);
		pathCoordinates.insertAt(0, latLon1);
		bounds.extend(latLon1);
		var latLon2 = new google.maps.LatLng(walks[i].latLon[1].lat,
		walks[i].latLon[1].lon);
		pathCoordinates.insertAt(1, latLon2);
		bounds.extend(latLon2);
		var poly = new google.maps.Polyline({
			path : pathCoordinates,
			strokeColor : "#cb005b",
			strokeOpacity : 0.75,
			strokeWeight : 4
		});
		Demo.polylines.push(poly);
		poly.setMap(Demo.map);

		var shadow = new google.maps.MarkerImage('images/xferdisk.png',
		new google.maps.Size(9, 9),
		new google.maps.Point(0,0),
		new google.maps.Point(4, 4));

		var image = new google.maps.MarkerImage('images/walk_popup.png',
		new google.maps.Size(30, 28),
		new google.maps.Point(0,0),
		new google.maps.Point(30, 28));

		var marker;

		if(i==0) {
			marker = createStartMarker(walks[i].latLon[0].lat,walks[i].latLon[0].lon );
		} else {
			var markerLatLon = new google.maps.LatLng(walks[i].latLon[0].lat,walks[i].latLon[0].lon);
			marker = new google.maps.Marker({
				map : Demo.map,
				position : markerLatLon,
				shadow: shadow,
				icon: image,
				title : 'Walk'
			});
		}
		if(i==walks.length-1) {
			createEndMarker(walks[i].latLon[1].lat,walks[i].latLon[1].lon);
		}
		Demo.markers.push(marker);
		var deptStation = (i==walks.length-1) ?  "to Destination" : json.directionslist[0].routes[i].deptStation;
		var html = _.template(walktemplate,  { distance : walks[i].distance, deptStation :deptStation });
		var stage = $("<div id='stage"+ i +"'/>").appendTo($('#directionsPanel'))
		$('<div id="walk'+i+'" class="stage walk">').html(html).appendTo(stage);
		bindInfoWindow(marker,Demo.map, html, "walk"+ i );

	}
}
	
function bindInfoWindow(marker, map,  html, markerId) {
	infoWindow = new google.maps.InfoWindow({
	});

	google.maps.event.addListener(marker, 'click', function() {
		if (infoWindow)
			infoWindow.close();
		infoWindow.setContent(html);
		infoWindow.open(map, marker);
	});
	//bind onlcick events to the div or other object in html
	markerObj =  document.getElementById(markerId);
	//you can create DOM event listeners for the map
	google.maps.event.addDomListener(markerObj, 'click', function() {
		if (infoWindow)
			infoWindow.close();
		infoWindow.setContent(html);
		infoWindow.open(map, marker);
		map.panTo(marker.getPosition());
	});
}


function processRoutes(json) {
	
	var routes = json.directionslist[0].routes;
	
	var routetemplate = '<span class="directionsDisplayIcon directionsBusIcon"> \
	<small> Take the <%= routeId %>  Bus (<%= numOfStops %> Stops)</small> \
	</span> <p><%= deptTime %> - <%= arrTime %></p> \
	<p><i>Get off at: </i><%= arrStation %></p>';
	
	var infoWindowTemplate = ('<div><strong>Take the <%= routeId %> \
	Bus </strong></br> <p><em>Departing:</em>\
	<%= deptTime %>  <em>  Arriving: </em>\
	<%= arrTime %> </p></div>');

	
	for (i = 0; i < routes.length; i++) {
		
		var pathCoordinates = new google.maps.MVCArray();
		for (x = 0; x < routes[i].latLon.length; x++) {
			var latLon = new google.maps.LatLng(
			routes[i].latLon[x].lat,
			routes[i].latLon[x].lon);
			bounds.extend(latLon);
			pathCoordinates.insertAt(x, latLon);
		}
		var poly = new google.maps.Polyline({
			path : pathCoordinates,
			strokeColor : "#0064cb",
			strokeOpacity : 0.5,
			strokeWeight : 6
		});
		
		Demo.polylines.push(poly);
		poly.setMap(Demo.map);
		Demo.map.fitBounds(bounds);
		var image = new google.maps.MarkerImage('images/bus_popup.png',
		new google.maps.Size(30, 28),
		new google.maps.Point(0,0),
		new google.maps.Point(0, 28));

		var shadow = new google.maps.MarkerImage('images/xferdisk.png',
		new google.maps.Size(9, 9),
		new google.maps.Point(0,0),
		new google.maps.Point(4, 4));

		var markerLatLon = new  google.maps.LatLng(routes[i].latLon[0].lat,routes[i].latLon[0].lon);
		var marker = new google.maps.Marker({
			map: Demo.map,
			position: markerLatLon,
			shadow: shadow,
			icon: image,
			title: 'Drag to Change'
		});
		Demo.markers.push(marker);
		var html = _.template(routetemplate, routes[i]);
		$("<div id='route"+i+"' class='stage bus'/>").html(html).appendTo($("#stage"+ i));
		bindInfoWindow(marker,Demo.map, html, "route"+ i );


	}


}



/**
 * Display route
 */
function showRoute(json) {

	Demo.clearOverlay();
	//Demo.clearDirectionMarkers();
	bounds = new google.maps.LatLngBounds();
	var pathCoordinates = new google.maps.MVCArray();
	for (x = 0; x < json.routelist.length; x++) {
		var stop = json.routelist[x];

		var latlng = new google.maps.LatLng(stop.latLon.lat, stop.latLon.lon);
		bounds.extend(latlng);

		var busStopIcon = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld='
				+ x + '|E2E4FF|000000';
		var marker = new google.maps.Marker({
			map : Demo.map,
			position : latlng,
			icon : busStopIcon,
			labelContent : "labelContent",
			labelAnchor : new google.maps.Point(22, 0),
			labelClass : "labels", // the CSS class for the label
			labelStyle : {
				opacity : 0.75
			}
		});

		Demo.markers.push(marker);
		Demo.map.fitBounds(bounds);
		pathCoordinates.insertAt(x, latlng);
		var infoWindowHtml = ("Stop: " + stop.stopId + " ,<br> " + stop.stopName);
		var infoWindow = new google.maps.InfoWindow({
			content : infoWindowHtml
		});

		listenMarker(marker, infoWindow);
	}
	var polyOptions = {
		path : pathCoordinates,
		strokeColor : "#00B2EE",
		strokeOpacity : 0.5,
		strokeWeight : 6
	};

	var poly = new google.maps.Polyline(polyOptions);
	poly.setMap(Demo.map);
	Demo.polylines.push(poly);

}


/**
 * Form validation
 */
 
function checkForm() {
	var startCheck = checkStart();
	var endCheck = checkEnd();
	if (startCheck && endCheck) {
		$('#directionsPanel').empty();
		geocodeStart();
	}
}

function checkStart(startaddress) {
	var startaddress = $("#startaddress");
	var check = ((startaddress.val() !== "Start Address...") && (startaddress.val().length != 0));
	if (!check)
		startaddress.addClass("error");
	else
		startaddress.removeClass("error");
	return check;
}

function checkEnd(endaddress) {
	var endaddress = $("#endaddress");
	var check = ((endaddress.val() !== "End Address...") && (endaddress.val().length != 0));
	if (!check)
		endaddress.addClass("error");
	else
		endaddress.removeClass("error");
	return check;
}


function geocodeStart() {
	var startAddress = $("#startaddress").val();
	geocoder.geocode({
		'address' : startAddress
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			Demo.lat1 = results[0].geometry.location.lat();
			Demo.lng1 = results[0].geometry.location.lng();
			geocodeEnd();
		} else {
			$("#statusPanel").html("<p>Start Address Not Found</p>")
			$("#startaddress").addClass("error");
			console.log("Start not found")
		}
	});

}

function geocodeEnd() {
	var endAddress = $("#endaddress").val();
	geocoder.geocode({
		'address' : endAddress
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			
			Demo.lat2 = results[0].geometry.location.lat();
			Demo.lng2 = results[0].geometry.location.lng();
			getDirections();

		} else {
			$("#statusPanel").html("<p>End Address Not Found</p>")
			$("#endaddress").addClass("error");
		}
	});
}

/**
 * Date and Time to Int
 */

function dateToInt() {
	var time = $("#time").val();
	var array = time.split(".");
	var hours = parseFloat(array[0]);
	var minutes = parseFloat(array[1]);
	var dayVal;
	switch ($("#day").val()) {
	case ("0"):
		dayVal = 2880;
		break;
	case ("6"):
		dayVal = 1440;
		break;
	default:
		dayVal = 0;
	}
	if (hours === 12 && time.indexOf('AM') !== -1) {
		hours = 0;
	} else if (hours !== 12 && time.indexOf('PM') !== -1) {
		hours += 12;
	}
	return (dayVal + (hours * 60 + minutes));
}

/**
 * Map Options
 */
var osmMapType = new google.maps.ImageMapType({
	getTileUrl : function(coord, zoom) {
		return "http://tile.openstreetmap.org/" + zoom + "/" + coord.x + "/"
				+ coord.y + ".png";
	},
	tileSize : new google.maps.Size(256, 256),
	isPng : true,
	alt : "OpenStreetMap layer",
	name : "OpenStreetMap",
	maxZoom : 19
});

var myOptions = {
	zoom : 13,
        center : new google.maps.LatLng(42.350663,13.399844),
        mapTypeId : 'OSM',
	mapTypeControl : true,
	mapTypeControlOptions : {
		mapTypeIds : [ 'OSM', google.maps.MapTypeId.ROADMAP,
				google.maps.MapTypeId.SATELLITE, google.maps.MapTypeId.HYBRID,
				google.maps.MapTypeId.TERRAIN ],
		style : google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
		position : google.maps.ControlPosition.BOTTOM_CENTER
	},

	panControl : true,
	panControlOptions : {
		position : google.maps.ControlPosition.RIGHT_CENTER
	},
	zoomControl : true,
	zoomControlOptions : {
		style : google.maps.ZoomControlStyle.LARGE,
		position : google.maps.ControlPosition.RIGHT_CENTER
	},	
    streetViewControl: true,
    streetViewControlOptions: {
        position: google.maps.ControlPosition.LEFT_TOP
    }	

};
