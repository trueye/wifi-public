<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.Connection"%>
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
	Class.forName(JDBC_DRIVER);
	conn = DriverManager.getConnection(JDBC_URL);	
	
	String num = request.getParameter("HIS_NUM");
	System.out.println("num " + num);
		
	String sql = "delete from history_table where HIS_NUM = ?";

	ps = conn.prepareStatement(sql);
	ps.setString(1, num);
	int result = ps.executeUpdate();

	ps.close();
	conn.close();
	%>
	<script>
		alert("삭제 완료!");
		location.href = 'history.jsp';
	</script>

</body>
</html>