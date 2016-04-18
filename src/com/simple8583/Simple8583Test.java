package com.simple8583;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.simple8583.util.EncodeUtil;
import com.simple8583.util.encrypt.MacUtil;

import jodd.typeconverter.Convert;

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
//        requestMap.put(SimpleConstants.MTI,"0800");
//      //  requestMap.put(SimpleConstants.TOP,"02");
//        requestMap.put(SimpleConstants.TPDU, "6000400000");
//        requestMap.put("3","000001");
//        requestMap.put("10","01");
//        requestMap.put("32","01");
//
//        requestMap.put("40","77537947");//87126291   
//        requestMap.put("64","");   
//
//        String ip = "58.32.228.203";
//        int port = 10666;
//        int timeout = 5000;//15s超时
//
//        String macKey = "1234567890ABCDEF";
//       // getMac(macKey, data);
//        SimpleClient simpleClient = new SimpleClient(ip,port,timeout);
//        simpleClient.setMacKey(macKey);
//        XmlReader xmlReader = new XmlReader("com/simple8583/Simple8583.xml");
//        //发送请求返回结果
//        Map<String,String> resultMap = simpleClient.sendToBank(requestMap,xmlReader);
       
//        String keys = MacUtil.DES_1("GH11111111123GFD","1234567890abcdef",0);
//        String keyss = MacUtil.DES_1("ECEE5780546BC231","1234567890abcdef",1);
//        System.out.println(keys);
//        
//        String str="123";  
//         
//               // DES数据加密  
//             String s1=encryptBasedDes(str);  
//                 
//               System.out.println(s1);  
//                  
//                // DES数据解密  
//                String s2=decryptBasedDes("LDiFUdf0iew=");  
//                
//              System.err.println(s2);  
//        String keys = MacUtil.DES_1("3ED116264C4C71C1","1234567890abcdef",1);
//        System.out.println(keys);
//        String str = "12312312eeeeeeeeffffffff";
//        String masterKey = str.substring(0,8);
//        String masterKeys = str.substring(8,16);
//        String masterKeyss = str.substring(16,24);
//        System.out.println(masterKey);
//        System.out.println(masterKeys);
//        System.out.println(masterKeyss);
//        String tt = "ffffffffffffffff";
//        byte[] by = tt.getBytes();
//        String e = "1234567890abcdef";
//        System.out.println(decrypt(e.getBytes(),"1234567890abcdef"));
//       System.out.println(decryptBasedDes("ECEE5780546BC231CF89E24F1802CAB41D576ACDAD328934")); 
        
        System.out.println("303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303031".length());
    }
    
    public static String str2HexStr(String str) {  
        char[] chars = "0123456789ABCDEF".toCharArray();  
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();  
        int bit;  
        for (int i = 0; i < bs.length; i++) {  
            bit = (bs[i] & 0x0f0) >> 4;  
            sb.append(chars[bit]);  
            bit = bs[i] & 0x0f;  
            sb.append(chars[bit]);  
        }  
        return sb.toString();  
    } 
    public static byte[] hexStringToByte(String hex) {
    int len = (hex.length() / 2);
    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++) {
     int pos = i * 2;
     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
    }
    return result;
   }
    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
     }
    /** 
    02.     * DES算法密钥 
    03.     */  
       private static final byte[] DES_KEY = "1234567890abcde".getBytes();  
       /** 
    06.     * 数据加密，算法（DES） 
    07.     * 
    08.     * @param data 
    09.     *            要进行加密的数据 
    10.     * @return 加密后的数据 
    11.     */  
       public static String encryptBasedDes(String data) {  
           String encryptedData = null;  
          try {  
               // DES算法要求有一个可信任的随机数源  
               SecureRandom sr = new SecureRandom();  
              DESKeySpec deskey = new DESKeySpec(DES_KEY);  
               // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象  
             SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
                SecretKey key = keyFactory.generateSecret(deskey);  
             // 加密对象  
               Cipher cipher = Cipher.getInstance("DES");  
             cipher.init(Cipher.ENCRYPT_MODE, key, sr);  
               // 加密，并把字节数组编码成字符串  
              encryptedData = new sun.misc.BASE64Encoder().encode(cipher.doFinal(data.getBytes()));  
           } catch (Exception e) {  
    //            log.error("加密错误，错误信息：", e);  
               throw new RuntimeException("加密错误，错误信息：", e);  
            }  
           return encryptedData;  
      }  

       /** 
       02.     * 数据解密，算法（DES） 
       03.     * 
       04.     * @param cryptData 
       05.     *            加密数据 
       06.     * @return 解密后的数据 
       07.     */  
           public static String decryptBasedDes(String cryptData) {  
              String decryptedData = null;  
             try {  
                 // DES算法要求有一个可信任的随机数源  
                   SecureRandom sr = new SecureRandom();  
                DESKeySpec deskey = new DESKeySpec(DES_KEY);  
                 // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象  
                   SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
                SecretKey key = keyFactory.generateSecret(deskey);  
                  // 解密对象  
                  Cipher cipher = Cipher.getInstance("DES");  
                  cipher.init(Cipher.DECRYPT_MODE, key, sr);  
                 // 把字符串解码为字节数组，并解密  
                 decryptedData = new String(cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(cryptData)));  
               } catch (Exception e) {  
      //            log.error("解密错误，错误信息：", e);  
                  throw new RuntimeException("解密错误，错误信息：", e);  
              }  
              return decryptedData;  
         }  
           
           
           private static byte[] decrypt(byte[] src, String password) throws Exception {  
               // DES算法要求有一个可信任的随机数源  
               SecureRandom random = new SecureRandom();  
               // 创建一个DESKeySpec对象  
               DESKeySpec desKey = new DESKeySpec(password.getBytes());  
               // 创建一个密匙工厂  
               SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
               // 将DESKeySpec对象转换成SecretKey对象  
               SecretKey securekey = keyFactory.generateSecret(desKey);  
               // Cipher对象实际完成解密操作  
               Cipher cipher = Cipher.getInstance("DES");  
               // 用密匙初始化Cipher对象  
               cipher.init(Cipher.DECRYPT_MODE, securekey, random);  
               // 真正开始解密操作  
               return cipher.doFinal(src);  
           }  
       

}
