package sh_bcm_card.factory;

import java.util.Map;

import sh_bcm_card.model.IsoPackage;
import sh_bcm_card.util.EncodeUtil;
import sh_bcm_card.util.encrypt.MacUtil;



/**
 * <p>报文组装抽象类.</p>
 *
 * @author Magic Joey
 * @version IsoMsgFactory.java 1.0 Created@2014-07-09 17:41 $
 */
public class IsoMsgFactory extends AbstractIsoMsgFactory{
	
	private  static IsoMsgFactory isoMsgFactory=new IsoMsgFactory();
	
	protected IsoMsgFactory(){
		
	}

	public static IsoMsgFactory getInstance(){
		return isoMsgFactory;
	}

	//生成前两个字节的长度位
	//根据约定不同需要对此方法进行重写
	@Override
	protected byte[] msgLength(int length) {
		byte[] rByte = new byte[2];
		rByte[0] = (byte)(length/255);
		rByte[1] = (byte)(length%255);
		return rByte;
	}

	//重写MAC校验方法，验证失败则抛出运行时异常
	@Override
	protected void macValidate(String sth,String mackey,Map<String,String> map) {
		String mac = null;
		try {
			mac = MacUtil.MAC(macKey, null, sth);
			//mac =  EncodeUtil.hex(mac(isoPackage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String returnMac = EncodeUtil.hex(EncodeUtil.binary(map.get("64")));//Binary编码转换为hex编码
		if(!(mac.substring(0,8).equals(returnMac.substring(0,8)))){
			throw new RuntimeException("MAC校验失败，返回值"+returnMac+",计算值"+mac);
		}
	}

	@Override
	protected byte[] mac(IsoPackage isoPackage) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
