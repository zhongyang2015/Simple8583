package simple_8583.entity;



/**
 * 签到实体类
 * @author zhangk
 *
 */
public class Sign  extends Registered{

  private String terminalNo;//终端编号
  private String businessNo;//商户编号
  private String version;//上送版本信息
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
public String getVersion() {
	return version;
}
public void setVersion(String version) {
	this.version = version;
}

}
