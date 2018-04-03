package linenumbers;

public class TestPreserveOriginalLineNumbers {
  public int run() {
    if (1==1) throw new RuntimeException("expected");
    return 0;
  }
}
