<?php


    include("conn.php");
    function security($conn,$str){
        return mysqli_real_escape_string($conn,htmlspecialchars(strip_tags(addslashes($str))));
    }



    if(isset($_POST['ok'])){
        $username = security($conn,$_POST['username']);
		$salt = bin2hex(random_bytes(8));
        $password = hash("sha256",$salt.security($conn,$_POST['password']));
        $email = security($conn,$_POST['email']);
        $start = security($conn,$_POST['start']);
        
        $q = "INSERT INTO `users` (`name`,`salt`,`password`,`email`,`region`,`starting_region`,`characterstyle_id`,`money`,`x`,`y`) VALUES
         ('".$username."', '".$salt."', '".$password."', '".$email."', '".$start."', '".$start."', '0', '1', '0', '0')";
        mysqli_query($conn, $q) or die(mysqli_error($conn));
		//create inventory too later!

    }
?>

<table align=center style="text-align:center;">
<form action=# method=post>
<tr>
    <td><input type=text name=username placeholder=Username></td>
    </tr>

    <tr>
    <td><input type=password name=password placeholder=Password></td>
    </tr>
    <tr>
    <td><input type=email name=email placeholder=E-mail></td>
    </tr>
    <tr>
    <td><p>You will get the basic character style, but after Level xx you can draw your own :)</p></td>
    </tr>
    <tr>
    <td><p>Please select where you want to start.</p></td>
    </tr>
    <tr>
    <td>
    <select name="start">
        <option value="futago">Futago Pass</option>
        <option value="hokkaido">Hokkaido</option>
        <option value="shikoku">Shikoku</option>
        <option value="mie">Mie</option>
        <option value="kai">Kai</option>
        <option value="mutsu">Mutsu</option>
        <option value="kyushu">Kyushu</option>
    </select>
    </td>
    </tr>
    <tr>
    <td><input type=submit name=ok value="Register"></td>
    </tr>

</form>
</table>