package javassist;

import java.lang.reflect.InvocationTargetException;

public class LineNumberTest extends JvstTestRoot {


  public LineNumberTest(String name) {
    super(name);
  }

  public void testAddTwoMethods() throws Exception {
    CtClass cc = sloader.makeClass("generated.testAddTwoMethods");
    CtMethod m1 = CtNewMethod.make(
        "public void run1() { " +
            "String a = null; " +
            "a.toString();" +
            "}",
        cc);
    cc.addMethod(m1);
    CtMethod m2 = CtNewMethod.make(
        "public void run2() { " +
            "String a = null; " +
            "a.toString();" +
            "}",
        cc);
    cc.addMethod(m2);
    cc.writeFile();
    Object obj = make(cc.getName());
    assetFirstLineNumber(obj, 40002, "run1");
    assetFirstLineNumber(obj, 40002, "run2");
  }

  public void testAddMethod() throws Exception {
    CtClass cc = sloader.makeClass("generated.testAddMethod");
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
    assetFirstLineNumber(obj, 40003, "run");
  }

  private void assetFirstLineNumber(Object obj, int lineNumber, String methodName, Object... args) throws Exception {
    try {
      invoke(obj, methodName, args);
      fail("Exception thrown expected!");
    }
    catch (InvocationTargetException ite) {
      StackTraceElement[] stackTrace = ite.getCause().getStackTrace();
      assertEquals(lineNumber, stackTrace[0].getLineNumber());
    }
  }
}
