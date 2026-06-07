package fiap.com.br.orbitpasscore.tour.controller;

import fiap.com.br.orbitpasscore.tour.dto.request.TourRequest;
import fiap.com.br.orbitpasscore.tour.dto.request.TourUpdateRequest;
import fiap.com.br.orbitpasscore.tour.dto.response.TourResponse;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tour.mapper.TourMapper;
import fiap.com.br.orbitpasscore.tour.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@Tag(name = "Tour", description = "Endpoints for managing space travel tours")
public class TourController {

    private final TourService tourService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new tour", description = "Endpoint to create a new space travel tour (Admin only)")
    @ApiResponse(responseCode = "201", description = "Tour created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    public ResponseEntity<TourResponse> create(@RequestBody @Valid TourRequest request) {
        Tour saved = tourService.create(TourMapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/api/tours/" + saved.getId()))
                .body(TourMapper.toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Get all tours", description = "Retrieve a list of all space travel tours")
    @ApiResponse(responseCode = "200", description = "Tours retrieved successfully")
    public ResponseEntity<List<TourResponse>> findAll() {
        return ResponseEntity.ok(tourService.findAll()
                .stream()
                .map(TourMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Cacheable(value = "tours", key = "#id")
    @Operation(summary = "Get tour by ID", description = "Retrieve details of a specific tour by its unique identifier, wrapped with HATEOAS hypermedia links")
    @ApiResponse(responseCode = "200", description = "Tour details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tour not found")
    public ResponseEntity<EntityModel<TourResponse>> findById(@PathVariable Long id) {
        TourResponse response = TourMapper.toResponse(tourService.findById(id));
        EntityModel<TourResponse> model = EntityModel.of(response);
        
        model.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TourController.class).findById(id)).withSelfRel());
        model.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TourController.class).findAll()).withRel("all-tours"));
                
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "tours", key = "#id")
    @Operation(summary = "Update a tour", description = "Update details of an existing space travel tour by its ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "Tour updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "404", description = "Tour not found")
    public ResponseEntity<TourResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TourUpdateRequest request) {
        Tour updated = tourService.update(id, TourMapper.toEntity(request));
        return ResponseEntity.ok(TourMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "tours", key = "#id")
    @Operation(summary = "Delete a tour", description = "Remove a space travel tour by its ID (Admin only)")
    @ApiResponse(responseCode = "244", description = "Tour deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tour not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
