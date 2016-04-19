package sh_bcm_card.factory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sh_bcm_card.key.SimpleConstants;
import sh_bcm_card.model.BitMap;
import sh_bcm_card.model.IsoField;
import sh_bcm_card.model.IsoPackage;
import sh_bcm_card.util.EncodeUtil;
import sh_bcm_card.util.SimpleUtil;
import sh_bcm_card.util.encrypt.MacUtil;

import com.google.common.base.Strings;


/**
 * <p>报文组装抽象类.</p>
 *
 * @author Magic Joey
 * @version AbstractIsoMsgFactory.java 1.0 Created@2014-07-10 10:43 $
 */
public abstract class AbstractIsoMsgFactory {

	protected String macKey;
	private String sts;

	protected AbstractIsoMsgFactory() {

	}
	
	// 入口和出口
	public byte[] pack(Map<String, String> dataMap,final IsoPackage pack)
            throws IOException, ClassNotFoundException {
        //深度拷贝，对拷贝后的对象进行操作，
		IsoPackage packClone = pack.deepClone();

		List<Integer> dataFieldList = new ArrayList<Integer>(dataMap.size());
		for (String key : dataMap.keySet()) {
			IsoField field = packClone.getIsoField(key);
			if (field == null) {
				continue;
			}
			field.setValue(dataMap.get(key));
			//数据域
			if (SimpleUtil.isNumeric(key)) {
				int val = Integer.valueOf(key);
				if(packClone.isBit64()&&val>64){
                    //设置位非64位图模式，即128模式
                    packClone.setBit64(false);
                    //将bitMap第一位置为1，表示这个数据域为128位长
					dataFieldList.add(1);
				}
				dataFieldList.add(val);
			}
		}
		// 生成BitMap
		BitMap bitMap = null;
		if(packClone.isBit64()){
            bitMap = new BitMap(64);
		}else{
            bitMap = new BitMap(128);
		}
		byte[] bitMapByte = bitMap.addBits(dataFieldList);
		//设置BitMap的值
		packClone.getIsoField(SimpleConstants.BIT_MAP).setByteValue(bitMapByte);
		
		
		
		
		//将数组合并
		return merge(packClone);
	}

	 public static String binary(byte[] bytes, int radix){  
		        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
		    }  
	 

    /**
     * 将返回信息拆成Map返回
     * @param bts
     * @param pack
     * @return
     * @throws Exception
     */
	public Map<String, String> unpack(byte[] bts,final IsoPackage pack)
			throws Exception {
		
		String sth = "";
		if (pack == null || pack.size() == 0) {
			throw new IllegalArgumentException("配置为空，请检查IsoPackage是否为空");
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		// 起判断的作用
		int offset = 0;
        //深度拷贝
		IsoPackage target =  pack.deepClone();
		// 获取到bitMap
		boolean hasBitMap = false;
		BitMap bitMap = null;
		for (IsoField field : target) {
				
			if (field.isAppData()) {//数据域
				if (hasBitMap) {
					int index = Integer.valueOf(field.getId());
					if(index==1){
						continue;//第一位不处理，只是标志位
					}
					if (bitMap.getBit(index - 1) == 1) {//XXX 位图中是否存在值
							//因为44域是加密处理的，我们需要得到他的16进制报文解密
							if("44".equals(field.getId())){
								offset += subByte(bts, offset, field);
								//获取此域的16进制报文 ,将此加密报文当作值传给外层去处理
								String hex = EncodeUtil.hex(field.getByteValue());
								field.setValue(hex);
								//String value=new String(field.getByteValue(),"gbk");
								returnMap.put(field.getId(), field.getValue());//XXX 密钥确定是否和注册时使用相同的密钥
							}else{
								offset += subByte(bts, offset, field);
								returnMap.put(field.getId(), field.getValue());//XXX 密钥确定是否和注册时使用相同的密钥
							}
							
						System.out.println(field.getId()+":"+field.getValue());
					}else{
						sts="";
					}
				}
			} else {//非数据域
				offset += subByte(bts, offset, field);
				returnMap.put(field.getId(), field.getValue());
				System.out.println(field.getId()+":"+field.getValue());
				if (field.getId().equalsIgnoreCase(SimpleConstants.BIT_MAP)) {
					hasBitMap = true;
					bitMap = BitMap.addBits(field.getByteValue());
				}
			}
			//验证MAC需要的域的信息
			if(!Strings.isNullOrEmpty(sts)){
				sth += sts;
			}
		}
		//MAC校验
		//macValidate(pack,returnMap);
		macValidate(sth,macKey,returnMap);
		return returnMap;
	}

	
	
	private int subByte(byte[] bts, int offset, IsoField field)
			throws UnsupportedEncodingException {
		byte[] val = null;
		byte[] macval = null;
		int length = field.getLength();
		switch (field.getIsoType()) {
		case NUMERIC:
		case CHAR:
		case BINARY:
			val = new byte[field.getLength()];
			System.arraycopy(bts, offset+1, val, 0, length);
			macval = val;//返回报文验证mac用的数据
			break;
		case LLVAR_NUMERIC:
			byte[] llvarNumLen = new byte[1];
			llvarNumLen[0] = bts[offset+1];
			// 除以2的原因是LLVAR_NUMERIC前面的报文域长度是数字长度而非字节长度
			int firstNumLen = Integer.valueOf(EncodeUtil.hex(llvarNumLen)) / 2;
			val = new byte[firstNumLen];
			macval = new byte[firstNumLen+1];//计算mac用的数据
			System.arraycopy(bts, offset + 2, val, 0, firstNumLen);
			System.arraycopy(bts, offset + 1, macval, 0, firstNumLen+1);//返回报文验证mac用的数据
			length = 1 + firstNumLen;
			break;
		case LLVAR:
			byte[] llvarLen = new byte[1];
			llvarLen[0] = bts[offset+1];//因为我们取报文的时候包括了包头的一位长度所以要多加一个长度
			int firstLen = Integer.valueOf(EncodeUtil.hex(llvarLen));
			val = new byte[firstLen];
			macval = new byte[firstLen+1];//计算mac用的数据
			System.arraycopy(bts, offset + 2, val, 0, firstLen);//因为我们取报文的时候包括了包头的一位长度所以要多加一个长度
			System.arraycopy(bts, offset + 1, macval, 0, firstLen+1);//返回报文验证mac用的数据
			length = 1 + firstLen;
			break;
		case LLLVAR:
			byte[] lllvarLen = new byte[2];
			lllvarLen[0] = bts[offset+1];//因为我们取报文的时候包括了包头的一位长度所以要多加一个长度
			lllvarLen[1] = bts[offset + 2];//因为我们取报文的时候包括了包头的一位长度所以要多加一个长度
			int first2Len = Integer.valueOf(EncodeUtil.hex(lllvarLen));
			val = new byte[first2Len];
			macval = new byte[first2Len+2];//计算mac用的数据
			System.arraycopy(bts, offset + 3, val, 0, first2Len);//因为我们取报文的时候包括了包头的一位长度所以要多加一个长度
			System.arraycopy(bts, offset + 1, macval, 0, first2Len+2);//返回报文验证mac用的数据
			length = 2 + first2Len;
			break;
		default:
			break;
		}
		//把mac计算需要的域的信息拼接成字符串方便后面计算mac
		if(!"top".equals(field.getId())&&!"MsgLength".equals(field.getId())&&!"tpdu".equals(field.getId())&&!"64".equals(field.getId())){
			sts = EncodeUtil.hex(macval);
		}else{
			sts = "";
		}
		field.setByteValue(val);
		return length;
	}

	// Byte数组的合并，不同byte数组域将被整合为一个大的byte数组
	private byte[] merge(IsoPackage isoPackage) throws IOException {
		ByteArrayOutputStream byteOutPut = new ByteArrayOutputStream(100);
		String sth ="";
		for (IsoField field : isoPackage) {
			if (field.isChecked()) {
				String value_16="";
                //Mac
				if (isoPackage.isMacPos(field.getId())) {// 判断是否为mac位，如果是进行mac算法
					try {
//						byteOutPut.write(mac(isoPackage));
//						System.out.println(field.getId() + ":"+  EncodeUtil.hex(mac(isoPackage)));
						System.out.println("16进制mac前数据"+sth);
						byteOutPut.write(EncodeUtil.bcd(MacUtil.MAC(macKey, null, sth)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					continue;//FIXME
				}
				switch (field.getIsoType()) {
				case LLVAR_NUMERIC:{
					byte[] lengthByte0 = new byte[1];
					lengthByte0 = EncodeUtil.bcd(field.getValue().length(), 1);
					byteOutPut.write(lengthByte0);
					value_16=EncodeUtil.hex(field.getByteValue());
					//value_16="0"+(value_16.length()/2)+value_16;
					if(value_16.length()>9){
						value_16=(value_16.length())+value_16;
					}else{
						value_16="0"+(value_16.length())+value_16;
					}
					break;
				}
					
				case LLVAR:{
					byte[] lengthByte = new byte[1];
					lengthByte = EncodeUtil.bcd(field.getByteValue().length, 1);
					byteOutPut.write(lengthByte);
					value_16=EncodeUtil.hex(field.getByteValue());
					//value_16="0"+(value_16.length()/2)+value_16;
					if(value_16.length()/2>9){
						value_16=(value_16.length()/2)+value_16;
					}else{
						value_16="0"+(value_16.length()/2)+value_16;
					}
					break;
				}
					
				case LLLVAR:{
					byte[] lengthByte2 = new byte[2];
					lengthByte2 = EncodeUtil.bcd(field.getByteValue().length, 2);
					byteOutPut.write(lengthByte2);
					value_16=EncodeUtil.hex(field.getByteValue());
//					if((value_16.length()/2>9&&value_16.length()/2<100) || value_16.length()/2>999){
//						value_16=(value_16.length()/2)+value_16;
//					}else{
//						value_16="000"+(value_16.length()/2)+value_16;
//					}
					if(value_16.length()/2<10){
						value_16="000"+(value_16.length()/2)+value_16;
					}else if(value_16.length()/2<100){
						value_16="00"+(value_16.length()/2)+value_16;
					}else if(value_16.length()/2<1000){
						value_16="0"+(value_16.length()/2)+value_16;
					}else{
						value_16=(value_16.length()/2)+value_16;
					}
					break;
				}
					
				default:
					break;
				}
				if(!field.getId().equals("tpdu")){
						sth+=value_16.equals("")?EncodeUtil.hex(field.getByteValue()):value_16;	
				}
			
				System.out.println(field.getId() + ":"+  EncodeUtil.hex(field.getByteValue()));
				byteOutPut.write(field.getByteValue());
			}
		}
		byte[] beforeSend = byteOutPut.toByteArray();//数据：tpdu+消息类型+bitmap+包体+mac
		byte[] bts = new byte[beforeSend.length + 5];//数据包头（1）+数据包长度（2）+数据+数据包尾(1)+LRC(1)
		
		//计算长度
		byte[] lenArr = EncodeUtil.bcd(String.valueOf(beforeSend.length),2);
		//设置包长
		System.arraycopy(lenArr, 0, bts, 1, 2);//数据包长度，复制到2-3位
		//设置数据
		System.arraycopy(beforeSend,0, bts, 3, beforeSend.length);//把数据放到4-leng+4
		//设置包尾
		bts[bts.length-2]=0x03;//包尾
		//设置数据包头
		bts[0]=0x02;
		//设置校验位
		bts[bts.length-1]=0x01;
		System.out.println(Arrays.toString(bts));
     	return bts;
	}
	
	//生成前两个字节的长度位
	//根据约定不同需要对此方法进行重写
	protected abstract byte[] msgLength(int length);
	
	//生成最后一位的MAC加密
	protected abstract byte[] mac(IsoPackage isoPackage) throws Exception;
	
	//对返回的数据进行MAC校验	
	//protected abstract void macValidate(IsoPackage isoPackage,Map<String,String> map);
	protected abstract void macValidate(String sth ,String mackey,Map<String,String> map);

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }
    
    public static byte[] Int2ByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for ( int i = iArrayLen; (i < 4) && (i > 0); i--) {
			bLocalArr[i-1] = (byte)( iSource>>8*(iArrayLen-i) & 0xFF );
		}
		return bLocalArr;
	}

	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}
    


    
}
