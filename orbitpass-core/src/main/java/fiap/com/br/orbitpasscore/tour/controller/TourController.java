package fiap.com.br.orbitpasscore.tour.controller;

import fiap.com.br.orbitpasscore.tour.dto.request.TourRequest;
import fiap.com.br.orbitpasscore.tour.dto.request.TourUpdateRequest;
import fiap.com.br.orbitpasscore.tour.dto.response.TourResponse;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tour.mapper.TourMapper;
import fiap.com.br.orbitpasscore.tour.service.TourService;
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
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponse> create(@RequestBody @Valid TourRequest request) {
        Tour saved = tourService.create(TourMapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/api/tours/" + saved.getId()))
                .body(TourMapper.toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    public ResponseEntity<List<TourResponse>> findAll() {
        return ResponseEntity.ok(tourService.findAll()
                .stream()
                .map(TourMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    public ResponseEntity<TourResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(TourMapper.toResponse(tourService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TourUpdateRequest request) {
        Tour updated = tourService.update(id, TourMapper.toEntity(request));
        return ResponseEntity.ok(TourMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
