package sh_bcm_card.entity;



/**
 * 返回对象实体类
 * @author zhangk
 *
 */
public class ReturnsObject {

   private String acceptedTime;// 受理时间 hh:mm:ss
   private String acceptedDate;//交易日期yyyymmdd
   private String serialNo;//终端硬件序列号
   private String terminalNo;//终端编号
   private String businessNo;//商户编号
   private String businessName;//商户名称
   private String posNo;//pos流水号
   private String batch;//批次号
   private String newMacKey;//新mac密钥
   private String workKey;//工作密钥
   private String primaryAccount;//主卡号
   private String returnCodes;//状态返回码
   private String money;//交易金额
   private String validity;//卡有效期
   private String clearDate;//主机清算日期
   private String billingMsg;//结算结果
   private String remark;//备注及反馈信息
   private String credentials;//持卡人证件号码
   private String conditionCode;//服务点条件码
   private String systemNumber;//系统参考号
   private String IDcard;//身份证号
   private String securityMsg;//安全控制信息
   private String backMoney;//返回金额    输入：产品编号（可选）
   private String operator;//操作员
   private String authorizeNum;//授权码
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
public String getSerialNo() {
	return serialNo;
}
public void setSerialNo(String serialNo) {
	this.serialNo = serialNo;
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
public String getNewMacKey() {
	return newMacKey;
}
public void setNewMacKey(String newMacKey) {
	this.newMacKey = newMacKey;
}

public String getWorkKey() {
	return workKey;
}
public void setWorkKey(String workKey) {
	this.workKey = workKey;
}
public String getPrimaryAccount() {
	return primaryAccount;
}
public void setPrimaryAccount(String primaryAccount) {
	this.primaryAccount = primaryAccount;
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
public String getClearDate() {
	return clearDate;
}
public void setClearDate(String clearDate) {
	this.clearDate = clearDate;
}
public String getBillingMsg() {
	return billingMsg;
}
public void setBillingMsg(String billingMsg) {
	this.billingMsg = billingMsg;
}
public String getRemark() {
	return remark;
}
public void setRemark(String remark) {
	this.remark = remark;
}
public String getReturnCodes() {
	return returnCodes;
}
public void setReturnCodes(String returnCodes) {
	this.returnCodes = returnCodes;
}
public String getBusinessName() {
	return businessName;
}
public void setBusinessName(String businessName) {
	this.businessName = businessName;
}
public String getCredentials() {
	return credentials;
}
public void setCredentials(String credentials) {
	this.credentials = credentials;
}
public String getPosNo() {
	return posNo;
}
public void setPosNo(String posNo) {
	this.posNo = posNo;
}
public String getConditionCode() {
	return conditionCode;
}
public void setConditionCode(String conditionCode) {
	this.conditionCode = conditionCode;
}
public String getSystemNumber() {
	return systemNumber;
}
public void setSystemNumber(String systemNumber) {
	this.systemNumber = systemNumber;
}
public String getIDcard() {
	return IDcard;
}
public void setIDcard(String iDcard) {
	IDcard = iDcard;
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
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}
public String getAuthorizeNum() {
	return authorizeNum;
}
public void setAuthorizeNum(String authorizeNum) {
	this.authorizeNum = authorizeNum;
}
   
   
   
}
