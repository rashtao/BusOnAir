
// remap jQuery to $
(function($){

 


})(window.jQuery);



/**
 * Context menu
 */

var clickedLatLng;
var contextMenu = $(document.createElement('ul')).attr('id', 'contextMenu');

contextMenu.append('<li><a href="#directionsFrom">Directions From Here</a></li>'
				+ '<li><a href="#directionsTo">Directions To Here</a></li>'
				+ '<li><a href="#zoomIn">Zoom in</a></li>'
				+ '<li><a href="#zoomOut">Zoom out</a></li>');


contextMenu.bind('contextmenu', function() {
	return false;
});


// Set some events on the context menu links
contextMenu.find('a').click(function() {

	contextMenu.fadeOut(75);
	var action = $(this).attr('href').substr(1);

	switch (action) {

	case 'directionsFrom':
		createStartMarker(clickedLatLng.lat(),clickedLatLng.lng() );
		if (Demo.directionMarkers[1] != null) 
			getDirections();
		break;

	case 'directionsTo':
		createEndMarker(clickedLatLng.lat(),clickedLatLng.lng() );
		if (Demo.directionMarkers[0] != null) 
			getDirections();
		break;

	case 'zoomIn':
		Demo.map.setZoom(Demo.map.getZoom() + 1);
		Demo.map.panTo(clickedLatLng);
		break;

	case 'zoomOut':
		Demo.map.setZoom(Demo.map.getZoom() - 1);
		Demo.map.panTo(clickedLatLng);
		break;
	}
	return false;
});

contextMenu.find('a').hover(function() {
	$(this).parent().addClass('hover');
}, function() {
	$(this).parent().removeClass('hover');
});

function showContextMenu(e) {

	contextMenu.hide();
	var mapDiv = $(Demo.map.getDiv()), x = e.pixel.x, y = e.pixel.y;
	clickedLatLng = e.latLng;

	// adjust if clicked to close to the edge of the map
	if (x > mapDiv.width() - contextMenu.width())
		x -= contextMenu.width();

	if (y > mapDiv.height() - contextMenu.height())
		y -= contextMenu.height();

	// Set the location and fade in the context menu
	contextMenu.css({
		top : y,
		left : x
	}).fadeIn(100);
};


var aDataSet = [
['M11','M11','M11','Destinazione'],
['12SC','12SC','12SC','Destinazione'],
['15IS','15IS','15IS','Destinazione'],
['5/8S','5/8S','5/8S','Destinazione'],
['16CG','16CG','16CG','Destinazione'],
['M2A','M2A','M2A','Destinazione'],
['M3N','M3N','M3N','Destinazione'],
['6SA','6SA','6SA','Destinazione'],
['16/AE','16/AE','16/AE','Destinazione'],
['92AS','92AS','92AS','Destinazione'],
['16N','16N','16N','Destinazione'],
['M11B','M11B','M11B','Destinazione'],
['8F','8F','8F','Destinazione'],
['59','59','59','Destinazione'],
['12I','12I','12I','Destinazione'],
['15S','15S','15S','Destinazione'],
['104B','104B','104B','Destinazione'],
['M2AB','M2AB','M2AB','Destinazione'],
['99','99','99','Destinazione'],
['10A','10A','10A','Destinazione'],
['11A','11A','11A','Destinazione'],
['16S','16S','16S','Destinazione'],
['M81','M81','M81','Destinazione'],
['M1','M1','M1','Destinazione'],
['99B','99B','99B','Destinazione'],
['10','10','10','Destinazione'],
['6H','6H','6H','Destinazione'],
['67','67','67','Destinazione'],
['10B','10B','10B','Destinazione'],
['7A','7A','7A','Destinazione'],
['M4B','M4B','M4B','Destinazione'],
['15N','15N','15N','Destinazione'],
['6S','6S','6S','Destinazione'],
['M12AE','M12AE','M12AE','Destinazione'],
['M6A','M6A','M6A','Destinazione'],
['78','78','78','Destinazione'],
['80S','80S','80S','Destinazione'],
['6DBS','6DBS','6DBS','Destinazione'],
['6DAS','6DAS','6DAS','Destinazione'],
['12AH','12AH','12AH','Destinazione'],
['8','8','8','Destinazione'],
['14','14','14','Destinazione'],
['82','82','82','Destinazione'],
['51','51','51','Destinazione'],
['77AS','77AS','77AS','Destinazione'],
['8L','8L','8L','Destinazione'],
['12B','12B','12B','Destinazione'],
['4','4','4','Destinazione'],
['6SBS','6SBS','6SBS','Destinazione'],
['16D','16D','16D','Destinazione'],
['M6','M6','M6','Destinazione'],
['6G','6G','6G','Destinazione'],
['108HS','108HS','108HS','Destinazione'],
['M4','M4','M4','Destinazione'],
['24','24','24','Destinazione'],
['13M','13M','13M','Destinazione'],
['35','35','35','Destinazione'],
['13G','13G','13G','Destinazione'],
['12SF','12SF','12SF','Destinazione'],
['6F','6F','6F','Destinazione'],
['5N','5N','5N','Destinazione'],
['16E','16E','16E','Destinazione'],
['M2','M2','M2','Destinazione'],
['15L','15L','15L','Destinazione'],
['M11A','M11A','M11A','Destinazione'],
['102','102','102','Destinazione'],
['M6AA','M6AA','M6AA','Destinazione'],
['M3H','M3H','M3H','Destinazione'],
['8N','8N','8N','Destinazione'],
['105C','105C','105C','Destinazione'],
['6SD','6SD','6SD','Destinazione'],
['4D','4D','4D','Destinazione'],
['12','12','12','Destinazione'],
['5PN','5PN','5PN','Destinazione'],
['15','15','15','Destinazione'],
['92A','92A','92A','Destinazione'],
['M6AB','M6AB','M6AB','Destinazione'],
['77A','77A','77A','Destinazione'],
['M2F','M2F','M2F','Destinazione'],
['6C','6C','6C','Destinazione'],
['105B','105B','105B','Destinazione'],
['15M','15M','15M','Destinazione'],
['12G','12G','12G','Destinazione'],
['105','105','105','Destinazione'],
['M9','M9','M9','Destinazione'],
['15/A','15/A','15/A','Destinazione'],
['16/AB','16/AB','16/AB','Destinazione'],
['8S','8S','8S','Destinazione'],
['32','32','32','Destinazione'],
['','','','Destinazione'],
['M6E','M6E','M6E','Destinazione'],
['M6AC','M6AC','M6AC','Destinazione'],
['15AS','15AS','15AS','Destinazione'],
['108','108','108','Destinazione'],
['16/A','16/A','16/A','Destinazione'],
['106S','106S','106S','Destinazione'],
['15CS','15CS','15CS','Destinazione'],
['33','33','33','Destinazione'],
['31A','31A','31A','Destinazione'],
['6SS','6SS','6SS','Destinazione'],
['13C','13C','13C','Destinazione'],
['104','104','104','Destinazione'],
['15BS','15BS','15BS','Destinazione'],
['6D','6D','6D','Destinazione'],
['104D','104D','104D','Destinazione'],
['M2AG','M2AG','M2AG','Destinazione'],
['83','83','83','Destinazione'],
['M88','M88','M88','Destinazione'],
['99D','99D','99D','Destinazione'],
['M3','M3','M3','Destinazione'],
['15D','15D','15D','Destinazione'],
['11','11','11','Destinazione'],
['5S','5S','5S','Destinazione'],
['30','30','30','Destinazione'],
['12AB','12AB','12AB','Destinazione'],
['4/DA','4/DA','4/DA','Destinazione'],
['16A','16A','16A','Destinazione'],
['9B','9B','9B','Destinazione'],
['106','106','106','Destinazione'],
['87','87','87','Destinazione'],
['40A','40A','40A','Destinazione'],
['6','6','6','Destinazione'],
['12AA','12AA','12AA','Destinazione'],
['80C','80C','80C','Destinazione'],
['77D','77D','77D','Destinazione'],
['16','16','16','Destinazione'],
['6DS','6DS','6DS','Destinazione'],
['6DB','6DB','6DB','Destinazione'],
['M2AC','M2AC','M2AC','Destinazione'],
['15E','15E','15E','Destinazione'],
['9','9','9','Destinazione'],
['80E','80E','80E','Destinazione'],
['6DD','6DD','6DD','Destinazione'],
['44','44','44','Destinazione'],
['5','5','5','Destinazione'],
['9C','9C','9C','Destinazione'],
['50','50','50','Destinazione'],
['M2AE','M2AE','M2AE','Destinazione'],
['5SA','5SA','5SA','Destinazione'],
['34','34','34','Destinazione'],
['31B','31B','31B','Destinazione'],
['10D','10D','10D','Destinazione'],
['7F','7F','7F','Destinazione'],
['6DA','6DA','6DA','Destinazione'],
['6SAS','6SAS','6SAS','Destinazione'],
['10E','10E','10E','Destinazione'],
['74','74','74','Destinazione'],
['99DC','99DC','99DC','Destinazione'],
['45','45','45','Destinazione'],
['11G','11G','11G','Destinazione'],
['108C','108C','108C','Destinazione'],
['16/AN','16/AN','16/AN','Destinazione'],
['13E','13E','13E','Destinazione'],
['M12AF','M12AF','M12AF','Destinazione'],
['80A','80A','80A','Destinazione'],
['16/AH','16/AH','16/AH','Destinazione'],
['M88D','M88D','M88D','Destinazione'],
['13','13','13','Destinazione'],
['5/8DL','5/8DL','5/8DL','Destinazione'],
['M5','M5','M5','Destinazione'],
['16F','16F','16F','Destinazione'],
['68','68','68','Destinazione'],
['12A','12A','12A','Destinazione'],
['16C','16C','16C','Destinazione'],
['16/AM','16/AM','16/AM','Destinazione'],
['M3L','M3L','M3L','Destinazione'],
['80','80','80','Destinazione'],
['13S','13S','13S','Destinazione'],
['11C','11C','11C','Destinazione'],
['15GS','15GS','15GS','Destinazione'],
['99S','99S','99S','Destinazione'],
['M82','M82','M82','Destinazione'],
['106C','106C','106C','Destinazione'],
['104C','104C','104C','Destinazione'],
['M1B','M1B','M1B','Destinazione'],
['7D','7D','7D','Destinazione'],
['M12AC','M12AC','M12AC','Destinazione'],
['101','101','101','Destinazione'],
['33D','33D','33D','Destinazione'],
['11D','11D','11D','Destinazione'],
['101F','101F','101F','Destinazione'],
['15O','15O','15O','Destinazione'],
['10C','10C','10C','Destinazione'],
['86','86','86','Destinazione'],
['M12A','M12A','M12A','Destinazione'],
['M12AD','M12AD','M12AD','Destinazione'],
['11F','11F','11F','Destinazione'],
['7','7','7','Destinazione'],
['16/AL','16/AL','16/AL','Destinazione']
    			];


