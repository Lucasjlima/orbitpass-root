package fiap.com.br.orbitpasscore.config;

import fiap.com.br.orbitpasscore.tourdate.dto.request.TourSearchRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.response.TourAvailabilityInfo;
import fiap.com.br.orbitpasscore.tourdate.service.TourDateService;
import java.util.List;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class TourDateFunctionConfig {

    @Bean
    @Description("Busca tours e datas disponíveis para um determinado destino espacial (Lua, Marte ou Órbita Terrestre), informando preços, vagas e disponibilidade de ingressos. IMPORTANTE: traduza o nome do destino para português antes de chamar esta função (Moon -> Lua, Mars -> Marte, Earth Orbit -> Órbita Terrestre).")
    public Function<TourSearchRequest, List<TourAvailabilityInfo>> buscarToursDisponiveis(TourDateService tourDateService) {
        return request -> tourDateService.findAvailabilityByDestination(request.destino());
    }
}
