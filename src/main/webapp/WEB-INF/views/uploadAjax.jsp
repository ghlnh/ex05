<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Insert title here</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css" integrity="sha512-xh6O/CkQoPOWDdYTDqeRdPCVd1SpvCA9XXcUnZS2FmJNp1coAFzvtCN9BmamE+4aHK8yyUHUSCcJHgXloTyT2A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>

.uploadResult{
	width:100%;
	background-color:gray;
}

.uploadResult ul{
	display:flex;
	flex-flow:row;
	justify-content:center;
	align-items:center
}

.uploadResult ul li{
	list-style:none;
	padding:10px;
	align-context: center,
	text-allign: center;
}

.uploadResult ul li img{
	width:100px;
}

.uploadResult ul li span{
	color:white
}

.bigPictureWrapper {
	position : absolute;
	display : none;
	justify-content : center;
	align-items : center;
	top : 0%;
	width : 100%;
	height : 100%;
	background-color : gray;
	z-index : 100;
	background : rgba(255,255,255,0.5);
}

.bigPicture {
	position : relative;
	display:flex;
	justify-content : center;
	align-items : center;
}

.bigPicture img{
	width : 600px;
}

</style>

</head>
<body>
<div class = 'bigPictureWrapper'>
	<div class = 'bigPicture'>
	</div>

</div>

<h1>Upload with Ajax</h1>

<div class="uploadDiv">
	<input type="file" name = "uploadFile" multiple>	
</div>

<div class="uploadResult">
	<ul>
	
	</ul>
</div>

<button id = "uploadBtn">Upload</button>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-OERcA2EqjJCMA+/3y+gxIOqMEjwtxJY7qPCqsdltbNJuaOe923+mo//f6V8Qbsw3" crossorigin="anonymous"></script>

<script src="https://code.jquery.com/jquery-3.3.1.min.js"
integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous">
</script>

<script>

function showImage(fileCallPath){
	//alert(fileCallPath);
	
	$(".bigPictureWrapper").css("display", "flex").show();
	
	$(".bigPicture")
	.html("<img src = '/display?fileName=" +encodeURI(fileCallPath)+"'>");
/* 	.animate({width:'100%', height:'100%'}, 1000); */
}
	
$(document).ready(function() {
	
	var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
	var maxSize = 5242880;	//5MB
	
	function checkExtension(fileName, fileSize) {
		
		if(fileSize >= maxSize) {
			alert("?????? ????????? ??????");
			return false;
		}
		if(regex.test(fileName)){
			alert("?????? ????????? ????????? ????????? ??? ??? ????????????.");
			return false;
		}
		return true;
	}
	var uploadResult = $(".uploadResult ul");
	
	function showUploadedFile(uploadResultArr) {
		var str = "";
		
		$(uploadResultArr).each(function(i, obj) {
			
			if(!obj.image) {
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/"+obj.uuid + "_" +obj.fileName);
				
				var fileLink = fileCallPath.replace(new RegExp(/\\/g),"/");
				
				str += "<li><a href='/download?fileName="+fileCallPath+"'><img src='/resources/img/attach.png'>"
	    		   +obj.fileName+"</a>" + "<span data-file=\ '"+ fileCallPath+"\' data-type='file'> X </span>"
	    		   + "<div></li>"

			} else {
				//str += "<li>"+obj.fileName + "</li>";
				var fileCallPath = encodeURIComponent( obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
				
				var originPath = obj.uploadPath + "\\" + obj.uuid + "_" + obj.fileName;
				
				originPath = originPath.replace(new RegExp(/\\/g),"/");
				
				str += "<li><a href=\"javascript:showImage(\'" + originPath + "\')\">"+ "<img src='/display?fileName=" + fileCallPath +"'></a>"+"<span data-file=\'"+fileCallPath+"\' data-type='image'> X </span>" + "</li>";
			}
		});
		uploadResult.append(str);
	}
	
	
	
	var cloneObj = $(".uploadDiv").clone();
	
	
	$("#uploadBtn").on("click", function(e){
		
		var formData = new FormData();
		var inputFile = $("input[name='uploadFile']");
		var files = inputFile[0].files;
		
		console.log(files);
		
		//formData??? File Data ??????
		for(var i =0; i < files.length; i++) {
			
			if(!checkExtension(files[i].name, files[i].size) ) {
				return false;
			}
			
			formData.append("uploadFile", files[i]);
		}
		
		$.ajax({
			url: '/uploadAjaxAction',
			processData: false,
			contentType: false,
			data: formData,
			type: 'POST',
			dataType: 'json',
			success: function(result){
				console.log(result);
				
				showUploadedFile(result);
				
				$(".uploadDiv").html(cloneObj.html());
			}
		});

	});
	
	$(".uploadResult").on("click", "span", function(e){
		var targetFile = $(this).data("file");
		var type = $(this).data("type");
		console.log(targetFile);
		
		$.ajax({
			url : '/deleteFile',
			data : {fileName : targetFile, type:type},
			dataType : 'text',
			type : 'POST',
				success : function(result){
					alert(result);
				}
		}); 
	});
	
	$(".bigPictureWrapper").on("click", function(e){
		/* $(".bigPicture").animate({width:'0%', height: '0%'}, 1000); */
		
			$(this).hide();
		
	});
}); 
</script>

</body>
</html>