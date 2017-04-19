package cn.com.flaginfo.pojo;

/**
 * 
 *<p>Title:组织树对象 </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年11月30日
 */
public class OrgTreePO {
	
     private String id;
     
     private String name;
     
     private String pId;
     
     private Integer groupLevel;
     
     //1:选中  0未选
     private Integer isChecked;
     
     private Integer sort;
     
     

	public Integer getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(Integer groupLevel) {
		this.groupLevel = groupLevel;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public Integer getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Integer isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "OrgTreePO [id=" + id + ", name=" + name + ", pId=" + pId
				+ ", isChecked=" + isChecked + "]";
	}
     

}
