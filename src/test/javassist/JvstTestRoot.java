package javassist;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class JvstTestRoot extends TestCase {
    // the directory where all compiled class files are found.
    public static final String PATH = "../target/test-classes/";

    // the directory where javassist.jar is found.
    public static final String JAR_PATH = "../";

    ClassPool sloader, dloader;
    Loader cloader;

    public JvstTestRoot(String name) {
        super(name);
    }

    protected void print(String msg) {
        System.out.println(msg);
    }

    protected void print(Exception e) {
        e.printStackTrace();
    }

    protected void setUp() throws Exception {
        sloader = ClassPool.getDefault();
        dloader = new ClassPool(null);
        dloader.appendSystemPath();
        dloader.insertClassPath(".");
        cloader = new Loader(dloader);
    }

    protected Object make(String name) throws Exception {
        return cloader.loadClass(name).newInstance();
    }

    protected int invoke(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method, new Class[0]);
        Object res = m.invoke(target, new Object[0]);
        return ((Integer)res).intValue();
    }

    protected int invoke(Object target, String method, int arg)
        throws Exception {
        Method m =
            target.getClass().getMethod(method, new Class[] { int.class });
        Object res = m.invoke(target, new Object[] { new Integer(arg)});
        return ((Integer) res).intValue();
    }

    protected <T> T invoke(Object target, String method, Object... args)
        throws Exception {
        Method m = target.getClass().getDeclaredMethod(method);
        Object res = m.invoke(target, args);
        return (T) res;
    }
}
