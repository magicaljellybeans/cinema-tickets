package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.exception.PriceNotFoundException;

public class TicketPriceServiceImpl implements TicketPriceService {

    @Override
    public int retrievePrice(TicketType type) throws PriceNotFoundException {
        switch (type) {
            case ADULT:
                return 20;
            case CHILD:
                return 10;
            case INFANT:
                return 0;
            default:
                throw new PriceNotFoundException();
        }
    }
    
}
