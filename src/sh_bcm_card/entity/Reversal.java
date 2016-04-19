package sh_bcm_card.entity;



/**
 * 冲正实体类
 * @author zhangk
 *
 */
public class Reversal extends Inquire{

	 private String authorizeNum;//授权码
	 private String cardNo;//卡片序列号
	 private String ICdata;//IC卡数据
	 private String processingCode;//处理码

	public String getAuthorizeNum() {
		return authorizeNum;
	}

	public void setAuthorizeNum(String authorizeNum) {
		this.authorizeNum = authorizeNum;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getICdata() {
		return ICdata;
	}

	public void setICdata(String iCdata) {
		ICdata = iCdata;
	}

	public String getProcessingCode() {
		return processingCode;
	}

	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	 
	 
}
