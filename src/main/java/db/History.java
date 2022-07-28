package db;

public class History {
	
	private int hisNum;	
	private String x;	
	private String y;	
	private String regDate;	
	
	public History() {}
	
	


	public History(int hisNum, String x, String y, String regDate) {
		super();
		this.hisNum = hisNum;
		this.x = x;
		this.y = y;
		this.regDate = regDate;
	}




	public int getHisNum() {
		return hisNum;
	}




	public void setHisNum(int hisNum) {
		this.hisNum = hisNum;
	}




	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}




	@Override
	public String toString() {
		return "History [hisNum=" + hisNum + ", x=" + x + ", y=" + y + ", regDate=" + regDate + "]";
	}

	
	
	
	

}
