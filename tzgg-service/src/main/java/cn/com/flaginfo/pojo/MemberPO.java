package cn.com.flaginfo.pojo;

public class MemberPO {
 
	private String id;
	
	private String name;
	
	private Integer isChecked;
	
	private String phone;
	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Integer getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Integer isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "MemberPO [id=" + id + ", name=" + name + ", isChecked="
				+ isChecked + "]";
	}
	
	
	
}
