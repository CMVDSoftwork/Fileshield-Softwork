package org.CMVD.Softwork.Fileshield.Servicios;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CifradorAESService {
    SecretKey generarClave();
    byte[] claveABytes(SecretKey clave);
    SecretKey bytesAClave(byte[] bytes);
    void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave) throws IOException, GeneralSecurityException;
    void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException;
    String cifrarTexto(String textoPlano, SecretKey clave);
}
