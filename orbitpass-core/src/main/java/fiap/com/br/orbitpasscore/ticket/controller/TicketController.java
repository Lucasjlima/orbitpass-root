package fiap.com.br.orbitpasscore.ticket.controller;

import fiap.com.br.orbitpasscore.security.UserDetailsImpl;
import fiap.com.br.orbitpasscore.ticket.dto.request.TicketPurchaseRequest;

import fiap.com.br.orbitpasscore.ticket.dto.response.TicketResponse;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.ticket.mapper.TicketMapper;
import fiap.com.br.orbitpasscore.ticket.repository.TicketRepository;
import fiap.com.br.orbitpasscore.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "Endpoints for purchasing and managing tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Get user tickets", description = "Retrieve a list of all tickets purchased by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    public List<TicketResponse> findAllMyTickets(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ticketRepository.findByUserId(principal.getId())
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Update ticket launch date", description = "Change the tour date of an existing ticket reservation")
    @ApiResponse(responseCode = "200", description = "Ticket date updated successfully")
    @ApiResponse(responseCode = "400", description = "Insufficient spots on the new date")
    @ApiResponse(responseCode = "404", description = "Ticket or new Tour Date not found")
    public TicketResponse updateTicketDate(@PathVariable Long id,
                                           @RequestBody Map<String, Long> payload,
                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        Long newTourDateId = payload.get("tourDateId");
        Ticket ticket = ticketService.updateTourDate(id, principal.getId(), newTourDateId);
        return TicketMapper.toResponse(ticket);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'DEFAULT_USER')")
    @Operation(summary = "Cancel a ticket", description = "Delete a ticket reservation and release the spot back to the tour date")
    @ApiResponse(responseCode = "204", description = "Ticket canceled and deleted successfully")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public void cancelTicket(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal) {
        ticketService.cancelAndBuildRelease(id, principal.getId());
    }
}