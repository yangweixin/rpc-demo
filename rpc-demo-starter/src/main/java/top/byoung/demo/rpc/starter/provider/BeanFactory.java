package top.byoung.demo.rpc.starter.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: BeanFactory
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
public class BeanFactory {

    private static Map<Class<?>, Object> beans = new HashMap<>();

    public BeanFactory() {
    }

    public static void addBean(Class<?> interfaceClass, Object bean) {
        beans.put(interfaceClass, bean);
    }

    public static Object getBean(Class<?> interfaceClass) {
        return beans.getOrDefault(interfaceClass, null);
    }
}
