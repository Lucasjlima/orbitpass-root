package fiap.com.br.orbitpasscore.tourdate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record TourSearchRequest(
        @JsonProperty(required = true)
        @JsonPropertyDescription("Nome do destino em português (ex: Lua, Marte, Órbita Terrestre)")
        String destino
) {}
