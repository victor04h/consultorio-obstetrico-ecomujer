package com.consultorio_obstetra.obstetra.dto.admin.cita;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Cuerpo de "Reagendar Cita": el admin aprueba o rechaza la solicitud. */
@Getter
@Setter
public class ReagendarRequest {

    @NotNull(message = "Debes indicar si la reasignación se aprueba o se rechaza")
    private Boolean aprobar;

    private LocalDateTime fechaHoraNueva;

    private String motivoRechazo;

    @AssertTrue(message = "Si apruebas la reasignación debes indicar la nueva fecha y hora")
    public boolean isFechaValidaCuandoAprueba() {
        return aprobar == null || !aprobar || fechaHoraNueva != null;
    }

    @AssertTrue(message = "Si rechazas la reasignación debes indicar el motivo")
    public boolean isMotivoValidoCuandoRechaza() {
        return aprobar == null || aprobar || (motivoRechazo != null && !motivoRechazo.isBlank());
    }
}
