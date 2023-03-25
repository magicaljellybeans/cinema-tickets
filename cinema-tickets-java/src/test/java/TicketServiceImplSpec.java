import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketPriceServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketServiceImplSpec {
    private TicketServiceImpl ticketServiceImpl;
    private Long validAccountId = 123L;

    private TicketPriceServiceImpl mockTicketPriceService = Mockito.mock(TicketPriceServiceImpl.class);
    private SeatReservationServiceImpl mockSeatReservationService = Mockito.mock(SeatReservationServiceImpl.class);
    private TicketPaymentServiceImpl mockTicketPaymentService = Mockito.mock(TicketPaymentServiceImpl.class);

    ticketServiceImpl = new TicketServiceImpl(
        mockTicketPriceService,
        mockSeatReservationService,
        mockTicketPaymentService
    );

    @Before public void initialize() {
        reset(mockTicketPriceService);
        reset(mockSeatReservationService);
        reset(mockTicketPaymentService);
    }

    @Test
    void singleAdultTicketPurchase() {
        ticketServiceImpl.purchaseTickets(validAccountId, new TicketTypeRequest(TicketType.ADULT, 1));
        verify(mockTicketPriceService).retrievePrice(TicketType.ADULT);
        verify(mockSeatReservationService).reserveSeat(validAccountId, 1);
        verify(mockTicketPaymentService).makePayment(validAccountId, 20);
    }

    @Test
    @DisplayName("20 adult tickets can be purchased.")
    void purchase20AdultTickets() {}

    @Test
    @DisplayName("21 adult tickets can not be purchased.")
    void purchase21AdultTickets() {}

    @Test
    @DisplayName("0 adult tickets can not be purchased.")
    void purchase0AdultTickets() {}

    @Test
    @DisplayName("Infant ticket can only be bought with an adult ticket")
    void checkExceptionIsThrownOnLoneInfant() {}

    @Test   
    @DisplayName("Child ticket can only be bought with an adult ticket")
    void checkExceptionIsThrownOnLoneChild() {}
}