<?php
//Connects to your Database 
mysql_connect("localhost", "tdk_admin", "virtualsignpost") or die(mysql_error()); 
mysql_select_db("tdk_vsp") or die(mysql_error()); 

 //checks cookies to make sure they are logged in 
 if(isset($_COOKIE['ID_your_site'])){ 

 	$username = $_COOKIE['ID_your_site']; 
 	$pass = $_COOKIE['Key_your_site']; 
 	$check = mysql_query("SELECT * FROM users WHERE username = '$username'")or die(mysql_error()); 

 	while($info = mysql_fetch_array( $check )){ 

		//if the cookie has the wrong password, they are taken to the login page 
 		if ($pass != $info['password']){
			header("Location: login.php"); 
 		}
		//otherwise they are shown the admin area
		else{
			?>
			<!DOCTYPE html>
			<html>
			<title>VSP</title>
				<head>
				<style type="text/css">
				html { height: 100% }
				body { height: 100%; margin: 0; padding: 0 }
				#map-canvas {
				width: 75%;
				height: 100%;
					background-color: #CCC;
				}
				</style>
				<script src="https://maps.googleapis.com/maps/api/js"></script>
				<script>
					function initialize() {
						var mapCanvas = document.getElementById('map-canvas');
						var mapOptions = {
							center: new google.maps.LatLng(48.7326, -122.4866),
							zoom: 13,
							mapTypeId: google.maps.MapTypeId.ROADMAP
						}
						var map = new google.maps.Map(mapCanvas, mapOptions)
						setMarkers(map, hazards);
						google.maps.event.addListener(map, 'rightclick', function(event) {
							placeMarker(event.latLng, map);

								document.getElementById('lat_text').value = event.latLng.lat();
								document.getElementById('lng_text').value = event.latLng.lng();
						});
					}
		
					var hazards = [
						['aggressive slackliners', 48.738779, -122.484702],
						['construction, left lane blocked', 48.749091, -122.478098],
						['seismic activity', 47.595163, -122.331655],
						['collision', 47.617421,-122.201673],
						['elephant asleep on path', -20.239326, 46.515856]
					];
		
					var infowindow = new google.maps.InfoWindow();
		
					function placeMarker(location, map) {
						var pin = new google.maps.Marker({
							position: location,
							map: map
						});
					}
		

			//test lat 48.75126561487703 and lng -122.4356031339111

					function setMarkers(map, locations) {
						for (var i = 0; i < locations.length; i++) {
							var hazard =  locations[i];
							var myLatLng = new google.maps.LatLng(hazard[1], hazard[2]);
							var marker = new google.maps.Marker({
								position: myLatLng,
								map: map,
								title: hazard[0]
							});
							google.maps.event.addListener(marker, 'click', (function(marker, i) {
								return function() {
									infowindow.setContent(locations[i][0]);
									infowindow.open(map, marker);
								}
							})(marker, i));
						}
					}

					function form2map (){
						var hazard_drop_box = document.getElementById("type_haz");
						var selected_hazard = hazard_drop_box.options[hazard_drop_box.seletedIndex].value;
						var new_lat = document.getElementById('lat_text').value;
						var new_lng = document.getElementById('lng_text').value;
						var new_hazard = {selected_hazard, new_lat, new_lng};
			
						setMarkers(map,new_hazard);

					}
		
					google.maps.event.addDomListener(window, 'load', initialize);

				</script>

				</head>
				<body>
				<div id="map-canvas" style="width: 60%; height: 70%"></div>

				<aside>
					Hazards<br>
					<select id="type_haz" name="hazards_menu">
					<option value="construction">construction</option>
					<option value="black_ice">black ice</option>
					<option value="car_accident">car accident</option>
					</select> 


					<form action="action_page.php">
					Latitude:<br>
					<input id="lat_text" type="text" name="lat_box">
					<br>
					Longitude:<br>
					<input id="lng_text" type="text" name="lng_box">
					<br><br>
	
					<textarea name ="message" cols="50" rows="5" maxlength="140">
					</textarea>
					<br>
					<input type="submit" value="Submit" onclick="form2map();">
					</form> 
				</aside>

				</body>
			</html>
			<?	
 		}
	}
}

 else{ //if the cookie does not exist, they are taken to the login screen 
	header("Location: login.php"); 
 }
 ?>
