<?php 

//Connects to your Database 
mysql_connect("localhost", "tdk_admin", "admin") or die(mysql_error()); 
mysql_select_db("tdk_vsp") or die(mysql_error()); 

if (isset($_GET['id'])) {
    mysql_query("DELETE FROM `hazards` WHERE `id`=$_GET[id]");
}
else {
	mysql_query("DELETE FROM `hazards` WHERE `id`=$_POST[haz_id]");
}

header("Location: index.php"); 

?>