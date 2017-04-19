package cn.com.flaginfo.pojo;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NotifyAppPO {
	
	private String id;
	
	private String title;
	//1:公共 2：指定
	private String type;
	
	private String isRead;
	
	private String content;
	
	private Date publishTime;
	
	private String publishOrgName;
	
	private List<Attachments> attachmentList;
	
	private String searchString;
	
	private HashSet<String> allParentGroup;
	
	private List<String> groupList;
	
	private String userId;
	
	private Date detailPublishTime;
	
	private String spId;
	
	
	
	
	
	
	public String getSpId() {
		return spId;
	}

	public void setSpId(String spId) {
		this.spId = spId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getDetailPublishTime() {
		return detailPublishTime;
	}

	public void setDetailPublishTime(Date detailPublishTime) {
		this.detailPublishTime = detailPublishTime;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public HashSet<String> getAllParentGroup() {
		return allParentGroup;
	}

	public void setAllParentGroup(HashSet<String> allParentGroup) {
		this.allParentGroup = allParentGroup;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getPublishOrgName() {
		return publishOrgName;
	}

	public void setPublishOrgName(String publishOrgName) {
		this.publishOrgName = publishOrgName;
	}

	public List<Attachments> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<Attachments> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
	
	
}
