package fiap.com.br.orbitpasscore.tour.controller;

import fiap.com.br.orbitpasscore.tour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
}
