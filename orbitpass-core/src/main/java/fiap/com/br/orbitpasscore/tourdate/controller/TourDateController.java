package fiap.com.br.orbitpasscore.tourdate.controller;

import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateUpdateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.response.TourDateResponse;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.tourdate.mapper.TourDateMapper;
import fiap.com.br.orbitpasscore.tourdate.service.TourDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour-dates")
@RequiredArgsConstructor
@Tag(name = "Tour Date", description = "Endpoints for managing space travel tour dates")
public class TourDateController {

    private final TourDateService tourDateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new tour date", description = "Endpoint to create a new departure date for a tour (Admin only)")
    @ApiResponse(responseCode = "201", description = "Tour date created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    public ResponseEntity<TourDateResponse> create(@RequestBody @Valid TourDateRequest request) {
        TourDate saved = tourDateService.create(request);
        return ResponseEntity
                .created(URI.create("/api/tour-dates/" + saved.getId()))
                .body(TourDateMapper.toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Get all tour dates", description = "Retrieve a list of all space travel tour dates")
    @ApiResponse(responseCode = "200", description = "Tour dates retrieved successfully")
    public ResponseEntity<List<TourDateResponse>> findAll() {
        return ResponseEntity.ok(tourDateService.findAll()
                .stream()
                .map(TourDateMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Get tour date by ID", description = "Retrieve details of a specific tour date by its ID")
    @ApiResponse(responseCode = "200", description = "Tour date details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tour date not found")
    public ResponseEntity<TourDateResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(TourDateMapper.toResponse(tourDateService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a tour date", description = "Update details of an existing tour date by its ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "Tour date updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "404", description = "Tour date not found")
    public ResponseEntity<TourDateResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TourDateUpdateRequest request) {
        TourDate updated = tourDateService.update(id, request);
        return ResponseEntity.ok(TourDateMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a tour date", description = "Remove a space travel tour date by its ID (Admin only)")
    @ApiResponse(responseCode = "244", description = "Tour date deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tour date not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourDateService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
