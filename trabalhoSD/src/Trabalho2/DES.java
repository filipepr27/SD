package Trabalho2;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {

    private final byte[] key;
    private Cipher encCipher;
    private Cipher decCipher;

    public DES() throws Exception {
        this.key = generateKey();
        initCiphers();
    }

    public DES(byte[] key) throws Exception {
        this.key = key;
        initCiphers();
    }

    private void initCiphers() throws Exception {
        byte[] iv = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        encCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        decCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        SecretKeySpec keySpec = new SecretKeySpec(key, "DES");
        encCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParams);
        decCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParams);

    }

    public byte[] encrypt(String message) throws Exception {
        byte[] iv = encCipher.getIV();
        byte[] encryptedMessage = encCipher.doFinal(message.getBytes());
        byte[] result = new byte[iv.length + encryptedMessage.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedMessage, 0, result, iv.length, encryptedMessage.length);
        return result;
    }

    public String decrypt(byte[] message) throws Exception {
        byte[] iv = Arrays.copyOfRange(message, 0, 8);
        byte[] encryptedMessage = Arrays.copyOfRange(message, 8, message.length);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        decCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "DES"), ivParams);
        return new String(decCipher.doFinal(encryptedMessage));
    }

    public static byte[] generateKey() {
        byte[] key = new byte[8]; // 64 bits = 8 bytes
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 8; i++) {
            key[i] = (byte) (random.nextInt(256));
        }

        for (int i = 0; i < 8; i++) {
            key[i] = adjustParity(key[i]);
        }

        return key;
    }

    private static byte adjustParity(byte b) {
        int ones = Integer.bitCount(b & 0xFF);
        if (ones % 2 == 0) {
            b ^= 1;
        }
        return b;
    }

}
