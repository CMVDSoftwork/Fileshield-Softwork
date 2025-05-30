package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncryptorService {
    public byte[] encrypt(byte[] data, String password) {
        String salt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(password, salt);
        return encryptor.encrypt(data);
    }

    public byte[] decrypt(byte[] encryptedData, String password, String salt) {
        BytesEncryptor encryptor = Encryptors.stronger(password, salt);
        return encryptor.decrypt(encryptedData);
    }
}
