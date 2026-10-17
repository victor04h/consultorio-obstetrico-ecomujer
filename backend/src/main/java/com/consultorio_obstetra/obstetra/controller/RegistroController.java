package com.consultorio_obstetra.obstetra.controller;

import com.consultorio_obstetra.obstetra.dto.common.MensajeResponse;
import com.consultorio_obstetra.obstetra.dto.registro.ConsultaDniRequest;
import com.consultorio_obstetra.obstetra.dto.registro.ConsultaDniResponse;
import com.consultorio_obstetra.obstetra.dto.registro.RegistroRequest;
import com.consultorio_obstetra.obstetra.dto.registro.RegistroResponse;
import com.consultorio_obstetra.obstetra.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints publicos del proceso de Registro (accesibles sin login, ver
 * SecurityConfig): consulta a la fuente externa de identidad y alta del
 * paciente (con o sin apoderado, segun su edad).
 */
@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping("/consultar-dni")
    public ResponseEntity<ConsultaDniResponse> consultarDni(@Valid @RequestBody ConsultaDniRequest request) {
        return ResponseEntity.ok(registroService.consultarDni(request.getNumeroDocumento()));
    }

    @PostMapping
    public ResponseEntity<RegistroResponse> completarRegistro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(registroService.completarRegistro(request));
    }

    @GetMapping("/validar-correo")
    public ResponseEntity<MensajeResponse> validarCorreo(@RequestParam String token) {
        registroService.validarCorreo(token);
        return ResponseEntity.ok(MensajeResponse.de("Correo validado correctamente. Ya puedes iniciar sesión."));
    }
}
