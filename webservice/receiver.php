<?php
date_default_timezone_set('Europe/Minsk');
$logname = 'log.txt';
$filename = 'received.txt';
$date = date("d-m-Y h:i:s");
$somecontent = "\r\n".$_REQUEST['model']." ".$date."\r\n".$_REQUEST['msg'];
$action = $_REQUEST['action'];

switch($action)
{
	case "sms":
	break;
}

function saveFile(){
// Вначале давайте убедимся, что файл существует и доступен для записи.
// if (is_writable($filename)) {

    // В нашем примере мы открываем $filename в режиме "дописать в конец".
    // Таким образом, смещение установлено в конец файла и
    // наш $somecontent допишется в конец при использовании fwrite().
    if (!$handle = fopen($filename, 'a')) {
         echo "cant open ($filename)";
         exit;
    }

    // Записываем $somecontent в наш открытый файл.
    if (fwrite($handle, $somecontent) === FALSE) {
        echo "cant write ($filename)";
        exit;
    }
    
    fclose($handle);

// } else {
    // echo "$filename not writable";
// }
}
?>