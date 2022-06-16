import java.util.*;

public class Substitute implements SymCipher {
  private byte[] key;
  private byte[] decoder;


  public Substitute() { // generate a new random code
    Random randoms = new Random();
    key = new byte[256];
    decoder = new byte[256];
    ArrayList<Integer> remaining = new ArrayList<Integer>();
    for (int i = 0; i < 256; i++) {
      remaining.add(i);
    }
    //create an int array with all numbers 0 - 255, then select random numbers from it one by one in order to create the random key.
    for (int i = 0; i < 256; i++) {
      int z = randoms.nextInt(remaining.size());
      int x = remaining.remove(z);
      key[i] = (byte) x;
      decoder[x] = (byte) i;
      System.out.print(x + " ");
    }
  } 

  public Substitute(byte[] Key) {
    //take key as an input, and while we're at it generate a decoder key to save work later
    this.key = Key;
    decoder = new byte[256];
    for (int i = 0; i < 256; i++) {
      decoder[byteToInt(key[i])] = (byte) i;
    }
  }

  private int byteToInt(byte b) {
    //private helper method since going from bytes to ints isnt easy
    int a = (int) b;
    if (a < 0) {
      a = 256 + a;
    }
    return a;
  }

  public byte[] encode(String M) {
    byte[] Mprime = M.getBytes();
    for (byte a : Mprime) {
    }
    byte[] arr = new byte[Mprime.length];
    for (int i = 0; i < Mprime.length; i++) {
      arr[i] = key[Mprime[i]];
    }
    return arr;
  }

  public String decode(byte[] M) {
    byte[] message = new byte[M.length];
    for (int i = 0; i < M.length; i++) {
      message[i] = decoder[byteToInt(M[i])];
    }
    String s = new String(message);
    return s;
  }

  public byte[] getKey() {
    return key;
  }
}