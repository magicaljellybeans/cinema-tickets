package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.exception.PriceNotFoundException;

public interface TicketPriceService {

    int retrievePrice(TicketType type) throws PriceNotFoundException;

}
