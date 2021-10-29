import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

public interface draftInterface {

        public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
            String password = "pKa&PW]/`h_9ydmvA";
            byte[] bytesPass = password.getBytes(StandardCharsets.UTF_8);
            String salt = "c4d61d8718dcd69458199545b8e17541";
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            for(int i=0; i<salt.length(); i+=2){
                

                String newArray = salt.substring(i, i+1);
                String newArray2 = salt.substring(i+1, i+2);

                //System.out.println(newArray);
                byte part = new BigInteger(newArray.toUpperCase(), 16).byteValue();
                byte part2 = new BigInteger(newArray2.toUpperCase(), 16).byteValue();

                int final1 = part*16 + part2;
                //byte t = (byte) final1 & 0xff;
                //test2.add((Byte)final1);
                output.write(final1);
                
            }
            
            byte[] saltByte = output.toByteArray();      
            
            //CREATING KEY (p||s)
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            output1.write(bytesPass);
            output1.write(saltByte);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //HASHING 200 times (p||s)

            byte[] hash = digest.digest(output1.toByteArray());

            for (int i = 0; i<199; i++){
                hash = digest.digest(hash);
            }


            //Encrypt key

            //GENERATING IV
            //int ivSize = 16;
            //byte[] iv = new byte[ivSize];
            //SecureRandom random = new SecureRandom();
            //random.nextBytes(iv);
            //IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            
            
            String IV = "e60cc196ceb8fee742f048e68d044707";

            ByteArrayOutputStream output3 = new ByteArrayOutputStream();
            
            for(int i=0; i<IV.length(); i+=2){
                

                String newArray = IV.substring(i, i+1);
                String newArray2 = IV.substring(i+1, i+2);

                //System.out.println(newArray);
                byte part = new BigInteger(newArray.toUpperCase(), 16).byteValue();
                byte part2 = new BigInteger(newArray2.toUpperCase(), 16).byteValue();

                int final1 = part*16 + part2;
                //byte t = (byte) final1 & 0xff;
                //test2.add((Byte)final1);
                output3.write(final1);
                
            }
            
            byte[] iv = output3.toByteArray(); 
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            
            
            
            
            
            //encrypt the message
            //CONVERT HASH 256 BITS INTO SECRET KEY

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(hash, "AES");
            
            String plainText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In dictum nunc vitae maximus tristique. Phasellus eu mauris ligula. Mauris sed lorem a dolor placerat porttitor et blandit risus. Donec nec augue sed augue accumsan blandit. Donec et mauris vestibulum, ultrices erat sed, lacinia ligula. Quisque elementum orci nec nulla sagittis, eget vulputate nulla auctor. Nullam maximus aliquam neque, non elementum turpis maximus a. Nulla nunc felis, mattis vel mauris eget, porta fringilla diam. Praesent hendrerit leo in porttitor aliquet.Sed elementum ligula eget nulla pharetra, eu blandit augue commodo. Duis sed rutrum erat, ac posuere magna. Vivamus efficitur finibus facilisis. Sed laoreet, felis vel hendrerit gravida, mauris sem ultricies enim, eu fermentum purus diam dapibus massa. Vestibulum luctus ipsum ut pharetra rutrum. Mauris eu luctus lorem. Pellentesque justo lacus, efficitur in dignissim nec, euismod non lorem. Mauris turpis ex, vestibulum a vestibulum non, laoreet id massa. Ut gravida dictum quam, sed ultricies enim dapibus vel. Nullam et mollis tortor, sit amet euismod justo.Nam suscipit condimentum magna, at finibus eros fermentum sollicitudin. Suspendisse eget laoreet mi. Phasellus faucibus malesuada sollicitudin. Nullam pellentesque suscipit consectetur. Curabitur luctus risus tortor, eget venenatis nunc varius vel. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vestibulum in metus accumsan, vehicula mi id, egestas libero. Duis blandit, turpis id porttitor pharetra, risus dui varius leo, eu pharetra nisl libero non tellus. Nullam ornare ante tortor, vitae ullamcorper nibh consectetur id. Aenean viverra nisl at turpis rhoncus vestibulum. Nullam eu magna et turpis ultricies fermentum in vulputate quam. Praesent tristique pellentesque arcu, vitae porttitor velit hendrerit vel. Integer interdum purus eu orci vulputate venenatisMorbi fringilla sed enim vel ultricies. Suspendisse blandit luctus dolor nec mattis. Nam ut hendrerit diam. Suspendisse sit amet rhoncus mauris, consectetur sagittis erat. Sed vel eleifend felis. Morbi ante purus, interdum at erat at, sodales bibendum diam. Vestibulum id metus mollis, auctor dolor nec, pellentesque nulla. Nam molestie nisl ac libero luctus, et suscipit urna tristique. Aliquam dignissim ex a orci dignissim, sed facilisis libero cursus. Phasellus ac lectus ut ex aliquam venenatis. Nulla at aliquet nisi. Sed accumsan eleifend velit, sit amet vehicula sapien placerat non.";            
            byte[]plainByte = plainText.getBytes();
            System.out.println("BEFORE ENCRYPTED "+(plainText.getBytes().length));
            
            int modulo = plainText.getBytes().length % 16;

            if(modulo != 0){
                ByteArrayOutputStream eof = new ByteArrayOutputStream();
                byte[] slice = new byte[plainText.getBytes().length - plainText.getBytes().length/16*16];
  
                int floor = Math.floorDiv(plainText.getBytes().length,16);
                //Copy elements of arr to slice
                for (int i = 0; i < slice.length; i++) {
                    slice[i] = plainText.getBytes()[floor*16 + i];
                }
                
                byte[] slicefinal = new byte[floor*16];
      
                // Copy elements of arr to slice
                for (int i = 0; i < slice.length; i++) {
                    slicefinal[i] = plainText.getBytes()[floor*16];
                }

                eof.write(slicefinal);

                byte[] unzero = new byte[]{ (byte) 0x80};

                
                eof.write(slice);
                eof.write(unzero);

                for(int i = 0; i < 16 - modulo - 1; i++){
                    byte[] zeropadding= new byte[]{ (byte) 0x00};
                    eof.write(zeropadding);

                }
                
                plainByte = eof.toByteArray();

            }

            System.out.println("SIZE ONCE PADDING: "+plainByte.length);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(plainByte);

            System.out.println("SIZE ONCE ENCRYPTED: "+encrypted.length);
            
            //MODULAR EXPONENTIATION

            BigInteger exponent = new BigInteger("65537");
            BigInteger modulus = new BigInteger("c406136c12640a665900a9df4df63a84fc855927b729a3a106fb3f379e8e4190ebba442f67b93402e535b18a5777e6490e67dbee954bb02175e43b6481e7563d3f9ff338f07950d1553ee6c343d3f8148f71b4d2df8da7efb39f846ac07c865201fbb35ea4d71dc5f858d9d41aaa856d50dc2d2732582f80e7d38c32aba87ba9", 16);
            BigInteger base = new BigInteger(bytesPass);
            //BigInteger base = new BigInteger("123");
            //BigInteger modulus = new BigInteger("511");
            BigInteger result = BigInteger.ONE;
            while (exponent.compareTo(BigInteger.ZERO) > 0) {
                if (exponent.testBit(0)) // then exponent is odd
                    result = (result.multiply(base)).mod(modulus);
                exponent = exponent.shiftRight(1);
                base = (base.multiply(base)).mod(modulus);
            }
            System.out.print("RESULT : "+result.mod(modulus));

            byte[] modular = result.mod(modulus).toByteArray();
            

            //CONVERTING MODULAR TO HEXADECIMAL

            StringBuilder modHEX = new StringBuilder();
            for(byte x : modular){
                modHEX.append(String.format("%02x", x));
            }

            System.out.println("HEXA2 : "+modHEX.toString());





            //DECRYPT

            //int keySize = 16;

            //byte[] iv2 = new byte[ivSize];
            //System.arraycopy(, arg1, arg2, arg3, arg4);
            /*
            Key s = new Key(bytesPass);
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, bytesPass, ivParameterSpec);
            byte[] decrypted = cipherDecrypt.doFinal(encrypted);

            String str = new String(decrypted, StandardCharsets.UTF_8);
            System.out.print(str);
            */


        }
        
	}

