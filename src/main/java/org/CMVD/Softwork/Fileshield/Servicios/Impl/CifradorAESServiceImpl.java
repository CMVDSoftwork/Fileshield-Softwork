package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CifradorAESServiceImpl implements CifradorAESService {
    private static final String MAGIC_HEADER = "FSHIELD";
    private static final String ALGORITMO = "AES";
    private static final String ALGORITMO_COMPLETO = "AES/CBC/PKCS5Padding";

    private IvParameterSpec generarIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

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

    public String claveABase64(SecretKey clave) {
        return Base64.getEncoder().encodeToString(clave.getEncoded());
    }

    public SecretKey base64AClave(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    @Override
    public void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave)
            throws IOException, GeneralSecurityException {
        int intentosMaximos = 3;
        int intentoActual = 0;
        boolean exito = false;

        while (!exito && intentoActual < intentosMaximos) {
            try {
                Thread.sleep(1000);

                if (!archivoOriginal.exists() || !archivoOriginal.canRead()) {
                    intentoActual++;
                    continue;
                }

                Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
                IvParameterSpec ivSpec = generarIv();
                cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

                try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                    fos.write(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8));
                    fos.write(ivSpec.getIV());

                     try (FileInputStream fis = new FileInputStream(archivoOriginal);
                         CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

                        byte[] buffer = new byte[1024];
                        int leido;
                        while ((leido = fis.read(buffer)) != -1) {
                            cos.write(buffer, 0, leido);
                        }
                     }
                    exito = true;
                }
            } catch (IOException e) {
                if (intentoActual >= intentosMaximos - 1) {
                    throw new IOException("No se pudo acceder al archivo después de " +
                            intentosMaximos + " intentos: " + e.getMessage(), e);
                }
                intentoActual++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Operación interrumpida", e);
            }
        }
    }

    public void cifrarArchivoStream(File inputFile, OutputStream outputStream, SecretKey clave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        IvParameterSpec ivSpec = generarIv();
        cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

        outputStream.write(ivSpec.getIV());

        try (FileInputStream fis = new FileInputStream(inputFile);
             CipherOutputStream cos = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    public byte[] cifrarBytes(byte[] datosOriginales, SecretKey clave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        IvParameterSpec ivSpec = generarIv();
        cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

        byte[] cifradoConIV = new byte[ivSpec.getIV().length + cipher.getOutputSize(datosOriginales.length)];
        System.arraycopy(ivSpec.getIV(), 0, cifradoConIV, 0, ivSpec.getIV().length);
        byte[] datosCifrados = cipher.doFinal(datosOriginales);
        System.arraycopy(datosCifrados, 0, cifradoConIV, ivSpec.getIV().length, datosCifrados.length);
        return cifradoConIV;
    }

    public String cifrarTexto(String textoPlano, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            IvParameterSpec ivSpec = generarIv();
            cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            byte[] combinado = new byte[ivSpec.getIV().length + cifrado.length];
            System.arraycopy(ivSpec.getIV(), 0, combinado, 0, ivSpec.getIV().length);
            System.arraycopy(cifrado, 0, combinado, ivSpec.getIV().length, cifrado.length);
            return Base64.getEncoder().encodeToString(combinado);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error al cifrar el texto", e);
        }
    }



    @Override
    public void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException {
        try (FileInputStream fis = new FileInputStream(archivoCifrado);
             FileOutputStream fos = new FileOutputStream(archivoDescifrado)) { // Mover fos aquí

            byte[] header = new byte[MAGIC_HEADER.length()];
            if (fis.read(header) != MAGIC_HEADER.length()) {
                throw new IOException("No se pudo leer el encabezado del archivo");
            }

            String firma = new String(header, StandardCharsets.UTF_8);
            if (!MAGIC_HEADER.equals(firma)) {
                throw new SecurityException("El archivo no contiene la firma esperada. ¿Ya está descifrado?");
            }

            byte[] iv = new byte[16];
            if (fis.read(iv) != 16) {
                throw new IOException("No se pudo leer el IV del archivo cifrado.");
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buffer = new byte[1024];
                int leido;
                while ((leido = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, leido);
                }
            }
        }
    }

    public void descifrarArchivoStream(InputStream inputStream, OutputStream outputStream, SecretKey clave) throws Exception {
        byte[] iv = new byte[16];
        if (inputStream.read(iv) != 16) {
            throw new IOException("No se pudo leer el IV del stream cifrado.");
        }
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        try (CipherInputStream cis = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public byte[] descifrarBytes(byte[] datosCifradosConIV, SecretKey clave) throws Exception {
        byte[] iv = new byte[16];
        System.arraycopy(datosCifradosConIV, 0, iv, 0, iv.length);
        byte[] datosCifrados = new byte[datosCifradosConIV.length - iv.length];
        System.arraycopy(datosCifradosConIV, iv.length, datosCifrados, 0, datosCifrados.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);
        return cipher.doFinal(datosCifrados);
    }

    public String descifrarTexto(String textoCifradoBase64ConIV, SecretKey clave) throws GeneralSecurityException {
        try {
            byte[] combinado = Base64.getDecoder().decode(textoCifradoBase64ConIV);
            byte[] iv = new byte[16];
            System.arraycopy(combinado, 0, iv, 0, iv.length);
            byte[] cifrado = new byte[combinado.length - iv.length];
            System.arraycopy(combinado, iv.length, cifrado, 0, cifrado.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);
            byte[] descifrado = cipher.doFinal(cifrado);
            return new String(descifrado, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new GeneralSecurityException("Error al descifrar el texto: " + e.getMessage(), e);
        }
    }
}
