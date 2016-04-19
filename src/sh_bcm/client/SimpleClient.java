package sh_bcm.client;

import sh_bcm.util.EncodeUtil;




/**
 * <p>发送客户端.</p>
 *
 * @author Magic Joey
 * @version AbstractClient.java 1.0 Created@2014-07-10 10:43 $
 */
public class SimpleClient extends AbstractClient{
	
	//构造方法
	public SimpleClient(String ip, int port){
		super(ip, port);
	}
	
	public SimpleClient(String ip,int port,int timeout) {
		super(ip, port, timeout);
	}

	
	// 接口长度的定义方式，可根据需求更改
	//因为取了前3个字节 ，然后第2 ，3是长度
	@Override
	protected int computeLength(byte[] lenBts){
		if(lenBts.length!=3){
			throw new IllegalArgumentException("字节长度不正确，预期值为3，实际值为："+lenBts.length);
		}
		// int size = ((lenbuf[0] & 0xff) << 8) | (lenbuf[1] & 0xff);//普通的长度编码
		byte[] len = new byte[2];
		len[0]=lenBts[1];
		len[1]=lenBts[2];
		return Integer.valueOf(EncodeUtil.hex(len));
		/*return (lenBts[1] & 0xff) * 256
				+ (lenBts[2] & 0xff);*/
		
	}

}
