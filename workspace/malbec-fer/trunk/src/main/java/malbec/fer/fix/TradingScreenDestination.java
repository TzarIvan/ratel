package malbec.fer.fix;

import static malbec.fer.util.OrderValidation.isValidClientOrderId;
import static malbec.util.FuturesSymbolUtil.*;

import java.util.List;
import java.util.Properties;

import malbec.fer.CancelRequest;
import malbec.fer.Order;
import malbec.fer.mapping.DatabaseMapper;
import malbec.util.EmailSettings;
import malbec.util.SystematicFacade;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.ClOrdID;
import quickfix.field.ExDestination;
import quickfix.field.ExecBroker;
import quickfix.field.ListID;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.SecurityType;
import quickfix.field.SenderSubID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.fix42.ExecutionReport;

public class TradingScreenDestination extends FixDestination {

    public TradingScreenDestination(String name, Properties config, EmailSettings emailSettings,
        DatabaseMapper dbm) {
        super(name, config, emailSettings, dbm);
        platform = "TRADS";
    }

    /*
     * (non-Javadoc)
     * 
     * @see malbec.fer.fix.FixDestination#populateCommonFields(malbec.fer.Order, quickfix.Message,
     * java.util.List)
     */
    @Override
    protected void populateCommonFields(Order order, Message fixMessage, List<String> conversionErrors) {

        // We need to set the tag 76 to the final execution partner
        // TS-STG is for all ticket orders
        // MFGBLUS is for MFGlobal
        // CITIFUT is for Citi Futures
        if (order.getRoute() == null) {
            conversionErrors.add("TradingScreen orders must specify a route");
        } else if (mapRouteToFixRoute(order.getRoute(), order.getSecurityType()) == null) {
            conversionErrors.add("Unable to map " + order.getRoute() + " to platform route");
        }

        super.populateCommonFields(order, fixMessage, conversionErrors);

        if (order.getBasketName() != null) {
            fixMessage.setField(new ListID(order.getBasketName()));
        }
        // For now, send the strategy tag as TEXT(tag 58)
        fixMessage.setString(Text.FIELD, order.getStrategy());
        fixMessage.setString(SenderSubID.FIELD, getUserID());
    }

    /**
     * We use MANS in the clients, but need to map to the correct FIX value.
     * 
     * @param route
     * @return
     */
    private String mapRouteToFixRoute(String route, String securityType) {
        if ("MANS".equalsIgnoreCase(route)) {
            if ("Futures".equalsIgnoreCase(securityType)) {
                return "MFGBLFUT";
            }
            return "MFGBLUS";
        } else if ("CITI".equalsIgnoreCase(route)) {
            return "CITIFUT";
        } else if ("TS".equalsIgnoreCase(route)) {
            return "TS-SS"; // TradingScreen simulator - UAT
            //return "TS-STG"; // TradingScreen staged orders
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see malbec.fer.fix.FixDestination#populateFuturesFields(malbec.fer.Order, quickfix.Message,
     * java.util.List)
     */
    @Override
    protected void populateFuturesFields(Order order, Message fixMessage, List<String> errors) {
        String ticker = order.getSymbol();
        // Need to send RIC code
        String symbolRoot = extractSymbolRoot(ticker);
        String maturityMonthYear = extractMaturityMonthFromSymbol(ticker);

        String platformSendingRoot = getDatabaseMapper().mapBloombergRootToPlatformSendingRoot(getPlatform(), symbolRoot);
        if (platformSendingRoot == null) {
            errors.add("Futures symbol not configured for " + getPlatform());
        } else {
            // use the else, to prevent the Null field value for QFJ
            String platformSendingSymbol = combineRootMaturityMonthYear(platformSendingRoot, maturityMonthYear);
            order.setSecurityIDSource(SecurityIDSource.RIC_CODE);
            fixMessage.setField(new Symbol(ticker));
            fixMessage.setField(new SecurityIDSource(SecurityIDSource.RIC_CODE));
            fixMessage.setField(new SecurityID(platformSendingSymbol));
        }

        String targetDestination = "TS-STG";
        if (isForceToTicket()) {
            targetDestination = "TS-STG";
        } else {
            // targetDestination = "TS-SS";
            targetDestination = mapRouteToFixRoute(order.getRoute(), order.getSecurityType());
        }

        order.setExchange(targetDestination);
        fixMessage.getHeader().setField(new ExecBroker(targetDestination));
        fixMessage.setString(SecurityType.FIELD, SecurityType.FUTURE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see malbec.fer.fix.FixDestination#determineAccount(malbec.fer.Order)
     */
    @Override
    protected String determineAccount(Order order) {
        // Based on the route, lookup the account
        if (order.getRoute() == null) {
            return null;
        }
        String accountType = order.isFutures() ? "FUTURES" : "EQUITY";
        
        return getDatabaseMapper().lookupAccountForRoute(getPlatform(), order.getRoute(), accountType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see malbec.fer.fix.FixDestination#populateCommonFieldsForCancel(malbec.fer.CancelRequest,
     * quickfix.Message, java.util.List)
     */
    @Override
    protected void populateCommonFieldsForCancel(CancelRequest cancelRequest, Message fixMessage,
        List<String> errors) {
        super.populateCommonFieldsForCancel(cancelRequest, fixMessage, errors);
    }

    @Override
    public void onMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound,
        UnsupportedMessageType, IncorrectTagValue {
        // Check that this is an order we sent
        String clientOrderId = message.getString(ClOrdID.FIELD);

        // TradingScreen ClientOrderIds are greater than 16 (20090601-144030726-652-86)
        if (isValidClientOrderId(clientOrderId) /* && dao.findOrderByClientOrderID(clientOrderId) != null */) {
            super.onMessage(message, sessionID);
        } else {
            log.warn("Received Execution report for order we did not create: " + message);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see malbec.fer.fix.FixDestination#populateEquityFields(malbec.fer.Order, quickfix.Message,
     * java.util.List)
     */
    @Override
    protected void populateEquityFields(Order order, Message fixMessage, List<String> errors) {
        super.populateEquityFields(order, fixMessage, errors);

        String targetDestination = "TS-STG";

        // String originalExchange = order.getExchange();

        if (isForceToTicket()) {
            targetDestination = "TS-STG";
        } else {
            // MFGBLUS is MF Global
            // targetDestination = "MFGBLUS";
            targetDestination = mapRouteToFixRoute(order.getRoute(), order.getSecurityType());
            // TS-SS is TradingScreen Simulator
            // targetDestination = "TS-SS";
        }

        order.setExchange(targetDestination);
        fixMessage.getHeader().setField(new ExecBroker(targetDestination));
        // We can only send limit orders to SMRT, all other need to be NITE

        try {
            // Skip all of this if we are cancelling the order
            if (MsgType.ORDER_CANCEL_REQUEST.equals(fixMessage.getHeader().getString(MsgType.FIELD))) {
                return;
            }

            char tif = fixMessage.getChar(TimeInForce.FIELD);
            char orderType = fixMessage.getChar(OrdType.FIELD);

            if (TimeInForce.AT_THE_CLOSE != tif && OrdType.LIMIT == orderType) {
                fixMessage.setString(ExDestination.FIELD, "SMRT");
            } else if (TimeInForce.AT_THE_CLOSE == tif) {
                // convert to FIX 4.2
                if (OrdType.LIMIT == orderType) {
                    fixMessage.setChar(OrdType.FIELD, OrdType.LIMIT_ON_CLOSE);
                } else if (OrdType.MARKET == orderType) {
                    fixMessage.setChar(OrdType.FIELD, OrdType.MARKET_ON_CLOSE);
                }
                fixMessage.setChar(TimeInForce.FIELD, TimeInForce.DAY);
                // We must lookup the exchange to send to o
                String ticker = fixMessage.getString(Symbol.FIELD);
                String destination = SystematicFacade.lookupTickerPrimaryExchange(ticker);
                fixMessage.setString(ExDestination.FIELD, destination);
            } else {
                fixMessage.setString(ExDestination.FIELD, "NITE");
                // fixMessage.setString(ExDestination.FIELD, "N");
                // fixMessage.setInt(111, 100); //MaxFloor
            }
        } catch (FieldNotFound e) {
            log.error("FIX Message is messed up", e);
        }
    }
    
    protected String getTextErrorMessage(Message fixMessage) throws FieldNotFound {
        String textField = fixMessage.getString(Text.FIELD);
        
        int pipePos = textField.indexOf('|');
        if (pipePos > 0) {
            return textField.substring(pipePos + 1, textField.length()).trim();
        }
        
        return textField;
    }

    /* (non-Javadoc)
     * @see malbec.fer.fix.FixDestination#setBuyToCover(quickfix.Message)
     */
    @Override
    protected void setBuyToCover(Message fixMessage) {
        //super.setBuyToCover(fixMessage);
        fixMessage.setChar(Side.FIELD, Side.BUY); // work around a PMD error -- Overriding method merely calls super
        // We are not doing this as MFGlobal and TradingScreen do not support
        // http://www.fixprotocol.org/specifications/fields/6000-6999
        //fixMessage.setChar(6098, 'Y'); // BuytoCoverIndicator
    }

}
