package sh_bcm_card.entity;



/**
 * 撤销实体类
 * @author zhangk
 *
 */
public class Revocation extends Inquire{

	 private String authorizeNum;//授权码

	public String getAuthorizeNum() {
		return authorizeNum;
	}

	public void setAuthorizeNum(String authorizeNum) {
		this.authorizeNum = authorizeNum;
	}
	 
	 
}
