package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByApellidoPAndApellidoM(String apellidoP, String apellidoM);
    Optional<Usuario> findByNombreAndApellidoP(String nombre, String apellidoP);
    Optional<Usuario> findByCorreoAndNombre(String correo, String nombre);
    @Override
    Optional<Usuario> findById(Integer idUsuario);


}
