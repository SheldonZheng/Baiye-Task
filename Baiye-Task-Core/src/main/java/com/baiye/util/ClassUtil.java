package com.baiye.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Baiye on 2017/1/17.
 */
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    public static ClassLoader getClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Set<Class<?>> getClassSet (String packageName)
    {
        Set<Class<?>> classSet = new HashSet<Class<?>>();

        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements())
            {
                URL url = urls.nextElement();
                if(url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file"))
                    {
                        String packagePath = url.getPath().replaceAll("%20","");
                        addClass(classSet,packagePath,packageName);
                    }
                    else if (protocol.equals("jar"))
                    {
                        readJarFile(classSet,url);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("get class set failure{}",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    public static Set<Class<?>> getClassSet(String packageName,ClassLoader classLoader,String jarFilePath)
    {
        Set<Class<?>> classSet = new HashSet<Class<?>>();

        try {
            JarFile jarFile = new JarFile(jarFilePath);
            if(jarFile != null)
            {
                Enumeration<JarEntry> entries = jarFile.entries();
                while(entries.hasMoreElements())
                {
                    String jarEntryName = entries.nextElement().getName();
                    if(StringUtils.isNotBlank(jarEntryName) && jarEntryName.endsWith(".class"))
                    {
                        String clsName = getClassName(jarEntryName);
                        Class cls = classLoader.loadClass(clsName);
                        if(cls != null)
                            classSet.add(cls);
                    }
                }
            }
            /*System.out.println(classLoader.getResource(""));
            Enumeration<URL> urls = classLoader.getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements())
            {
                URL url = urls.nextElement();
                if(url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file"))
                    {
                        String packagePath = url.getPath().replaceAll("%20","");
                        addClass(classSet,packagePath,packageName);
                    }
                    else if (protocol.equals("jar"))
                    {
                        readJarFile(classSet,url);
                    }
                }
            }*/
        } catch (IOException e) {
            logger.error("get class set failure{}",e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            logger.error("get class set failure{}",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    public static Class<?> loadClass(String className,boolean isInitialized)
    {
        Class<?> cls = null;
        try {
            //  System.out.println(className);
            cls = Class.forName(className,isInitialized,getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("load class failure{}",e);
            throw new RuntimeException(e);
        }

        return cls;
    }

    private static void addClass(Set<Class<?>> classSet,String packagePath,String packageName)
    {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for(File file : files)
        {
            String fileName = file.getName();
            if(file.isFile())
            {
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtils.isNotEmpty(packageName))
                    className = packageName + "." + className;
                doAddClass(classSet,className);
            }
            else
            {
                String subPackagePath = fileName;
                if(StringUtils.isNotEmpty(packagePath))
                    subPackagePath = packagePath + "/" + subPackagePath;

                String subPackageName = fileName;
                if(StringUtils.isNotEmpty(packageName))
                    subPackageName = packageName + "." + subPackageName;

                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet,String className)
    {
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }

    private static void readJarFile(Set<Class<?>> classSet,URL url) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        if(jarURLConnection != null)
        {
            JarFile jarFile = jarURLConnection.getJarFile();
            if(jarFile != null)
            {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while(jarEntries.hasMoreElements())
                {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if(jarEntryName.endsWith(".class"))
                    {
                        String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                        doAddClass(classSet,className);
                    }
                }
            }
        }
    }

    private static String getClassName(String jarEntryName) {
        if (jarEntryName.endsWith(".class")) {
            return jarEntryName.replace("/", ".").substring(0, jarEntryName.lastIndexOf("."));
        }
        return null;
    }
}
