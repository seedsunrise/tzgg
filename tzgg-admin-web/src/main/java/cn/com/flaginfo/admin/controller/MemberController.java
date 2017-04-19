package cn.com.flaginfo.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.flaginfo.pojo.MemberPO;
import cn.com.flaginfo.pojo.OrgTreePO;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.service.MemberService;
import cn.com.flaginfo.utils.AdminSSOUserUtils;
import cn.com.flaginfo.utils.HeadHelper;

/**
 * 
 * <p>
 * Title:人员Controller
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
 * @date 2016年12月8日
 */
@Controller
@RequestMapping("member")
public class MemberController {

	@Autowired
	private MemberService memberService;

	/**
	 * 
	 * desc:获取人员列表
	 * 
	 * @param request
	 * @param notifyId
	 * @return
	 * @author:liming
	 * @date:2016年12月8日
	 */
	@RequestMapping("list")
	@ResponseBody
	public Object getMemberList(HttpServletRequest request, String notifyId) {

		SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
		// 组装数据
		String groupId = ssoUser.getCurRole().getGroupId();
		String spId = ssoUser.getSpId();
		String contactsId = ssoUser.getContactsId();

		List<MemberPO> list = memberService.getMemberList(groupId, spId,contactsId);

		if (!StringUtils.isEmpty(notifyId)) {
			List<String> idList = memberService.getMemberByNotifyId(notifyId);
			compareIdForMemberList(idList, list);
		}

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
	private void compareIdForMemberList(List<String> idList,
			List<MemberPO> memberList) {
		// 如果其中有一个list为空，直接返回
		if (CollectionUtils.isEmpty(idList)
				|| CollectionUtils.isEmpty(memberList)) {
			return;
		}
		// size小的在外部循环减小循环次数
		for (String id : idList) {

			for (MemberPO member : memberList) {

				if (member.getId().equals(id)) {
					member.setIsChecked(1);
					break;
				}

			}

		}

	}

}
