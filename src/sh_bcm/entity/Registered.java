package sh_bcm.entity;



/**
 * 注册实体类
 * @author zhangk
 *
 */
public class Registered {

   private String operatorId;//操作员编号
   private String mechanismNo;//受理方机构编号
   private String serialNo;//终端硬件序列号
public String getOperatorId() {
	return operatorId;
}
public void setOperatorId(String operatorId) {
	this.operatorId = operatorId;
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

   
}
