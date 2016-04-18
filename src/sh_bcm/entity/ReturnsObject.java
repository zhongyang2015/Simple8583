package sh_bcm.entity;



/**
 * 返回对象实体类
 * @author zhangk
 *
 */
public class ReturnsObject {

   private String acceptedTime;// 受理时间 hh:mm:ss
   private String acceptedDate;//交易日期yyyymmdd
   private String mechanismNo;//受理方机构编号
   private String serialNo;//终端硬件序列号
   private String terminalNo;//终端编号
   private String businessNo;//商户编号
   private String terminalNum;//终端流水号
   private String batch;//批次号
   private String newMacKey;//新mac密钥
   private String masterKey;//新主密钥
   private String billingData;//结算数据
   private String primaryAccount;//主卡号
   private String returnCodes;//状态返回码
   private String money;//交易金额
   private String validity;//卡有效期
   private String clearDate;//主机清算日期
   private String accountType;//账户类型
   private String ticketData;//礼券数据
   private String billingMsg;//结算结果
   private String remark;//备注及反馈信息
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
public String getMechanismNo() {
	return mechanismNo;
}
public void setMechanismNo(String mechanismNo) {
	this.mechanismNo = mechanismNo;
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
public String getTerminalNum() {
	return terminalNum;
}
public void setTerminalNum(String terminalNum) {
	this.terminalNum = terminalNum;
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
public String getMasterKey() {
	return masterKey;
}
public void setMasterKey(String masterKey) {
	this.masterKey = masterKey;
}
public String getBillingData() {
	return billingData;
}
public void setBillingData(String billingData) {
	this.billingData = billingData;
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
public String getAccountType() {
	return accountType;
}
public void setAccountType(String accountType) {
	this.accountType = accountType;
}
public String getTicketData() {
	return ticketData;
}
public void setTicketData(String ticketData) {
	this.ticketData = ticketData;
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
   
   
   
}
