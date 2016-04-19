package sh_bcm_card.entity;



/**
 * 基础实体类
 * @author zhangk
 *
 */
public class BaseObject {
  private String posNo;//pos流水号	
  private String acceptedTime;//交易时间HHMMSS(时分秒)
  private String acceptedDatr;//交易日期MMDD(月日)
  private String conditionCode;//条件码
  private String terminalNo;//终端编号
  private String businessNo;//商户编号
  private String operator;//操作员
public String getPosNo() {
	return posNo;
}
public void setPosNo(String posNo) {
	this.posNo = posNo;
}
public String getAcceptedTime() {
	return acceptedTime;
}
public void setAcceptedTime(String acceptedTime) {
	this.acceptedTime = acceptedTime;
}
public String getAcceptedDatr() {
	return acceptedDatr;
}
public void setAcceptedDatr(String acceptedDatr) {
	this.acceptedDatr = acceptedDatr;
}
public String getConditionCode() {
	return conditionCode;
}
public void setConditionCode(String conditionCode) {
	this.conditionCode = conditionCode;
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
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}

}
