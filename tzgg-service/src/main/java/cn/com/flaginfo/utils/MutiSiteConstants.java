package cn.com.flaginfo.utils;



public class MutiSiteConstants {
	public static final String JSONSERVERIP = "jsonServerIp";
	public static final String API_USER_KEY = "api.user.key";
	public static final String API_USER_VERSION = "api.user.version";
	public static final String SPID = "api.user.spId";
	public static final String CONTACTSID = "api.user.contactsId";
	public static final String FILE_UPLOAD_URL = "file_upload_url";
	public static final String FILE_DOWNLOAD_URL = "file_downLoad_url";
	public static final String APP_CONTEXT_DOMAIN = "app.context.domain";
	/**获取spid和contactsId下所有组织节点**/
	public static final String CONTACTS_GROUP_LIST="contacts_group_list";
	public static final String CONTACTS_GROUP_CHILDREN_LIST="contacts_group_children_list";
	public static final String CONTACTS_GROUP_MEMBER_COUNT="contacts_group_member_count";
	public static final String CONTACTS_GROUP_TAG_LIST="contacts_group_tag_list";
	public static final String CONTACTS_CONF_LIST="contacts_conf_list";
	public static final String CONTACTS_MEMBER_GET_BY_GROUP = "contacts_member_get_by_group";
	public static final String CONTACTS_GROUP_GET_BY_ID = "contacts_group_get_by_id";
	public static final String CONTACTS_GROUP_PARENTS_LIST="contacts_group_parents_list";
	public static final String ROLE_LIST="role_list";
	public static final String GLOBAL="global";
	/**安康党建redis-key的前缀**/
	public static final String JEDISTITLE=":";
	/**需要同步的groupId**/
	public static final String SYNC_GROUP_ID="api.sync.groupId";
	public static final String SSOUSER="sso_user";
	public static final String API_CONTACTS_VERSION="api.contacts.version";
	public static final String NO_PASS="no_pass"; 
	public static final String PASS="pass"; 
	/**各种类型的appId**/
	public static final String PARTY_ACTIVITY_APPID = "party.activity.appid";
	/**模板内容的key**/
	public static final String PARTY_ACTIVITY = "partyActivity";
	/**视频地址**/
	public static final String VEDIO_UPLOAD_URL="api.vedio.url";
	
	public static final String VEDIO_CHECK="vedio.check";
	
	/**超级管理员**/
	public static final String USER_ROLE_SUPER_ADMIN = "0";
	/**write**/
	public static final String GROUP_AUTH_WRITE = "write";
	/**read**/
	public static final String GROUP_AUTH_READ="read";
	/**操作权限**/
	public static final String GROUP_AUTH="auth";
	/**超级管理员id**/
	public static final String SUPER_ADMIN_ID = "admin";

	/**parent node**/
	public static final String GROUP_AUTH_PARENT="none";

	/**成功标志**/
	public static final int SUCCESS = 200;
	/**成功标志**/
	public static final String SUCCESS_STR = "200";
	/**redis缓存时间**/
	public static final int REDIS_MINUTE = 10 * 60;

	/**删除标志**/
	public static final int DEL = 1;
	public static final int NO_DEL = 0;
	/**发送消息接口**/
    public static final String CMC_SEND="cmc_send";

}
