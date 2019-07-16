import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Auther: yuyao
 * @Date: 2019/5/29 10:49
 * @Description:
 */
public class TestInvocationHandler implements InvocationHandler {



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Class:" + method.getDeclaringClass().getName());
        System.out.println("Method:"  + method.getName());
        System.out.println("parameterTypes:" + Arrays.toString(method.getParameterTypes()));
        System.out.println("parameterVals:" + Arrays.toString(args));
        return null;
    }


}