

var oTable;
var gaiSelected =  [];

$(document).ready(
		function() {

			geocoder = new google.maps.Geocoder();
			bounds = new google.maps.LatLngBounds();
			Demo.map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
			Demo.map.mapTypes.set('OSM', osmMapType);
			Demo.map.setMapTypeId('OSM');

            //Start & End Address
            $('#startaddress').autogeocomplete();
            $('#endaddress').autogeocomplete();

            $('input.switcheroo').focus(function(){
                if (this.value == this.defaultValue)
                    this.value = '';
            }).blur(function(){
                if (this.value == '')
                    this.value = this.defaultValue;
            });	

            //Time Picker - http://github.com/perifer/timePicker
			var now = new Date();
			$("#day").val(now.getDay());
			$("#time").timePicker({
				  startTime: new Date(0, 0, 0, 5, 00, 0),
				  endTime: new Date(0, 0, 0, 23, 30, 0),
				  show24Hours: false, separator: '.', step: 15});
			$.timePicker("#time").setTime(now);

			// Google Maps Context Menu
			$(Demo.map.getDiv()).append(contextMenu);
			google.maps.event.addListener(Demo.map, 'rightclick', function(e) {
				showContextMenu(e);
			});

			// Hide context menu on some events
			$.each('click dragstart zoom_changed maptypeid_changed'.split(' '),
					function(i, name) {
						google.maps.event.addListener(Demo.map, name,
								function() {
									contextMenu.hide();
								});
					});
			//Accordian Events
		    $("#accordion").accordion({ fillSpace: true });
		    $(window).resize(function(){
		        $("#accordion").accordion("resize");
		    });

		    //DataTable	    
			$('#dynamic').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>' );
			var oTable = $('#example').dataTable( {
				"aaData": aDataSet,
				"bPaginate": false,
				"bInfo": false,
				"sScrollY": "400px",
				"aoColumns": [
				              { "bSearchable": false,"bVisible": false },
							  {"bSearchable": false,"bVisible": false },
							  {"sTitle": "Route","bSearchable": true,"bVisible": true },
							  {"sTitle": "Towards","bSearchable": true,"bVisible": true }]
			} );
			$('.dataTables_scrollHead').width('325px');
			$('.dataTables_scrollHeadInner').width('325px');

			/* DataTable Click event handler */
			$('#example tbody tr').live('click', function() {
				var aData = oTable.fnGetData(this);
				$.getJSON(document.URL + "routesearch", {routeId : aData[1]}, function(data) {
					showRoute(data);
				});
			});

			/* Add a click handler to the rows */
			$("#example tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
			});
		});
