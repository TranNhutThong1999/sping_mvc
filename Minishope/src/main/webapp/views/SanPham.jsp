<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file ="include/Taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
		<jsp:include page="include/Header.jsp"></jsp:include>
		<link rel="stylesheet" href='<c:url value="/resources/css/SanPham.css" />'>
		<link rel="stylesheet" href='<c:url value="/resources/css/DanhMuc.css" />'>
		<link rel="stylesheet" href='<c:url value="/resources/css/OneSanPham.css" />'>
		<link rel="stylesheet" href='<c:url value="/resources/css/Footer.css" />'>
		
	<title>Sản Phẩm</title>
</head>
<body>
	<div id="main" class="container-fluid background" data-soLuongSP="${soLuongSP}">
		<jsp:include page="include/MenuMain.jsp"></jsp:include>
		<jsp:include page="include/Breadcrumb.jsp"></jsp:include>
		<div class="row" style="margin:0px 70px;">
			<div class="col-md-12">
				<div class="row tieude">
					<div class="Sort" >
					<div class="sort-money" data-sort="desc" ><i id="asc" class="fa fa-sort-asc "   aria-hidden="true"></i></div>
					<div class="sort-money" data-sort="asc" ><i  id="desc" class="fa fa-sort-desc "   aria-hidden="true"></i></div>
					</div>
					<h1 id="DanhMuc" data-search="${keyWords}"
						data-idDanhMuc="${danhMuc.getIdDanhMuc()}">${danhMuc.getTenDanhMuc()}</h1>
				</div>
				<div id="change" class="change">
					<div class="row oneSanpHam" id="test">
						<tbody>
						<jsp:include page="include/OneSanPham.jsp"></jsp:include>
						
						</tbody>
					</div>
				</div>
				<c:if test="${not empty messenger_Search_Error }">
					<div class="text-control text-center" style="text-algin:center;"><h3>${messenger_Search_Error}</h3></div>	
				</c:if>
				<div class="row phan-trang">
					<nav aria-label="Page navigation example">
						<ul class="pagination">
							<li class="page-item"><a class="page-link" href="#">Previous</a></li>
							<li id="paging" class="page-item active"><a class="page-link" href="#">1</a></li>
							<c:forEach var="i" begin="2" end="${numberPagination}">
								<li id="paging" class="page-item "><a class="page-link" href="#">${i}</a></li>
							</c:forEach>
							<li class="page-item"><a class="page-link" href="#">Next</a></li>
						</ul>
					</nav>
				</div>
			</div>



		</div>

	</div>
		
	<jsp:include page="include/Footer.jsp"></jsp:include>
	<jsp:include page="include/Scrip.jsp"></jsp:include>
	<script type="text/javascript">
	function FBlogout(){
			FB.logout(function(response) {
		  // user is now logged out
		});
	}
	</script>
<script type="text/javascript">
		$(document).ready(function() {
			var typeSort="";
			var soLuongSP=$("#main").attr("data-soLuongSP");
			var idDanhMuc= $("#DanhMuc").attr("data-idDanhMuc");
			var content;
			var trangBatDau=0;
			var keyWords=$("#DanhMuc").attr("data-search");
			$(".sort-money").click(function(){
				typeSort =$(this).attr("data-sort");
				if(idDanhMuc===""){
					if(keyWords===""){
						content={
								trangBatDau:trangBatDau,
								soLuongSP:soLuongSP,
								typeSort:typeSort,
								sortBy:"giaTien"}
					}else{
						content={
								keyWords:keyWords,
								trangBatDau:trangBatDau,
								soLuongSP:soLuongSP,
								typeSort:typeSort,
								sortBy:"giaTien"
								}
					}
				}else{
					content={
							idDanhMuc : idDanhMuc,
							trangBatDau:trangBatDau,
							soLuongSP:soLuongSP,
							typeSort:typeSort,
							sortBy:"giaTien"
					}
				}
				$.ajax({
					url:"/Minishope/Api/SanPham",
					type:"get",
					data:content,
					success:function(value){
						$('html, body').stop();
						var item= $('#test');
								var s = value.map(function(x,index){
									var y = x.giaTien+"";
									let result;
									if(index!==8 & index!==9){
										 result="<div class='col-md-3 animate__animated  animate__zoomIn '> <a href='../ChiTiet/"+x.idSanPham+"'> <div class='card' style='width: 18rem;'>	<img class='card-img-top' src='/Minishope/resources/Images/"+x.hinhSanPham[0].location+"' alt='Card image cap'> <div class='card-body'><p class='card-text'> $"+x.giaTien+"</p> ";
										result+=x.hinhSanPham.map(function(z){
											return "<img class='card-img-top item-image mr-1' src='/Minishope/resources/Images/"+z.location+"'alt='Card image cap'> ";
										})
										result+="</div></a></div>"
									}else{
										 result="<div class='col-md-6 animate__animated  animate__zoomIn' style='margin-bottom:18px;'> <a href='../ChiTiet/"+x.idSanPham+"'> <div class='card' style='width: 18rem;'>	<img class='card-img-top' src='/Minishope/resources/Images/"+x.hinhSanPham[0].location+"' alt='Card image cap'> <div class='card-body'><p class='card-text'> $"+x.giaTien+"</p> ";
										result+=x.hinhSanPham.map(function(z){
											return "<img class='card-img-top item-image mr-1' src='/Minishope/resources/Images/"+z.location+"'alt='Card image cap'> ";
										})
										result+="</div></a></div>"
									}
								
									return result ;
								})
								item.html(s);
							
					},
				error:function(value){
					alert("errr");
				}
				})
			})
			
			 $("body").on("click", ".page-item", function() {
			var soTrangReal = $(this).text();
			 trangBatDau = (soTrangReal - 1) * soLuongSP
			$(this).parent().find(".page-item").removeClass("active");
			$(this).addClass("active");
			
			if(idDanhMuc==""){
				if(keyWords===""){
					if(typeSort==="" & soLuongSP===""){
						content={
								trangBatDau : trangBatDau,
								soLuongSP:soLuongSP
						}
					}else{
						content={
						trangBatDau : trangBatDau,
						soLuongSP:soLuongSP,
						typeSort:typeSort,
						sortBy:"giaTien"
						}
					}
				}else{
					if(typeSort==="" & soLuongSP===""){
						content={
								keyWords:keyWords,
								trangBatDau : trangBatDau,
								soLuongSP:soLuongSP
						}
					}else{
						content={
						keyWords:keyWords,
						trangBatDau :trangBatDau,
						soLuongSP:soLuongSP,
						typeSort:typeSort,
						sortBy:"giaTien"
						}
					}

				}
			
			}else{
				if(typeSort==="" & soLuongSP===""){
					content={
							idDanhMuc : idDanhMuc,
							trangBatDau : trangBatDau,
							soLuongSP:soLuongSP}
					
					}else{
				content={
						idDanhMuc : idDanhMuc,
						trangBatDau : trangBatDau,
						soLuongSP:soLuongSP,
						typeSort:typeSort,
						sortBy:"giaTien"
					}
				}
			}
			$.ajax({
				url : "/Minishope/Api/SanPham",
				type:"get",
				data :content,
				success : function(value) {
				 var arr;
				var item= $('#test');
						var s = value.map(function(x,index){
							let y = x.giaTien+"";
							console.log(x.hinhSanPham)
							let f = "$"+y.split( /(?=(?:\d{3})+(?:\.|$))/g ).join( "," );
							let result;
							if(index!==8 & index!==9){
								 result="<div class='col-md-3 animate__animated  animate__zoomIn '> <a href='../ChiTiet/"+x.idSanPham+"'> <div class='card' style='width: 18rem;'>	<img class='card-img-top' src='/Minishope/resources/Images/"+x.hinhSanPham[0].location+"' alt='Card image cap'> <div class='card-body'><p class='card-text'> $"+x.giaTien+"</p> ";
								result+=x.hinhSanPham.map(function(z){
									return "<img class='card-img-top item-image mr-1' src='/Minishope/resources/Images/"+z.location+"'alt='Card image cap'> ";
								})
								result+="</div></a></div>"
							}else{
								 result="<div class='col-md-6 animate__animated  animate__zoomIn' style='margin-bottom:18px;'> <a href='../ChiTiet/"+x.idSanPham+"'> <div class='card' style='width: 18rem;'>	<img class='card-img-top' src='/Minishope/resources/Images/"+x.hinhSanPham[0].location+"' alt='Card image cap'> <div class='card-body'><p class='card-text'> $"+x.giaTien+"</p> ";
								result+=x.hinhSanPham.map(function(z){
									return "<img class='card-img-top item-image mr-1' src='/Minishope/resources/Images/"+z.location+"'alt='Card image cap'> ";
								})
								result+="</div></a></div>"
							}
						
							return result ;
						})
					
						item.html(s);
					
						console.log(value)
				},
				error : function(value) {
					alert("orrrro")
				},
			})
			//return false;
		}) 
		
		
		});
	</script>
</body>
</html>