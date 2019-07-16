import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.msgpack.template.Templates.TString;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 18:11
 * @Description:
 */
public class Test {


    public String a(String a){
        return a + "*********";
    }

    public String b(Object a, List<String> list , Integer c, Boolean d){
        System.out.println("多参数方法");
        return "";
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
//        Test t = new Test();
//        Method a = Test.class.getDeclaredMethod("a", new Class[]{String.class});
//        Object invoke = MethodUtil.invoke(a, t, new Object[]{"abc"});
//        System.out.println(invoke);


        List<String> src = new ArrayList<>();
        src.add("abc");
        src.add("msgpack");
        src.add("viver");
        MessagePack messagePack = new MessagePack();
        byte[] raw = messagePack.write(src);
        List<String> dst = messagePack.read(raw, Templates.tList(TString));
        dst.forEach(s -> System.out.println(s));


    }




}