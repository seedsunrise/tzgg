package cn.com.flaginfo.admin.controller;


import cn.com.flaginfo.commons.utils.config.SystemConfig;
import cn.com.flaginfo.dao.AttachmentsMapper;
import cn.com.flaginfo.example.AttachmentsExample;
import cn.com.flaginfo.pojo.Attachments;
import cn.com.flaginfo.utils.HeadHelper;
import cn.com.flaginfo.utils.MutiSiteConstants;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("fileUpload")
public class FileUploadController {

    @Autowired
    private AttachmentsMapper attachmentsMapper;

    @ResponseBody
    @RequestMapping(value = "/upload", produces = "application/json;charset=utf8")
    public Object uploadFile(HttpServletRequest request) throws IOException {
        MultipartResolver resolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        final CommonsMultipartFile orginalFile = (CommonsMultipartFile) multipartRequest.getFile("file");
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = orginalFile.getOriginalFilename();
        String type = fileName.split("\\.")[fileName.split("\\.").length - 1];
        InputStream inputStream = null;
        HttpClient client = new HttpClient();
        List<String> ts = null;// 返回
        String targetURL = null;// 指定URL
        String downURL = null;// 指定URL
        if (!orginalFile.isEmpty()) {
            targetURL = SystemConfig.getString(MutiSiteConstants.FILE_UPLOAD_URL);
            downURL = SystemConfig.getString(MutiSiteConstants.FILE_DOWNLOAD_URL);
            PostMethod filePost = new PostMethod(targetURL);
            try {
                Part[] parts = {new FilePart("Filedata", new PartSource() {
                    @Override
                    public long getLength() {
                        return orginalFile.getSize();
                    }

                    @Override
                    public String getFileName() {
                        return orginalFile.getOriginalFilename();
                    }

                    @Override
                    public InputStream createInputStream() throws IOException {
                        return orginalFile.getInputStream();
                    }
                }), new StringPart("access_token", "token"), new StringPart("type", type),};
                filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
                client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                int status = client.executeMethod(filePost);
                inputStream = filePost.getResponseBodyAsStream();
                ts = IOUtils.readLines(inputStream);
                String re = ts.get(0);
                JSONObject json = JSONObject.fromObject(re);
                json.put("path", downURL + File.separator + json.optString("path"));
                map.put("status", String.valueOf(status));
                map.put("result", json);
                map.put("fileName", fileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                filePost.releaseConnection();
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return HeadHelper.response(map);
    }

    /**
     * 文件下载
     * @param id
     * @param response
     * @return
     */
    @RequestMapping("downLoad")
    @ResponseBody
    public Object downLoad(String id, HttpServletResponse response) {
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            AttachmentsExample attachmentsExample = new AttachmentsExample();
            attachmentsExample.createCriteria().andIdEqualTo(id);
            List<Attachments> attachmentses = attachmentsMapper.selectByExample(attachmentsExample);
            if (CollectionUtils.isEmpty(attachmentses)) {
                return HeadHelper.errResponse("找不到该附件");
            }
            Attachments attachments = attachmentses.get(0);
            String path = attachments.getPath().replace("/\\","/");
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            input = new DataInputStream(conn.getInputStream());
            output = new DataOutputStream(response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment;filename=" + new String( attachments.getName().getBytes("gb2312"), "ISO8859-1" ) + ";");
            byte[] buffer = new byte[1024 * 8];
            int count = 0;
            while ((count = input.read(buffer)) > 0) {
                output.write(buffer, 0, count);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
                if (null != input) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return HeadHelper.response("");
    }
}
