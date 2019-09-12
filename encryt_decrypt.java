//make hashmap register both port and public key for each client
//each client will also hold its private key. So every client will generate a pub-pri key and then send the pub key to the server for registration.
//send - FETCHKEY pub key of recepient
//each transfer is in base64 format and not in byte[]

//64(H',M') to be sent
//M' - encrypted message
//H' = KpvtA(H) ; H = hash(M')
//at reciever we need pubKeyA and hence check - hash(M') = KpubA(H')

//all to be sent with the encrypted message

//(H',M') to be sent at place of message
//hashmap registers pub key too.
//FETCHKEY for both end clients
import  java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.MessageDigest;
import java.util.*;
import javax.crypto.Cipher;

class CryptographyExample {

    private static final String ALGORITHM = "RSA";

    public static byte[] encrypt(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));


        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(inputData);

        return encryptedBytes;
    }

    public static byte[] decrypt(byte[] privateKey, byte[] inputData) throws Exception {

        PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(inputData);

        return decryptedBytes;
    }


    public static byte[] encryptUsingPrivate(byte[] privateKey, byte[] inputData) throws Exception {
        PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(inputData);
        return encryptedBytes;
    }

    public static byte[] decryptUsingPublic(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(inputData);
        return decryptedBytes;
    }

    public static byte[] getHash(byte[] message)throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedMessage = md.digest(message);
        return hashedMessage;

    }
   
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        // 512 is keysize
        keyGen.initialize(512, random);
        KeyPair generateKeyPair = keyGen.generateKeyPair();
        return generateKeyPair;
    }

    // public static MessageDigest md;

    public static void main(String[] args) throws Exception {

        String originalMessage = "The message to be dododod encrypted and sent";
        KeyPair generateKeyPairB = generateKeyPair();
        byte[] publicKeyB = generateKeyPairB.getPublic().getEncoded();
        byte[] privateKeyB = generateKeyPairB.getPrivate().getEncoded();
        KeyPair generateKeyPairA = generateKeyPair();
        byte[] publicKeyA = generateKeyPairA.getPublic().getEncoded();
        byte[] privateKeyA = generateKeyPairA.getPrivate().getEncoded();

        byte[] encryptedData = encrypt(publicKeyB, originalMessage.getBytes());//M'
        byte[] shaEncryptedData = getHash(encryptedData);//H = hash(M')
        byte[] hDash = encryptUsingPrivate(privateKeyA, shaEncryptedData);//H' = KpvtA(H);

   

        String encryption64 = Base64.getEncoder().encodeToString(encryptedData);
        String hDash64 =  Base64.getEncoder().encodeToString(hDash);
        ////////////////////////////////////// decryption
        ////////////////////////////////////// part///////////////////////////////////////////

        // byte[] pvk = Base64.getDecoder().decode(pvk64);
        byte[] decryptedData = decrypt(privateKeyB, Base64.getDecoder().decode(encryption64));
        System.out.println("Decrypted Message: " + new String(decryptedData));
        
        // hash(M') = KpubA(H')
        if(Arrays.equals(getHash(Base64.getDecoder().decode(encryption64)),decryptUsingPublic(publicKeyA,Base64.getDecoder().decode(hDash64)))) System.out.println("YESs");
        else System.out.println("NOo");


    }
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
  
        // digest() method called  
        // to calculate message digest of an input  
        // and return array of byte 
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 
    
    public static String toHexString(byte[] hash) 
    { 
        // Convert byte array into signum representation  
        BigInteger number = new BigInteger(1, hash);  
  
        // Convert message digest into hex value  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
  
        // Pad with leading zeros 
        while (hexString.length() < 32)  
        {  
            hexString.insert(0, '0');  
        }  
  
        return hexString.toString();  
    }
/*
    public static void senderGenerate(String message, String pubKeyB, String pvtKeyA) throws Exception {
        // A->B

        byte[] publicKeyB = Base64.getDecoder().decode(pubKeyB);
        byte[] privateKeyA = Base64.getDecoder().decode(pvtKeyA);

        byte[] mDash = encrypt(publicKeyB, message.getBytes());// M'

        byte[] shaMdash = md.digest(mDash);// H

        byte[] hDash = encrypt(privateKeyA, (Base64.getEncoder().encodeToString(shaMdash)).getBytes());// H'
                                                                                                       // =kvtA(64(H))
        ////////////////////////////////
        String mDash64 = Base64.getEncoder().encodeToString(mDash);
        String hDash64 = Base64.getEncoder().encodeToString(hDash);

    }

    public static void recieverGenerate(String pubKeyA, String pvtKeyB, String hDash64, String mDash64)
            throws Exception {
        // A->B
        byte[] publicKeyA = Base64.getDecoder().decode(pubKeyA);
        byte[] privateKeyB = Base64.getDecoder().decode(pvtKeyB);
        byte[] hDash = Base64.getDecoder().decode(hDash64);
        byte[] mDash = Base64.getDecoder().decode(mDash64);
        byte[] h = Base64.getDecoder().decode(decrypt(publicKeyA, hDash)); // H' = pvtA(64(H))

        byte[] shaMdash = md.digest(mDash);

        // return true (h==mDash);

        String message = new String(decrypt(privateKeyB, mDash));

    }
*/
}
