package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class CifradorAESServiceImpl implements CifradorAESService {
    private static final String ALGORITMO = "AES";

    @Override
    public SecretKey generarClave() {
        try {
            KeyGenerator generador = KeyGenerator.getInstance(ALGORITMO);
            generador.init(256);
            return generador.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar clave AES", e);
        }
    }

    @Override
    public byte[] claveABytes(SecretKey clave) {
        return clave.getEncoded();
    }

    @Override
    public SecretKey bytesAClave(byte[] bytes) {
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    @Override
    public void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, clave);

        try (FileInputStream fis = new FileInputStream(archivoOriginal);
             FileOutputStream fos = new FileOutputStream(archivoDestino);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[1024];
            int leido;
            while ((leido = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, leido);
            }
        }
    }

    @Override
    public void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, clave);

        try (FileInputStream fis = new FileInputStream(archivoCifrado);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(archivoDescifrado)) {

            byte[] buffer = new byte[1024];
            int leido;
            while ((leido = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, leido);
            }
        }
    }

    public String cifrarTexto(String textoPlano, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cifrado);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error al cifrar el texto", e);
        }
    }
}
