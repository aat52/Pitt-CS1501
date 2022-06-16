import java.util.*;

public class Add128 implements SymCipher {
  private byte[] key;

  public Add128() {
    Random randoms = new Random();
    key = new byte[128];
    for (int i = 0; i < 128; i++) {
      key[i] = (byte) randoms.nextInt(256);
    }
  }

  public Add128(byte[] Key) {
    this.key = Key;
  }

  public byte[] encode(String M) {
    byte[] Mprime = M.getBytes();
    byte[] arr = new byte[Mprime.length];
    int i = 0;
    int j = 0;
    for (byte a : Mprime) {
      arr[j] = (byte) (a + key[i]);
      i++;
      j++;
      if (i == 128) {
        i = 0;
      }
    }
    return arr;
  }


  public String decode(byte[] M) {
    byte[] message = new byte[M.length];
    int i = 0;
    int j = 0;
    for (byte a : M) {
      byte b = (byte) (a - key[i]);
      message[j] = b;
      i++;
      j++;
      if (i == 128) {
        i = 0;
      }
    }
    String s = new String(message);
    return s;
  }


  public byte[] getKey() {
    return key;
  }
}