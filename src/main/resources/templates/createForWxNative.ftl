<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<div id="myqrcode"></div>

<script src="https://cdn.bootcss.com/jquery/1.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>
    //jQuery('#qrcode').qrcode("this plugin is great");
    jQuery('#myqrcode').qrcode({
        text	: "${codeUrl}"
    });
</script>

</body>
</html>