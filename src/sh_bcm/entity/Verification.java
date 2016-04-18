package sh_bcm.entity;



/**
 * 兑换实体类
 * @author zhangk
 *
 */
public class Verification  extends Registered{
  private String money;//兑换金额
  private String terminalNo;//终端编号
  private String businessNo;//商户编号
  private String batch;//批次号
  private String couponCode;//券码
  private String terminalNum;//终端流水号
  private String acceptedTime;//受理时间 hh:mm:ss
  private String acceptedDate;//交易日期yyyymmdd
  private String PIN;//联机PIN  16个F表示
public String getMoney() {
	return money;
}
public void setMoney(String money) {
	this.money = money;
}
public String getTerminalNo() {
	return terminalNo;
}
public void setTerminalNo(String terminalNo) {
	this.terminalNo = terminalNo;
}
public String getBusinessNo() {
	return businessNo;
}
public void setBusinessNo(String businessNo) {
	this.businessNo = businessNo;
}
public String getBatch() {
	return batch;
}
public void setBatch(String batch) {
	this.batch = batch;
}
public String getCouponCode() {
	return couponCode;
}
public void setCouponCode(String couponCode) {
	this.couponCode = couponCode;
}
public String getTerminalNum() {
	return terminalNum;
}
public void setTerminalNum(String terminalNum) {
	this.terminalNum = terminalNum;
}
public String getAcceptedTime() {
	return acceptedTime;
}
public void setAcceptedTime(String acceptedTime) {
	this.acceptedTime = acceptedTime;
}
public String getAcceptedDate() {
	return acceptedDate;
}
public void setAcceptedDate(String acceptedDate) {
	this.acceptedDate = acceptedDate;
}
public String getPIN() {
	return PIN;
}
public void setPIN(String pIN) {
	PIN = pIN;
}
  
  
  

}
