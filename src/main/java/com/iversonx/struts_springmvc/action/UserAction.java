package com.iversonx.struts_springmvc.action;

import com.iversonx.struts_springmvc.bean.User;
import com.iversonx.struts_springmvc.service.UserService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class UserAction extends ActionSupport {

    @Autowired
    private UserService userService;

    private static final long serialVersionUID = -1353901915599323577L;

    private Integer id;
    private List<User> users;
    private User user;
    /**
     * 列表 已支持
     */
    public String list() {
        users = userService.list();
        return "success";
    }

    /**
     * 详情 已支持
     */
    public String detail() {
        user = userService.detail(id);
        return "detail";
    }

    /**
     * 已支持 type="redirect"
     */
    public String add() {
        userService.add(user);
        return "add";
    }

    /**
     * 已支持 type="redirectAction"
     */
    public String update() {
        userService.update(user);
        return "update";
    }

    /**
     * 已支持 type="redirect"
     */
    public String delete() {
        userService.delete(id);
        return "success";
    }

    // 已支持 文件上传 type="redirect"
    private File file;
    private String fileName;
    public String upload() throws IOException {
        //name:upload_42654f24_5bfe_4665_aaa5_25e560a6c4b5_00000002.tmp
        //path:45845
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        FileUtils.copyFile(file, new File("upload/" + fileName));
        return "success";
    }

    // 文件下载 支持
    public void download() throws IOException {
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        HttpServletResponse response = attr.getResponse();
        //获取路径
        String path = request.getSession().getServletContext().getRealPath("/show.jsp");

        //获取文件
        File file = new File(path);
        response.setContentLength((int) file.length());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=show.jsp");

        byte[] buffer = new byte[400];
        int len = 0;

        InputStream is = new FileInputStream(file);
        OutputStream os = response.getOutputStream();

        while((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        os.close();
        is.close();
    }

    // 文件下载2 支持
    public void download2() throws Exception{
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String path = request.getSession().getServletContext().getRealPath("/detail.jsp");
        fileName = "detail2.jsp";
        inputStream =  new FileInputStream(new File(path));
    }

    private InputStream inputStream;
    public InputStream getInputStream() throws Exception{
        return inputStream;
    }

    // ajax请求1
    public void ajax1() throws Exception{
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = attr.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print("HelloWorld");
        out.flush();
        out.close();
    }


    // ajax请求3
    private InputStream ajax3Stream;
    public String ajax3() throws Exception {
        String str = "HelloWorld3333";
        ajax3Stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        return "success";
    }

    public InputStream getAjax3Stream() {
        return ajax3Stream;
    }

    // ajax请求4 暂不支持
    private String name;
    private String sex;

    public String ajax4() {
        name = "kobe";
        sex = "男";
        return "success";
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
