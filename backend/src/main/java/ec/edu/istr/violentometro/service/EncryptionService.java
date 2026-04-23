package ec.edu.istr.violentometro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${app.encryption.secret-key}")
    private String secretKey;

    private static final String ALGORITHM  = "AES/CBC/PKCS5Padding";
    // IV de 16 bytes en cero — igual que el frontend
    private static final byte[] IV_BYTES   = new byte[16];

    public String encrypt(String data) {
        try {
            SecretKeySpec key  = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(IV_BYTES);
            Cipher cipher      = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encrypted   = cipher.doFinal(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key  = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(IV_BYTES);
            Cipher cipher      = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decoded     = Base64.getUrlDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar", e);
        }
    }
}