package com.baiye.container;

import com.baiye.helper.ClassHelper;
import com.baiye.task.SimpleTask;
import com.baiye.task.Task;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Baiye on 2017/1/19.
 */
public class SimpleTaskLocalContainer extends AbstractContainer{

    private ExecutorService executorService;

    public SimpleTaskLocalContainer(String packageName) {
        super(packageName);
    }

    public SimpleTaskLocalContainer(String packageName, Integer THREAD_POOL_SIZE) {
        super(packageName, THREAD_POOL_SIZE);
    }

    @Override
    protected void init() {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public void run() {
        Set<Class<?>> classSet = ClassHelper.getBaiyeTaskClassAnnotation(packageName);
        if(CollectionUtils.isEmpty(classSet))
            return;

        for (Class<?> cls : classSet) {
            Object classInstance = null;
            try {
                classInstance = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            List<Method> methodList = ClassHelper.getSimpleTaskMethods(cls);
            if(CollectionUtils.isNotEmpty(methodList))
            {
                for (Method method : methodList) {
                    Task task = new SimpleTask(classInstance,method,new Object[]{});
                    executorService.execute(task);
                }
            }

        }
    }
}