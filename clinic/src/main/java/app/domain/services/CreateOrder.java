package app.domain.services;

import java.sql.Date;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Order;
import app.domain.models.OrderItem;
import app.domain.models.Patient;
import app.domain.models.User;
import app.domain.models.Role;
import app.domain.models.ItemType;
import app.domain.ports.UserPort;
import app.domain.ports.PatientPort;
import app.domain.ports.OrderPort;

public class CreateOrder {

    private UserPort userPort;
    private PatientPort patientPort;
    private OrderPort orderPort;

    public void create(Order order) throws BusinessException {
        
        // validacion 1: numero de orden unico (0 - 999999)
        if (order.getId() < 0 || order.getId() > 999999) {
            throw new BusinessException("El numero de orden debe estar entre 0 y 999999 (max 6 digitos)");
        }
        if (orderPort.existsOrderNumber(order.getId())) {
            throw new BusinessException("El numero de orden ya existe en el sistema");
        }

        // validacion 2: medico existe en el sistema
        User doctor = userPort.findByDocument(order.getDoctor().getDocument());
        if (doctor == null) {
            throw new BusinessException("El medico con ese documento no existe en el sistema");
        }

        // validaciopn 3: medico tiene rol de DOCTOR
        if (!doctor.getRole().equals(Role.DOCTOR)) {
            throw new BusinessException("Solo los medicos con el rol DOCTOR pueden crear las ordens");
        }

        // validacion 4: paciente esta registradp
        Patient patient = patientPort.findById(order.getPatient().getId());
        if (patient == null) {
            throw new BusinessException("El paciente no existe registrado en el sistema");
        }

        // validacion 5: minimo 1 iten en la orden
        if (order.getOrderItems() == null || order.getOrderItems().length == 0) {
            throw new BusinessException("La orden debe contener al menos un item");
        }

        // validacion 6: no items duplicados en la orden
        for (int i = 0; i < order.getOrderItems().length; i++) {
            for (int j = i + 1; j < order.getOrderItems().length; j++) {
                if (order.getOrderItems()[i].getId() == order.getOrderItems()[j].getId()) {
                    throw new BusinessException("No puede existir dos elementos en la misma orden con el mismo numero de item");
                }
            }
        }

        // validacion 7: regla de ayuda diagnostica
        boolean hasMedicalSupport = false;
        boolean hasMedicine = false;
        boolean hasProcedure = false;

        for (OrderItem item : order.getOrderItems()) {
            if (item.getItemType() == ItemType.MEDICALSUPPORT) {
                hasMedicalSupport = true;
            } else if (item.getItemType() == ItemType.MEDICINE) {
                hasMedicine = true;
            } else if (item.getItemType() == ItemType.PROCEDURE) {
                hasProcedure = true;
            }
        }

        if (hasMedicalSupport && (hasMedicine || hasProcedure)) {
            throw new BusinessException("Cuando se receta un medical support no puede recetarse medicamentos ni procedimientos, ya que no se tiene certeza del diagnostico");
        }

        // validacion 8: asignar fecha automatica
        order.setDate(new Date(System.currentTimeMillis()));

        // validfacion 9: asignar datos validados
        order.setPatient(patient);
        order.setDoctor(doctor);

        // valdiacion 10: persistencia en la base de datos
        orderPort.save(order);
    }

}
