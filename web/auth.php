<?php

include('Crypt/RSA.php');
include("conn.php");

function security($conn,$str){
        return mysqli_real_escape_string($conn,htmlspecialchars(strip_tags(addslashes($str))));
    }
//// Get request, Send out pubkey
    if(isset($_GET['getpubkey'])){
        $privkey = null;
        $config = array(
            "config" => "E:\programok\OpenSSL-Win64\bin\cnf\openssl.cnf",
            "private_key_bits" => 2048,
            "private_key_type" => OPENSSL_KEYTYPE_RSA,
            "encrypt_key" => false,

        );
        $gen = openssl_pkey_new($config);
       
        openssl_pkey_export($gen, $privkey, "", $config);
        $pubkey = openssl_pkey_get_details($gen);
        $pubkey = $pubkey["key"];

        echo $pubkey;
        $ID = md5(bin2hex(random_bytes(8)));
        echo $ID;
        $q = "INSERT INTO `auth` (`privkey`, `hash`) VALUES ('".$privkey."', '".$ID."')";
        mysqli_query($conn, $q) or die(mysql_error($conn));
    }

    //////// Check validity
    if(isset($_POST['username']) && isset($_POST['password'])){
        $privatekey = file_get_contents("key.pem"); // mindig újat generálni!
        $pass = security($conn,$_POST['password']);
        $username = security($conn,$_POST['username']);
        
  
       $data = openssl_pkey_get_private($privatekey, '');

        $pass = str_replace(" ", "+",$pass);
       
      
        openssl_private_decrypt(base64_decode($pass), $passmd5, $data);
        while($msg = openssl_error_string()){
            echo $msg."\n";
        }


        $q = "SELECT * FROM `users` WHERE `name`='".$username."' AND `password`='".$passmd5."'";
      
        $num = mysqli_num_rows(mysqli_query($conn,$q));
        if($num == 1){
            echo "validate".md5("Account ".$username." has been found!");
        }else{
            echo "validate".md5("User not exists");
        }
     //Note: check if user/pass are exists in database, if yes, than send back a hash.
       //echo "validate".md5("Account ".$username." has been found!");
    }

    
?>