<?php 

//Connects to your Database 
mysql_connect("localhost", "tdk_admin", "admin") or die(mysql_error()); 
mysql_select_db("tdk_vsp") or die(mysql_error()); 

if ($_POST['direction'] == 'X') {
	mysql_query("INSERT INTO hazards (type, latitude, longitude, direction, message, expiration) VALUES ('$_POST[type_haz]', '$_POST[lat_text]', '$_POST[lng_text]', NULL, '$_POST[message]', '$_POST[time_text]')");
}
else {
	mysql_query("INSERT INTO hazards (type, latitude, longitude, direction, message, expiration) VALUES ('$_POST[type_haz]', '$_POST[lat_text]', '$_POST[lng_text]', '$_POST[direction]', '$_POST[message]', '$_POST[time_text]')");

}
/*function add_to_database (){
	$insertQuery = "INSERT INTO hazards (type, latitude, longitude, message) VALUES (";
	$insertQuery .= $_POST['type_haz'] . ", " . $_POST['lat_text'] . ", " . $_POST['lng_text'] . ", " . $_POST['message'];
	$insertQuery .= ")";
	$adding_to = mysql_query($insertQuery) or die(mysql_error());
	
	mysqli_query($conn, $insertQuery);
}*/
header("Location: index.php"); 

?>
