<?php

// create new hazard row in database

// array for JSON response
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if (isset($_GET["latitude"]) && isset($_GET["longitude"])) {
    
    $latitude = $_GET['latitude'];
    $longitude = $_GET['longitude'];
    
    $results = mysql_query("SELECT * FROM hazards");
    
    if (!empty($results)) {
	if (mysql_num_rows($results) > 0) {
	    //$results = mysql_fetch_array($results);
	    
	    $response["hazard"] = array();
	    
	    while ($row = mysql_fetch_array($results)) {
		$hazard = array();
		$hazard["id"] = $row["id"];
		$hazard["type"] = $row["type"];
		$hazard["latitude"] = $row["latitude"];
		$hazard["longitude"] = $row["longitude"];
		$hazard["message"] = $row["message"];
		//$response["success"] = 1;
		//$response["hazard"] = array();
		array_push($response["hazard"], $hazard);
	    }
	    $response["success"] = 1;
	    echo json_encode($response);
	}
	else {
	    $response["success"] = 0;
	    $response["error"] = "No hazards in range";
	    echo json_encode($response);
	}
    }
    else {
	$response["success"] = 0;
	$response["error"] = "No hazards in database";
	echo json_encode($response);
    }
}
else {
    $response["success"] = 0;
    $response["error"] = "Required lat/long missing";
    echo json_encode($response);
}
?>
    