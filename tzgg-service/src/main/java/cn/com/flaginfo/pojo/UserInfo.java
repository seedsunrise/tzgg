package cn.com.flaginfo.pojo;

public class UserInfo {
	private String id;
	private String name;
	//手机号
	private String personalPhoneNo;
	//
	private String memberId;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPersonalPhoneNo() {
		return personalPhoneNo;
	}
	public void setPersonalPhoneNo(String personalPhoneNo) {
		this.personalPhoneNo = personalPhoneNo;
	}
	/*public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}*/
	
}
