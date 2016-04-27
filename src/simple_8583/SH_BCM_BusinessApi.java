package simple_8583;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import simple_8583.entity.Billing;
import simple_8583.entity.Registered;
import simple_8583.entity.Rekey;
import simple_8583.entity.Sign;
import simple_8583.entity.Verification;
import simple_8583.util.EncodeUtil;
import simple_8583.util.SetParam;


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
	static String configUrl = "simple_8583/Simple8583.xml";//配置文件XML的路径
	static String ip = "58.32.228.203";//发送报文的ip
	static int port = 10666;//发送报文的端口号
	static String initMacKey = "1234567890abcdef";//初始密钥
	static int timeout = 5000;//15s超时
	public static String TPDU = "6000400000";
	public static String validity = "99999999";//卡有效期，无需刷卡直接输入券码时有效期填99999999
	public static String posMethod = "01";//poss输入方式 01代表手动输入
	public static String accountType = "07";//账户类型 07代表礼券账户
	public static String primaryAccount = "0000000000000000";//主账号  如果直接输入券码 主账号就写0000000000000000
	//交易处理码
	public static String CODE_registered = "000001";//注册
	public static String CODE_sign = "000000";//签到
	public static String CODE_rekey = "000010";//密钥重置
	public static String CODE_verification = "050000";//兑换
	public static String CODE_billing = "000020";//结算
	//消息类型
	public static String MSG_TYPEA = "0800";
	public static String MSG_TYPEB = "0600";
	public static String MSG_TYPEC = "0500";
	public static String MSG_TYPED = "0400";
	
	//此部分数据在实际中应该是固定的
	static String mechanismNo = "01";
	static String operatorId = "01";
	static String serialNo = "18200000100000300000745";
	
/********************** 测 试 方 法  *****************************/
	//调用接口获取的重要参数
	static String newMacKey = null;//mac密钥
	static String masterKey = null;//主密钥
	static String terminalNo = null;//终端号
	static String businessNo = null;//商户号
	static String beach = null;//批次号
	static String terminalNum = null;//流水号
	static String acceptedTime = null;//交易时间
	static String acceptedDate = null;//交易日期
	
	
	public static void zhuche(){
		Map<String, String> requestMap = new HashMap<String, String>();
    	Registered r = new Registered();
    	r.setMechanismNo(mechanismNo);
		r.setOperatorId(operatorId);
		r.setSerialNo(serialNo);
		//注册获取终端号，商户号
		requestMap = registered(r, initMacKey);
		if(ReturnCode.success.equals(requestMap.get("39"))){
			terminalNo = requestMap.get("41");//终端号
			businessNo = requestMap.get("42");//商户号
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO
			System.out.println("mac校验失败");
		}else{
			// TODO 失败处理
		}
		
	}
	public static void chongzhi(){
		Rekey re = new Rekey();
		re.setMechanismNo(mechanismNo);
		re.setOperatorId(operatorId);
		re.setSerialNo(serialNo);
		re.setTerminalNo(terminalNo);
		re.setBusinessNo(businessNo);
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = rekey(re, initMacKey);
		if(ReturnCode.success.equals(requestMap.get("39"))){
			newMacKey = requestMap.get("newMacKey");
			masterKey = requestMap.get("masterKey");
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO
			System.out.println("mac校验失败");
		}else{
			// TODO 失败处理
		}
		
	}
	public static void qiandao(){
		Sign sign = new Sign();
		sign.setMechanismNo(mechanismNo);
		sign.setOperatorId(operatorId);
		sign.setSerialNo(serialNo);
		sign.setTerminalNo(terminalNo);
		sign.setBusinessNo(businessNo);
		sign.setVersion("1.3.22");//版本号
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = login(sign, newMacKey, masterKey);
		
		if(ReturnCode.success.equals(requestMap.get("39"))){
			newMacKey = requestMap.get("newMacKey");
			terminalNum = requestMap.get("11");//流水号
			beach = requestMap.get("60");
		}
		else if(ReturnCode.registerAgainA.equals(requestMap.get("39"))||ReturnCode.registerAgainB.equals(requestMap.get("39"))){//应答码  如果是90和F1则需要重新注册 密钥重置
			// TODO 需要重新注册 密钥重置
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO 重新签到
			System.out.println("mac校验失败");
		}else{
			// TODO 当异常处理
		}
		
	}
	public static void duihuan(){
		Verification ve = new Verification();
		ve.setMoney("1");//兑换券张数
		ve.setMechanismNo(mechanismNo);
		//流水号加1处理
		String num = String.valueOf((Integer.valueOf(terminalNum)+1));
		if(num.length()<7){
			int t = 6-num.length();
			for(int j=0;j<t;j++){ 
				num ="0"+num; 
				} 
		}		
		ve.setTerminalNum(num);
		ve.setOperatorId(operatorId);
		ve.setSerialNo(serialNo);
		ve.setTerminalNo(terminalNo);
		ve.setBusinessNo(businessNo);
		ve.setBatch(beach);
		ve.setCouponCode("1816004030201940");//券码号
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = verification(ve,newMacKey);
		String code = requestMap.get("39");// 返回码等于40时候 为无效券码// 返回码等于45时 可用次数不足//A0mac校验失败 //应答码  如果是90和F1则需要重新注册 密钥重置
		if(ReturnCode.success.equals(requestMap.get("39"))){
			acceptedTime = requestMap.get("12");//交易时间
			acceptedDate = requestMap.get("13");//交易日期
			terminalNum = requestMap.get("11");//流水号
			beach = requestMap.get("60");//批次号
		}else if(ReturnCode.errorCode.equals(requestMap.get("39"))){
			System.out.println("无效券码");
		}
		else if(ReturnCode.noTimes.equals(requestMap.get("39"))){
			System.out.println("可用次数不足");
		}
		else if(ReturnCode.registerAgainA.equals(requestMap.get("39"))||ReturnCode.registerAgainB.equals(requestMap.get("39"))){//应答码  如果是90和F1则需要重新注册 密钥重置
			// TODO 需要重新注册 密钥重置
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO 重新签到
			System.out.println("mac校验失败");
		}else{
			// TODO 当异常处理  需要冲正处理
		}
		
	}
	
	public static void jiesuan(){
		Billing bi = new Billing();
        bi.setMechanismNo(mechanismNo);
        bi.setOperatorId(operatorId);
        bi.setSerialNo(serialNo);
        bi.setTerminalNo(terminalNo);
        bi.setBusinessNo(businessNo);
        bi.setBatch(beach);
        bi.setBillingData("100510000000000000000000000000020000000000000000000200000000000000000000000000000000000000000000000000000000000000000000");
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap = billing(bi,newMacKey);
		String code = requestMap.get("39");//应答码  如果是90和F1则需要重新注册 密钥重置
		if(ReturnCode.success.equals(requestMap.get("39"))){
			// TODO退签 重新签到
		}
		else if(ReturnCode.registerAgainA.equals(requestMap.get("39"))||ReturnCode.registerAgainB.equals(requestMap.get("39"))){//应答码  如果是90和F1则需要重新注册 密钥重置
			// TODO 需要重新注册 密钥重置
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO 重新签到
			System.out.println("mac校验失败");
		}else{
			// TODO 当异常处理
		}
	}
	
	public static void chongzheng(){
		Verification ve = new Verification();
		ve.setMoney("1");
		ve.setMechanismNo(mechanismNo);
		ve.setTerminalNum(terminalNum);
		ve.setOperatorId(operatorId);
		ve.setSerialNo(serialNo);
		ve.setTerminalNo(terminalNo);
		ve.setBusinessNo(businessNo);
		ve.setBatch(beach);
		ve.setCouponCode("1816004030201940");//券码
		ve.setAcceptedDate(acceptedDate);
		ve.setAcceptedTime(acceptedTime);
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = reversal(ve,newMacKey);
		String code = requestMap.get("39");//如果是90和F1则需要重新注册 密钥重置
		if(ReturnCode.success.equals(requestMap.get("39"))){
			// TODO 冲正成功
		}
		else if(ReturnCode.registerAgainA.equals(requestMap.get("39"))||ReturnCode.registerAgainB.equals(requestMap.get("39"))){//应答码  如果是90和F1则需要重新注册 密钥重置
			// TODO 需要重新注册 密钥重置
		}else if(ReturnCode.macError.equals(requestMap.get("39"))){
			// TODO 重新签到
			System.out.println("mac校验失败");
		}else{
			// TODO 当异常处理
		}
	}
    public static void main(String[] args) throws Exception {
    	
  //------
    	 zhuche();
    	 chongzhi();
    	 qiandao();
    	// jiesuan();
    	 duihuan();
    	// chongzheng();
    	
		// 注册测试 OK
		/*Registered r = new Registered();
		r.setMechanismNo("01");
		r.setOperatorId("01");
		r.setSerialNo("18200000100000300000745");
		registered(r, initMacKey);*/
         
		// 密钥重置 OK
/*		Rekey re = new Rekey();
		re.setMechanismNo("01");
		re.setOperatorId("01");
		re.setSerialNo("18200000100000300000745");
		re.setTerminalNo("00000751");
		re.setBusinessNo("182000001000003");
		rekey(re, initMacKey);*/
		
		// 签到测试 签到之后要返回批次号
/*		Sign sign = new Sign();
		sign.setMechanismNo("01");
		sign.setOperatorId("01");
		sign.setSerialNo("18200000100000300000745");
		sign.setTerminalNo("00000751");
		sign.setBusinessNo("182000001000003");
		sign.setVersion("1.3.22");
		login(sign, "526386FD707A5F7A", "22073E3C31712528");*/
		
		// 兑换 11域名流水号不能重复+1处理(券码输入只支持16位和12位券码)
		// 返回码等于40时候 为无效券码
		// 返回码等于45时 可用次数不足
		/*Verification ve = new Verification();
		ve.setMoney("1");
		ve.setMechanismNo("01");
		ve.setTerminalNum("000926");
		ve.setOperatorId("01");
		ve.setSerialNo("18200000100000300000745");
		ve.setTerminalNo("00000751");
		ve.setBusinessNo("182000001000003");
		ve.setBatch("000001");
		ve.setCouponCode("1816004030201940");
		ve.setAcceptedDate("20160425");
		ve.setAcceptedTime("161357");
		//verification(ve,"161604EC2631BE1A");
		
		reversal(ve,"161604EC2631BE1A");*/
    }  

    
    
/********************** 业 务 接 口  实 现  ****************************/
    /**
     * 注册接口
     * @param requestMap
     * @return
     */
	public static Map<String, String> registered(Registered regis,String macKey) {
		//--参数验证,参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setRegisteredParam(regis);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
		// 获取返回报文的应答码 "00"表示成功
		String replyCode = resultMap.get("39");// 此域为应答码
		if ("00".equals(replyCode)) {
			//******************* 返回获取的信息 *******************************************************//
			resultMap.get("12");// 受理时间 hh:mm:ss 
			resultMap.get("13");// 交易日期yyyymmdd
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号  *(后面需要的参数)
			resultMap.get("42");// 商户编号  *(后面需要的参数)
			// TODO 需要集成的业务
			
		} else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
		return resultMap;
	}

    /**
     * 签到接口
     * @param requestMap
     * @param macKey 新mac密钥
     * @param masterKey 新主密钥
     */
	public static Map<String, String> login(Sign sign,String macKey,String masterKey){
		//--参数验证,参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setLoginParam(sign);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		resultMap.get("11");//终端流水号
    		resultMap.get("12");// 受理时间 hh:mm:ss
			resultMap.get("13");// 交易日期yyyymmdd
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号
			resultMap.get("42");// 商户编号
			resultMap.get("60");//批次号 *(后面需要的参数)
			String remark2 = resultMap.get("63");//新work密钥（16）+新MAC密钥（16）
			//解密这串报文因为此域报文时加密处理的
			Map<String, String> map = new HashMap<String, String>();
			//用新主密钥去解密
			map = InterfaceAPI.decryptionDES(remark2,masterKey);
			String newMacKey = map.get("newMacKey");//新mac密钥  *(后面需要的参数)
			resultMap.put("newMacKey", newMacKey);
			System.out.println("newMacKey:"+map.get("newMacKey"));
			
    		// TODO 需要集成的业务
    		
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
    	
    }
	
	
	/**
	 * 密钥重置接口
	 * @param rekey
	 * @param macKey
	 * @return
	 */
	
	public static Map<String, String> rekey(Rekey rekey,String macKey){
		//--参数验证，参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setRekeyParam(rekey);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		resultMap.get("12");// 受理时间 hh:mm:ss
			resultMap.get("13");// 交易日期yyyymmdd
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号
			resultMap.get("42");// 商户编号
			String remark = resultMap.get("63");//自定义数据（新主密钥（16）+work密钥（16）+新MAC密钥（16）经DES加密）
			//DES解密此字段
			//解密这串报文因为此域报文时加密处理的
			Map<String, String> map = new HashMap<String, String>();
			map = InterfaceAPI.decryptionDES(remark,macKey);
			String masterKey =map.get("masterKey");//新主密钥   *(后面需要的参数)
			String newMacKey = map.get("newMacKey");//新mac密钥   *(后面需要的参数)
			resultMap.put("masterKey", masterKey);
			resultMap.put("newMacKey", newMacKey);
			System.out.println("masterKey:"+masterKey+";newMacKey:"+newMacKey);
    		//TODO 需要集成的业务
    		
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	/**
	 * 结算接口
	 * 接口说明：如果调用了此接口，就必须重新签到
	 * @param bill
	 * @param macKey
	 * @return
	 */
	public static Map<String, String> billing(Billing bill,String macKey){
		//--参数验证，参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setBillingParam(bill);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
    	// 获取返回报文的应答码 "00"表示成功，结算平；"39"表示结算部平
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode) || "98".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		resultMap.get("12");// 受理时间 hh:mm:ss
			resultMap.get("13");// 交易日期yyyymmdd
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号
			resultMap.get("42");// 商户编号
			resultMap.get("60");// 批次号
			resultMap.get("62");//结算数据
    		//TODO 需要集成的业务
    	}
    	
    	else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
    
	/**
	 * 核销接口
	 * @param ver
	 * @param macKey
	 * @return
	 */
	public static Map<String, String> verification(Verification ver,String macKey){
		//--参数验证，参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setVerificationParam(ver);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
    	// 获取返回报文的应答码 "00"表示成功
    	//返回码等于40时候 为无效券码
        //返回码等于45时 可用次数不足
    	String logMsg = resultMap.get("44");// 返回码说明 
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		resultMap.get("2");// 卡号  *(后面冲正需要的参数)
    		resultMap.get("4");// 交易金额(兑换礼券张数)  *(后面冲正需要的参数)
    		resultMap.get("11");// 终端流水号  *(后面冲正需要的参数)
    		resultMap.get("12"); // 受理时间 hh:mm:ss   *(后面冲正需要的参数)
    		resultMap.get("13");// 交易日期yyyymmdd    *(后面冲正需要的参数)
    		resultMap.get("14");// 卡有效期   *(后面冲正需要的参数)
			resultMap.get("15");// 主机清算日期 *(后面冲正需要的参数)
			resultMap.get("24") ;// 账户类型 ‘07代表礼券账户’ *(后面冲正需要的参数)
			resultMap.get("32");// 受理方机构编号 *(后面冲正需要的参数)
			resultMap.get("40");// 终端硬件序列号 *(后面冲正需要的参数)
			resultMap.get("41");// 终端编号 *(后面冲正需要的参数)
			resultMap.get("42");// 商户编号  *(后面冲正需要的参数)
			resultMap.get("60");// 批次号  *(后面冲正需要的参数)
			resultMap.get("63");// 返回券码信息 
			//TODO 
			//需要集成的业务
    	}
    	
    	else {
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	/**
	 * 冲正接口
	 * 说明：冲正传参的数据取需要充正的那条记录的数据，值修改消息类型（包括交易日期和时间都要跟兑换接口的一样）
	 * @param ver
	 * @param macKey
	 * @return
	 */
	public static Map<String, String> reversal(Verification ver,String macKey){
		//-- 参数验证，参数设置
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap = SetParam.setReversalParam(ver);
		if(requestMap==null){
			log.info("requestMap is null");
			return null;
		}
		
		//--读取配置文件组织报文
		byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
		System.out.println("发送的报文：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
		
		//--发送报文获得返回的字节数组
		byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
		System.out.println("返回的报文：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
		
		//--解析返回的数组返回一个map
    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
    	
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		resultMap.get("2");// 卡号
    		resultMap.get("4");// 交易金额(兑换礼券张数)
    		resultMap.get("11");// 终端流水号
    		resultMap.get("12"); // 受理时间 hh:mm:ss
    		resultMap.get("13");// 交易日期yyyymmdd
    		resultMap.get("14");// 卡有效期
			resultMap.get("15");// 主机清算日期
			resultMap.get("24") ;// 账户类型 ‘07代表礼券账户’
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号
			resultMap.get("42");// 商户编号
			resultMap.get("60");// 批次号
			resultMap.get("63");// 返回券码信息
			//TODO 
			//需要集成的业务
    	}else {
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	
   
    
    
}
