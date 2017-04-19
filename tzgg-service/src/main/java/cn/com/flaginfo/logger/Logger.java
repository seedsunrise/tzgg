package cn.com.flaginfo.logger;


import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by chenzhipan on 2016/11/2.
 */
public class Logger {

    private static Set<String> logs = new HashSet();

    private static Set<String> logsCopy = new HashSet<>();

    private org.slf4j.Logger  logger;

    public Logger(Class clazz){
        logger  = LoggerFactory.getLogger(clazz);
        logs.add(logger.getName());
        logsCopy.add(logger.getName());
    };


    /**
     * 获取所有打印日志的类
     * @return
     */
    public Set<String> getLogs(){
        return logs;
    }

    /**
     * 删除某个类的日志输出
     * @param name
     * @return
     */
    public Set<String> remove(String name){
        logs.remove(name);
        return logs;
    }

    /**
     * 清除所有的日志输出
     * @return
     */
    public Set<String> removeAll(){
        logs.clear();
        return logs;
    }

    /**
     * 激活某一个日志
     * @param className
     * @return
     */
    public Set<String> activate(String className){
        logs.add(className);
        return  logs;
    }

    /**
     * 激活所有的日志
     * @return
     */
    public Set<String> activateAll(){
        logs.addAll(logsCopy);
        return logs;
    }


    /**
     * debug打印日志
     * @param message
     */
    public void debug(String message){
        if(logs.contains(logger.getName())){
            logger.debug(message);
        }
    }

    /**
     * debug打印日志
     * @param message
     */
    public void debug(String message,Object ...o){
        if(logs.contains(logger.getName())){
            logger.debug(message,o);
        }
    }

    /**
     * info打印日志
     * @param message
     */
    public void info(String message){
        if(logs.contains(logger.getName())){
            logger.info(message);
        }
    }

    /**
     * info打印日志
     * @param message
     */
    public void info(String message,Object... o){
        if(logs.contains(logger.getName())){
            logger.info(message,o);
        }
    }

    /**
     * error打印日志
     * @param message
     */
    public  void error(String message){
        if(logs.contains(logger.getName())){
            logger.error(message);
        }
    }

    /**
     * error打印日志
     * @param message
     */
    public  void error(String message,Object o){
        if(logs.contains(logger.getName())){
            logger.error(message,o);
        }
    }

}
