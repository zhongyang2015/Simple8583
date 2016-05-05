package simple_8583;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import simple_8583.key.SimpleConstants;
import simple_8583.util.EncodeUtil;

import com.google.common.base.Strings;


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
	
	//
	static String mechanismNo = "01";//受理方机构编号
	static String operatorId = "01";
	static String serialNo = "18200000100000300000745";//终端硬件序列号
	
	static String terminalNo = "00000751";//终端编号
	static String businessNo = "182000001000003";//商户编号
	static String version = "1.3.22";//版本
	static String masterKey = "2FB728A6271D2708";//主密钥
	static String macKey = "03D6E0072AC17EEF";//新mac密钥
	static String beachNo = "000002";//批次号
	static String couponCode = "1816004030201940";//券码
	static String num = "000953";//流水号
	static String amount = "1";//金额/数量
	static String acceptedTime = "172159";//兑换受理时间
	static String acceptedDate = "20160505";//兑换受理日期
	static String billData = "";//结算数据
	
	//---------------------对应接口实现区------------------------------------		
	/**
	 * 
	 *@描述  ：注册接口
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param operatorId 操作员编号
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param key mac秘钥
	 *@return 
	 */
	public static Map<String,String> registered(String operatorId,String mechanismNo,String serialNo,String key){
		System.out.println("[注册]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getRegisteredMsg(operatorId, mechanismNo, serialNo);
		if(dataMap==null){
			log.info("getRegisteredMsg error");
			return null;
		}
    	//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
		// 获取返回报文的应答码 "00"表示成功
		String replyCode = resultMap.get("39");// 此域为应答码
		if ("00".equals(replyCode)) {
			resultMap.get("12");// 受理时间 hh:mm:ss
			resultMap.get("13");// 交易日期yyyymmdd
			resultMap.get("32");// 受理方机构编号
			resultMap.get("40");// 终端硬件序列号
			resultMap.get("41");// 终端编号 *(后面需要的参数)
			resultMap.get("42");// 商户编号 *(后面需要的参数)
			// TODO 需要集成的业务
			return resultMap;

		} else {
			//FIXME
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
	}
	
	
	/**
	 * 
	 *@描述  ：签到接口
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param operatorId 操作员编号
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param terminalNo 终端编号
	 *@param businessNo 商户编号
	 *@param version 版本号
	 *@param key mac秘钥
	 *@param masterKey 主密钥
	 *@return 
	 */
	public static Map<String,String> login(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo,String version,String key,String masterKey){
		System.out.println("[签到]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getLoginMsg(operatorId, mechanismNo, serialNo,terminalNo,businessNo,version);
		if(dataMap==null){
			log.info("getLoginMsg error");
			return null;
		}
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
    	if ("00".equals(resultMap.get("39"))) {
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
			return resultMap;
    		// TODO 需要集成的业务
    		
    	}else {
    		// FIXME
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
			return null;
		}
    	
	}
	
	
	/**
	 * 
	 *@描述  ：密钥重置接口
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param operatorId 操作员编号
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param terminalNo 终端编号
	 *@param businessNo 商户编号
	 *@param key mac秘钥
	 *@return 
	 */
	public static Map<String,String> pwdReset(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo,String key){
		System.out.println("[密钥重置]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getPwdResetMsg(operatorId, mechanismNo, serialNo,terminalNo,businessNo);
		if(dataMap==null){
			log.info("getPwdResetMsg error");
			return null;
		}
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
    	// 获取返回报文的应答码 "00"表示成功
    	if ("00".equals(resultMap.get("39"))) {
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
			map = InterfaceAPI.decryptionDES(remark,key);
			String masterKey =map.get("masterKey");//新主密钥   *(后面需要的参数)
			String newMacKey = map.get("newMacKey");//新mac密钥   *(后面需要的参数)
			resultMap.put("masterKey", masterKey);
			resultMap.put("newMacKey", newMacKey);
			System.out.println("masterKey:"+masterKey+";newMacKey:"+newMacKey);
    		//TODO 需要集成的业务
    		
    	}else {
    		// FIXME
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	/**
	 * 
	 *@描述  ：兑换接口
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param primaryAccount 主账号
	 *@param amount 交易金额
	 *@param operatorId 操作员编号
	 *@param num 终端流水号  需要不能重复目前方法是 签到的流水+1处理 //TODO
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param terminalNo 终端编号
	 *@param businessNo 商户编号
	 *@param beachNo 批次号（跟签到产生的批次号相同）
	 *@param couponCode 券码
	 *@param key mac秘钥
	 *@return 
	 */
	public static Map<String,String> cost(String primaryAccount,String amount,String operatorId,String num,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String couponCode,String key){
		System.out.println("[兑换]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getCostMsg(primaryAccount,amount,operatorId,num, mechanismNo, serialNo,terminalNo,businessNo,beachNo,couponCode);
		if(dataMap==null){
			log.info("getCostMsg error");
			return null;
		}
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
    	// 获取返回报文的应答码 "00"表示成功
    	//返回码等于40时候 为无效券码//FIXME
        //返回码等于45时 可用次数不足//FIXME
    	String logMsg = resultMap.get("44");// 返回码说明 
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
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
			// TODO 
			//需要集成的业务
    	}
    	
    	else {
    		// FIXME
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	/**
	 * 
	 *@描述  ：冲正接口  （参数与兑换交易的一致 只是交易类型改成0400）
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param primaryAccount 主账号
	 *@param amount 交易金额
	 *@param operatorId 操作员编号
	 *@param num 终端流水号  //TODO  
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param terminalNo 终端编号
	 *@param businessNo 商户编号
	 *@param beachNo 批次号（跟签到产生的批次号相同）
	 *@param couponCode 券码
	 *@param key mac秘钥
	 *@return 
	 */
	public static Map<String,String> reversal(String primaryAccount,String amount,String operatorId,String num,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String couponCode,String key){
		System.out.println("[冲正]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getReversalMsg(primaryAccount,amount,operatorId,num, mechanismNo, serialNo,terminalNo,businessNo,beachNo,couponCode);
		if(dataMap==null){
			log.info("getReversalMsg error");
			return null;
		}
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
    	// 获取返回报文的应答码 "00"表示成功
    	//返回码等于40时候 为无效券码 //FIXME
        //返回码等于45时 可用次数不足//FIXME
    	String logMsg = resultMap.get("44");// 返回码说明 
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
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
			// TODO 
			//需要集成的业务
    	}
    	
    	else {
    		// FIXME
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	/**
	 * 
	 *@描述  ：结算接口 
	 *@创建人：zhongy
	 *@创建时间：2016年5月2日 下午6:46:17
	 *@修改人：
	 *@修改时间：
	 *@修改描述：
	 *@param operatorId 操作员编号
	 *@param mechanismNo 受理方机构编号
	 *@param serialNo 终端硬件序列号
	 *@param terminalNo 终端编号
	 *@param businessNo 商户编号
	 *@param beachNo 批次号（跟签到产生的批次号相同）
	 *@param billData 结算数据
	 *@param key mac秘钥
	 *@return 
	 */
	public static Map<String,String> billing(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String billData,String key){
		System.out.println("[结算]--------------");//XXX
		//获取对应的发送数据
		Map<String,String> dataMap = getBillingMsg(operatorId,mechanismNo, serialNo,terminalNo,businessNo,beachNo,billData);
		if(dataMap==null){
			log.info("getBillingMsg error");
			return null;
		}
		//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(dataMap,key);
    	if(resultMap==null){
    		return null;
    	}
    	//FIXME
    	// 获取返回报文的应答码 "00"表示成功，结算平；"39"表示结算部平
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode) || "98".equals(replyCode)) {
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
    		//FIXME
			String logMsg = resultMap.get("44");// 返回码说明
			log.info("transaction failed ，" + logMsg);
		}
    	return resultMap;
	}
	
	
//-----------------------------------------------------------报文组装区---------------------------------------------------------	
	//-----------------------------------------------------------报文组装区---------------------------------------------------------	
	
	//获取签到的报文
	
	//注册报文
	public static Map<String,String> getRegisteredMsg(String operatorId,String mechanismNo,String serialNo){
		//参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)) {
			log.info("params is null");
			return null;
		}
		
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("3",CODE_registered);//设置交易处理码
		requestMap.put("10", operatorId);//设置操作员编号
		requestMap.put("32", mechanismNo);//受理方机构编号
		requestMap.put("40",serialNo);//终端硬件序列号
		requestMap.put("64", "");
		return requestMap;
	}
	
	//签到报文
	public static Map<String,String> getLoginMsg(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo,String version){
		// 参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)
				|| Strings.isNullOrEmpty(terminalNo)
				|| Strings.isNullOrEmpty(businessNo)) {
			log.info("params is null");
			return null;
		}
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("3",CODE_sign);//设置交易处理码
		requestMap.put("10", operatorId);//设置操作员编号
		requestMap.put("32", mechanismNo);//受理方机构编号
		requestMap.put("40",serialNo);//终端硬件序列号
		requestMap.put("41",terminalNo);//终端编号
		requestMap.put("42",businessNo);//商户编号
		if(Strings.isNullOrEmpty(serialNo)){
			requestMap.put("62",serialNo);//上送版本号
		}
		requestMap.put("64", "");
		return requestMap;
    	
	}
	//密钥重置报文
	public static Map<String,String> getPwdResetMsg(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo){
		// 参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)
				|| Strings.isNullOrEmpty(terminalNo)
				|| Strings.isNullOrEmpty(businessNo)) {
			log.info("params is null");
			return null;
		}
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,SH_BCM_BusinessApi.MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU, SH_BCM_BusinessApi.TPDU);
		requestMap.put("3",SH_BCM_BusinessApi.CODE_rekey);//设置交易处理码
		requestMap.put("10", operatorId);//设置操作员编号
		requestMap.put("32", mechanismNo);//受理方机构编码
		requestMap.put("40", serialNo);//终端硬件序列号
		requestMap.put("41",terminalNo);//终端编号	      
		requestMap.put("42",businessNo);//商户号
		requestMap.put("64", "");//终端硬件序列号
		return requestMap;
	}
	//兑换
	public static Map<String,String> getCostMsg(String primaryAccount,String amount,String operatorId,String num,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String couponCode){
		// 参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)
				|| Strings.isNullOrEmpty(terminalNo)
				|| Strings.isNullOrEmpty(businessNo)
				|| Strings.isNullOrEmpty(beachNo)
				|| Strings.isNullOrEmpty(couponCode)
				|| Strings.isNullOrEmpty(num)) {
			log.info("params is null");
			return null;
		}
		// 券码要求是16位或12位，否者不符合
		if (couponCode.trim().length() != 16 && couponCode.trim().length() != 12) {
			log.info("CouponCode is null or length is not 16 or 12");
			return null;
		}
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEB);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("2",primaryAccount);
		requestMap.put("3",CODE_verification);//设置交易处理码
		requestMap.put("4",amount);//交易金额
		requestMap.put("10",operatorId);//设置操作员编号
		requestMap.put("11",num);//终端流水号//XXX 此处取值要取签到流水号加1
		requestMap.put("12",new SimpleDateFormat("HHmmss").format(new Date()));//受理交易时间
	    requestMap.put("13",new SimpleDateFormat("yyyyMMdd").format(new Date()));//交易日期
	    requestMap.put("14",validity);//卡有效期
	    requestMap.put("22",posMethod);//POS输入方式
        requestMap.put("24",accountType);//账户类型
		requestMap.put("32",mechanismNo);//受理方机构编码
		requestMap.put("40",serialNo);//终端硬件序列号
		requestMap.put("41",terminalNo);//终端编号	      
		requestMap.put("42",businessNo);//商户号
		requestMap.put("43","FFFFFFFFFFFFFFFFFFFFF0");//TODO
		requestMap.put("60",beachNo);//批次号
		requestMap.put("63",couponCode);//券码号
		requestMap.put("64", "");//
		return requestMap;
	}
	//冲正
	public static Map<String,String> getReversalMsg(String primaryAccount,String amount,String operatorId,String num,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String couponCode){
		// 参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)
				|| Strings.isNullOrEmpty(terminalNo)
				|| Strings.isNullOrEmpty(businessNo)
				|| Strings.isNullOrEmpty(beachNo)
				|| Strings.isNullOrEmpty(couponCode)
				|| Strings.isNullOrEmpty(num)) {
			log.info("params is null");
			return null;
		}
		// 券码要求是16位或12位，否者不符合
		if (couponCode.trim().length() != 16
				&& couponCode.trim().length() != 12) {
			log.info("CouponCode is null or length is not 16 or 12");
			return null;
		}
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI, MSG_TYPED);// 设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("2", primaryAccount);
		requestMap.put("3", CODE_verification);// 设置交易处理码
		requestMap.put("4", amount);// 交易金额
		requestMap.put("10", operatorId);// 设置操作员编号
		requestMap.put("11", num);// 终端流水号//XXX 此处取值要取签到流水号加1
		requestMap.put("12", acceptedTime);// 受理交易时间
		requestMap.put("13", acceptedDate);// 交易日期
		requestMap.put("14", validity);// 卡有效期
		requestMap.put("22", posMethod);// POS输入方式
		requestMap.put("24", accountType);// 账户类型
		requestMap.put("32", mechanismNo);// 受理方机构编码
		requestMap.put("40", serialNo);// 终端硬件序列号
		requestMap.put("41", terminalNo);// 终端编号
		requestMap.put("42", businessNo);// 商户号
		requestMap.put("43", "FFFFFFFFFFFFFFFFFFFFF0");// TODO
		requestMap.put("60", beachNo);// 批次号
		requestMap.put("63", couponCode);// 券码号
		requestMap.put("64", "");//
		return requestMap;
	}
	//结算
	public static Map<String,String> getBillingMsg(String operatorId,String mechanismNo,String serialNo,String terminalNo,String businessNo,String beachNo,String billData){
		// 参数发非空
		if (Strings.isNullOrEmpty(operatorId)
				|| Strings.isNullOrEmpty(mechanismNo)
				|| Strings.isNullOrEmpty(serialNo)
				|| Strings.isNullOrEmpty(terminalNo)
				|| Strings.isNullOrEmpty(businessNo)
				|| Strings.isNullOrEmpty(beachNo)){
			log.info("params is null");
			return null;
		}
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI, MSG_TYPEC);// 设置消息类型
		requestMap.put(SimpleConstants.TPDU, TPDU);
		requestMap.put("3", CODE_billing);// 设置交易处理码
		requestMap.put("10", operatorId);// 设置操作员编号
		requestMap.put("12", new SimpleDateFormat("HHmmss").format(new Date()));// 受理交易时间
		requestMap.put("13", new SimpleDateFormat("yyyyMMdd").format(new Date()));// 交易日期
		requestMap.put("32", mechanismNo);// 受理方机构编码
		requestMap.put("40", serialNo);// 终端硬件序列号
		requestMap.put("41", terminalNo);// 终端编号
		requestMap.put("42", businessNo);// 商户号
		requestMap.put("43", "FFFFFFFFFFFFFFFFFFFFF0");//XXX
		if(!Strings.isNullOrEmpty(billData)){
			requestMap.put("62", billData);// 结算数据
		}
		
		requestMap.put("64", "");// 终端硬件序列号
		return requestMap;
	}
	
	
	
/********************** 测 试 方 法  *****************************/
	
    public static void main(String[] args) throws Exception {
    	//注册
		registered(operatorId, mechanismNo, serialNo, initMacKey);
		//重置密钥
		pwdReset(operatorId, mechanismNo, serialNo, terminalNo, businessNo,initMacKey);
		//签到
		login(operatorId, mechanismNo, serialNo, terminalNo, businessNo,version, macKey, masterKey);
		//兑换
		cost(primaryAccount, amount, operatorId, num, mechanismNo, serialNo,terminalNo, businessNo, beachNo, couponCode, macKey);
		//冲正
		reversal(primaryAccount, amount, operatorId, num, mechanismNo,serialNo, terminalNo, businessNo, beachNo, couponCode, macKey);
		//结算
		billing(operatorId, mechanismNo, serialNo, terminalNo, businessNo,beachNo, billData, macKey);
   	    
   	    
    }  

	
	
	/**
	   * 
	   *@描述  ：
	   *@创建人：zhongy
	   *@创建时间：2016年5月2日 下午7:02:22
	   *@修改人：
	   *@修改时间：
	   *@修改描述：
	   *@param requestMap 请求的参数集合
	   *@param macKey mac 效验的key
	   *@return   返回结果集 map 异常的时候返回null
	   */
	    public static Map readAndSend (Map<String,String> requestMap,String macKey){
	    	//--读取配置文件组织报文
			byte[] sendData = InterfaceAPI.getPackets(requestMap, macKey, configUrl);
			System.out.println("[发送的报文]：" + EncodeUtil.hex(sendData));// 将报文用16进制打印出来
			
			//--发送报文获得返回的字节数组
			byte[] backData = InterfaceAPI.sendPackets(sendData, ip, port, timeout);
			System.out.println("[返回的报文]：" + EncodeUtil.hex(backData));// 将报文用16进制打印出来
			
			//--解析返回的数组返回一个map
	    	Map<String, String> resultMap = InterfaceAPI.resolve(backData, requestMap, configUrl);
	    	return resultMap;
	    }  
    
    
}
