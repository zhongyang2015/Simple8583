package com.simple8583;

import com.simple8583.client.SimpleClient;
import com.simple8583.factory.XmlReader;
import com.simple8583.key.SimpleConstants;

import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>测试类.</p>
 *
 * @author Magic Joey
 * @version Simple8583Test.java 1.0 Created@2015-6-15 12:46 $
 */
public class Simple8583Test {

    public static void main(String[] args) throws Exception {
        Map<String,String> requestMap = new HashMap<String,String>();

        
//        requestMap.put(SimpleConstants.MTI,"0430");
//        requestMap.put("15","SP99515061500104387");
//        requestMap.put("13","张清源");
//        requestMap.put("14","1567");
//        requestMap.put("12","pay");
//        requestMap.put("3","12M01041");   
//        requestMap.put("2","470000");  
//        requestMap.put("7","492500");
//        requestMap.put("6","6228210250013865710");
//        requestMap.put("5","04");
//        requestMap.put("4","01041");
//        requestMap.put("9","012");
//        requestMap.put("8","SP99515061500104387");
        requestMap.put(SimpleConstants.MTI,"0800");
      //  requestMap.put(SimpleConstants.TOP,"02");
        requestMap.put(SimpleConstants.TPDU, "6000400000");
        requestMap.put("3","000001");
        requestMap.put("10","01");
        requestMap.put("32","01");
        requestMap.put("40","87126291");   
        requestMap.put("64","D2C8A0B7FE2983F9");   
        String ip = "58.32.228.203";
        int port = 10666;
        int timeout = 5000;//15s超时


        String macKey = "1234567890abcdef";
       // getMac(macKey, data);
        SimpleClient simpleClient = new SimpleClient(ip,port,timeout);
        simpleClient.setMacKey(macKey);
        XmlReader xmlReader = new XmlReader("com/simple8583/simple8583.xml");
        Map<String,String> resultMap = simpleClient.sendToBank(requestMap,xmlReader);
        System.out.println(resultMap);
    }
}
