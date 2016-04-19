package sh_bcm_card.entity;



/**
 * 查询实体类
 * @author zhangk
 *
 */
public class Inquire extends BaseObject{

	private String IDcard;//身份证
	private String money;//金额
	private String validity;//有效期
	private String input;//输入方式
	private String twoTracks;//二磁道内容
	private String threeTracks;//三磁道内容
	private String currencyCode;//货币代码
	private String passWord;//个人密码密文
	private String securityMsg;//安全控制信息
	private String backMoney;//返回金额    输入：产品编号（可选）
	private String credentials;//持卡人证件号码
	private String billNum;//票据号
	public String getIDcard() {
		return IDcard;
	}
	public void setIDcard(String iDcard) {
		IDcard = iDcard;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getTwoTracks() {
		return twoTracks;
	}
	public void setTwoTracks(String twoTracks) {
		this.twoTracks = twoTracks;
	}
	public String getThreeTracks() {
		return threeTracks;
	}
	public void setThreeTracks(String threeTracks) {
		this.threeTracks = threeTracks;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getSecurityMsg() {
		return securityMsg;
	}
	public void setSecurityMsg(String securityMsg) {
		this.securityMsg = securityMsg;
	}
	public String getBackMoney() {
		return backMoney;
	}
	public void setBackMoney(String backMoney) {
		this.backMoney = backMoney;
	}
	public String getCredentials() {
		return credentials;
	}
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	public String getBillNum() {
		return billNum;
	}
	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}
	
	
}
