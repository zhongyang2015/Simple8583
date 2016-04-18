package sh_bcm.entity;



/**
 * 结算实体类
 * @author zhangk
 *
 */
public class Billing  extends Registered{

  private String terminalNo;//终端编号
  private String businessNo;//商户编号
  private String acceptedTime;//受理时间 hh:mm:ss
  private String acceptedDate;//交易日期yyyymmdd
  private String batch;//批次号
  private String billingData;//结算数据
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
public String getBatch() {
	return batch;
}
public void setBatch(String batch) {
	this.batch = batch;
}
public String getBillingData() {
	return billingData;
}
public void setBillingData(String billingData) {
	this.billingData = billingData;
}


}
