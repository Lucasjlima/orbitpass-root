package fiap.com.br.orbitpasscore.ticket.controller;

import fiap.com.br.orbitpasscore.security.UserDetailsImpl;
import fiap.com.br.orbitpasscore.ticket.dto.request.TicketPurchaseRequest;
import fiap.com.br.orbitpasscore.ticket.dto.response.TicketResponse;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.ticket.mapper.TicketMapper;
import fiap.com.br.orbitpasscore.ticket.service.TicketService;
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
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TicketResponse purchase(@Valid @RequestBody TicketPurchaseRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        Ticket ticket = ticketService.purchase(principal.getId(), request.tourDateId(), request.paymentType());
        return TicketMapper.toResponse(ticket);
    }
}
