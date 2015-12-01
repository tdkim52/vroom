<?php

// create new hazard row in database

// array for JSON response
$response = array();

//header('Content-Type: application/json');

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if (isset($_GET["latitude"]) && isset($_GET["longitude"])) {
    
    $latitude = $_GET['latitude'];
    $longitude = $_GET['longitude'];
    
    $lat1 = $latitude - 3.00;
    $lat2 = $latitude + 3.00;
    $lon1 = $longitude - 3.00;
    $lon2 = $longitude + 3.00;
    
    $withinQuery = "SELECT * FROM hazards WHERE " . "(latitude BETWEEN ";
    $withinQuery .= $lat1 . " AND " . $lat2 . ") "; 
    $withinQuery .= "AND (longitude BETWEEN " . $lon1 . " AND " . $lon2 . ")";
    
    $results = mysql_query($withinQuery);
    
    if (!empty($results)) {
	    if (mysql_num_rows($results) > 0) { 
	    
	        $response["hazard"] = array();
	    
	        while ($row = mysql_fetch_array($results)) {
	    	    $hazard = array();
	    	    $hazard["id"] = $row["id"];
	    	    $hazard["type"] = $row["type"];
	    	    $hazard["latitude"] = $row["latitude"];
	    	    $hazard["longitude"] = $row["longitude"];
	    	    $hazard["direction"] = $row["direction"];
	    	    $hazard["message"] = $row["message"];    
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
elseif (isset($_GET["all"])) {
    $allQuery = "SELECT * FROM hazards";
    $results = mysql_query($allQuery);
    
    if (!empty($results)) {
        if(mysql_num_rows($results) > 0) {
            $response["hazard"] = array();
            while ($row = mysql_fetch_array($results)) {
                $hazard = array();
                $hazard["id"] = $row["id"];
                $hazard["type"] = $row["type"];
                $hazard["latitude"] = $row["latitude"];
                $hazard["longitude"] = $row["longitude"];
                $hazard["direction"] = $row["direction"];
                $hazard["message"] = $row["message"];
                array_push($response["hazard"], $hazard);
            }
            $response["success"] = 1;
            echo json_encode($response);
        }
        else {
            $response["success"] = 0;
            $response["error"] = "No hazards in database";
            echo json_encode($reponse);
        }
    }
    else {
        $response["success"] = 0;
        $response["error"] = "No hazards in database";
        echo json_encode($reponse);
    }
}
else {
    $response["success"] = 0;
    $response["error"] = "Required lat/long missing";
    echo json_encode($response);
}
?>
    
