package sh_bcm_card;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import sh_bcm.util.encrypt.MacUtil;
import sh_bcm_card.client.SimpleClient;
import sh_bcm_card.entity.BackSign;
import sh_bcm_card.entity.Consumption;
import sh_bcm_card.entity.Inquire;
import sh_bcm_card.entity.ReturnsObject;
import sh_bcm_card.entity.Reversal;
import sh_bcm_card.entity.Revocation;
import sh_bcm_card.entity.Sign;
import sh_bcm_card.factory.XmlReader;
import sh_bcm_card.key.SimpleConstants;
import sh_bcm_card.util.AnsixUtil;
import sh_bcm_card.util.EncodeUtil;

import com.google.common.base.Strings;

/**
 * <p>交行信用卡接口</p>
 *
 * @author zk
 * 2016-4-9 
 * 
 */
public class SH_BCM_BusinessCardApi {
	
/********************* 常 量  数  据 ******************************/
	static Logger log = Logger.getLogger(SH_BCM_BusinessCardApi.class);
	static String configUrl = "sh_bcm_card/Simple8583.xml";//配置文件XML的路径
	static String ip = "";//发送报文的ip
	static int port = 0;//发送报文的端口号
	static int timeout = 5000;//15s超时
	static String initMacKey = "1234567890abcdef";//初始密钥
	static String TPDU = "6001000100";
	//交易处理码
	static String CODE_sign = "910000";//签到
	static String CODE_backSign = "920000";//退签
	static String CODE_consumption = "180005";//积分消费
	static String CODE_inquire = "300002";//积分查询
	static String CODE_revocation = "200005";//积分消费撤销
	//消息类型
	static String MSG_TYPEA = "0820";
	static String MSG_TYPEB = "0200";
	static String MSG_TYPEC = "0400";
	
/********************** 测 试 方 法  *****************************/
    public static void main(String[] args) throws Exception {
    	Sign s = new Sign();
    	s.setPosNo("000001");
    	s.setBusinessNo("222");
    	s.setTerminalNo("223");
    	s.setConditionCode("00");
    	s.setOperator("012222");
    	s.setCredentials("0000000000");
    	sign(s,"1234567890abcdef");
    }  

    
    
/********************** 业 务 接 口  实 现  ****************************/
   

    /**
     * 签到接口
     * @param Sign
     * @param key 新mac密钥
     */
	public static ReturnsObject sign(Sign sign,String key){
		//--参数验证
		if(sign==null){
			log.info("sign is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getPosNo())) {
			log.info("PosNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getConditionCode())) {
			log.info("ConditionCode is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(sign.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}	
		if (Strings.isNullOrEmpty(sign.getOperator())) {
			log.info("Operator is null");
			return null;
		}
		//--参数设置
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("3",CODE_sign);//设置交易处理码
		requestMap.put("11", sign.getPosNo());//设置POS流水号
		requestMap.put("12", new SimpleDateFormat("HHmmss").format(new Date()));//交易时间
		requestMap.put("13", new SimpleDateFormat("MMdd").format(new Date()));//交易日期
		requestMap.put("25",sign.getConditionCode());//服务条件码
		requestMap.put("41",sign.getTerminalNo());//终端编号	      
		requestMap.put("42",sign.getBusinessNo());//商户号
		requestMap.put("60",sign.getOperator());//操作员号密码
		if(!Strings.isNullOrEmpty(sign.getCredentials())){
			requestMap.put("61",sign.getCredentials());//持卡人证件号码
		}
		requestMap.put("64", "");//
		
		
    	//获取返回信息resultMap
    	Map<String, String> resultMap = readAndSend(requestMap,key);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
			re.setSerialNo(resultMap.get("40"));// 终端硬件序列号
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			re.setBusinessName(resultMap.get("43"));// 商户名称
			if(!Strings.isNullOrEmpty(resultMap.get("44"))){//FIXME
				String keys = resultMap.get("44");
				//解密这串报文因为此域报文时加密处理的
				Map<String, String> map = new HashMap<String, String>();
				//用新主密钥去解密
				map = decryptionDES(keys,key);
				re.setNewMacKey(map.get("newMacKey"));//mac密钥
				re.setWorkKey(map.get("workKey"));//工作密钥
			}
			if(!Strings.isNullOrEmpty(resultMap.get("61"))){
				re.setCredentials(resultMap.get("61"));
			}
			return re;
    		
    	}else {
			// FIXME
			return null;
		}
    	
    }
	
	/**
	 * 退签接口
	 * @param sign
	 * @param key
	 * @return
	 */
	public static ReturnsObject backSign(BackSign backSign,String key){
		
		//--参数验证
				if(backSign==null){
					log.info("BackSign is null");
					return null;
				}
				if (Strings.isNullOrEmpty(backSign.getPosNo())) {
					log.info("PosNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(backSign.getConditionCode())) {
					log.info("ConditionCode is null");
					return null;
				}
				if (Strings.isNullOrEmpty(backSign.getTerminalNo())) {
					log.info("TerminalNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(backSign.getBusinessNo())) {
					log.info("BusinessNo is null");
					return null;
				}	
				if (Strings.isNullOrEmpty(backSign.getOperator())) {
					log.info("Operator is null");
					return null;
				}
				if (Strings.isNullOrEmpty(backSign.getBatch())) {
					log.info("Batch is null");
					return null;
				}
				//--参数设置
				Map<String,String> requestMap = new HashMap<String, String>();
				requestMap.put(SimpleConstants.MTI,MSG_TYPEA);//设置消息类型
				requestMap.put(SimpleConstants.TPDU,TPDU);
				requestMap.put("3",CODE_backSign);//设置交易处理码
				requestMap.put("11", backSign.getPosNo());//设置POS流水号
				requestMap.put("12", new SimpleDateFormat("HHmmss").format(new Date()));//交易时间
				requestMap.put("13", new SimpleDateFormat("MMdd").format(new Date()));//交易日期
				requestMap.put("25",backSign.getConditionCode());//服务条件码
				requestMap.put("41",backSign.getTerminalNo());//终端编号	      
				requestMap.put("42",backSign.getBusinessNo());//商户号
				requestMap.put("60",backSign.getOperator());//操作员号密码
				requestMap.put("62",backSign.getBatch());//批次号
				requestMap.put("64", "");
				
		    	//获取返回信息resultMap
		    	Map<String, String> resultMap = readAndSend(requestMap,key);
		    	// 获取返回报文的应答码 "00"表示成功
		        String replyCode = resultMap.get("39");// 此域为应答码
		    	if ("00".equals(replyCode)) {
		    		/******************* 返回获取的信息 *******************************************************/
		    		ReturnsObject re = new ReturnsObject();
		    		re.setPosNo(resultMap.get("11"));//pos流水号
		    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
					re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
					re.setCredentials(resultMap.get("25"));
					re.setSystemNumber(resultMap.get("37"));//系统参考号
					re.setTerminalNo(resultMap.get("41"));// 终端编号
					re.setBusinessNo(resultMap.get("42"));// 商户编号
					if(!Strings.isNullOrEmpty(resultMap.get("44"))){//FIXME
						String keys = resultMap.get("44");
						//解密这串报文因为此域报文时加密处理的
						Map<String, String> map = new HashMap<String, String>();
						//用密钥去解密
						map = decryptionDES(keys,key);
						re.setNewMacKey(map.get("newMacKey"));//mac密钥
						re.setWorkKey(map.get("workKey"));//工作密钥
					}
					return re;
		    		
		    	}else {
					// FIXME
					return null;
				}
	}
	
	
	/**
	 * 积分折抵比例查询
	 * @param inquire
	 * @param key
	 * @return
	 */
	public static ReturnsObject inquire (Inquire inquire,String key){
		//-----------------------参数验证------------------------------
		if(inquire==null){
			log.info("Inquire is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getPosNo())) {
			log.info("PosNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getConditionCode())) {
			log.info("ConditionCode is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}	
		if (Strings.isNullOrEmpty(inquire.getOperator())) {
			log.info("Operator is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getBillNum())) {
			log.info("BillNum is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getMoney())) {
			log.info("Money is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getInput())) {
			log.info("input is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getCurrencyCode())) {
			log.info("currencyCode is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getCredentials())) {
			log.info("credentials is null");
			return null;
		}
		if (Strings.isNullOrEmpty(inquire.getIDcard())) {
			log.info("IDcard is null");
			return null;
		}
		//---------------参数设置--------------------------------------------
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEB);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("2", inquire.getIDcard());//设置身份证
		requestMap.put("3",CODE_inquire);//设置交易处理码
		requestMap.put("4", inquire.getMoney());//设置金额
		requestMap.put("11", inquire.getPosNo());//设置POS流水号
		if(!Strings.isNullOrEmpty(inquire.getValidity())){
			requestMap.put("14",inquire.getValidity());//卡有效期
		}
		requestMap.put("22",inquire.getInput());//POS输入方式
		requestMap.put("25",inquire.getConditionCode());//服务条件码
		if(!Strings.isNullOrEmpty(inquire.getTwoTracks())){
			requestMap.put("35",inquire.getTwoTracks());//二磁道内容
		}
		if(!Strings.isNullOrEmpty(inquire.getThreeTracks())){
			requestMap.put("36",inquire.getThreeTracks());//三磁道内容
		}
		
		requestMap.put("41",inquire.getTerminalNo());//终端编号	      
		requestMap.put("42",inquire.getBusinessNo());//商户号
		requestMap.put("49",inquire.getCurrencyCode());//货币代码
		//个人密码密文
		if(!Strings.isNullOrEmpty(inquire.getPassWord())){
			//TODO
			//ANSI X9.8标准加密---根据53域 来判断他是带主账号还是不带主账号
			//ANSI X9.8标准加密-带主账号
			String pas = AnsixUtil.process(inquire.getPassWord(), "填加密主账号");//FIXME
			requestMap.put("52",pas);//个人密码密文
			
		}
		if(!Strings.isNullOrEmpty(inquire.getSecurityMsg())){
			requestMap.put("53",inquire.getSecurityMsg());//安全控制信息
		}
		if(!Strings.isNullOrEmpty(inquire.getBackMoney())){
			requestMap.put("54",inquire.getBackMoney());//返回金额
		}
		requestMap.put("60",inquire.getOperator());//操作员号密码
		requestMap.put("61",inquire.getCredentials());//持卡人证件号码
		requestMap.put("62",inquire.getBillNum());//批次号
		requestMap.put("64", "");
		
    	//---------------获取返回信息resultMap-----------------------------
    	Map<String, String> resultMap = readAndSend(requestMap,key);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setIDcard(resultMap.get("2"));//身份证号
    		re.setMoney(resultMap.get("4"));//交易金额
    		re.setPosNo(resultMap.get("11"));//pos流水号
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
			re.setValidity(resultMap.get("14"));
			re.setCredentials(resultMap.get("25"));
			re.setSystemNumber(resultMap.get("37"));//系统参考号
			re.setAuthorizeNum(resultMap.get("38"));//授权码
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			if(!Strings.isNullOrEmpty(resultMap.get("44"))){
				String keys = resultMap.get("44");
				//解密这串报文因为此域报文时加密处理的
				Map<String, String> map = new HashMap<String, String>();
				//用密钥去解密
				map = decryptionDES(keys,key);
				re.setNewMacKey(map.get("newMacKey"));//mac密钥
				re.setWorkKey(map.get("workKey"));//工作密钥
			}
			re.setSecurityMsg(resultMap.get("53"));// 安全控制信息
			re.setBackMoney(resultMap.get("54"));// 返回金额
			re.setOperator(resultMap.get("60"));// 操作员号密码
			re.setCredentials(resultMap.get("61"));// 持卡人证件号码
			return re;
    		
    	}else {
			// FIXME
			return null;
		}
	}
	
	/**
	 * 消费交易
	 * @param con
	 * @param key
	 * @return
	 */
	public static ReturnsObject consumption (Consumption con,String key){
		
		//-----------------------参数验证------------------------------
				if(con==null){
					log.info("Consumption is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getPosNo())) {
					log.info("PosNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getConditionCode())) {
					log.info("ConditionCode is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getTerminalNo())) {
					log.info("TerminalNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getBusinessNo())) {
					log.info("BusinessNo is null");
					return null;
				}	
				if (Strings.isNullOrEmpty(con.getOperator())) {
					log.info("Operator is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getBillNum())) {
					log.info("BillNum is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getMoney())) {
					log.info("Money is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getInput())) {
					log.info("input is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getCurrencyCode())) {
					log.info("currencyCode is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getCredentials())) {
					log.info("credentials is null");
					return null;
				}
				if (Strings.isNullOrEmpty(con.getIDcard())) {
					log.info("IDcard is null");
					return null;
				}
				//---------------参数设置--------------------------------------------
				Map<String,String> requestMap = new HashMap<String, String>();
				requestMap.put(SimpleConstants.MTI,MSG_TYPEB);//设置消息类型
				requestMap.put(SimpleConstants.TPDU,TPDU);
				requestMap.put("2", con.getIDcard());//设置身份证
				requestMap.put("3",CODE_consumption);//设置交易处理码
				requestMap.put("4", con.getMoney());//设置金额
				requestMap.put("11", con.getPosNo());//设置POS流水号
				if(!Strings.isNullOrEmpty(con.getValidity())){
					requestMap.put("14",con.getValidity());//卡有效期
				}
				requestMap.put("22",con.getInput());//POS输入方式
				requestMap.put("25",con.getConditionCode());//服务条件码
				if(!Strings.isNullOrEmpty(con.getTwoTracks())){
					requestMap.put("35",con.getTwoTracks());//二磁道内容
				}
				if(!Strings.isNullOrEmpty(con.getThreeTracks())){
					requestMap.put("36",con.getThreeTracks());//三磁道内容
				}
				
				requestMap.put("41",con.getTerminalNo());//终端编号	      
				requestMap.put("42",con.getBusinessNo());//商户号
				requestMap.put("49",con.getCurrencyCode());//货币代码
				//个人密码密文
				if(!Strings.isNullOrEmpty(con.getPassWord())){
					//TODO
					//ANSI X9.8标准加密---根据53域 来判断他是带主账号还是不带主账号
					//ANSI X9.8标准加密-带主账号
					String pas = AnsixUtil.process(con.getPassWord(), "填加密主账号");//FIXME
					requestMap.put("52",pas);//个人密码密文
					
				}
				if(!Strings.isNullOrEmpty(con.getSecurityMsg())){
					requestMap.put("53",con.getSecurityMsg());//安全控制信息
				}
				if(!Strings.isNullOrEmpty(con.getBackMoney())){
					requestMap.put("54",con.getBackMoney());//返回金额
				}
				requestMap.put("60",con.getOperator());//操作员号密码
				requestMap.put("61",con.getCredentials());//持卡人证件号码
				requestMap.put("62",con.getBillNum());//批次号
				requestMap.put("64", "");
				
		    	//---------------获取返回信息resultMap-----------------------------
		    	Map<String, String> resultMap = readAndSend(requestMap,key);
		    	// 获取返回报文的应答码 "00"表示成功
		        String replyCode = resultMap.get("39");// 此域为应答码
		    	if ("00".equals(replyCode)) {
		    		/******************* 返回获取的信息 *******************************************************/
		    		ReturnsObject re = new ReturnsObject();
		    		re.setIDcard(resultMap.get("2"));//身份证号
		    		re.setMoney(resultMap.get("4"));//交易金额
		    		re.setPosNo(resultMap.get("11"));//pos流水号
		    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
					re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
					re.setValidity(resultMap.get("14"));
					re.setCredentials(resultMap.get("25"));
					re.setSystemNumber(resultMap.get("37"));//系统参考号
					re.setAuthorizeNum(resultMap.get("38"));//授权码
					re.setTerminalNo(resultMap.get("41"));// 终端编号
					re.setBusinessNo(resultMap.get("42"));// 商户编号
					if(!Strings.isNullOrEmpty(resultMap.get("44"))){
						String keys = resultMap.get("44");
						//解密这串报文因为此域报文时加密处理的
						Map<String, String> map = new HashMap<String, String>();
						//用密钥去解密
						map = decryptionDES(keys,key);
						re.setNewMacKey(map.get("newMacKey"));//mac密钥
						re.setWorkKey(map.get("workKey"));//工作密钥
					}
					re.setSecurityMsg(resultMap.get("53"));// 安全控制信息
					re.setBackMoney(resultMap.get("54"));// 返回金额
					re.setOperator(resultMap.get("60"));// 操作员号密码
					re.setCredentials(resultMap.get("61"));// 持卡人证件号码
					return re;
		    		
		    	}else {
					// FIXME
					return null;
				}
	}
	
	/**
	 * 撤销
	 * @param revo
	 * @param key
	 * @return
	 */
	public static ReturnsObject revocation(Revocation revo,String key){
		
		//-----------------------参数验证------------------------------
		if(revo==null){
			log.info("Revocation is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getPosNo())) {
			log.info("PosNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getConditionCode())) {
			log.info("ConditionCode is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getTerminalNo())) {
			log.info("TerminalNo is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getBusinessNo())) {
			log.info("BusinessNo is null");
			return null;
		}	
		if (Strings.isNullOrEmpty(revo.getOperator())) {
			log.info("Operator is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getBillNum())) {
			log.info("BillNum is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getMoney())) {
			log.info("Money is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getInput())) {
			log.info("input is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getCurrencyCode())) {
			log.info("currencyCode is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getCredentials())) {
			log.info("credentials is null");
			return null;
		}
		if (Strings.isNullOrEmpty(revo.getIDcard())) {
			log.info("IDcard is null");
			return null;
		}
		//---------------参数设置--------------------------------------------
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap.put(SimpleConstants.MTI,MSG_TYPEB);//设置消息类型
		requestMap.put(SimpleConstants.TPDU,TPDU);
		requestMap.put("2", revo.getIDcard());//设置身份证
		requestMap.put("3",CODE_consumption);//设置交易处理码
		requestMap.put("4", revo.getMoney());//设置金额
		requestMap.put("11", revo.getPosNo());//设置POS流水号
		if(!Strings.isNullOrEmpty(revo.getValidity())){
			requestMap.put("14",revo.getValidity());//卡有效期
		}
		requestMap.put("22",revo.getInput());//POS输入方式
		requestMap.put("25",revo.getConditionCode());//服务条件码
		if(!Strings.isNullOrEmpty(revo.getTwoTracks())){
			requestMap.put("35",revo.getTwoTracks());//二磁道内容
		}
		if(!Strings.isNullOrEmpty(revo.getThreeTracks())){
			requestMap.put("36",revo.getThreeTracks());//三磁道内容
		}
		if(!Strings.isNullOrEmpty(revo.getAuthorizeNum())){
			requestMap.put("38",revo.getAuthorizeNum());//授权码
		}
		requestMap.put("41",revo.getTerminalNo());//终端编号	      
		requestMap.put("42",revo.getBusinessNo());//商户号
		requestMap.put("49",revo.getCurrencyCode());//货币代码
		//个人密码密文
		if(!Strings.isNullOrEmpty(revo.getPassWord())){
			//TODO
			//ANSI X9.8标准加密---根据53域 来判断他是带主账号还是不带主账号
			//ANSI X9.8标准加密-带主账号
			String pas = AnsixUtil.process(revo.getPassWord(), "填加密主账号");//FIXME
			requestMap.put("52",pas);//个人密码密文
			
		}
		if(!Strings.isNullOrEmpty(revo.getSecurityMsg())){
			requestMap.put("53",revo.getSecurityMsg());//安全控制信息
		}
		if(!Strings.isNullOrEmpty(revo.getBackMoney())){
			requestMap.put("54",revo.getBackMoney());//返回金额
		}
		requestMap.put("60",revo.getOperator());//操作员号密码
		requestMap.put("61",revo.getCredentials());//持卡人证件号码
		requestMap.put("62",revo.getBillNum());//批次号
		requestMap.put("64", "");
		
    	//---------------获取返回信息resultMap-----------------------------
    	Map<String, String> resultMap = readAndSend(requestMap,key);
    	// 获取返回报文的应答码 "00"表示成功
        String replyCode = resultMap.get("39");// 此域为应答码
    	if ("00".equals(replyCode)) {
    		/******************* 返回获取的信息 *******************************************************/
    		ReturnsObject re = new ReturnsObject();
    		re.setIDcard(resultMap.get("2"));//身份证号
    		re.setMoney(resultMap.get("4"));//交易金额
    		re.setPosNo(resultMap.get("11"));//pos流水号
    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
			re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
			re.setValidity(resultMap.get("14"));
			re.setCredentials(resultMap.get("25"));
			re.setSystemNumber(resultMap.get("37"));//系统参考号
			re.setAuthorizeNum(resultMap.get("38"));//授权码
			re.setTerminalNo(resultMap.get("41"));// 终端编号
			re.setBusinessNo(resultMap.get("42"));// 商户编号
			if(!Strings.isNullOrEmpty(resultMap.get("44"))){
				String keys = resultMap.get("44");
				//解密这串报文因为此域报文时加密处理的
				Map<String, String> map = new HashMap<String, String>();
				//用密钥去解密
				map = decryptionDES(keys,key);
				re.setNewMacKey(map.get("newMacKey"));//mac密钥
				re.setWorkKey(map.get("workKey"));//工作密钥
			}
			re.setSecurityMsg(resultMap.get("53"));// 安全控制信息
			re.setBackMoney(resultMap.get("54"));// 返回金额
			re.setOperator(resultMap.get("60"));// 操作员号密码
			re.setCredentials(resultMap.get("61"));// 持卡人证件号码
			return re;
    		
    	}else {
			// FIXME
			return null;
		}
	}
	
	/**
	 * 冲正
	 * @param reversal
	 * @param key
	 * @return
	 */
	public static ReturnsObject reversal (Reversal reversal,String key){
		//-----------------------参数验证------------------------------
				if(reversal==null){
					log.info("reversal is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getPosNo())) {
					log.info("PosNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getConditionCode())) {
					log.info("ConditionCode is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getTerminalNo())) {
					log.info("TerminalNo is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getBusinessNo())) {
					log.info("BusinessNo is null");
					return null;
				}	
				if (Strings.isNullOrEmpty(reversal.getOperator())) {
					log.info("Operator is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getBillNum())) {
					log.info("BillNum is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getMoney())) {
					log.info("Money is null");
					return null;
				}
				
				if (Strings.isNullOrEmpty(reversal.getCurrencyCode())) {
					log.info("currencyCode is null");
					return null;
				}
				
				if (Strings.isNullOrEmpty(reversal.getIDcard())) {
					log.info("IDcard is null");
					return null;
				}
				if (Strings.isNullOrEmpty(reversal.getProcessingCode())) {
					log.info("ProcessingCode is null");
					return null;
				}
				//---------------参数设置--------------------------------------------
				Map<String,String> requestMap = new HashMap<String, String>();
				requestMap.put(SimpleConstants.MTI,MSG_TYPEC);//设置消息类型
				requestMap.put(SimpleConstants.TPDU,TPDU);//
				requestMap.put("2", reversal.getIDcard());//设置身份证
				requestMap.put("3",reversal.getProcessingCode());//设置交易处理码
				requestMap.put("4", reversal.getMoney());//设置金额
				requestMap.put("11", reversal.getPosNo());//设置POS流水号
				if(!Strings.isNullOrEmpty(reversal.getValidity())){
					requestMap.put("14",reversal.getValidity());//卡有效期
				}
				if(!Strings.isNullOrEmpty(reversal.getInput())){
					requestMap.put("22",reversal.getInput());//POS输入方式
				}
				requestMap.put("25",reversal.getConditionCode());//服务条件码
				if(!Strings.isNullOrEmpty(reversal.getTwoTracks())){
					requestMap.put("35",reversal.getTwoTracks());//二磁道内容
				}
				if(!Strings.isNullOrEmpty(reversal.getThreeTracks())){
					requestMap.put("36",reversal.getThreeTracks());//三磁道内容
				}
				if(!Strings.isNullOrEmpty(reversal.getAuthorizeNum())){
					requestMap.put("38",reversal.getAuthorizeNum());//授权码
				}
				requestMap.put("41",reversal.getTerminalNo());//终端编号	      
				requestMap.put("42",reversal.getBusinessNo());//商户号
				requestMap.put("49",reversal.getCurrencyCode());//货币代码
				//个人密码密文
				if(!Strings.isNullOrEmpty(reversal.getPassWord())){
					//TODO
					//ANSI X9.8标准加密---根据53域 来判断他是带主账号还是不带主账号
					//ANSI X9.8标准加密-带主账号
					String pas = AnsixUtil.process(reversal.getPassWord(), "填加密主账号");//FIXME
					requestMap.put("52",pas);//个人密码密文
					
				}
				if(!Strings.isNullOrEmpty(reversal.getSecurityMsg())){
					requestMap.put("53",reversal.getSecurityMsg());//安全控制信息
				}
				if(!Strings.isNullOrEmpty(reversal.getICdata())){
					requestMap.put("55",reversal.getICdata());//IC卡数据
				}
				requestMap.put("60",reversal.getOperator());//操作员号密码
				if(!Strings.isNullOrEmpty(reversal.getCredentials())){
					requestMap.put("61",reversal.getCredentials());//持卡人证件号码
				}
				
				requestMap.put("62",reversal.getBillNum());//批次号
				requestMap.put("64", "");
				
		    	//---------------获取返回信息resultMap-----------------------------
		    	Map<String, String> resultMap = readAndSend(requestMap,key);
		    	// 获取返回报文的应答码 "00"表示成功
		        String replyCode = resultMap.get("39");// 此域为应答码
		    	if ("00".equals(replyCode)) {
		    		/******************* 返回获取的信息 *******************************************************/
		    		ReturnsObject re = new ReturnsObject();
		    		re.setIDcard(resultMap.get("2"));//身份证号
		    		re.setMoney(resultMap.get("4"));//交易金额
		    		re.setPosNo(resultMap.get("11"));//pos流水号
		    		re.setAcceptedTime(resultMap.get("12"));// 受理时间 hh:mm:ss
					re.setAcceptedDate(resultMap.get("13"));// 交易日期mmdd
					re.setValidity(resultMap.get("14"));//卡有效期
					re.setCredentials(resultMap.get("25"));//服务点条件码
					re.setSystemNumber(resultMap.get("37"));//系统参考号
					re.setAuthorizeNum(resultMap.get("38"));//授权码
					re.setTerminalNo(resultMap.get("41"));// 终端编号
					re.setBusinessNo(resultMap.get("42"));// 商户编号
					if(!Strings.isNullOrEmpty(resultMap.get("44"))){
						String keys = resultMap.get("44");
						//解密这串报文因为此域报文时加密处理的
						Map<String, String> map = new HashMap<String, String>();
						//用密钥去解密
						map = decryptionDES(keys,key);
						re.setNewMacKey(map.get("newMacKey"));//mac密钥
						re.setWorkKey(map.get("workKey"));//工作密钥
					}
					re.setSecurityMsg(resultMap.get("53"));// 安全控制信息
					re.setIDcard(resultMap.get("55"));// IC卡数据域
					return re;
		    		
		    	}else {
					// FIXME
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
	 * 解密44域密钥包 此方法只针对交行核销接口
	 * @param str 
	 * @param key 解密的key
	 * @return
	 */
	public static  Map decryptionDES(String str,String key){
		Map<String,String> map = new HashMap<String,String>();
		
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
		
		return map;
	}
	
}
