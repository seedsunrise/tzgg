package cn.com.flaginfo.pojo;

import java.util.List;

public class SsoUser {
	private SpInfo loginSpInfo;
	private UserInfo userInfo;
	private SpRole curRole;
	private List<String> groupList;
	private List<Role> roles;
	private String spId;
	private String contactsId;

	public String getSpId() {
		return spId;
	}

	public void setSpId(String spId) {
		this.spId = spId;
	}

	public String getContactsId() {
		return contactsId;
	}

	public void setContactsId(String contactsId) {
		this.contactsId = contactsId;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public SpInfo getLoginSpInfo() {
		return loginSpInfo;
	}
	public void setLoginSpInfo(SpInfo loginSpInfo) {
		this.loginSpInfo = loginSpInfo;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public SpRole getCurRole() {
		return curRole;
	}
	public void setCurRole(SpRole curRole) {
		this.curRole = curRole;
	}

}
