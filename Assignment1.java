import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class Assignment1 implements Assignment1Interface{
    
    public static void main(String[] args) throws IOException{

        Assignment1 obj = new Assignment1();

        String password = "pKa&PW]/`h_9ydmvA";
        String salt = "c4d61d8718dcd69458199545b8e17541";
        String iv = "e60cc196ceb8fee742f048e68d044707";
        String plainText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In dictum nunc vitae maximus tristique. Phasellus eu mauris ligula. Mauris sed lorem a dolor placerat porttitor et blandit risus. Donec nec augue sed augue accumsan blandit. Donec et mauris vestibulum, ultrices erat sed, lacinia ligula. Quisque elementum orci nec nulla sagittis, eget vulputate nulla auctor. Nullam maximus aliquam neque, non elementum turpis maximus a. Nulla nunc felis, mattis vel mauris eget, porta fringilla diam. Praesent hendrerit leo in porttitor aliquet.Sed elementum ligula eget nulla pharetra, eu blandit augue commodo.";            
        //byte[]plainByte = plainText.getBytes();
        System.out.println("SIZE 1 : " +plainText.length());

        BigInteger exponent = new BigInteger("65537");
        BigInteger modulus = new BigInteger("c406136c12640a665900a9df4df63a84fc855927b729a3a106fb3f379e8e4190ebba442f67b93402e535b18a5777e6490e67dbee954bb02175e43b6481e7563d3f9ff338f07950d1553ee6c343d3f8148f71b4d2df8da7efb39f846ac07c865201fbb35ea4d71dc5f858d9d41aaa856d50dc2d2732582f80e7d38c32aba87ba9", 16);

        byte[] bytesPass = password.getBytes(StandardCharsets.UTF_8);
        
        byte[] saltHex = obj.hexToByte(salt);
        
        byte[] hashKey = obj.generateKey(bytesPass, saltHex);

        byte[] ivByte = obj.hexToByte(iv);


        //InputStream inputStream = new FileInputStream("Assignment1.class");
        //byte[] plainTextBytes = Files.toByteArray("Password.txt");
        //byte[] plainTextBytes = ByteStreams.toByteArray("Password.txt");

        
        InputStream inputStream = new FileInputStream("Assignment1.class");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

 
        int byteRead;

        while ((byteRead = inputStream.read()) != -1) {
            outputStream.write(byteRead);
        }
        

        byte[] plainTextBytes = outputStream.toByteArray();

        //byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = obj.encryptAES(plainTextBytes, ivByte, hashKey);

        byte[] decrypted = obj.decryptAES(encrypted, ivByte, hashKey);

        FileOutputStream fos = new FileOutputStream("Result.class");
        fos.write(decrypted);
        
        fos.close();
        String s = new String(decrypted, StandardCharsets.UTF_8);

        System.out.println("SIZE 2: " +s.length());


        System.out.println("DECRYPTED : "+ s);

        
        byte[] modByte = obj.encryptRSA(bytesPass, exponent, modulus);

    }

    public byte[] generateKey(byte[] password, byte[] salt) {
        ByteArrayOutputStream genKeyStream = new ByteArrayOutputStream();
        try {
            genKeyStream.write(password);
            genKeyStream.write(salt);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //HASHING 200 times (p||s)

            byte[] key = digest.digest(genKeyStream.toByteArray());

            for (int i = 0; i<199; i++){
                key = digest.digest(key);
            }

            return key;
            
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encryptAES(byte[] plaintext, byte[] iv, byte[] key) {
         IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
         //encrypt the message
        //CONVERT HASH 256 BITS INTO SECRET KEY
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        int lengthOfplaintext = plaintext.length;

        System.out.println("BEFORE ENCRYPTED " + lengthOfplaintext);
        int modulo = lengthOfplaintext % 16;

        if(modulo != 0){
            ByteArrayOutputStream eof = new ByteArrayOutputStream();
            //byte[] endOfMessage = new byte[lengthOfplaintext - lengthOfplaintext/16*16];

            int floor = Math.floorDiv(lengthOfplaintext,16);
            System.out.println("FLOOR :"+floor);

            byte[] start = Arrays.copyOfRange(plaintext, 0, floor);
            byte[] end = Arrays.copyOfRange(plaintext, floor, lengthOfplaintext);
            /*
            //Copy elements of arr to slice
            for (int i = 0; i < endOfMessage.length; i++) {
                endOfMessage[i] = plaintext[floor*16 + i];
            }
            
            byte[] startOfMessage = new byte[floor*16];
    
            // Copy elements of arr to slice
            for (int i = 0; i < endOfMessage.length; i++) {
                startOfMessage[i] = plaintext[floor*16];
            }
            */

            try {
                eof.write(start);
                byte[] oneZeros = new byte[]{ (byte) 0x80};

           
                eof.write(end);
                eof.write(oneZeros);
    
            } catch (IOException e) {

                e.printStackTrace();
            }


            for(int i = 0; i < 16 - (lengthOfplaintext-(16*floor)) - 1; i++){
                byte[] zeropadding= new byte[]{ (byte) 0x00};
                try {
                    eof.write(zeropadding);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            
            plaintext = eof.toByteArray();

        }

        System.out.println("SIZE ONCE PADDING: " + plaintext.length);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(plaintext);

            System.out.println("SIZE ONCE ENCRYPTED: "+encrypted.length);

            return encrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException 
                    | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] decryptAES(byte[] ciphertext, byte[] iv, byte[] key) {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        // encrypt the message
        // CONVERT HASH 256 BITS INTO SECRET KEY
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        int lengthOfplaintext = ciphertext.length;
        //byte[] textDecryptedWithPadding = new byte[lengthOfplaintext];

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(ciphertext);

                    
        //int counterSizeRealMessage = lengthOfplaintext - 1;
        for (int c = lengthOfplaintext - 1 ; c >= 0; c-- ) {

            if (decrypted[c] == (byte) 0x80) {
                byte[] textDecrypted = Arrays.copyOfRange(decrypted, 0, c);
                System.out.println("SIZE ONCE DECRYPTED : " +textDecrypted.length);

                return textDecrypted;
            }

        }


        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();

        }

        return null;
    }

    public byte[] encryptRSA(byte[] plaintext, BigInteger exponent, BigInteger modulus) {
        BigInteger base = new BigInteger(plaintext);

        BigInteger expoModular = modExp(base, exponent, modulus);
        
        return expoModular.toByteArray();
    }

    public BigInteger modExp(BigInteger base, BigInteger exponent, BigInteger modulus) {
        
        BigInteger result = BigInteger.ONE;
            while (exponent.compareTo(BigInteger.ZERO) > 0) {
                if (exponent.testBit(0)) 
                    result = (result.multiply(base)).mod(modulus);
                exponent = exponent.shiftRight(1);
                base = (base.multiply(base)).mod(modulus);
            }
            System.out.print("RESULT : "+result.mod(modulus));

            return result.mod(modulus);
        
    }


    //This functions takes a string as a parameter and converts it to its
    // value in an array of bytes 
    public byte[] hexToByte(String stringNotConverted){
        ByteArrayOutputStream streamOfBytes = new ByteArrayOutputStream();

        for(int i=0; i<stringNotConverted.length(); i+=2){
            

            String firstCharOfHex = stringNotConverted.substring(i, i+1);
            String secondCharOfHex = stringNotConverted.substring(i+1, i+2);

            byte part = new BigInteger(firstCharOfHex.toUpperCase(), 16).byteValue();
            byte part2 = new BigInteger(secondCharOfHex.toUpperCase(), 16).byteValue();

            int finalHex = part*16 + part2;

            streamOfBytes.write(finalHex);
        }

        return streamOfBytes.toByteArray();      

    }
    
}
