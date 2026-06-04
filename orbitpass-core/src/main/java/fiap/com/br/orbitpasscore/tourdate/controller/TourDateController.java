package fiap.com.br.orbitpasscore.tourdate.controller;

import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateUpdateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.response.TourDateResponse;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.tourdate.mapper.TourDateMapper;
import fiap.com.br.orbitpasscore.tourdate.service.TourDateService;
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
public class TourDateController {

    private final TourDateService tourDateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourDateResponse> create(@RequestBody @Valid TourDateRequest request) {
        TourDate saved = tourDateService.create(request);
        return ResponseEntity
                .created(URI.create("/api/tour-dates/" + saved.getId()))
                .body(TourDateMapper.toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    public ResponseEntity<List<TourDateResponse>> findAll() {
        return ResponseEntity.ok(tourDateService.findAll()
                .stream()
                .map(TourDateMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    public ResponseEntity<TourDateResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(TourDateMapper.toResponse(tourDateService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourDateResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TourDateUpdateRequest request) {
        TourDate updated = tourDateService.update(id, request);
        return ResponseEntity.ok(TourDateMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourDateService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
