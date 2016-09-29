package javassist.bytecode;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class LineNumberHelper {
  private final static int FIRST_LINE = 40000;

  private int nextLine = FIRST_LINE;
  private SortedMap<Integer, Integer> linesMap = new TreeMap<>();

  public void registerLine(int bytecodeIndex) {
    if (linesMap.containsKey(bytecodeIndex)) {
      throw new IllegalStateException("Trying to declare second line number to same bytecode operator");
    }

    linesMap.put(bytecodeIndex, ++nextLine);
  }

  public byte[] getTable() {
    byte[] lines = new byte[2 + 4 * linesMap.size()];
    ByteArray.write16bit(linesMap.size(), lines, 0);
    int i = 0;
    for (Map.Entry<Integer, Integer> e : linesMap.entrySet()) {
      Integer codePoint = e.getKey();
      Integer lineNumber = e.getValue();
      ByteArray.write16bit(codePoint, lines, i * 4 + 2);
      ByteArray.write16bit(lineNumber, lines, i * 4 + 4);
      i++;
    }

    return lines;
  }

  public int getCount() {
    return linesMap.size();
  }
}
