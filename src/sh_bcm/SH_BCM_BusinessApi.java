package sh_bcm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import sh_bcm.entity.Billing;
import sh_bcm.entity.Registered;
import sh_bcm.entity.Rekey;
import sh_bcm.entity.ReturnsObject;
import sh_bcm.entity.Sign;
import sh_bcm.entity.Verification;

import com.google.common.base.Strings;
import com.simple8583.client.SimpleClient;
import com.simple8583.factory.XmlReader;
import com.simple8583.key.SimpleConstants;
import com.simple8583.util.encrypt.MacUtil;

/**
 * <p>交行核销接口</p>
 *
 * @author zk
 * 2016-4-9 
 * 
 */
public class SH_BCM_BusinessApi {
	
/********************* 常 量  数  据 ******************************/
	static Logger log = Logger.getLogger(SH_BCM_BusinessApi.class);
	static String configUrl = "sh_bcm/Simple8583.xml";//配置文件XML的路径
	static String ip = "58.32.228.203";//发送报文的ip
	static int port = 10666;//发送报文的端口号
	static String initMacKey = "1234567890abcdef";//初始密钥
	static int timeout = 5000;//15s超时
	static String TPDU = "6000400000";
	static String validity = "99999999";//卡有效期，无需刷卡直接输入券码时有效期填99999999
	static String posMethod = "01";//poss输入方式 01代表手动输入
	static String accountType = "07";//账户类型 07代表礼券账户
	static String primaryAccount = "0000000000000000";//主账号  如果直接输入券码 主账号就写0000000000000000
	//交易处理码
	static String CODE_registered = "000001";//注册
	static String CODE_sign = "000000";//签到
	static String CODE_rekey = "000010";//密钥重置
	static String CODE_verification = "050000";//兑换
	static String CODE_billing = "000020";//结算
	//消息类型
	static String MSG_TYPEA = "0800";
	static String MSG_TYPEB = "0600";
	static String MSG_TYPEC = "0500";
	static String MSG_TYPED = "0400";
	
/********************** 测 试 方 法  *****************************/
    public static void main(String[] args) throws Exception {
    
    }  

    
    
/********************** 业 务 接 口  实 现  ****************************/
    /**
     * 注册接口
     * @param requestMap
     * @return
     */
	public static ReturnsObject registered(Registered regis,String macKey) {
		//---参数验证
		if(regis==null){
			log.info("Registered is null");
			return null;
		}
		if (Strings.isNullOrEmpty(regis.getOperatorId())) {
			log.info("OperatorId is null");
			return null;
		}
		if (Strings.isNullOrEmpty(regis.getMechanismNo())) {
			log.info("MechanismNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(regis.getSerialNo())) {
			log.info("SerialNo is null");
			return null;
		}
		//---参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("3",CODE_registered);//设置交易处理码
		requestMap.put("10", regis.getOperatorId());//设置操作员编号
		requestMap.put("32", regis.getMechanismNo());//受理方机构编码
		requestMap.put("40", regis.getSerialNo());//终端硬件序列号
		requestMap.put("64", "");//终端硬件序列号
		
		//获取返回信息resultMap
		Map<String, String> resultMap = readAndSend(requestMap,macKey);
		// 获取返回报文的应答码 "00"表示成功
		String replyCode = resultMap.get("39");// 此域为应答码
		if ("00".equals(replyCode)) {
			//******************* 返回获取的信息 *******************************************************//
			ReturnsObject re = new ReturnsObject();
			re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			return re;
			// TODO 需要集成的业务

		} else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
	}

    /**
     * 签到接口
     * @param requestMap
     * @param macKey 新mac密钥
     * @param masterKey 新主密钥
     */
	public static ReturnsObject sign(Sign sign,String macKey,String masterKey){
		//--参数验证
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
		//--参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("3",CODE_sign);//设置交易处理码
		requestMap.put("10", sign.getOperatorId());//设置操作员编号
		requestMap.put("32", sign.getMechanismNo());//受理方机构编码
		requestMap.put("40", sign.getSerialNo());//终端硬件序列号
		requestMap.put("41",sign.getTerminalNo());//终端编号	      
		requestMap.put("42",sign.getBusinessNo());//商户号
		if(!Strings.isNullOrEmpty(sign.getVersion())){
			requestMap.put("62", sign.getVersion());//版本信息
		}
		requestMap.put("64", "");//终端硬件序列号
		
		
    	//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,macKey);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			re.setBatch(resultMap.get("60"));//批次号
			String remark2 = resultMap.get("63");//新work密钥（16）+新MAC密钥（16）
			//解密这串报文因为此域报文时加密处理的
			Map<String, String> map = new HashMap<String, String>();
			//用新主密钥去解密
			map = decryptionDES(remark2,masterKey);
			//String workKey = map.get("workKey");//新work密钥
			re.setNewMacKey(map.get("newMacKey"));//新mac密钥
			System.out.println("newMacKey:"+map.get("newMacKey"));
			return re;
    		// TODO 需要集成的业务
    		
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
    	
    }
	
	/**
	 * 参数下载接口
	 * @param requestMap
	 */
//	public static void download(Map<String,String> requestMap){
//		//获取返回信息resultMap
//    	Map<String, String> resultMap = readAndSend(requestMap);
//    	// 获取返回报文的应答码 "00"表示成功
//        String replyCode = resultMap.get("39");// 此域为应答码
//    	if ("00".equals(replyCode)) {
//    		/******************* 返回获取的信息 *******************************************************/
//    		String acceptedTime = resultMap.get("12");// 受理时间 hh:mm:ss
//			String acceptedDate = resultMap.get("13");// 交易日期yyyymmdd
//			String mechanismNo = resultMap.get("32");// 受理方机构编号
//			String serialNo = resultMap.get("40");// 终端硬件序列号
//			String terminalNo = resultMap.get("41");// 终端编号
//			String businessNo = resultMap.get("42");// 商户编号
//			String terminalStatus = resultMap.get("45");// 终端状态码
//			String remark = resultMap.get("63");//自定义数据
//    		// TODO 需要集成的业务
//    		
//    	}else {
//			String logMsg = resultMap.get("44");// 返回码说明
//			log.info("transaction failed ，" + logMsg);
//		}
//	}
	
	
	/**
	 * 密钥重置接口
	 * @param rekey
	 * @param macKey
	 * @return
	 */
	
	public static ReturnsObject rekey(Rekey rekey,String macKey){
		//--参数验证
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
		
		//--参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("3",CODE_rekey);//设置交易处理码
		requestMap.put("10", rekey.getOperatorId());//设置操作员编号
		requestMap.put("32", rekey.getMechanismNo());//受理方机构编码
		requestMap.put("40", rekey.getSerialNo());//终端硬件序列号
		requestMap.put("41",rekey.getTerminalNo());//终端编号	      
		requestMap.put("42",rekey.getBusinessNo());//商户号
		requestMap.put("64", "");//终端硬件序列号
		
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,macKey);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			String remark = resultMap.get("63");//自定义数据（新主密钥（16）+work密钥（16）+新MAC密钥（16）经DES加密）
			//DES解密此字段
			//解密这串报文因为此域报文时加密处理的
			Map<String, String> map = new HashMap<String, String>();
			map = decryptionDES(remark,macKey);
			re.setMasterKey(map.get("masterKey"));//新主密钥
			//String workKey = map.get("workKey");//新work密钥
			re.setNewMacKey(map.get("newMacKey"));//新mac密钥
			System.out.println("masterKey:"+re.getMasterKey()+";newMacKey:"+re.getNewMacKey());
			return re;
    		//TODO 需要集成的业务
    		
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
	}
	
	/**
	 * 结算接口
	 * 接口说明：如果调用了此接口，就必须重新签到
	 * @param bill
	 * @param macKey
	 * @return
	 */
	public static ReturnsObject billing(Billing bill,String macKey){
		//--参数验证
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
		
		//--参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEC);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("3",CODE_billing);//设置交易处理码
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
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,macKey);
    	// 获取返回报文的应答码 "00"表示成功，结算平；"39"表示结算部平
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode) || "98".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			re.setBatch(resultMap.get("60"));// 批次号
			re.setBillingData(resultMap.get("62"));//结算数据
			if("00".equals(replyCode)){
				re.setBillingMsg("结算平");
			}else{
				re.setBillingMsg("结算不平");
			}
			return re;
    		//TODO 需要集成的业务
    	}
    	
    	else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
	}
    
	/**
	 * 核销接口
	 * @param ver
	 * @param macKey
	 * @return
	 */
	public static ReturnsObject verification(Verification ver,String macKey){
		//--参数验证
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
		//--参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEB);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("2",primaryAccount);
		requestMap.put("3",CODE_verification);//设置交易处理码
		requestMap.put("4",ver.getMoney());//交易金额
		requestMap.put("10",ver.getOperatorId());//设置操作员编号
		requestMap.put("11",ver.getTerminalNum());//终端流水号
		requestMap.put("12",new SimpleDateFormat("HHmmss").format(new Date()));//受理交易时间
	    requestMap.put("13",new SimpleDateFormat("yyyyMMdd").format(new Date()));//交易日期
	    requestMap.put("14",validity);//卡有效期
	    requestMap.put("22",posMethod);//POS输入方式
        requestMap.put("24",accountType);//账户类型
		requestMap.put("32",ver.getMechanismNo());//受理方机构编码
		requestMap.put("40",ver.getSerialNo());//终端硬件序列号
		requestMap.put("41",ver.getTerminalNo());//终端编号	      
		requestMap.put("42",ver.getBusinessNo());//商户号
		 requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");
		requestMap.put("60",ver.getBatch());//批次号
		requestMap.put("63",ver.getCouponCode());//券码号
		requestMap.put("64", "");//
		
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,macKey);
    	ReturnsObject re = new ReturnsObject();
    	// 获取返回报文的应答码 "00"表示成功
    	//返回码等于40时候 为无效券码
        //返回码等于45时 可用次数不足
    	String logMsg = resultMap.get("44");// 返回码说明 
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		re.setPrimaryAccount(resultMap.get("2"));// 卡号
    		re.setMoney(resultMap.get("4"));// 交易金额(兑换礼券张数)
    		re.setTerminalNum(resultMap.get("11"));// 终端流水号
    		re.setAcceptedTime(resultMap.get("12")); // 受理时间 hh:mm:ss
    		re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
    		re.setValidity(resultMap.get("14"));// 卡有效期
			re.setClearDate(resultMap.get("15"));// 主机清算日期
			re.setAccountType(resultMap.get("24")) ;// 账户类型 ‘07代表礼券账户’
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			re.setBatch(resultMap.get("60"));// 批次号
			re.setTicketData(resultMap.get("63"));// 返回券码信息
			re.setReturnCodes(replyCode);//应答码
			re.setRemark(logMsg);
			//TODO 
			//需要集成的业务
    	}
    	
    	else {
			log.info("transaction failed ，" + logMsg);
			re.setRemark(logMsg);
			
		}
    	return re;
	}
	
	/**
	 * 冲正接口
	 * 说明：冲正传参的数据取需要充正的那条记录的数据，值修改消息类型（包括交易日期和时间都要跟兑换接口的一样）
	 * @param ver
	 * @param macKey
	 * @return
	 */
	public static ReturnsObject reversal(Verification ver,String macKey){
		//--参数验证
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

		//-- 参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI, MSG_TYPED);// 设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("2", primaryAccount);
		requestMap.put("3", CODE_verification);// 设置交易处理码
		requestMap.put("4", ver.getMoney());// 交易金额
		requestMap.put("10", ver.getOperatorId());// 设置操作员编号
		requestMap.put("11", ver.getTerminalNum());// 终端流水号
		requestMap.put("12", ver.getAcceptedTime());// 受理交易时间  （取兑换接口的时间）
		requestMap.put("13",ver.getAcceptedDate());// 交易日期（取兑换接口的时间）
		requestMap.put("14", validity);// 卡有效期
		requestMap.put("22", posMethod);// POS输入方式
		requestMap.put("24", accountType);// 账户类型
		requestMap.put("32", ver.getMechanismNo());// 受理方机构编码
		requestMap.put("40", ver.getSerialNo());// 终端硬件序列号
		requestMap.put("41", ver.getTerminalNo());// 终端编号
		requestMap.put("42", ver.getBusinessNo());// 商户号
		requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");
		requestMap.put("60", ver.getBatch());// 批次号
		requestMap.put("63", ver.getCouponCode());// 券码号
		requestMap.put("64", "");//
		// 获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,macKey);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setPrimaryAccount(resultMap.get("2"));// 卡号
    		re.setMoney(resultMap.get("4"));// 交易金额(兑换礼券张数)
    		re.setTerminalNum(resultMap.get("11"));// 终端流水号
    		re.setAcceptedTime(resultMap.get("12")); // 受理时间 hh:mm:ss
    		re.setAcceptedDate(resultMap.get("13"));// 交易日期yyyymmdd
    		re.setValidity(resultMap.get("14"));// 卡有效期
			re.setClearDate(resultMap.get("15"));// 主机清算日期
			re.setAccountType(resultMap.get("24")) ;// 账户类型 ‘07代表礼券账户’
			re.setMechanismNo(resultMap.get("32"));// 受理方机构编号
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			re.setBatch(resultMap.get("60"));// 批次号
			re.setTicketData(resultMap.get("63"));// 返回券码信息
			re.setReturnCodes(replyCode);//应答码
			//TODO 
			//需要集成的业务
    		return re;
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
	}
	
	
    /**
     * 读取配置文件发送报文获取返回报文信息
     * @param requestMap
     * @return
     */
    public static Map readAndSend (Map<String,String> requestMap,String macKey){
    	
    	//放入请求参数
    	SimpleClient simpleClient = new SimpleClient(ip,port,timeout);
    	//放入密钥
         simpleClient.setMacKey(macKey);
         XmlReader xmlReader;
         Map<String,String> resultMap = new HashMap<String, String>();
		try {
			//读取配置文件
			xmlReader = new XmlReader(configUrl);
			//发送请求返回结果
			resultMap = simpleClient.sendToBank(requestMap,xmlReader);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.info("exception "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("exception "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("exception "+e.getMessage());
			e.printStackTrace();
		}
		
    	return resultMap;
    }
    
    /**
	 * 解密63域密钥包 此方法只针对交行核销接口
	 * 说明：1，密钥重置接口会返回加密的63域，此域包涵3个密钥，返回报文时24位16进制数（一位长度代表报文2数字位）
	 *     先要取得此报文将报文按8位长度分隔成3块分别对应新主密钥，work密钥，新MAC密钥，然后先去第一个8位
	 *     DES解密，解密出来的新主密钥用来分别给work密钥和新MAC密钥解密用的
	 *     2，签到接口会返回加密的63域，此域包涵2个密钥，共16位16进制数。
	 * @param str 
	 * @param key 解密的key
	 * @return
	 */
	public static  Map decryptionDES(String str,String key){
		Map<String,String> map = new HashMap<String,String>();
		//适用于2个接口，返回的长度也有2种
		if(str.length()/2==24 || str.length()/2==16){
			if(str.length()/2==24){
				//将字符串按8位分隔
				String master = str.substring(0,16);
				String work = str.substring(16,32);
				String newMac = str.substring(32,48);
				//先解密第一个8位,得到主密钥
				String masterKey = MacUtil.DES_1(master,key,1);
				//用主密钥去解其他2个密钥
				String workKey = MacUtil.DES_1(work,masterKey,1);
				String newMacKey = MacUtil.DES_1(newMac,masterKey,1);
				map.put("masterKey", masterKey);
				map.put("workKey", workKey);
				map.put("newMacKey", newMacKey);
			}
			if(str.length()/2==16){
				//将字符串按8位分隔
				String workKey = str.substring(0,16);
				String newMac = str.substring(16,32);
				//用主密钥去解其他2个密钥
				String masterKey = MacUtil.DES_1(workKey,key,1);
				String newMacKey = MacUtil.DES_1(newMac,key,1);
				map.put("workKey", workKey);
				map.put("newMacKey", newMacKey);
			}
		}
		
		return map;
	}
}
