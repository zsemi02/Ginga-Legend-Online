<?php
if(!isset($_GET['name']))
	die();
include('conn.php');
function security($conn,$str){
        return mysqli_real_escape_string($conn,htmlspecialchars(strip_tags(addslashes($str))));
    }
$name = security($conn,$_GET['name']);
$sql = "select `salt` from `users` where `name`='".$name."'";
$salt = mysqli_fetch_assoc(mysqli_query($conn, $sql));
echo "salt".$salt['salt'];
?>