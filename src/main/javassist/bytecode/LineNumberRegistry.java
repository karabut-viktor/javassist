package javassist.bytecode;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javassist.compiler.ast.Stmnt;

public class LineNumberRegistry {
  private final static int FIRST_LINE = 40000;

  private final Bytecode bc;
  private int nextLine = FIRST_LINE;
  private SortedMap<Integer, Integer> linesMap = new TreeMap<>();

  public LineNumberRegistry(Bytecode bc) {
    this.bc = bc;
  }

  public void addNewLinesIfAny(CodeAttribute ca) {
    if (linesMap.size() > 0) {
      try {
        ca.getAttributes().add(new LineNumberAttribute(bc.getConstPool(), bc.getConstPool().addUtf8Info(LineNumberAttribute.tag),
            new DataInputStream(new ByteArrayInputStream(getTable()))));
      }
      catch (IOException e) {
        throw new RuntimeException(e); // should never reach here.
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
      if (linesMap.containsKey(bytecodeIndex)) {
        throw new IllegalStateException("Trying to declare second line number to same bytecode operator");
      }
      linesMap.put(bytecodeIndex, ++nextLine);
    }
  }

  private byte[] getTable() {
    byte[] lines = new byte[4 + 2 + 4 * linesMap.size()];
    ByteArray.write32bit(lines.length - 4, lines, 0);// Table size for DataInputStream
    ByteArray.write16bit(linesMap.size(), lines, 0 + 4);
    int i = 0;
    for (Map.Entry<Integer, Integer> e : linesMap.entrySet()) {
      Integer codePoint = e.getKey();
      Integer lineNumber = e.getValue();
      ByteArray.write16bit(codePoint, lines, i * 4 + 2 + 4);
      ByteArray.write16bit(lineNumber, lines, i * 4 + 4 + 4);
      i++;
    }
    return lines;
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