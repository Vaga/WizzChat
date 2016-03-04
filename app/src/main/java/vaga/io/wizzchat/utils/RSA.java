package vaga.io.wizzchat.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

    public static String encrypt(String base64, String data) throws Exception {

        // Retrieve the public key
        byte[] encodedKey = Base64.decode(base64, Base64.DEFAULT);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        // Encrypt the data
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        String encrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        return encrypted;

    }

    public static String decrypt(String base64, String data) throws Exception {

        // Retrieve the private key
        byte[] encodedKey = Base64.decode(base64, Base64.DEFAULT);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        // Decrypt the data
        byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedData = cipher.doFinal(encryptedData);
        String decrypted = new String(decryptedData);

        return decrypted;
    }
}
