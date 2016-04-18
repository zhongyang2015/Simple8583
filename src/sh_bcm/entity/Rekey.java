package sh_bcm.entity;



/**
 * 密钥重置实体类
 * @author zhangk
 *
 */
public class Rekey  extends Registered{

  private String terminalNo;//终端编号
  private String businessNo;//商户编号
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
  
}
