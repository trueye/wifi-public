<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="service.WifiService"%>
<%@ page import="db.Wifi"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="css/style.css">
<style type="text/css">
div {
	font-size: 20pt;
	font-weight: bolder;
	text-align: center;
}

p {
	text-align: center;
}
</style>
<title>와이파이 정보 구하기</title>
</head>
<body>
	<%
	WifiService wifiAPI = new WifiService();
	List<Wifi> wifiList = wifiAPI.list();
	System.out.println(wifiList.size());
	
	%>
	<%if (wifiList.size() != 0) { %>
	<div> 이미 정보를 저장하였습니다.</div>
	<p><a href="wifi.jsp">홈 으로 가기</a>
	
	<%} else { 
		wifiAPI.load_wifi();
		wifiList = wifiAPI.list();
	%>
	<div><%=wifiList.size()%>개의 WIFI 정보를 정상적으로 저장하였습니다.</div>
	<p><a href="wifi.jsp">홈 으로 가기</a>
	<%} %>
</body>
</html>