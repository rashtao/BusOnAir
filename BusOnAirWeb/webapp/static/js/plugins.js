
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
['0','0','','Funivia'],
['31','31','4','cansatessa'],
['164','164','4/DA','stazione'],
['181','181','4D','s.s.17'],
['28','28','5','universitàcoppito'],
['154','154','5/8DL','terminalbus'],
['153','153','5/8S','universitàcoppito'],
['166','166','5N','terminalbus'],
['55','55','5PN','terminalbus'],
['160','160','5S','matteodaleonessa'],
['183','183','5SA','gignanopiazza'],
['25','25','6','paganica'],
['129','129','6C','terminalbus'],
['133','133','6D','paganica'],
['128','128','6DA','terminalbus'],
['15','15','6DAS','terminalbus'],
['130','130','6DB','INFN - assergi 2'],
['3','3','6DBS','paganica'],
['132','132','6DD','leonardodavinciregioneabruzzo'],
['162','162','6DS','paganica'],
['135','135','6F','terminalbus'],
['138','138','6G','Funivia'],
['140','140','6H','terminalbus'],
['145','145','6S','terminalbus'],
['179','179','6SA','paganica'],
['73','73','6SAS','terminalbus'],
['41','41','6SBS','stazione'],
['175','175','6SD','terminalbus'],
['167','167','6SS','paganica'],
['24','24','7','CAMARDA'],
['102','102','7A','terminalbus'],
['106','106','7D','terminalbus'],
['105','105','7F','paganica'],
['35','35','8','fontegrossa'],
['81','81','8F','terminalbus'],
['78','78','8L','universitàcoppito'],
['77','77','8N','scuolecollesapone'],
['76','76','8S','mulinopile'],
['34','34','9','terminalbus'],
['58','58','9B','terminalbus'],
['60','60','9C','terminalbus'],
['83','83','10','terminalbus'],
['113','113','10A','terminalbus'],
['112','112','10B','terminalbus'],
['111','111','10C','terminalbus'],
['110','110','10D','bivio pianola'],
['109','109','10E','bivio pianola'],
['52','52','11','terminalbus'],
['142','142','11A','Scuola Patini - VIA ANTICA ARISCHIA'],
['143','143','11C','genzano'],
['136','136','11D','terminalbus'],
['141','141','11F','bagnogrande'],
['139','139','11G','terminalbus'],
['54','54','12','terminalbus'],
['158','158','12A','terminalbus'],
['155','155','12AA','terminalbus'],
['156','156','12AB','terminalbus'],
['161','161','12AH','terminalbus'],
['157','157','12B','terminalbus'],
['159','159','12G','terminalbus'],
['163','163','12I','terminalbus'],
['107','107','12SC','terminalbus'],
['104','104','12SF','terminalbus'],
['50','50','13','terminalbus'],
['176','176','13C','sanmarco-pozza'],
['177','177','13E','sanmarco-pozza'],
['180','180','13G','sanmarco-pozza'],
['186','186','13M','sanmarco-pozza'],
['169','169','13S','terminalbus'],
['51','51','14','s.s.80-s.p.33'],
['45','45','15','terminalbus'],
['75','75','15/A','terminalbus'],
['49','49','15AS','stazione'],
['79','79','15BS','terminalbus'],
['93','93','15CS','terminalbus'],
['36','36','15D','terminalbus'],
['40','40','15E','terminalbus'],
['32','32','15GS','terminalbus'],
['72','72','15IS','terminalbus'],
['23','23','15L','terminalbus'],
['26','26','15M','biviocansatessa'],
['29','29','15N','terminalbus'],
['30','30','15O','terminalbus'],
['20','20','15S','terminalbus'],
['47','47','16','terminalbus'],
['178','178','16/A','terminalbus'],
['101','101','16/AB','s.elia-collevernesco'],
['92','92','16/AE','picenze'],
['122','122','16/AH','picenze'],
['116','116','16/AL','picenze'],
['120','120','16/AM','picenze'],
['118','118','16/AN','picenze'],
['16','16','16A','terminalbus'],
['18','18','16C','scuolecollesapone'],
['103','103','16CG','terminalbus'],
['17','17','16D','terminalbus'],
['65','65','16E','bazzano'],
['67','67','16F','terminalbus'],
['56','56','16N','terminalbus'],
['57','57','16S','terminalbus'],
['19','19','24','scuolecollesapone'],
['22','22','30','terminalbus'],
['90','90','31A','aquilone'],
['86','86','31B','terminalbus'],
['27','27','32','terminalbus'],
['7','7','33','stazione'],
['38','38','33D','terminalbus'],
['8','8','34','aquilone'],
['6','6','35','viasaragat(aquilone)'],
['53','53','40A','finanza'],
['174','174','44','finanza'],
['173','173','45','s.s.80-s.p.33'],
['188','188','50','scuolecollesapone'],
['182','182','51','collecapocroce'],
['149','149','59','scuolecollesapone'],
['124','124','67','c.a.s.e.coppito3'],
['126','126','68','scuolecollesapone'],
['146','146','74','finanza'],
['108','108','77A','terminalbus'],
['48','48','77AS','universitàcoppito'],
['114','114','77D','finanza'],
['91','91','78','collecapocroce'],
['119','119','80','terminalbus'],
['80','80','80A','terminalbus'],
['82','82','80C','terminalbus'],
['85','85','80E','terminalbus'],
['74','74','80S','terminalbus'],
['115','115','82','terminalbus'],
['117','117','83','finanza'],
['121','121','86','scuolecollesapone'],
['123','123','87','scuolecollesapone'],
['33','33','92A','aquilone'],
['21','21','92AS','aquilone'],
['69','69','99','aquilone'],
['43','43','99B','aquilone'],
['42','42','99D','terminalbus'],
['165','165','99DC','terminalbus'],
['61','61','99S','aquilone'],
['70','70','101','cese(dir.s.r.80)'],
['184','184','101F','cese(dir.s.r.80)'],
['68','68','102','cese(dir.s.r.80)'],
['66','66','104','terminalbus'],
['99','99','104B','terminalbus'],
['97','97','104C','finanza'],
['95','95','104D','finanza'],
['64','64','105','cansatessa'],
['87','87','105B','nucleoindustriale(innamorati)'],
['88','88','105C','cansatessa'],
['63','63','106','aquilone'],
['62','62','106C','paganica'],
['44','44','106S','terminalbus'],
['59','59','108','paganica'],
['14','14','108C','paganica'],
['89','89','108HS','terminalbus'],
['12','12','M1','paganica'],
['13','13','M1B','nucleoindustriale(innamorati)'],
['11','11','M2','paganica'],
['84','84','M2A','universitàcoppito'],
['137','137','M2AB','universitàcoppito'],
['134','134','M2AC','s.gregoriopaese'],
['147','147','M2AE','ASSERGI ALTO'],
['144','144','M2AG','CAMARDA'],
['46','46','M2F','paganica'],
['10','10','M3','menzano'],
['127','127','M3H','menzano'],
['125','125','M3L','terminalbus'],
['131','131','M3N','menzano'],
['9','9','M4','finanza'],
['148','148','M4B','cese paese'],
['4','4','M5','finanza'],
['2','2','M6','Funivia'],
['185','185','M6A','ctr.Lilletta'],
['170','170','M6AA','ctr.Lilletta'],
['171','171','M6AB','ctr.Lilletta'],
['172','172','M6AC','ctr.Lilletta'],
['187','187','M6E','terminalbus'],
['5','5','M9','terminalbus'],
['1','1','M11','terminalbus'],
['37','37','M11A','monteluco'],
['39','39','M11B','universitàcoppito'],
['71','71','M12A','terminalbus'],
['94','94','M12AC','terminalbus'],
['96','96','M12AD','terminalbus'],
['98','98','M12AE','focedisassa'],
['100','100','M12AF','terminalbus'],
['151','151','M81','s.s.17'],
['150','150','M82','s.vittorino'],
['152','152','M88','casaline'],
['168','168','M88D','menzano'],
['0','0','','Funivia'],
['31','31','4','terminalbus'],
['164','164','4/DA','cansatessa'],
['181','181','4D','scuolecollesapone'],
['28','28','5','reissromolisiemens'],
['154','154','5/8DL','universitàcoppito'],
['153','153','5/8S','fontegrossa'],
['166','166','5N','gignano'],
['55','55','5PN','gignanopiazza'],
['160','160','5S','gignanopiazza'],
['183','183','5SA','terminalbus'],
['25','25','6','terminalbus'],
['129','129','6C','Funivia'],
['133','133','6D','terminalbus'],
['128','128','6DA','Assergi'],
['15','15','6DAS','paganicapiazza'],
['130','130','6DB','terminalbus'],
['3','3','6DBS','terminalbus'],
['132','132','6DD','paganicapiazza'],
['162','162','6DS','scuolecollesapone'],
['135','135','6F','Funivia'],
['138','138','6G','terminalbus'],
['140','140','6H','Funivia'],
['145','145','6S','paganicapiazza'],
['179','179','6SA','scuolecollesapone'],
['73','73','6SAS','paganicapiazza'],
['41','41','6SBS','paganicapiazza'],
['175','175','6SD','INFN - assergi 2'],
['167','167','6SS','terminalbus'],
['24','24','7','bazzano'],
['102','102','7A','CAMARDA'],
['106','106','7D','CAMARDA'],
['105','105','7F','terminalbus'],
['35','35','8','universitàcoppito'],
['81','81','8F','fontegrossa'],
['78','78','8L','fontegrossa'],
['77','77','8N','universitàcoppito'],
['76','76','8S','fontegrossa'],
['34','34','9','terminalbus'],
['58','58','9B','terminalbus'],
['60','60','9C','aragno'],
['83','83','10','terminalbus'],
['113','113','10A','terminalbus'],
['112','112','10B','terminalbus'],
['111','111','10C','scuolecollesapone'],
['110','110','10D','terminalbus'],
['109','109','10E','terminalbus'],
['52','52','11','terminalbus'],
['142','142','11A','bagnogrande'],
['143','143','11C','scuolecollesapone'],
['136','136','11D','collecapocroce'],
['141','141','11F','Via Carducci'],
['139','139','11G','scuolecollesapone'],
['54','54','12','terminalbus'],
['158','158','12A','s.s.17ovest'],
['155','155','12AA','focedisassa'],
['156','156','12AB','focedisassa'],
['161','161','12AH','terminalbus'],
['157','157','12B','s.s.17ovest'],
['159','159','12G','terminalbus'],
['163','163','12I','terminalbus'],
['107','107','12SC','terminalbus'],
['104','104','12SF','Ponte San Giovanni'],
['50','50','13','s.s17-sassascalocasen.i.'],
['176','176','13C','siemensreissromoli'],
['177','177','13E','terminalbus'],
['180','180','13G','collecapocroce'],
['186','186','13M','terminalbus'],
['169','169','13S','cese paese'],
['51','51','14','scuolecollesapone'],
['45','45','15','terminalbus'],
['75','75','15/A','s.s.17ovest'],
['49','49','15AS','arischiaiacp'],
['79','79','15BS','monteomo'],
['93','93','15CS','terminalbus'],
['36','36','15D','terminalbus'],
['40','40','15E','terminalbus'],
['32','32','15GS','terminalbus'],
['72','72','15IS','terminalbus'],
['23','23','15L','terminalbus'],
['26','26','15M','terminalbus'],
['29','29','15N','terminalbus'],
['30','30','15O','terminalbus'],
['20','20','15S','terminalbus'],
['47','47','16','mapfossa'],
['178','178','16/A','picenze'],
['101','101','16/AB','picenze'],
['92','92','16/AE','terminalbus'],
['122','122','16/AH','terminalbus'],
['116','116','16/AL','terminalbus'],
['120','120','16/AM','terminalbus'],
['118','118','16/AN','terminalbus'],
['16','16','16A','terminalbus'],
['18','18','16C','terminalbus'],
['103','103','16CG','terminalbus'],
['17','17','16D','terminalbus'],
['65','65','16E','terminalbus'],
['67','67','16F','terminalbus'],
['56','56','16N','terminalbus'],
['57','57','16S','terminalbus'],
['19','19','24','testrina'],
['22','22','30','stazione'],
['90','90','31A','stazione'],
['86','86','31B','stazione'],
['27','27','32','finanza'],
['7','7','33','finanza'],
['38','38','33D','aquilone'],
['8','8','34','terminalbus'],
['6','6','35','stazione'],
['53','53','40A','terminalbus'],
['174','174','44','coppito'],
['173','173','45','scuolecollesapone'],
['188','188','50','stazione'],
['182','182','51','s.gregoriopaese'],
['149','149','59','c.a.s.e.coppito3'],
['124','124','67','scuolecollesapone'],
['126','126','68','s.s.17'],
['146','146','74','stazione'],
['108','108','77A','Rotatoria Vila delle fiamme gialle'],
['48','48','77AS','terminalbus'],
['114','114','77D','terminalbus'],
['91','91','78','scuolecollesapone'],
['119','119','80','aquilone'],
['80','80','80A','cese(dir.s.r.80)'],
['82','82','80C','aquilone'],
['85','85','80E','aquilone'],
['74','74','80S','Via Carducci'],
['115','115','82','roiocolle'],
['117','117','83','terminalbus'],
['121','121','86','c.a.s.e.coppito3'],
['123','123','87','pozza'],
['33','33','92A','terminalbus'],
['21','21','92AS','crocerossa'],
['69','69','99','aquilone'],
['43','43','99B','terminalbus'],
['42','42','99D','aquilone'],
['165','165','99DC','aquilone'],
['61','61','99S','aquilone'],
['70','70','101','cese(dir.s.r.80)'],
['184','184','101F','cese(dir.s.r.80)'],
['68','68','102','cese(dir.s.r.80)'],
['66','66','104','finanza'],
['99','99','104B','finanza'],
['97','97','104C','terminalbus'],
['95','95','104D','terminalbus'],
['64','64','105','nucleoindustriale(innamorati)'],
['87','87','105B','cansatessa'],
['88','88','105C','nucleoindustriale(innamorati)'],
['63','63','106','paganicapiazza'],
['62','62','106C','terminalbus'],
['44','44','106S','paganicapiazza'],
['59','59','108','aquilone'],
['14','14','108C','terminalbus'],
['89','89','108HS','MAP Onna'],
['12','12','M1','CAMARDA'],
['13','13','M1B','paganica'],
['11','11','M2','s.gregoriopaese'],
['84','84','M2A','paganica'],
['137','137','M2AB','paganica'],
['134','134','M2AC','universitàcoppito'],
['147','147','M2AE','universitàcoppito'],
['144','144','M2AG','universitàcoppito'],
['46','46','M2F','s.gregoriopaese'],
['10','10','M3','finanza'],
['127','127','M3H','terminalbus'],
['125','125','M3L','menzano'],
['131','131','M3N','finanza'],
['9','9','M4','cese paese'],
['148','148','M4B','finanza'],
['4','4','M5','collefracido'],
['2','2','M6','terminalbus'],
['185','185','M6A','terminalbus'],
['170','170','M6AA','terminalbus'],
['171','171','M6AB','terminalbus'],
['172','172','M6AC','terminalbus'],
['187','187','M6E','Funivia'],
['5','5','M9','terminalbus'],
['1','1','M11','universitàcoppito'],
['37','37','M11A','terminalbus'],
['39','39','M11B','terminalbus'],
['71','71','M12A','terminalbus'],
['94','94','M12AC','terminalbus'],
['96','96','M12AD','terminalbus'],
['98','98','M12AE','terminalbus'],
['100','100','M12AF','terminalbus'],
['151','151','M81','Scuola Patini - VIA ANTICA ARISCHIA'],
['150','150','M82','Scuola Patini - VIA ANTICA ARISCHIA'],
['152','152','M88','Scuola media pagliare- SASSA'],
['168','168','M88D','Scuola media pagliare- SASSA']
  
];


