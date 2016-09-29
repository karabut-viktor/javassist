package javassist;

import java.lang.reflect.InvocationTargetException;

public class LineNumberTest extends JvstTestRoot {


  public LineNumberTest(String name) {
    super(name);
  }

  public void testAddMethod() throws Exception {
    CtClass cc = sloader.makeClass("test1.Loop");
    CtMethod m = CtNewMethod.make(
        "public void run() { " +
            "String a = null; " +
            "int k = 2;" +
            "a.toString();" +
            "k = 3;" +
            "}",
        cc);
    cc.addMethod(m);
    cc.writeFile();
    Object obj = make(cc.getName());
    assetFirstLineNumber(obj, 40003);
  }

  private void assetFirstLineNumber(Object obj, int lineNumber) throws Exception {
    try {
      invoke(obj, "run");
      fail("Exception thrown expected!");
    }
    catch (InvocationTargetException ite) {
      StackTraceElement[] stackTrace = ite.getCause().getStackTrace();
      assertEquals(lineNumber, stackTrace[0].getLineNumber());
    }
  }
}
