package malbec.fer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import malbec.fer.IOrderDestination;
import malbec.fer.mapping.DatabaseMapper;

public class BaseOrderRequestProcessor implements IOrderRequestProcessor {

    protected final DatabaseMapper dbm;
    
    protected BaseOrderRequestProcessor(DatabaseMapper dbm) {
        this.dbm = dbm;
    }

    @Override
    public Map<String, String> process(Map<String, String> sourceMessage,
            Map<String, IOrderDestination> orderDestinations) {
        return null;
    }

    protected IOrderDestination determineDestination(Map<String, IOrderDestination> orderDestinations, String platform) {
        if (platform == null) {
            return null;
        }
        return orderDestinations.get(platform.toUpperCase());
    }

    /**
     * Add the list of strings that are errors to the map
     * 
     * @param message
     * @param errors
     * @return
     */
    protected String addErrorsToMessage(Map<String, String> message, List<String> errors) {
        StringBuilder sb = new StringBuilder(1024);
        int i = 1;
    
        // TODO check for existing ERROR_x keys and start from there
        for (String error : errors) {
            message.put("ERROR_" + i, "Failed to send order: " + error);
            sb.append(error).append(";");
            i++;
        }
    
        return sb.toString();
    }

    protected void addErrorToMessage(Map<String, String> returnMessage, String error) {
        List<String> errors = new ArrayList<String>();
        errors.add(error);
        addErrorsToMessage(returnMessage, errors);
    }

    protected String buildPlatformList(Map<String, IOrderDestination> orderDestinations) {
        StringBuffer sb = new StringBuffer(128);
    
        boolean first = true;
        for (String platformKey : orderDestinations.keySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(platformKey);
        }
    
        return sb.toString();
    }

}
