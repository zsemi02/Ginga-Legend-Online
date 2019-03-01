<?php

include('Crypt/RSA.php');
include("conn.php");

function security($conn,$str){
        return mysqli_real_escape_string($conn,htmlspecialchars(strip_tags(addslashes($str))));
    }

    if(isset($_GET['getpubkey'])){
        
        $publickey = file_get_contents("public.pem");
        
        echo $publickey;
    }
    if(isset($_POST['username']) && isset($_POST['password'])){
        $privatekey = file_get_contents("key.pem"); // teszt után hardcodeolni, mindig újat generálni!
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