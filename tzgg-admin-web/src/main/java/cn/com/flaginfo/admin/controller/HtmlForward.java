package cn.com.flaginfo.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by maowenjie on 2016/9/12.
 */
@Controller
@RequestMapping("html")
public class HtmlForward {

    @RequestMapping(value = "forward")
    public void forward(HttpServletResponse response) throws Exception{
        toHtml(response, "/page/notice/notice_management.html");
    }

    private void toHtml(HttpServletResponse response,String path) throws Exception{
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.sendRedirect(path);
    }
}
