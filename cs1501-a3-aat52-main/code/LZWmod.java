/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
  private static final int R = 256;        // number of input chars
  private static int L = 512;       // number of codewords = 2^W
  private static final int Wmin = 9;
  private static final int Wmax = 16;
  private static int W = Wmin;         // codeword width, starts at min by default and will iterate upwards as the upper limit of each width is reached.
  private static boolean reset; //read in whether we are using resets for this compression, then change how we compress/expand accordingly

  public static void compress() {
    BinaryStdOut.write(reset);
    TSTmod<Integer> st = new TSTmod<Integer>();
    for (int i = 0; i < R; i++){
      StringBuilder a = new StringBuilder("" + (char) i);
      st.put(a, i);
    }
    int code = R+1;  // R is codeword for EOF
    StringBuilder sb = new StringBuilder();
    char nextChar = BinaryStdIn.readChar();
    // we'll read in the next character seperately, so we can save it after we append it to the word. This way, at the end we can delete it from the word but still have it saved as the start of the next word.
    while (!BinaryStdIn.isEmpty()) {
      // what do we do if we're out of slots?
      if (code >= L) {
        // first we try to increase our word size if we're not at the limit
        if (W < Wmax) {
          W++;
          L = ((int) Math.pow(2,W));
        // if that fails, we can try a reset if the user has allowed it
        } else if (reset) {
          // just resetting everything we did before the loop started.
          W = Wmin;
          L = ((int) Math.pow(2,W));
          //TSTmod is TST modified to work with StringBuilders
          st = new TSTmod<Integer>();
          for (int i = 0; i < R; i++){
            StringBuilder a = new StringBuilder("" + (char) i);
            st.put(a, i);
          }
          code = R+1;  // R is codeword for EOF
        }
      }
      //start with a stringbuilder that's the single character from the previous loop, then loop until we find a word we dont have yet.
      sb = new StringBuilder();
      sb.append(nextChar);
      while (st.stringExists(sb) && !BinaryStdIn.isEmpty()) {
        nextChar = BinaryStdIn.readChar();
        sb.append(nextChar);
      }
      //if we have space, lets append our word to the dict
      if (code < L) {
        st.put(sb,code++);
      }
      // for bugfixing:
      //System.out.print("" + L);
      //System.out.println(", " + code);
      //System.out.println(sb);
      //we dont want the last character, that's for the next word.
      sb.deleteCharAt(sb.length() - 1);
      BinaryStdOut.write(st.get(sb), W);
    }
    BinaryStdOut.write(R, W);
    BinaryStdOut.close();
  }


  public static void expand() {
    reset = BinaryStdIn.readBoolean();
    String[] st = new String[((int) Math.pow(2,Wmax))];
    int i; // next available codeword value

    // initialize symbol table with all 1-character strings
    for (i = 0; i < R; i++) {
      st[i] = "" + (char) i;
    }
    st[i++] = "";                        // (unused) lookahead for EOF

    int codeword = BinaryStdIn.readInt(W);
    String val = st[codeword];
    while (true) {
      BinaryStdOut.write(val);
      if (i + 1 >= L) {
        // i + 1 since the expansion is "lagging" one behind the compression
        if (W < Wmax) {
          //increment w if we can, if we can't see if we can reset, same as above.
          //System.out.println("incrementing W");
          W++;
          L = ((int) Math.pow(2,W));
        } else if (reset) {
          // Do everything we did before the while loop started
          //System.out.println("resetting");
          W = Wmin;
          L = ((int) Math.pow(2,W));
          st = new String[((int) Math.pow(2,Wmax))];
          for (i = 0; i < R; i++) {
            st[i] = "" + (char) i;
          }
          st[i++] = "";
          codeword = BinaryStdIn.readInt(W);
          val = st[codeword];
          BinaryStdOut.write(val);
        }
      }
      //read in next word
      codeword = BinaryStdIn.readInt(W);
      //System.out.println(codeword);
      //The rest of this is effectively unchanged since LZW.java.
      if (codeword == R) {
        break;
      }
      String s = st[codeword];
      if (i == codeword) {
        s = val + val.charAt(0);   // special case hack
        //System.out.println("Special case");
      }
      if (i < L) {
        st[i++] = val + s.charAt(0);
      }
      //System.out.println(val);
      //System.out.print("" + L + ", ");
      //System.out.println("" + i);
      val = s;
    }
    BinaryStdOut.close();
  }



  public static void main(String[] args) {
    // check if user wants to reset, use set the reset variable to that. The expand method will override this if chosen
    if (args.length > 1 && args[1].equals("r")) {
      reset = true;
    } else {
      reset = false;
    }
    if (args[0].equals("-")) {
      compress();
    } else if (args[0].equals("+")) {
      expand();
    } else {
      throw new RuntimeException("Illegal command line argument");
    }
  }

}
