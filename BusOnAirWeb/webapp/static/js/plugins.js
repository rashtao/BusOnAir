
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
['44','44','','terminalbus'],
['217','217','','Funivia'],
['0','0','4','cansatessa'],
['218','218','4','terminalbus'],
['169','169','4/DA','stazione'],
['68','68','4D','s.s.17'],
['164','164','5','universitàcoppito'],
['235','235','5','universitàcoppito'],
['150','150','5/8DL','terminalbus'],
['84','84','5/8S','universitàcoppito'],
['166','166','5N','terminalbus'],
['209','209','5N','universitàcoppito'],
['167','167','5PN','terminalbus'],
['165','165','5S','terminalbus'],
['226','226','5S','terminalbus'],
['168','168','5SA','gignanopiazza'],
['179','179','6','paganica'],
['230','230','6','paganica'],
['42','42','6C','Funivia'],
['214','214','6C','terminalbus'],
['118','118','6D','terminalbus'],
['224','224','6D','paganica'],
['40','40','6DA','terminalbus'],
['124','124','6DAS','terminalbus'],
['41','41','6DB','INFN - assergi 2'],
['237','237','6DB','siemensreissromoli'],
['182','182','6DBS','paganica'],
['114','114','6DD','leonardodavinciregioneabruzzo'],
['115','115','6DS','paganica'],
['234','234','6DS','terminalbus'],
['47','47','6F','terminalbus'],
['208','208','6F','Funivia'],
['48','48','6G','Funivia'],
['195','195','6G','terminalbus'],
['49','49','6H','terminalbus'],
['194','194','6H','Funivia'],
['123','123','6S','paganica'],
['197','197','6S','paganica'],
['181','181','6SA','paganica'],
['116','116','6SAS','terminalbus'],
['112','112','6SBS','stazione'],
['43','43','6SD','terminalbus'],
['210','210','6SD','INFN - assergi 2'],
['180','180','6SS','paganica'],
['105','105','7','CAMARDA'],
['246','246','7','terminalbus'],
['100','100','7A','terminalbus'],
['104','104','7D','terminalbus'],
['243','243','7D','CAMARDA'],
['173','173','7F','paganica'],
['82','82','8','fontegrossa'],
['198','198','8','universitàcoppito'],
['83','83','8F','terminalbus'],
['86','86','8L','universitàcoppito'],
['151','151','8N','scuolecollesapone'],
['85','85','8S','terminalbus'],
['204','204','8S','scuolecollesapone'],
['55','55','9','terminalbus'],
['236','236','9','terminalbus'],
['57','57','9B','terminalbus'],
['56','56','9C','terminalbus'],
['32','32','10','terminalbus'],
['192','192','10','terminalbus'],
['33','33','10A','terminalbus'],
['34','34','10B','terminalbus'],
['35','35','10C','terminalbus'],
['36','36','10D','bivio pianola'],
['37','37','10E','bivio pianola'],
['175','175','11','terminalbus'],
['228','228','11','terminalbus'],
['158','158','11A','Scuola Patini - VIA ANTICA ARISCHIA'],
['161','161','11C','genzano'],
['162','162','11D','terminalbus'],
['176','176','11F','bagnogrande'],
['163','163','11G','terminalbus'],
['94','94','12','terminalbus'],
['14','14','12A','terminalbus'],
['240','240','12A','terminalbus'],
['8','8','12AA','terminalbus'],
['9','9','12AB','terminalbus'],
['16','16','12AH','terminalbus'],
['92','92','12B','terminalbus'],
['93','93','12G','terminalbus'],
['95','95','12I','terminalbus'],
['96','96','12SC','terminalbus'],
['91','91','12SF','terminalbus'],
['62','62','13','terminalbus'],
['238','238','13','sassa scalo'],
['24','24','13C','sanmarco-pozza'],
['221','221','13C','terminalbus'],
['27','27','13E','terminalbus'],
['203','203','13E','sanmarco-pozza'],
['28','28','13G','sanmarco-pozza'],
['29','29','13M','sanmarco-pozza'],
['25','25','13S','s.s17-sassascalocasen.i.'],
['239','239','13S','terminalbus'],
['188','188','14','s.s.80-s.p.33'],
['134','134','15','terminalbus'],
['215','215','15','terminalbus'],
['135','135','15/A','terminalbus'],
['136','136','15AS','stazione'],
['137','137','15BS','terminalbus'],
['138','138','15CS','terminalbus'],
['139','139','15D','terminalbus'],
['140','140','15E','terminalbus'],
['141','141','15GS','terminalbus'],
['144','144','15IS','terminalbus'],
['146','146','15L','terminalbus'],
['147','147','15M','biviocansatessa'],
['145','145','15N','terminalbus'],
['142','142','15O','terminalbus'],
['143','143','15S','terminalbus'],
['125','125','16','terminalbus'],
['233','233','16','terminalbus'],
['19','19','16/A','terminalbus'],
['211','211','16/A','picenze'],
['18','18','16/AB','s.elia-collevernesco'],
['50','50','16/AE','picenze'],
['53','53','16/AH','picenze'],
['51','51','16/AL','picenze'],
['54','54','16/AM','picenze'],
['52','52','16/AN','picenze'],
['126','126','16A','terminalbus'],
['127','127','16C','scuolecollesapone'],
['129','129','16CG','terminalbus'],
['132','132','16D','terminalbus'],
['133','133','16E','bazzano'],
['128','128','16F','terminalbus'],
['130','130','16N','terminalbus'],
['131','131','16S','terminalbus'],
['178','178','24','scuolecollesapone'],
['148','148','30','stazione'],
['227','227','30','stazione'],
['88','88','31A','aquilone'],
['90','90','31B','optimes'],
['183','183','32','terminalbus'],
['149','149','33','stazione'],
['186','186','33D','terminalbus'],
['117','117','34','aquilone'],
['7','7','35','viasaragat(aquilone)'],
['64','64','40A','finanza'],
['184','184','44','finanza'],
['106','106','45','s.s.80-s.p.33'],
['107','107','50','scuolecollesapone'],
['177','177','51','collecapocroce'],
['17','17','59','scuolecollesapone'],
['157','157','67','c.a.s.e.coppito3'],
['97','97','68','scuolecollesapone'],
['89','89','74','finanza'],
['109','109','77A','terminalbus'],
['111','111','77AS','universitàcoppito'],
['70','70','77D','finanza'],
['152','152','78','piccinini'],
['196','196','78','collecapocroce'],
['71','71','80','aquilone'],
['242','242','80','terminalbus'],
['185','185','80A','terminalbus'],
['187','187','80C','terminalbus'],
['73','73','80E','terminalbus'],
['229','229','80E','aquilone'],
['72','72','80S','terminalbus'],
['200','200','80S','terminalbus'],
['99','99','82','terminalbus'],
['108','108','83','terminalbus'],
['225','225','83','finanza'],
['98','98','86','scuolecollesapone'],
['22','22','87','scuolecollesapone'],
['78','78','92A','aquilone'],
['77','77','92AS','aquilone'],
['189','189','92AS','aquilone'],
['76','76','99','aquilone'],
['75','75','99B','aquilone'],
['80','80','99D','terminalbus'],
['79','79','99DC','terminalbus'],
['81','81','99S','aquilone'],
['155','155','101','coppito'],
['245','245','101','cese(dir.s.r.80)'],
['156','156','101F','cese(dir.s.r.80)'],
['39','39','102','cese(dir.s.r.80)'],
['212','212','102','cese(dir.s.r.80)'],
['63','63','104','finanza'],
['222','222','104','terminalbus'],
['67','67','104B','c.a.s.e.coppito3'],
['190','190','104B','terminalbus'],
['66','66','104C','finanza'],
['65','65','104D','finanza'],
['1','1','105','cansatessa'],
['220','220','105','cansatessa'],
['170','170','105B','nucleoindustriale(innamorati)'],
['2','2','105C','cansatessa'],
['120','120','106','paganica'],
['232','232','106','paganica'],
['121','121','106C','paganica'],
['247','247','106C','terminalbus'],
['119','119','106S','terminalbus'],
['110','110','108','paganica'],
['219','219','108','aquilone'],
['122','122','108C','paganica'],
['241','241','108C','terminalbus'],
['113','113','108HS','terminalbus'],
['102','102','M1','paganica'],
['193','193','M1','paganica'],
['87','87','M1B','paganica'],
['231','231','M1B','nucleoindustriale(innamorati)'],
['21','21','M2','paganica'],
['202','202','M2','s.gregoriopaese'],
['103','103','M2A','universitàcoppito'],
['101','101','M2AB','universitàcoppito'],
['171','171','M2AC','s.gregoriopaese'],
['172','172','M2AE','ASSERGI ALTO'],
['174','174','M2AG','CAMARDA'],
['20','20','M2F','paganica'],
['23','23','M3','menzano'],
['223','223','M3','finanza'],
['30','30','M3H','menzano'],
['61','61','M3L','terminalbus'],
['31','31','M3N','menzano'],
['59','59','M4','finanza'],
['206','206','M4','via dell\'aeroporto'],
['60','60','M4B','cese paese'],
['74','74','M5','collefracido'],
['201','201','M5','finanza'],
['46','46','M6','terminalbus'],
['205','205','M6','Funivia'],
['3','3','M6A','terminalbus'],
['199','199','M6A','ctr.Lilletta'],
['4','4','M6AA','terminalbus'],
['213','213','M6AA','ctr.Lilletta'],
['5','5','M6AB','ctr.Lilletta'],
['216','216','M6AB','terminalbus'],
['6','6','M6AC','ctr.Lilletta'],
['45','45','M6E','terminalbus'],
['58','58','M9','terminalbus'],
['160','160','M11','terminalbus'],
['244','244','M11','universitàcoppito'],
['38','38','M11A','monteluco'],
['207','207','M11A','terminalbus'],
['159','159','M11B','universitàcoppito'],
['12','12','M12A','terminalbus'],
['10','10','M12AC','terminalbus'],
['11','11','M12AD','terminalbus'],
['15','15','M12AE','focedisassa'],
['13','13','M12AF','terminalbus'],
['153','153','M81','Scuola Patini - VIA ANTICA ARISCHIA'],
['191','191','M81','s.s.17'],
['154','154','M82','s.vittorino'],
['69','69','M88','casaline'],
['26','26','M88D','menzano']
    			];


