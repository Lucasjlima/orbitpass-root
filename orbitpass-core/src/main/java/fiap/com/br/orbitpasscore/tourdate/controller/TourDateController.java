package fiap.com.br.orbitpasscore.tourdate.controller;

import fiap.com.br.orbitpasscore.tourdate.service.TourDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour-dates")
@RequiredArgsConstructor
public class TourDateController {

    private final TourDateService tourDateService;
}
