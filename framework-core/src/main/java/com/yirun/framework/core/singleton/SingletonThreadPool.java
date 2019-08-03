package com.yirun.framework.core.singleton;

import java.util.concurrent.*;

/**
 * 默认线程池
 *
 * @author zc.ding
 * @create 2018/9/28
 */
public class SingletonThreadPool {
    
    private static volatile ExecutorService FIXED_THREAD_POOL;
    private final static String FIXED_THREAD_POOL_LOCK = "FIXED_THREAD_POOL";
    
    private static volatile ExecutorService WORK_STEALING_POOL;
    private final static String WORK_STEALING_POOL_LOCK = "WORK_STEALING_POOL";
    
    private static volatile ExecutorService CACHED_THREAD_POOL;
    private final static String CACHED_THREAD_POOL_LOCK = "CACHED_THREAD_POOL";
    
    private SingletonThreadPool(){}
    
    /**
    *  通过FixedThreadPool池执行业务操作
    *  <p>
    *       核心线程数：20， 最大500， 线程空闲时间0秒     
    *  </p>
    *  @param callable  业务操作
    *  @return java.util.concurrent.Future<T>
    *  @date  Date              ：2018/9/28
    *  @author                  ：zc.ding@foxmail.com
    */
    public static <T> Future<T> callFixedThreadPool(Callable<T> callable){
        if(FIXED_THREAD_POOL == null){
            synchronized (FIXED_THREAD_POOL_LOCK){
                if(FIXED_THREAD_POOL == null){
                    FIXED_THREAD_POOL = new ThreadPoolExecutor(50, 200, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> FIXED_THREAD_POOL.shutdown()));
                }
            }
        }
        return FIXED_THREAD_POOL.submit(callable);
    }

    /**
     *  通过WorkStealThreadPool池执行业务操作
     *  @param callable  业务操作
     *  @return java.util.concurrent.Future<T>
     *  @date  Date              ：2018/9/28
     *  @author                  ：zc.ding@foxmail.com
     */
    public static <T> Future<T> callWorkStealPool(Callable<T> callable){
        if(WORK_STEALING_POOL == null){
            synchronized (WORK_STEALING_POOL_LOCK){
                if(WORK_STEALING_POOL == null){
                    WORK_STEALING_POOL = Executors.newWorkStealingPool();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> WORK_STEALING_POOL.shutdown()));
                }
            }
        }
        return WORK_STEALING_POOL.submit(callable);
    }

    /**
     *  通过CachedThreadPool池执行业务操作
     *  <p>
     *      核心线程数20，最大增加至500线程，60s存活时间
     *  </p>
     *  @param callable  业务操作
     *  @return java.util.concurrent.Future<T>
     *  @date  Date              ：2018/9/28
     *  @author                  ：zc.ding@foxmail.com
     */
    public static <T> Future<T> callCachedThreadPool(Callable<T> callable){
        if(CACHED_THREAD_POOL == null){
            synchronized (CACHED_THREAD_POOL_LOCK){
                if(CACHED_THREAD_POOL == null){
                    CACHED_THREAD_POOL = new ThreadPoolExecutor(20, 500, 60L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> CACHED_THREAD_POOL.shutdown()));
                }
            }
        }
        return CACHED_THREAD_POOL.submit(callable);
    }
}
