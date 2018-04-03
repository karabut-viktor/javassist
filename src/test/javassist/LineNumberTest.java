package javassist;

import java.lang.reflect.InvocationTargetException;

public class LineNumberTest extends JvstTestRoot {


  public LineNumberTest(String name) {
    super(name);
  }

  public void testPreserveOriginalLineNumbers() throws Exception {
    CtClass cc = sloader.get("linenumbers.TestPreserveOriginalLineNumbers");
    CtMethod m = cc.getDeclaredMethod("run");
    m.insertBefore("{ if (1==1) { String a = null; } }");
    cc.writeFile();
    Object obj = make(cc.getName());
    assertFirstLineNumber(obj, 5, "run");
  }

  public void testInsertAfter() throws Exception {
    CtClass cc = sloader.get("linenumbers.TestInsertAfter");
    CtMethod m = cc.getDeclaredMethod("run");
    m.insertAfter("{ if (1==1) { throw new RuntimeException(\"Expected\"); } }");
    cc.writeFile();
    Object obj = make(cc.getName());
    assertFirstLineNumber(obj, 40002, "run");
  }

  public void testInsertBefore() throws Exception {
    CtClass cc = sloader.get("linenumbers.TestInsertBefore");
    CtMethod m = cc.getDeclaredMethod("run");
    m.insertBefore("{ if (1==1) { throw new RuntimeException(\"Expected\"); } }");
    cc.writeFile();
    Object obj = make(cc.getName());
    assertFirstLineNumber(obj, 40002, "run");
  }

  public void testAddTwoMethods() throws Exception {
    CtClass cc = sloader.makeClass("generated.testAddTwoMethods");
    CtMethod m1 = CtNewMethod.make(""+
        "public int run1() { " +
        "  String a = null; " +
        "  a.toString();" +
        "  return 0;" +
        "}", cc);
    cc.addMethod(m1);
    CtMethod m2 = CtNewMethod.make("" +
        "public int run2() { " +
        "  String a = null; " +
        "  a.toString();" +
        "  return 0;" +
        "}", cc);
    cc.addMethod(m2);
    cc.writeFile();
    Object obj = make(cc.getName());
    assertFirstLineNumber(obj, 40002, "run1");
    assertFirstLineNumber(obj, 40002, "run2");
  }

  public void testAddMethod() throws Exception {
    CtClass cc = sloader.makeClass("generated.testAddMethod");
    CtMethod m = CtNewMethod.make("" +
        "public int run() { " +
        "  String a = null; " +
        "  int k = 2;" +
        "  a.toString();" +
        "  k = 3;" +
        "  return 0;" +
        "}", cc);
    cc.addMethod(m);
    cc.writeFile();
    Object obj = make(cc.getName());
    assertFirstLineNumber(obj, 40003, "run");
  }

  private void assertFirstLineNumber(Object obj, int lineNumber, String methodName) throws Exception {
    try {
      invoke(obj, methodName);
      fail("Exception thrown expected!");
    }
    catch (InvocationTargetException ite) {
      ite.getCause().printStackTrace();
      StackTraceElement[] stackTrace = ite.getCause().getStackTrace();
      assertEquals(lineNumber, stackTrace[0].getLineNumber());
    }
  }
}
