<?php
$conn = mysqli_connect("localhost", "root", "", "gingammorpg");
mysqli_query($conn,"SET NAMES 'UTF8'");
mysqli_query($conn,"SET CHARACTER SET UTF8");
if(!$conn){
	die(mysqli_error($conn));
}
?>