package com.consultorio_obstetra.obstetra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de correos transaccionales (validacion de cuenta, recuperacion de
 * contrasena). El servidor SMTP se configura en application.properties;
 * mientras no tenga credenciales reales, los envios fallan silenciosamente
 * (solo se registra un warning) para no bloquear el registro de pacientes
 * en un entorno de desarrollo/academico sin correo configurado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCorreoValidacion(String destinatario, String token) {
        String asunto = "Confirma tu cuenta - Consultorio Obstétrico";
        String cuerpo = "Hola,\n\n"
                + "Gracias por registrarte. Usa el siguiente código para validar tu correo:\n\n"
                + token + "\n\n"
                + "Si no solicitaste este registro, ignora este mensaje.";
        enviar(destinatario, asunto, cuerpo);
    }

    private void enviar(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
        } catch (Exception ex) {
            log.warn("No se pudo enviar el correo a {} (¿SMTP sin configurar?): {}", destinatario, ex.getMessage());
        }
    }
}
