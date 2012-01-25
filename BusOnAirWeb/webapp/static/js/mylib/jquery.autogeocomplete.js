/*------------------------------------------------------

	Modified version of - @Google Maps Autocomplete  https://github.com/lorenzsell/Geocoded-Autocomplete
	
------------------------------------------------------- */

(function($) {

    // google map variables
    var geocoder;
    var map;
    var marker;
    
    // set plugin options
    var map_frame_id;
    var map_window_id;
    var lat_id;
    var lng_id;
    var addr_id;
    var lat;
    var lng;
    var map_zoom;


    $.fn.extend({
        
        autogeocomplete: function(){
        	        
            // extend plugin options            
            map_window_id = "mapwindow";
            map_frame_id = "mapframe";
            lat_id = "filter_lat";
            lng_id = "filter_lng";
            addr_id = "filter_address";
            geocoder = new google.maps.Geocoder();            
            this.autocomplete({
            
                // fetch address values
                source: function(request, response) {
			
//                	var address = request.term +", Dublin , Ireland";
                	var address = request.term +", L\'Aquila , Abruzzo";
                    geocoder.geocode( {'address': address }, function(results, status) {
                        
                        var item_count = 0;                       
                        // limit to Ireland only results
                        var filter_results = [];                       
                        $.each(results, function(item){
//                            if(results[item].formatted_address.toLowerCase() !== 'dublin, co. fingal, ireland')
//                            {
                                filter_results.push(results[item]);
//                            }
                        });
                        // parse and format returned suggestions
                        response($.map(filter_results, function(item) {
                    
                        // split returned string
                        var place_parts = item.formatted_address.split(",");
                        var place = place_parts[0];
                        var place_details = "";
                        
                        // parse city, state, and zip
                        for(i=1;i<place_parts.length;i++){
                            place_details += place_parts[i];
                            if(i !== place_parts.length-1) place_details += ",";
                        }
                            
                        // return top 5 results
                        if (item_count < 5) {
                            item_count++;
                            return {
                                label:  place,
                                value: item.formatted_address,
                                desc: place_details,
                                latitude: item.geometry.location.lat(),
                                longitude: item.geometry.location.lng()
                            }
                        }

                        }));
                    })
                },
                // set the minimum length of string to autocomplete
                minLength: 3,
                // set geocoder data when an address is selected
                select: function(event, ui) {
                    $("#" + lat_id).val(ui.item.latitude);
                    $("#" + lng_id).val(ui.item.longitude);
                }  
            })
            // format how each suggestions is presented
            .data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" ).data( "item.autocomplete", item )
			.append( "<a><strong>" + item.label + "</strong><br>" + item.desc + "</a>").appendTo( ul );
            };

        }
            
    });

    
})(jQuery);
