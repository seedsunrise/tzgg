package cn.com.flaginfo.app.controller;


import cn.com.flaginfo.logger.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chenzhipan on 2016/11/3.
 */
@Controller
@RequestMapping("logger")
public class LoggerController {

    Logger logger = new Logger(LoggerController.class);

    /**
     * 关闭所以的日志输出
     * @return
     */
    @RequestMapping("removeAll")
    @ResponseBody
    public Object removeAll(){
        logger.removeAll();
        return logger.getLogs();
    }

    /**
     * 关闭某一个类的日志输出
     * @param name
     * @return
     */
    @RequestMapping("remove")
    @ResponseBody
    public Object remove(String name){
        logger.remove(name);
        return logger.getLogs();
    }

    /**
     * 开启某一个类的日志输出
     * @param className
     * @return
     */
    @RequestMapping("activate")
    @ResponseBody
    public Object activate(String className){
        logger.activate(className);
        return logger.getLogs();
    }

    /**
     * 开启所有类的日志输出
     * @return
     */
    @RequestMapping("activateAll")
    @ResponseBody
    public Object activateAll(){
        logger.activateAll();
        return logger.getLogs();
    }

    /**
     * 获取所有在输出日志的类
     * @return
     */
    @RequestMapping("getLogs")
    @ResponseBody
    public Object getLogs(){
        return logger.getLogs();
    }
}
