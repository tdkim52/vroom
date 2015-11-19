<?php
//Connects to your Database
$conn = mysql_connect("localhost", "tdk_admin", "admin") or die(mysql_error());
mysql_select_db("tdk_vsp") or die(mysql_error());
/*
This is a php function to add to the database. it is called when the html form is submitted
*/
function add_to_database (){
	$insertQuery = "INSERT INTO hazards (type, latitude, longitude, message) VALUES (";
	$insertQuery .= $_POST['type_haz'] . ", " . $_POST['lat_text'] . ", " . $_POST['lng_text'] . ", " . $_POST['message'];
	$insertQuery .= ")";
	$adding_to = mysql_query($insertQuery) or die(mysql_error());

	mysqli_query($conn, $insertQuery);
}

 //checks cookies to make sure they are logged in
 if(isset($_COOKIE['ID_your_site'])){

 	$username = $_COOKIE['ID_your_site'];
 	$pass = $_COOKIE['Key_your_site'];
 	$check = mysql_query("SELECT * FROM users WHERE username = '$username'")or die(mysql_error());

	$hazards = array();


 	while($info = mysql_fetch_array( $check )){

		//if the cookie has the wrong password, they are taken to the login page
 		if ($pass != $info['password']){
			header("Location: login.php");
 		}
		//otherwise they are shown the admin area
		else{
			$i = 0;
			$s = 1;
			$retrieve = mysql_query("SELECT * FROM hazards")or die(mysql_error());
			while ($row = mysql_fetch_array($retrieve, MYSQL_ASSOC)) {
				$hazards [$i] = array($row['id'], $row['type'], $row['latitude'], $row['longitude'], $row['direction'], $row['message']);
				$i++;
			}

			if (isset($_POST['type_haz']) && isset($_POST['lat_text']) && isset($_POST['lng_text'])) {
				$insertQuery = "INSERT INTO hazards (type, latitude, longitude, direction, message) VALUES ('";
				$insertQuery .= $_POST['type_haz'] . "', '" . $_POST['lat_text'] . "', '" . $_POST['lng_text'] . "'";
			if (isset($_POST['direction'])) {
				$insertQuery .= ", '" . $_POST['direction'] . "'";
			}
			if (isset($_POST['message'])) {
				$insertQuery .= ", '" . $_POST['message'] . "')";
			}
			else {
				$insertQuery .= ")";
			}
			$result = mysql_query($insertQuery);

			}

			?>
			<!DOCTYPE html>
			<html>
			<title>VSP</title>
				<head>
				<style type="text/css">
				html { height: 100% }
				body { height: 100%; margin: 0; padding: 0 }
				#map-canvas {
				width: 65%;
				height: 100%;
				float: left;
					background-color: #CCC;
				}

				#form{
					width: 35%;
					height: 100%;
					float: right;
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
						//var hazards = <?php echo $hazards?>;
						var map = new google.maps.Map(mapCanvas, mapOptions)
						setMarkers(map, hazards);
						google.maps.event.addListener(map, 'rightclick', function(event) {
								placeMarker(event.latLng, map);

								/* This is where we get the lat and long
								 * and trim them and update the form with the correct length
								 */

								document.getElementById('lat_text').value = event.latLng.lat();
								document.getElementById('lng_text').value = event.latLng.lng();

								var new_lat = document.getElementById('lat_text').value;
								var trim_lat = new_lat.split(".");
								var fin_lat = trim_lat[0] + "." + trim_lat[1].slice(0,6);

								document.getElementById('lat_text').value = fin_lat;

								var new_lng = document.getElementById('lng_text').value;
								var trim_lng = new_lng.split(".");
								var fin_lng = trim_lng[0] + "." + trim_lng[1].slice(0,6);

								document.getElementById('lng_text').value = fin_lng;


						});

					}

  					var hazards = <?php echo json_encode($hazards); ?>;

					var infowindow = new google.maps.InfoWindow();

					var pin;

					function placeMarker(location, map) {
						if ( pin ) {
					    pin.setPosition(location);
					  } else {
					    pin = new google.maps.Marker({
					      position: location,
					      map: map
					    });
					  }
					}

					function setMarkers(map, locations) {
						for (var i = 0; i < locations.length; i++) {
							var hazard =  locations[i];
							var dir_icon;
							switch (hazard[4]) {
								case 'N':
									dir_icon = 'images/north.png';
									break;
								case 'E':
									dir_icon = 'images/east.png';
									break;
								case 'S':
									dir_icon = 'images/south.png';
									break;
								case 'W':
									dir_icon = 'images/west.png';
									break;
								default:
									dir_icon = 'images/warning.png';
							}
							var myLatLng = new google.maps.LatLng(hazard[2], hazard[3]);
							var contentString = '<div id="content">'+
						      '<div id="siteNotice">'+
						      '</div>'+
						      '<h1 id="firstHeading" class="firstHeading">' + hazard[1] + '</h1>'+
						      '<div id="bodyContent">'+
						      '<p>Hazard ID:  ' + hazard[0] +
						      '<br>Latitude:  ' + hazard[2] +
						      '<br>Longitude: ' + hazard[3] +
						      '<br>Direction: ' + hazard[4] +
						      '<br>Message:   ' + hazard[5] + '</p>' +
						      '<p><a href="http://vsp.tdkim.com/delete.php?id=' + hazard[0] +'">' +
						      '<b>DELETE</a>' +
						      '</div>'+
						      '</div>';
							var marker = new google.maps.Marker({
								position: myLatLng,
								map: map,
								icon: dir_icon,
								title: hazard[0]
							});
							google.maps.event.addListener(marker, 'click', (function(marker, contentString) {
								return function() {
									infowindow.setContent(contentString);
									infowindow.open(map, marker);
									document.getElementById('haz_id').value = marker.title;
								}
							})(marker, contentString));
						}
					}

					google.maps.event.addDomListener(window, 'load', initialize);

				</script>

				</head>
				<body>


				<div id="map-canvas" style="width: 60%; height: 70%"></div>

				<div id="form">

				<aside>
					<form name = "haz_form" method="POST" action="submit.php">
						Hazards<br>
						<select id="type_haz" name="type_haz">
						<option value="other">Other</option>
						<option value="construction">Construction</option>
						<option value="black_ice">Black Ice</option>
						<option value="car_accident">Car Accident</option>
						</select>
						<br>
						Direction<br>
						<select id="direction" name="direction">
						<option value='X'>Not Applicable</option>
						<option value='N'>North</option>
						<option value='E'>East</option>
						<option value='S'>South</option>
						<option value='W'>West</option>
						</select>
						<br>
						Latitude:<br>
						<input id="lat_text" type="text" name="lat_text">
						<br>
						Longitude:<br>
						<input id="lng_text" type="text" name="lng_text">
						<br>
						Length of incident:<br>
						<input id="time_text" type="text" name="time_text">
						<br><br>
						<textarea name ="message" cols="50" rows="5" maxlength="140"></textarea>
						<br>
						<input type="submit" value="Submit">
					</form>
				</aside>
				<aside>
					<form name = "delete_form" method="POST" action="delete.php">
						Hazard ID #<br>
						<input id="haz_id" type="text" name="haz_id">
						<br>
						<input type="submit" value="Delete">
					</form>
				</aside>

			</div>

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
