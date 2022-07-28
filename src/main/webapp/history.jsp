<%@page import="java.util.ArrayList"%>
<%@page import="db.History"%>
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
<title>와이파이 정보 구하기</title>
</head>
<body>
	<%
	WifiService wifiAPI = new WifiService();
	List<History> historyList = new ArrayList<>();
	historyList = wifiAPI.listHistory();
	int num = 3;
	%>
	<h1>와이파이 정보 구하기</h1>
	<div>
		<a href="wifi.jsp">홈</a> | <a href="history.jsp"> 위치 히스토리 목록</a> | <a
			href="load-wifi.jsp"> Open API 와이파이 정보 가져오기</a>
	</div>
	<br>
	<table>
		<thead>
			<tr>
				<th>ID</th>
				<th>X좌표</th>
				<th>Y좌표</th>
				<th>조회일자</th>
				<th>비고</th>
			</tr>
		</thead>
		<tbody>
			<tr>

				<%
				for (History history : historyList) {
				%>
			
			<tr>
				<td><%=history.getHisNum()%></td>
				<td><%=history.getX()%></td>
				<td><%=history.getY()%></td>
				<td><%=history.getRegDate()%></td>
				<td><form name="frm" method="post" action="delete.jsp">
						<input type="submit" value="삭제">
						<input type="hidden" name=HIS_NUM value="<%=history.getHisNum()%>">
					</form></td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
	<script type="text/javascript">
		function deletehistory() {
			if (confirm("삭제하시겠습니까?")) {
				location.href = "delete.jsp";
			}
		}
	</script>


</body>
</html>