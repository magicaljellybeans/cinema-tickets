package uk.gov.dwp.uc.pairtest;

import java.util.HashMap;
import java.util.Map;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private TicketPriceService ticketPriceService;
    private SeatReservationService seatReservationService;
    private TicketPaymentService ticketPaymentService;
    /**
     * Should only have private methods other than the one below.
     */

     public TicketServiceImpl(TicketPriceService ticketPriceService, SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService) {
        this.ticketPriceService = ticketPriceService;
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
     }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        authenticate(accountId);

        final Map<TicketType, Integer> ticketRequests = getCombinedTicketRequests(ticketTypeRequests);
        validate(ticketRequests);
        
        final int totalPrice = getPriceTotal(ticketRequests);
        final int seatsRequired = getSeatAmountRequired(ticketRequests);

        seatReservationService.reserveSeat(accountId, seatsRequired);
        ticketPaymentService.makePayment(accountId, totalPrice);
    }

    private void authenticate(Long accountId) {
        if (accountId <= 0) { 
            System.out.println("WARN: Authentication of account failed.");
            throw new InvalidPurchaseException();
        }
    }

    private Map<TicketType, Integer> getCombinedTicketRequests(TicketTypeRequest... ticketTypeRequests) {
        Map<TicketType, Integer> ticketRequests = new HashMap<TicketType, Integer>();
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            ticketRequests.put(ticketTypeRequest.getTicketType(), ticketTypeRequest.getNoOfTickets());
        }
        return ticketRequests;
    }

    private void validate(Map<TicketType, Integer> ticketRequests) {
        if (ticketRequests.isEmpty()) {
            System.out.println("WARN: No ticket requests made.");
            throw new InvalidPurchaseException();
        }
        if(ticketRequests.values().stream().reduce(0, Integer::sum) > 20) {
            System.out.println("WARN: More than 20 tickets requested.");
            throw new InvalidPurchaseException();
        }
        if(ticketRequests.get(TicketType.ADULT) < 1) {
            System.out.println("WARN: No adult tickets in requests.");
            throw new InvalidPurchaseException();
        }
    }

    private int getPriceTotal(Map<TicketType, Integer> ticketRequests) {
        int total = 0;
        for (var ticketRequest: ticketRequests.entrySet()) {
            total += ticketRequest.getValue() * ticketPriceService.retrievePrice(ticketRequest.getKey());
        }
        return total;
    }

    private int getSeatAmountRequired(Map<TicketType, Integer> ticketRequests) {
        int total = 0;
        for (var ticketRequest: ticketRequests.entrySet()) {
            if(ticketRequest.getKey() == TicketType.ADULT || ticketRequest.getKey() == TicketType.CHILD) {
                total += ticketRequest.getValue();
            }
        }
        return total;
    }

}
