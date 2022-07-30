package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.History;
import db.Wifi;

public class WifiService {

	private static final String JDBC_URL = "jdbc:sqlite:contacts.db";
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";

	private Connection conn = null;
	private String driver = null;
	private String url = null;

	private PreparedStatement ps = null;
	private ResultSet rs = null;

	public WifiService() {
		this(JDBC_URL);
	}

	public WifiService(String url) {
		this.driver = JDBC_DRIVER;
		this.url = url;
	}

	// SQLite 연결
	public Connection connection() {

		try {
			Class.forName(this.driver);
			this.conn = DriverManager.getConnection(this.url);

			System.out.println("연결 완료");

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return this.conn;

	}

	// SQLite 연결 해제
	public void closeConnection() {
		try {
			if (this.ps != null) {
				this.ps.close();
			}
			if (this.conn != null) {
				this.conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.conn = null;

			System.out.println("연결 해제");
		}
	}

	// 와이파이 테이블 생성
	public void createTableWifi() {
		
		connection();

		String sql = "create table wifi_table " + " (MGR_NO varchar(30)," + " WRDOFC varchar(30),"
				+ " MAIN_NM varchar(30)," + " ADRES1 varchar(30)," + " ADRES2 varchar(30),"
				+ " INSTL_FLOOR varchar(30)," + " INSTL_TY varchar(30)," + " INSTL_MBY varchar(30),"
				+ " SVC_SE varchar(30)," + " CMCWR varchar(30)," + " CNSTC_YEAR date," + " INOUT_DOOR varchar(30),"
				+ " REMARS3 varchar(30)," + " LAT real," + " LNT real," + " WORK_DTTM date" + " )";

		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 생성 완료");
		
		closeConnection();

	}

	// 히스토리 테이블 생성
	public void createTableHistory() {
	
		connection();

		String sql = "create table history_table " + " (HIS_NUM INTEGER primary key autoincrement , LAT rear,"
				+ " LNT rear," + " REG_DATE varchar(30)" + " )";

		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 생성 완료");
		
		closeConnection();

	}

	// 테스트용
	public void load_wifi2() throws SQLException, IOException, ParseException, ClassNotFoundException {

		connection();

		long start = 17780;
		long end = 17999;

		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
		urlBuilder.append("/"
				+ URLEncoder.encode("5259565847646c7a313036524b64554f", "UTF-8")); /* 인증키 (sample사용시에는 호출시 제한됩니다.) */
		urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /* 요청파일타입 (xml,xmlf,xls,json) */
		urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8")); /* 서비스명 (대소문자 구분 필수입니다.) */
		urlBuilder.append("/");
		urlBuilder.append(start);
		urlBuilder.append("/");
		urlBuilder.append(end);

		URL url = new URL(urlBuilder.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + connection.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다. */
		BufferedReader rd;

		// 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
		if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		connection.disconnect();

		long list_total_count;

		JSONParser parser = new JSONParser();
		JSONObject jobj = (JSONObject) parser.parse(sb.toString());

		JSONObject tbPublicWifiInfo = (JSONObject) jobj.get("TbPublicWifiInfo");
		list_total_count = (long) tbPublicWifiInfo.get("list_total_count");

		System.out.println(list_total_count);

		JSONArray row = (JSONArray) tbPublicWifiInfo.get("row");

		String sql = "insert into wifi_table values " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		ps = conn.prepareStatement(sql);

		for (int i = 0; i < row.size(); i++) {
			JSONObject row_no = (JSONObject) row.get(i);
			ps.setString(1, (String) row_no.get("X_SWIFI_MGR_NO"));
			ps.setString(2, (String) row_no.get("X_SWIFI_WRDOFC"));
			ps.setString(3, (String) row_no.get("X_SWIFI_MAIN_NM"));
			ps.setString(4, (String) row_no.get("X_SWIFI_ADRES1"));
			ps.setString(5, (String) row_no.get("X_SWIFI_ADRES2"));
			ps.setString(6, (String) row_no.get("X_SWIFI_INSTL_FLOOR"));
			ps.setString(7, (String) row_no.get("X_SWIFI_INSTL_TY"));
			ps.setString(8, (String) row_no.get("X_SWIFI_INSTL_MBY"));
			ps.setString(9, (String) row_no.get("X_SWIFI_SVC_SE"));
			ps.setString(10, (String) row_no.get("X_SWIFI_CMCWR"));
			ps.setString(11, (String) row_no.get("X_SWIFI_CNSTC_YEAR"));
			ps.setString(12, (String) row_no.get("X_SWIFI_INOUT_DOOR"));
			ps.setString(13, (String) row_no.get("X_SWIFI_REMARS3"));
			ps.setString(14, (String) row_no.get("LAT"));
			ps.setString(15, (String) row_no.get("LNT"));
			ps.setString(16, (String) row_no.get("WORK_DTTM"));

			ps.executeUpdate();

			System.out.println("X_SWIFI_MGR_NO " + row_no.get("X_SWIFI_MGR_NO"));
		}

		closeConnection();

	}

	public void load_wifi() throws SQLException, IOException, ParseException, ClassNotFoundException {
//		String jdbcUrl = "jdbc:sqlite:contacts.db";
//		Class.forName("org.sqlite.JDBC");
//		Connection connection = DriverManager.getConnection(jdbcUrl);

		long loadStart = System.currentTimeMillis();

		connection();
		conn.setAutoCommit(false);

		long start = 1;
		long end = 1000;
		boolean isEnd = true;
		while (isEnd) {

			long apiStart = System.currentTimeMillis(); // 코드 실행 전에 시간 받아오기
			StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
			urlBuilder.append("/" + URLEncoder.encode("5259565847646c7a313036524b64554f",
					"UTF-8")); /* 인증키 (sample사용시에는 호출시 제한됩니다.) */
			urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /* 요청파일타입 (xml,xmlf,xls,json) */
			urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8")); /* 서비스명 (대소문자 구분 필수입니다.) */
			urlBuilder.append("/");
			urlBuilder.append(start);
			urlBuilder.append("/");
			urlBuilder.append(end);
//		urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); /* 요청시작위치 (sample인증키 사용시 5이내 숫자) */
//		urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); /* 요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨) */

			URL url = new URL(urlBuilder.toString());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + connection.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다. */
			BufferedReader rd;

			// 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
			if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			connection.disconnect();

			long apiEnd = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
			long apiTime = apiEnd - apiStart; // 두 시간에 차 계산
			System.out.println("API 호출 : " + apiTime + " ms");

//			connection();

			long parseStart = System.currentTimeMillis(); // 코드 실행 전에 시간 받아오기

			long list_total_count;

			JSONParser parser = new JSONParser();
			JSONObject jobj = (JSONObject) parser.parse(sb.toString());

			JSONObject tbPublicWifiInfo = (JSONObject) jobj.get("TbPublicWifiInfo");
			list_total_count = (long) tbPublicWifiInfo.get("list_total_count");

			JSONArray row = (JSONArray) tbPublicWifiInfo.get("row");

			long parseEnd = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
			long parseTime = parseEnd - parseStart; // 두 시간에 차 계산
			System.out.println("파싱 : " + parseTime + " ms");

			String sql = "insert into wifi_table values " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ps = conn.prepareStatement(sql);

			long insertStart = System.currentTimeMillis(); // 코드 실행 전에 시간 받아오기

			// 실험할 코드 추가

			for (int i = 0; i < row.size(); i++) {
				JSONObject row_no = (JSONObject) row.get(i);
				ps.setString(1, (String) row_no.get("X_SWIFI_MGR_NO"));
				ps.setString(2, (String) row_no.get("X_SWIFI_WRDOFC"));
				ps.setString(3, (String) row_no.get("X_SWIFI_MAIN_NM"));
				ps.setString(4, (String) row_no.get("X_SWIFI_ADRES1"));
				ps.setString(5, (String) row_no.get("X_SWIFI_ADRES2"));
				ps.setString(6, (String) row_no.get("X_SWIFI_INSTL_FLOOR"));
				ps.setString(7, (String) row_no.get("X_SWIFI_INSTL_TY"));
				ps.setString(8, (String) row_no.get("X_SWIFI_INSTL_MBY"));
				ps.setString(9, (String) row_no.get("X_SWIFI_SVC_SE"));
				ps.setString(10, (String) row_no.get("X_SWIFI_CMCWR"));
				ps.setString(11, (String) row_no.get("X_SWIFI_CNSTC_YEAR"));
				ps.setString(12, (String) row_no.get("X_SWIFI_INOUT_DOOR"));
				ps.setString(13, (String) row_no.get("X_SWIFI_REMARS3"));
				ps.setString(14, (String) row_no.get("LAT"));
				ps.setString(15, (String) row_no.get("LNT"));
				ps.setString(16, (String) row_no.get("WORK_DTTM"));

				ps.addBatch();
				ps.clearParameters();

//				System.out.println("X_SWIFI_MGR_NO " + row_no.get("X_SWIFI_MGR_NO"));
			}
//			closeConnection();

			long insertEnd = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
			long insertTime = insertEnd - insertStart; // 두 시간에 차 계산
			System.out.println("디비 저장 : " + insertTime + " ms");

			if (end >= list_total_count) {
				end = list_total_count;
				isEnd = false;
			}

			ps.executeBatch();

			ps.clearBatch();

			conn.commit();

			start = start + 1000;
			end = end + 1000;

		}
//		ps.executeBatch();
//		conn.commit();

		closeConnection();

		long loadEnd = System.currentTimeMillis();
		long loadTime = loadEnd - loadStart;
		System.out.println("총로드타임 : " + loadTime + " ms");

	}

	// 테스트용
	public void addHistory(String x, String y, String regDate) {

		connection();

		String sql = "insert into history_table (LAT, LNT, REG_DATE) values " + "(?, ?, ?)";

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, x);
			ps.setString(2, y);
			ps.setString(3, regDate);

			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println(x);
		//System.out.println(y);
		//System.out.println(regDate);

		closeConnection();

	}

	// 리스트 출력하기
	public List<Wifi> list() throws SQLException, ClassNotFoundException {

		connection();
		List<Wifi> wifiList = new ArrayList<>();

		String sql = "select rowid, * from wifi_table ";
		Statement statement = conn.createStatement();

		rs = statement.executeQuery(sql);

		while (rs.next()) {
			int rowId = rs.getInt("rowid");
			String mgrNo = rs.getString("MGR_NO");
			String wrdofc = rs.getString("WRDOFC");
			String mainNm = rs.getString("MAIN_NM");
			String adres1 = rs.getString("ADRES1");
			String adres2 = rs.getString("ADRES2");
			String floor = rs.getString("INSTL_FLOOR");
			String ty = rs.getString("INSTL_TY");
			String mby = rs.getString("INSTL_MBY");
			String svcSe = rs.getString("SVC_SE");
			String cmcwr = rs.getString("CMCWR");
			String year = rs.getString("CNSTC_YEAR");
			String inoutDoor = rs.getString("INOUT_DOOR");
			String remars3 = rs.getString("REMARS3");
			double x = rs.getDouble("LAT");
			double y = rs.getDouble("LNT");
			Date workDate = rs.getDate("WORK_DTTM");

			Wifi wifi = new Wifi();
			wifi.setX_SWIFI_MGR_NO(mgrNo);
			wifi.setX_SWIFI_WRDOFC(wrdofc);
			wifi.setX_SWIFI_MAIN_NM(mainNm);
			wifi.setX_SWIFI_ADRES1(adres1);
			wifi.setX_SWIFI_ADRES2(adres2);
			wifi.setX_SWIFI_INSTL_FLOOR(floor);
			wifi.setX_SWIFI_INSTL_TY(ty);
			wifi.setX_SWIFI_INSTL_MBY(mby);
			wifi.setX_SWIFI_SVC_SE(svcSe);
			wifi.setX_SWIFI_CMCWR(cmcwr);
			wifi.setX_SWIFI_CNSTC_YEAR(year);
			wifi.setX_SWIFI_INOUT_DOOR(inoutDoor);
			wifi.setX_SWIFI_REMARS3(remars3);
			wifi.setLAT(x);
			wifi.setLNT(y);
			wifi.setWORK_DTTM(workDate);

			wifiList.add(wifi);

//			System.out.println(rowId + " | " + mgrNo + " | " + wrdofc + " | " + mainNm + " | " + adres1 + " | " + adres2
//					+ " | " + floor + " | " + ty + " | " + mby + " | " + svcSe + " | " + cmcwr + " | " + year + " | "
//					+ inoutDoor + " | " + remars3 + " | " + x + " | " + y + " | " + workDate);
		}

		closeConnection();
		System.out.println("size: " + wifiList.size());
		return wifiList;
	}

	public List<History> listHistory() throws SQLException, ClassNotFoundException {

		connection();
		List<History> historyList = new ArrayList<>();

		String sql = "select * from history_table order by HIS_NUM desc ";
		Statement statement = conn.createStatement();

		rs = statement.executeQuery(sql);

		while (rs.next()) {
			int hisNum = rs.getInt("HIS_NUM");
			String x = rs.getString("LAT");
			String y = rs.getString("LNT");
			String regDate = rs.getString("REG_DATE");

			History history = new History();
			history.setHisNum(hisNum);
			history.setX(x);
			history.setY(y);
			history.setRegDate(regDate);

			historyList.add(history);

			System.out.println(hisNum + " | " + x + " | " + y + " | " + regDate);

		}

		closeConnection();
		return historyList;
	}

	// 좌표 받아서 거리 출력하기
	public List<Wifi> list(double lat, double lnt) throws SQLException, ClassNotFoundException {

		connection();
		List<Wifi> wifiList = new ArrayList<>();

		String sql = "select rowid, * from wifi_table order by rowid limit 0, 20";
		Statement statement = conn.createStatement();

		rs = statement.executeQuery(sql);

		while (rs.next()) {
			double x = rs.getDouble("LAT");
			double y = rs.getDouble("LNT");
			double distance = Math.round(Math.sqrt(Math.pow((x - lnt), 2) + Math.pow((y - lat), 2)) * 10000) / 10000.0;
			String mgrNo = rs.getString("MGR_NO");
			String wrdofc = rs.getString("WRDOFC");
			String mainNm = rs.getString("MAIN_NM");
			String adres1 = rs.getString("ADRES1");
			String adres2 = rs.getString("ADRES2");
			String floor = rs.getString("INSTL_FLOOR");
			String ty = rs.getString("INSTL_TY");
			String mby = rs.getString("INSTL_MBY");
			String svcSe = rs.getString("SVC_SE");
			String cmcwr = rs.getString("CMCWR");
			String year = rs.getString("CNSTC_YEAR");
			String inoutDoor = rs.getString("INOUT_DOOR");
			String remars3 = rs.getString("REMARS3");

			Date workDate = rs.getDate("WORK_DTTM");

			Wifi wifi = new Wifi();
			wifi.setDistance(distance);
			wifi.setX_SWIFI_MGR_NO(mgrNo);
			wifi.setX_SWIFI_MGR_NO(mgrNo);
			wifi.setX_SWIFI_WRDOFC(wrdofc);
			wifi.setX_SWIFI_MAIN_NM(mainNm);
			wifi.setX_SWIFI_ADRES1(adres1);
			wifi.setX_SWIFI_ADRES2(adres2);
			wifi.setX_SWIFI_INSTL_FLOOR(floor);
			wifi.setX_SWIFI_INSTL_TY(ty);
			wifi.setX_SWIFI_INSTL_MBY(mby);
			wifi.setX_SWIFI_SVC_SE(svcSe);
			wifi.setX_SWIFI_CMCWR(cmcwr);
			wifi.setX_SWIFI_CNSTC_YEAR(year);
			wifi.setX_SWIFI_INOUT_DOOR(inoutDoor);
			wifi.setX_SWIFI_REMARS3(remars3);
			wifi.setLAT(x);
			wifi.setLNT(y);
			wifi.setWORK_DTTM(workDate);

			wifiList.add(wifi);

			System.out.println(distance + " | " + mgrNo + " | " + wrdofc + " | " + mainNm + " | " + adres1 + " | "
					+ adres2 + " | " + floor + " | " + ty + " | " + mby + " | " + svcSe + " | " + cmcwr + " | " + year
					+ " | " + inoutDoor + " | " + remars3 + " | " + x + " | " + y + " | " + workDate);
		}

		closeConnection();

		return wifiList;

	}

	// 와이파이 테이블 비우기
	public void deleteWifi() {

		connection();

		String sql = "delete from wifi_table";
		Statement statement;

		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 비우기 완료");

		closeConnection();

	}

	// 와이파이 테이블 삭제하기
	public void dropWifi() {

		connection();

		String sql = "drop table wifi_table";
		Statement statement;

		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 비우기 완료");

		closeConnection();

	}

	// 히스토리 테이블 비우기
	public void deleteHistory() {

		connection();

		String sql = "delete from history_table";
		Statement statement;

		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 비우기 완료");

		closeConnection();

	}

	// 히스토리 테이블 삭제하기
	public void dropHistory() {

		connection();

		String sql = "drop table history_table";
		Statement statement;

		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("테이블 비우기 완료");

		closeConnection();

	}

}