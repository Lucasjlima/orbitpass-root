package fiap.com.br.orbitpasscore.ticket.controller;

import fiap.com.br.orbitpasscore.security.UserDetailsImpl;
import fiap.com.br.orbitpasscore.ticket.dto.request.TicketPurchaseRequest;
import fiap.com.br.orbitpasscore.ticket.dto.response.TicketResponse;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.ticket.mapper.TicketMapper;
import fiap.com.br.orbitpasscore.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "Endpoints for purchasing tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Purchase a ticket", description = "Initiate a ticket purchase for a space travel tour")
    @ApiResponse(responseCode = "202", description = "Ticket purchase initiated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload or insufficient spots")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    public TicketResponse purchase(@Valid @RequestBody TicketPurchaseRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        Ticket ticket = ticketService.purchase(principal.getId(), request.tourDateId(), request.paymentType());
        return TicketMapper.toResponse(ticket);
    }
}
