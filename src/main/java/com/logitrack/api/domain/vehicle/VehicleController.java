package com.logitrack.api.domain.vehicle;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Endpoint TMS: Cadastrar veículo na frota
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody @Valid Vehicle vehicle) {
        if (vehicleService.existsByPlate(vehicle.getPlate())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict se a placa já existir
        }

        Vehicle savedVehicle = vehicleService.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }

    // Endpoint TMS: Listar frota disponível (Paginado)
    @GetMapping
    public ResponseEntity<Page<Vehicle>> listAll(
            @PageableDefault(page = 0, size = 10, sort = "model", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<Vehicle> vehiclesPage = vehicleService.findAll(pageable);
        return ResponseEntity.ok(vehiclesPage);
    }
}
