package cn.com.flaginfo.pojo;
/**
 * 登录用户角色
 * @author ping.wang@flaginfo.com.cn
 *
 */
public class SpRole {
    /**
     * 角色类别
     */
	private String roleType;
	/**
	 * 角色名称
	 */
	private String roleName;
	/**
	 * 角色ID
	 */
	private String roleId;
	/**
	 * 分组ID
	 */
	private String groupId;

	private String groupName;

	private String contactsId;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getContactsId() {
		return contactsId;
	}

	public void setContactsId(String contactsId) {
		this.contactsId = contactsId;
	}

	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	@Override
	public String toString() {
		return "SpRole [roleType=" + roleType + ", roleName=" + roleName + ", roleId=" + roleId + "]";
	}
	
	
}
