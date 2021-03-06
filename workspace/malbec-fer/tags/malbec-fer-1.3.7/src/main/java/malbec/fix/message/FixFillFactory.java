package malbec.fix.message;

import java.util.HashMap;
import java.util.Map;

import malbec.fer.mapping.IDatabaseMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.field.MsgType;
import quickfix.field.MultiLegReportingType;
import quickfix.field.SenderCompID;

public class FixFillFactory {

    private static final Logger staticLog = LoggerFactory.getLogger(FixFill.class);
    
    private static Map<String, IFixFillConverter> fillConverters = new HashMap<String, IFixFillConverter>();
    
    private static IFixFillConverter defaultConverter;

    private static IDatabaseMapper dbm;
    
    
    public static FixFill valueOf(Message fixExecution) {
        try {
            // sanity checks first
            Header header = fixExecution.getHeader();
            String msgType = header.getString(MsgType.FIELD);
            
            String multiLegReport = null;
            
            if (fixExecution.isSetField(MultiLegReportingType.FIELD)) {
                multiLegReport = String.valueOf(fixExecution.getChar(MultiLegReportingType.FIELD));
            }
            
            // We can only process Execution reports, and skip multi-leg reporting
            if (!"8".equals(msgType) || "3".equals(multiLegReport)) {
                return null;
            }
            
            // determine the SenderCompId to determine the strategy to use
            IFixFillConverter converter = fillConverters.get(header.getString(SenderCompID.FIELD));
            return converter != null ? converter.valueOf(fixExecution) : defaultConverter.valueOf(fixExecution); 
        } catch (FieldNotFound e) {
            staticLog.error("Unable to convert ExecutionReport to FixFill", e);
        }

        return null;  
    }
   
    /**
     * We may have to add logic for SenderSubId as Fills from EMSX will not actually be
     * from Bloomberg.
     * 
     */
    private static void initialize() {
        if (dbm == null) {
            throw new IllegalStateException("DatabaseMapper was not initialized");
        }
        defaultConverter = new DefaultFixFillConverter(dbm);

        // The drop copy sessions
        RediFixFillConverter redi = new RediFixFillConverter(dbm);
        fillConverters.put("REDIRPT", redi);

        fillConverters.put("TRAD", new TradeStationFixFillConverter(dbm));
        fillConverters.put("TRADEWEB", new TradeWebFixFillConverter(dbm));
        fillConverters.put("MSDW-PPT", new PassportFixFillConverter(dbm));
        
        TradingScreenFixFillConverter tradingScreen = new TradingScreenFixFillConverter(dbm);
        fillConverters.put("TSRPT", tradingScreen);
        
        // FIX session
        fillConverters.put("REDI", redi);
        fillConverters.put("TS", tradingScreen);
    }

    public static void initialize(IDatabaseMapper mapper) {
        dbm = mapper;
        initialize();
    }
}
