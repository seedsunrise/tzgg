package cn.com.flaginfo.utils;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 任务管理器
 * 用ExecutorService实现
 *
 */
public class TaskManager {
	//利用Guava提供的ThreadFactoryBuilder工具类来生成特殊命名的工作线程.
	final ThreadFactory taskThreadFactory = new ThreadFactoryBuilder()
										    .setNameFormat("TaskPool-%d")
										    .setDaemon(true)
										    .build();
	//private final static ExecutorService executorService = Executors.newFixedThreadPool(POOL_THREAD_NUM,taskThreadFactory);
	final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
	// corePoolSize :5 
	// maximumPoolSize : 30
	ExecutorService executorService = new ThreadPoolExecutor(5, 30,
	        5L, TimeUnit.MILLISECONDS,
	        queue,taskThreadFactory);
	
	//饿汉式单例模式，全局的单例实例在类加载时构建
	private TaskManager(){}
	
	private static final TaskManager taskManager = new TaskManager();
	
	public static TaskManager getInstance(){
		return taskManager;
	}
	
	/**
	 * 提交一个任务到队列中执行
	 * @param command
	 */
	public void execute(Runnable command){
		executorService.execute(command);
    }
    
    public void shutdown(){
    	executorService.shutdown();
    }
    
    public Future<?> submit(Runnable task){
    	return executorService.submit(task);
    }
    
    public boolean isShutdown(){
    	return executorService.isShutdown();
    }
    
    public boolean isTerminated(){
    	return executorService.isTerminated();
    }
    
    public int getQueueSize(){
    	return queue.size();
    }
}
