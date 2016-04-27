package simple_8583.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import simple_8583.SH_BCM_BusinessApi;
import simple_8583.entity.Billing;
import simple_8583.entity.Registered;
import simple_8583.entity.Rekey;
import simple_8583.entity.Sign;
import simple_8583.entity.Verification;
import simple_8583.key.SimpleConstants;

/**
 * 设置参数
 */
public class SetParam {
	
	static Logger log = Logger.getLogger(SetParam.class);
	
	/**
	 * 设置注册参数
	 * @param regis
	 * @return
	 */
    public static Map<String,String> setRegisteredParam(Registered regis){
    	if(regis==null){
    		log.info(":Registered is null");
    		return null;
		}
    	if (Strings.isNullOrEmpty(regis.getOperatorId())) {
			log.info(":operatorId is null");
			return null;
		}
    	if (Strings.isNullOrEmpty(regis.getMechanismNo())) {
			log.info(":mechanismNo is null");
			return null;
		}
    	if (Strings.isNullOrEmpty(regis.getSerialNo())) {
			log.info(":serialNo is null");
			return null;
		}
    	Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,SH_BCM_BusinessApi.TPDU);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_registered);//设置交易处理码
		requestMap.put("10", regis.getOperatorId());//设置操作员编号
		requestMap.put("32", regis.getMechanismNo());//受理方机构编码
		requestMap.put("40", regis.getSerialNo());//终端硬件序列号
		requestMap.put("64", "");//终端硬件序列号
		return requestMap;
    }

    /**
     * 设置签到参数
     * @param sign
     * @return
     */
    public static Map<String,String> setLoginParam(Sign sign){
    	if(sign==null){
			log.info("sign is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}	
    	Map<String,String> requestMap = new HashMap<String, String>();
    	requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,SH_BCM_BusinessApi.TPDU);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_sign);//设置交易处理码
		requestMap.put("10", sign.getOperatorId());//设置操作员编号
		requestMap.put("32", sign.getMechanismNo());//受理方机构编码
		requestMap.put("40", sign.getSerialNo());//终端硬件序列号
		requestMap.put("41",sign.getTerminalNo());//终端编号	      
		requestMap.put("42",sign.getBusinessNo());//商户号
		if(!Strings.isNullOrEmpty(sign.getVersion())){
			requestMap.put("62", sign.getVersion());//版本信息
		}
		requestMap.put("64", "");//终端硬件序列号
		return requestMap;
    	
    }
    
    /**
     * 设置密钥重置参数
     * @param rekey
     * @return
     */
    public static Map<String,String> setRekeyParam(Rekey rekey){
    	if(rekey==null){
			log.info("Rekey is null");
			return null;
		}
		if (Strings.isNullOrEmpty(rekey.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(rekey.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(rekey.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(rekey.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(rekey.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}
    	Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, SH_BCM_BusinessApi.TPDU);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_rekey);//设置交易处理码
		requestMap.put("10", rekey.getOperatorId());//设置操作员编号
		requestMap.put("32", rekey.getMechanismNo());//受理方机构编码
		requestMap.put("40", rekey.getSerialNo());//终端硬件序列号
		requestMap.put("41",rekey.getTerminalNo());//终端编号	      
		requestMap.put("42",rekey.getBusinessNo());//商户号
		requestMap.put("64", "");//终端硬件序列号
		return requestMap;
    }
    
    /**
     * 设置结算参数
     * @param bill
     * @return
     */
    public static Map<String,String> setBillingParam(Billing bill){
    	if(bill==null){
			log.info("Billing is null");
			return null;
		}
		if (Strings.isNullOrEmpty(bill.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(bill.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(bill.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(bill.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(bill.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}	
		if (Strings.isNullOrEmpty(bill.getBatch())) {
			log.info("Batch is null");
			return null;
		}
    	Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEC);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, SH_BCM_BusinessApi.TPDU);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_billing);//设置交易处理码
		requestMap.put("10", bill.getOperatorId());//设置操作员编号
		requestMap.put("12",new SimpleDateFormat("HHmmss").format(new Date()));//受理交易时间
	    requestMap.put("13",new SimpleDateFormat("yyyyMMdd").format(new Date()));//交易日期
		requestMap.put("32", bill.getMechanismNo());//受理方机构编码
		requestMap.put("40", bill.getSerialNo());//终端硬件序列号
		requestMap.put("41",bill.getTerminalNo());//终端编号	      
		requestMap.put("42",bill.getBusinessNo());//商户号
		requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");
		requestMap.put("62",bill.getBillingData());//商户号
		requestMap.put("64", "");//终端硬件序列号	
		return requestMap;
    }
    
    /**
     * 核消
     * @param ver
     * @return
     */
    public static Map<String,String> setVerificationParam(Verification ver){
    	if(ver==null){
			log.info("Verification is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getBatch())) {
			log.info("Batch is null");
			return null;
		}
		//券码要求是16位或12位，否者不符合
		if(Strings.isNullOrEmpty(ver.getCouponCode())||ver.getCouponCode().trim().length()!=16 && ver.getCouponCode().trim().length()!=12){
			log.info("CouponCode is null or length is not 16 or 12");
			return null;
		}
    	Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEB);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, SH_BCM_BusinessApi.TPDU);
		requestMap.put("2",SH_BCM_BusinessApi.primaryAccount);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_verification);//设置交易处理码
		requestMap.put("4",ver.getMoney());//交易金额
		requestMap.put("10",ver.getOperatorId());//设置操作员编号
		requestMap.put("11",ver.getTerminalNum());//终端流水号
		requestMap.put("12",new SimpleDateFormat("HHmmss").format(new Date()));//受理交易时间
	    requestMap.put("13",new SimpleDateFormat("yyyyMMdd").format(new Date()));//交易日期
	    requestMap.put("14",SH_BCM_BusinessApi.validity);//卡有效期
	    requestMap.put("22",SH_BCM_BusinessApi.posMethod);//POS输入方式
        requestMap.put("24",SH_BCM_BusinessApi.accountType);//账户类型
		requestMap.put("32",ver.getMechanismNo());//受理方机构编码
		requestMap.put("40",ver.getSerialNo());//终端硬件序列号
		requestMap.put("41",ver.getTerminalNo());//终端编号	      
		requestMap.put("42",ver.getBusinessNo());//商户号
		requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");
		requestMap.put("60",ver.getBatch());//批次号
		requestMap.put("63",ver.getCouponCode());//券码号
		requestMap.put("64", "");//
		return requestMap;
    }
    
    public static Map<String,String> setReversalParam(Verification ver){
    	if (ver == null) {
			log.info("Verification is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(ver.getBatch())) {
			log.info("Batch is null");
			return null;
		}
		//券码要求是16位或12位，否者不符合
		if (Strings.isNullOrEmpty(ver.getCouponCode())
				|| ver.getCouponCode().trim().length() != 16
				&& ver.getCouponCode().trim().length() != 12) {
			log.info("CouponCode is null or length is not 16 or 12");
			return null;
		}
    	Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI, SH_BCM_BusinessApi.MSG_TYPED);// 设置消息类型
		requestMap.put(SimpleConstants.TPDU, SH_BCM_BusinessApi.TPDU);
		requestMap.put("2", SH_BCM_BusinessApi.primaryAccount);
		requestMap.put("3", SH_BCM_BusinessApi.CODE_verification);// 设置交易处理码
		requestMap.put("4", ver.getMoney());// 交易金额
		requestMap.put("10", ver.getOperatorId());// 设置操作员编号
		requestMap.put("11", ver.getTerminalNum());// 终端流水号
		requestMap.put("12", ver.getAcceptedTime());// 受理交易时间  （取兑换接口的时间）
		requestMap.put("13",ver.getAcceptedDate());// 交易日期（取兑换接口的时间）
		requestMap.put("14", SH_BCM_BusinessApi.validity);// 卡有效期
		requestMap.put("22", SH_BCM_BusinessApi.posMethod);// POS输入方式
		requestMap.put("24", SH_BCM_BusinessApi.accountType);// 账户类型
		requestMap.put("32", ver.getMechanismNo());// 受理方机构编码
		requestMap.put("40", ver.getSerialNo());// 终端硬件序列号
		requestMap.put("41", ver.getTerminalNo());// 终端编号
		requestMap.put("42", ver.getBusinessNo());// 商户号
		requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");
		requestMap.put("60", ver.getBatch());// 批次号
		requestMap.put("63", ver.getCouponCode());// 券码号
		requestMap.put("64", "");//
		return requestMap;
    }
}