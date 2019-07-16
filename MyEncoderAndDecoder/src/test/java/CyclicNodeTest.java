



/**
 * ClassName: CyclicNodeTest
 *
 * @author leegoo
 * @Description:
 * @date 2019年06月11日
 */

public class CyclicNodeTest {
    static CyclicNode n5 = new CyclicNode();
    static CyclicNode n4 = new CyclicNode();
    static CyclicNode n3 = new CyclicNode();
    static CyclicNode n2 = new CyclicNode();
    static CyclicNode n1 = new CyclicNode();

    static {
        n1.setVal(1);
        n2.setVal(2);
        n3.setVal(3);
        n4.setVal(4);
        n5.setVal(5);
        n1.setNext(n2);
        n2.setNext(n3);
        n3.setNext(n4);
        n4.setNext(n5);

       n2.setPre(n1);
    }

    public static void main(String[] args) {
        System.out.println(n1);
    }



}

