db_connect.php
<?php

/*
	A class file to connect to database
*/

class DB_CONNECT {
	
	// constructor
	function __construct() {
		// connecting to databse
		$this->connect();
	}

	// destructor
	function __destruct() {
		// clsoing db connection
		$this->close();
	}

	// function to connect with database
	function connect() {
		// import database connection variables
		require_once __DIR__ . '/db_config.php';
		
		// connecting to mysql database
		$con = mysql_connect(DB_SERVER, DB_USER, DB_PWD) or die(mysql_error());
		
		// selecting database
		$db = mysql_select_db(DB_DATABASE) or die(mysql_error()) or die(mysql_error());

		// returning connection cursor
		return $con;
	}

	// function to close db connection
	function close() {
		mysql_close();
	}
	
}
// http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
?>






