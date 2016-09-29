package javassist;

import java.lang.reflect.InvocationTargetException;

public class LineNumberTest extends JvstTestRoot {


  public LineNumberTest(String name) {
    super(name);
  }

  public void testAddMethod() throws Exception {
    CtClass cc = sloader.makeClass("test1.Loop");
    CtMethod m = CtNewMethod.make(
        "public int run(int i) { " +
            "int k = 0;" +
            "String a = null; " +
            "k = 3;" +
            "a.toString();" +
            "while (true) { if (k++ > 10) return i; } }",
        cc);
    cc.addMethod(m);
    cc.writeFile();
    Object obj = make(cc.getName());
    try {
      invoke(obj, "run", 3);
    }
    catch (InvocationTargetException ite) {
      StackTraceElement[] stackTrace = ite.getCause().getStackTrace();
      ite.getCause().printStackTrace();
      assertEquals(50004, stackTrace[0].getLineNumber());
    }
  }
}
