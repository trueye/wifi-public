<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="service.WifiService"%>
<%@page import="java.util.List"%>
<%@page import="service.WifiService"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="db.Wifi"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="css/style.css">
<title>와이파이 정보 구하기</title>
</head>
<body>
<%
	String JDBC_URL = "jdbc:sqlite:contacts.db";
	String JDBC_DRIVER = "org.sqlite.JDBC";

	Connection conn = null;
	String driver = null;
	String url = null;

	PreparedStatement ps = null;
	%>
	<%
	System.out.println("hello");
	WifiService wifiAPI = new WifiService();
	wifiAPI.createTableWifi();
	wifiAPI.createTableHistory();
	//wifiAPI.deleteWifi();
	//wifiAPI.dropWifi();
	//wifiAPI.dropHistory();
	
	
	List<Wifi> wifiList = new ArrayList<>();
	
	String lat = request.getParameter("LAT");
	String lnt = request.getParameter("LNT");
			
	//System.out.println("LAT: " + lat);
	//System.out.println("LNT: " + lnt);		

	
	%>
	
	<%
	SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
	Calendar cal = Calendar.getInstance();
	String time = format.format(cal.getTime());
	%>

	<h1>와이파이 정보 구하기</h1>
	<div>
		<a href="wifi.jsp">홈</a> | <a href="history.jsp"> 위치 히스토리 목록</a> | <a
			href="load-wifi.jsp"> Open API 와이파이 정보 가져오기</a>
	</div>
	<br>

	<form class="frm">
		LAT: <input class="lat" type="text" name="LAT"> | LNT: <input
			class="lnt" type="text" name="LNT"> <input
			class="my-position" type="button" value="내 위치 가져오기"> <input
			type="button" value="근처 WIPI 정보 보기" onclick="filter()">
	</form>
	<table>
		<thead>
			<tr>
				<th>거리(Km)</th>
				<th>관리번호</th>
				<th>자치구</th>
				<th>와이파이명</th>
				<th>도로명주소</th>
				<th>상세주소</th>
				<th>설치위치(층)</th>
				<th>설치유형</th>
				<th>설치기관</th>
				<th>서비스구분</th>
				<th>망종류</th>
				<th>설치년도</th>
				<th>실내외구분</th>
				<th>WIFI접속환경</th>
				<th>X좌표</th>
				<th>Y좌표</th>
				<th>작업일자</th>
			</tr>
		</thead>
		<tbody>
			<tr>

				<%
				if (lat != null) {					
					wifiList = wifiAPI.list(Double.parseDouble(lat), Double.parseDouble(lnt));
					wifiAPI.addHistory(lat, lnt, time);
					for (Wifi wifi : wifiList) {
				%>
			
			<tr>
				<td><%=wifi.getDistance()%></td>
				<td><%=wifi.getX_SWIFI_MGR_NO()%></td>
				<td><%=wifi.getX_SWIFI_WRDOFC()%></td>
				<td><%=wifi.getX_SWIFI_MAIN_NM()%></td>
				<td><%=wifi.getX_SWIFI_ADRES1()%></td>
				<td><%=wifi.getX_SWIFI_ADRES2()%></td>
				<td><%=wifi.getX_SWIFI_INSTL_FLOOR()%></td>
				<td><%=wifi.getX_SWIFI_INSTL_TY()%></td>
				<td><%=wifi.getX_SWIFI_INSTL_MBY()%></td>
				<td><%=wifi.getX_SWIFI_SVC_SE()%></td>
				<td><%=wifi.getX_SWIFI_CMCWR()%></td>
				<td><%=wifi.getX_SWIFI_CNSTC_YEAR()%></td>
				<td><%=wifi.getX_SWIFI_INOUT_DOOR()%></td>
				<td><%=wifi.getX_SWIFI_REMARS3()%></td>
				<td><%=wifi.getLAT()%></td>
				<td><%=wifi.getLNT()%></td>
				<td><%=wifi.getWORK_DTTM()%></td>
			</tr>
			<%
			}
			%>
			<%
			} else {
			%>
			<tr>
				<td style="text-align: center;" colspan="17">위치 정보를 입력한 후에 조회해
					주세요.</td>

			</tr>

			<%
			}
			%>
		</tbody>
	</table>
	<script type="text/javascript">
		const findMyposition = () => {
			
			const lat = document.querySelector('.lat');
			const lnt = document.querySelector('.lnt');
			
			const success = (position) => {
				console.log(position)
				const latitude = position.coords.latitude;
				const longitude = position.coords.longitude;
				
				console.log(latitude + ' ' + longitude);
				lat.value = latitude;	
				lnt.value = longitude;
			}
			
			const error = () => {
				alert('위치 정보 가져오기 실패');
			}
			
			
			navigator.geolocation.getCurrentPosition(success, error);
		}
	
		document.querySelector('.my-position').addEventListener('click', findMyposition);
	
		function filter() {
			var frm = document.querySelector('.frm');
			var lat = document.querySelector('.lat');
			var lnt = document.querySelector('.lnt');
			
			if(lat.value.trim() == "") {
				alert("좌표를 입력해주세요.");
				return lat.focus();
			}
			if(lnt.value.trim() == "") {
				alert("좌표를 입력해주세요.");
				return lnt.focus();
			}	
			frm.submit();
		}
	
	</script>
</body>
</html>