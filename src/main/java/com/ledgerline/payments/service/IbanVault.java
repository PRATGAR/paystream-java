package com.ledgerline.payments.service;

import com.ledgerline.payments.config.AppProperties;
import com.ledgerline.payments.config.CryptoSettings;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class IbanVault {

    private final SecretKeySpec key;

    public IbanVault(AppProperties props) {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(props.vaultKey()), "AES");
    }

    public String seal(String iban) {
        try {
            Cipher cipher = newCipher(Cipher.ENCRYPT_MODE);
            byte[] sealed = cipher.doFinal(iban.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sealed);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("IBAN could not be sealed", e);
        }
    }

    private Cipher newCipher(int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CryptoSettings.IBAN_TRANSFORMATION);
        cipher.init(mode, key);
        return cipher;
    }
}
