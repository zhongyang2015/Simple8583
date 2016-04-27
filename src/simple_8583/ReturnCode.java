package simple_8583;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import simple_8583.factory.IsoMsgFactory;
import simple_8583.factory.XmlReader;
import simple_8583.model.IsoPackage;
import simple_8583.util.EncodeUtil;
import simple_8583.util.MacUtil;




/**
 * <p>交行核销主要常量</p>
 * @author zk
 * 2016-4-9 
 * 
 */
public class ReturnCode {
	static String success = "00";//成功
	static String registerAgainA = "90";//重新注册
	static String registerAgainB = "F1";//重新注册
	static String macError = "A0";//成功
	static String noTimes = "45";//可用次数不足
	static String errorCode = "40";//错误券码
}
