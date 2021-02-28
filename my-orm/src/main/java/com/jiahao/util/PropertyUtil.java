package com.jiahao.util;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取工具类
 * @author JiaHao
 */
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Properties props;
    static{
        loadProps();
    }

    synchronized static private void loadProps(){
        logger.trace("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
             //<!--第一种，通过类加载器进行获取properties文件流-->
                    in = PropertyUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
             //!--第二种，通过类进行获取properties文件流-->
                    //in = PropertyUtil.class.getResourceAsStream("/jdbc.properties");
                    props.load(in);
        } catch (FileNotFoundException e) {
            logger.error("jdbc.properties文件未找到");
        } catch (IOException e) {
            logger.error("出现IOException");
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("jdbc.properties文件流关闭出现异常");
            }
        }
        logger.trace("加载properties文件内容完成...........");
        logger.trace("properties文件内容：" + props);
    }

    public static String getProperty(String key){
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
