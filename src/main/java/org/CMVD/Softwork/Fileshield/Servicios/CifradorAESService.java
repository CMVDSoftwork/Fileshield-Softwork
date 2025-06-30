package org.CMVD.Softwork.Fileshield.Servicios;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public interface CifradorAESService {
    SecretKey generarClave();
    byte[] claveABytes(SecretKey clave);
    SecretKey bytesAClave(byte[] bytes);
    void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave) throws IOException, GeneralSecurityException;
    void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException;
    String descifrarTexto(String textoCifradoBase64, SecretKey clave) throws GeneralSecurityException;
    String cifrarTexto(String textoPlano, SecretKey clave);
    SecretKey base64AClave(String clave);
    String claveABase64(SecretKey clave);
    public void cifrarArchivoStream(File inputFile, OutputStream outputStream, SecretKey clave) throws Exception;
    byte[] descifrarBytes(byte[] datosCifradosConIV, SecretKey clave) throws Exception;
    void descifrarArchivoStream(InputStream inputStream, OutputStream outputStream, SecretKey clave) throws Exception;
    byte[] cifrarBytes(byte[] datosOriginales, SecretKey clave) throws Exception;
}
