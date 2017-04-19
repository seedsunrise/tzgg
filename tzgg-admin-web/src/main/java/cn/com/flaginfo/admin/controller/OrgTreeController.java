package cn.com.flaginfo.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.flaginfo.pojo.OrgTreePO;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.service.NotifyOrgService;
import cn.com.flaginfo.service.OrgTreeService;
import cn.com.flaginfo.utils.AdminSSOUserUtils;
import cn.com.flaginfo.utils.HeadHelper;

/**
 * 
 * <p>
 * Title:组织架构Controller
 * </p>
 * *
 * <p>
 * Description:
 * </p>
 * *
 * <p>
 * Company:flaginfo
 * </p>
 * 
 * @author liming
 * @date 2016年12月7日
 */
@Controller
@RequestMapping("orgTree")
public class OrgTreeController {

	@Autowired
	private OrgTreeService orgTreeService;

	@Autowired
	private NotifyOrgService notifyOrgService;

	/**
	 * 
	 * desc:
	 * 
	 * @return
	 * @author:liming
	 * @date:2016年12月7日
	 */
	@RequestMapping("org")
	@ResponseBody
	public Object getOrgTree(HttpServletRequest request, String notifyId,
			Integer isManager) {

		SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
		String groupName = ssoUser.getCurRole().getGroupName();
		// 获取相关数据
		String groupId = ssoUser.getCurRole().getGroupId();
		String spId = ssoUser.getSpId();
		String contactsId = ssoUser.getContactsId();
		// 调用service从大平台获取组织结构
		List<OrgTreePO> list = orgTreeService.orgTree(groupId, spId, contactsId);
		// 添加管理员节点到list当中
		OrgTreePO self = new OrgTreePO();
		self.setId(groupId);
		self.setName(groupName);
		self.setGroupLevel(1);
		list.add(self);
		List<String> idList = null;
		// 通过ID反查发送给管理员的消息所包含的组织
		if (!StringUtils.isEmpty(notifyId) && isManager != null
				&& 1 == isManager) {

			idList = notifyOrgService.getOrgManager(notifyId);

		}
		// 通过ID反查发送给所有人的消息所包含的组织
		else if (!StringUtils.isEmpty(notifyId) && isManager != null
				&& 0 == isManager) {

			idList = notifyOrgService.getOrgAll(notifyId);

		}
		// 对比数据设置checked
		compareIdForOrgList(idList, list);

		return HeadHelper.response(list);
	}

	/**
	 * 
	 * desc:对比ID，设置checked
	 * 
	 * @param idList
	 * @param orgList
	 * @author:liming
	 * @date:2016年12月8日
	 */
	private void compareIdForOrgList(List<String> idList,
			List<OrgTreePO> orgList) {
		// 如果其中有一个list为空，直接返回
		if (CollectionUtils.isEmpty(idList) || CollectionUtils.isEmpty(orgList)) {
			return;
		}
		// size小的在外部循环减小循环次数
		for (String id : idList) {

			for (OrgTreePO org : orgList) {

				if (org.getId().equals(id)) {
					org.setIsChecked(1);
					break;
				}

			}

		}

	}

}
