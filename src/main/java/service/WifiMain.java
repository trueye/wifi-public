package service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.json.simple.parser.ParseException;

public class WifiMain {
	public static void main(String[] args) throws SQLException, IOException, ParseException, ClassNotFoundException {

		WifiService wifiAPI = new WifiService();

//		wifiAPI.createTableWifi(); // 테이블 생성
//		wifiAPI.createTableHistory();
//		wifiAPI.load_wifi(); // DB 저장
//		wifiAPI.list();	// 리스트 출력
//		wifiAPI.deleteWifi(); // 테이블 비우기
//		wifiAPI.deleteHistory();
//		wifiAPI.dropWifi();	// 테이블 삭제
//		wifiAPI.dropHistory();


	}
}