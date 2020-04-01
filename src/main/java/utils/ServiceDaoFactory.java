package utils;




import domain.Privilege;
import domain.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


/**
 * Created by ozc on 2017/4/12.
 */
public class ServiceDaoFactory {

    private static final ServiceDaoFactory factory = new ServiceDaoFactory();

    private ServiceDaoFactory() {
    }

    public static ServiceDaoFactory getInstance() {
        return factory;
    }


    //需要判断该用户是否有权限
    public <T> T createDao(String className, Class<T> clazz, final User user) {

//    * proxy:代理类代理的真实代理对象
//    * method:我们所要调用某个对象真实的方法的Method对象
//    * args:指代代理对象方法传递的参数
//    * 通过Proxy类的newProxyInstance方法创建代理对象，我们来看下方法中的参数
//        ---
//    * 第一个参数：people.getClass().getClassLoader()，使用handler对象的classloader对象来加载我们的代理对象
//    * 第二个参数：people.getClass().getInterfaces()，这里为代理类提供的接口是真实对象实现的接口，这样代理对象就能像真实对象一样调用接口中的所有方法
//    * 第三个参数：handler，我们将代理对象关联到上面的InvocationHandler对象上
        try {
            //得到该类的类型
            final T t = (T) Class.forName(className).newInstance();
            //返回一个动态代理对象出去
            return (T) Proxy.newProxyInstance(ServiceDaoFactory.class.getClassLoader(), t.getClass().getInterfaces(), new InvocationHandler() {

                @Override

                public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, PrivilegeException {
                    //判断用户调用的是什么方法
                    String methodName = method.getName();
                    System.out.println(methodName);

                    //得到用户调用的真实方法
                    Method method1 = t.getClass().getMethod(methodName,method.getParameterTypes());

                    //查看方法上有没有注解
                    permission permis = method1.getAnnotation(permission.class);

                    //如果注解为空，那么表示该方法并不需要权限，直接调用方法即可
                    if (permis == null) {
                        return method.invoke(t, args);
                    }

                    //如果注解不为空，得到注解上的权限
                    String privilege = permis.value();

                    //设置权限【后面通过它来判断用户的权限有没有自己】
                    Privilege p = new Privilege();
                    p.setName(privilege);

                    //到这里的时候，已经是需要权限了，那么判断用户是否登陆了
                    if (user == null) {

                        //这里抛出的异常是代理对象抛出的，sun公司会自动转换成运行期异常抛出，于是在Servlet上我们根据getCause()来判断是不是该异常，从而做出相对应的提示。
                        throw new PrivilegeException("对不起请先登陆");
                    }

                    //执行到这里用户已经登陆了，并且方法需要权限，判断用户有没有权限
                    Method m = t.getClass().getMethod("findUserPrivilege", String.class);
                    List<Privilege> list = (List<Privilege>) m.invoke(t, user.getId());

                    //看下权限集合中有没有包含方法需要的权限。使用contains方法，在Privilege对象中需要重写hashCode和equals()
                    if (!list.contains(p)) {
                        //这里抛出的异常是代理对象抛出的，sun公司会自动转换成运行期异常抛出，于是在Servlet上我们根据getCause()来判断是不是该异常，从而做出相对应的提示。
                        throw new PrivilegeException("您没有权限，请联系管理员！");
                    }

                    //执行到这里的时候，已经有权限了，所以可以放行了
                    return method.invoke(t, args);
                }
            });

        } catch (Exception e) {
            new RuntimeException(e);
        }
        return null;
    }
}