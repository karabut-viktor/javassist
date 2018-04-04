package javassist.bytecode;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javassist.compiler.ast.Stmnt;

public class LineNumberRegistry {
  private final static int FIRST_LINE = 40000;
  private static Map<Object, Integer> blockNumber = new WeakHashMap<Object, Integer>();

  private final Bytecode bc;
  private int nextLine = FIRST_LINE;
  private SortedMap<Integer, Integer> linesMap = new TreeMap<Integer, Integer>();

  public LineNumberRegistry(Bytecode bc) {
    this.bc = bc;
    Integer i = blockNumber.get(bc.getConstPool());
    if (i == null) {
      i = Integer.valueOf(0);
    }
    else {
      i++;
      nextLine += i * 1000;
    }
    blockNumber.put(bc.getConstPool(), i);
  }

  public void addNewLinesIfAny(CodeAttribute ca, int startPos) {
    if (linesMap.size() > 0) {
      LineNumberAttribute oa = (LineNumberAttribute) ca.getAttribute(LineNumberAttribute.tag);
      if (oa == null) {
        try {
          ca.getAttributes().add(new LineNumberAttribute(bc.getConstPool(), bc.getConstPool().addUtf8Info(LineNumberAttribute.tag),
              new DataInputStream(new ByteArrayInputStream(getNewTable()))));
        }
        catch (IOException e) {
          throw new RuntimeException(e); // should never reach here.
        }
      }
      else {
        oa.info = getTable(oa, startPos);
      }
    }
  }

  /**
   * Tracks op codes and decides what we should consider new lines on the code
   */
  public void addNewLineNumberIfShouldBeNewLine(Stmnt st) {
    // We count those operators as new lines
    boolean newLine = isOnNewLine(st);
    System.out.println((newLine ? '+' : '-') + " Line number for: " + st);
    if (newLine) {
      Integer bytecodeIndex = bc.getSize();
      if (!linesMap.containsKey(bytecodeIndex)) {
        linesMap.put(bytecodeIndex, ++nextLine);
      }
    }
  }

  private byte[] getTable(LineNumberAttribute oa, int startPos) {
    byte[] lines = new byte[oa.info.length + 4 * linesMap.size()];
    ByteArray.write16bit((lines.length - 2) / 4, lines, 0);
    addData(lines, 0, startPos);
    System.arraycopy(oa.info, 2, lines, lines.length - oa.info.length + 2, oa.info.length - 2);
    return lines;
  }

  private byte[] getNewTable() {
    byte[] lines = new byte[4 + 2 + 4 * linesMap.size()];
    ByteArray.write32bit(lines.length - 4, lines, 0);// Table size for DataInputStream
    ByteArray.write16bit(linesMap.size(), lines, 4);
    addData(lines, 4, 0);
    return lines;
  }

  private void addData(byte[] lines, int index, int startPos) {
    int i = 0;
    for (Map.Entry<Integer, Integer> e : linesMap.entrySet()) {
      Integer codePoint = e.getKey() + startPos;
      Integer lineNumber = e.getValue();
      System.out.println("debug n " + codePoint + " " + lineNumber);
      ByteArray.write16bit(codePoint, lines, i * 4 + 2 + index);
      ByteArray.write16bit(lineNumber, lines, i * 4 + 4 + index);
      i++;
    }
  }

  private boolean isOnNewLine(Stmnt st) {
    int op = st.getOperator();
    return op == Stmnt.EXPR ||
        op == Stmnt.DECL ||
        op == Stmnt.FOR ||
        op == Stmnt.WHILE ||
        op == Stmnt.CASE ||
        op == Stmnt.SWITCH ||
        op == Stmnt.TRY ||
        op == Stmnt.CATCH ||
        op == Stmnt.FINALLY ||
        op == Stmnt.BREAK ||
        op == Stmnt.RETURN ||
        op == Stmnt.CONTINUE ||
        op == Stmnt.IF ||
        op == Stmnt.ELSE ||
        op == Stmnt.THROW;
  }
}