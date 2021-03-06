package com.fftw.bloomberg.rtf.messages;

import org.joda.time.LocalDate;
import static com.fftw.util.strings.FixedWidthExtractor.*;

/**
 * 001-005 Highest sequence number generated by Bloomberg I5
 * 006-010 Highest sequence number transferred to Client I5
 * 011-015 First sequence number transferred to Client I5
 * 016-021 Last ticket number of the day I6
 * 022-029 Next trade date (YYYYMMDD I8
 */
public class RtfEndOfDay implements RtfMessageBody {

    private LocalDate messageDate;
    private int messageSequenceNumber;

    private int maxSequenceGenerated;
    private int maxSequenceSent;
    private int firstSequence;
    private int lastTicket;
    private LocalDate nextTradeDate;

    public int getMaxSequenceGenerated() {
        return maxSequenceGenerated;
    }

    public int getMaxSequenceSent() {
        return maxSequenceSent;
    }

    public int getFirstSequence() {
        return firstSequence;
    }

    public int getLastTicket() {
        return lastTicket;
    }

    public LocalDate getNextTradeDate() {
        return nextTradeDate;
    }

    public char getMessageType() {
        return '5';
    }

    public LocalDate getMessageDate() {
        return messageDate;
    }

    public int getMessageSequenceNumber() {
        return messageSequenceNumber;
    }

    public boolean hasRawMessage() {
        return false;
    }

    public String getRawMessage() {
        throw new UnsupportedOperationException("End-Of-Day does not store raw message");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);

        String delimiter = ", ";

        sb.append("messageDate=").append(messageDate);
        sb.append(delimiter).append("sequenceNumber=").append(messageSequenceNumber);
        sb.append(delimiter).append("maxSequenceGenerated=").append(maxSequenceGenerated);
        sb.append(delimiter).append("maxSequenceSent=").append(maxSequenceSent);
        sb.append(delimiter).append("fistSequence=").append(firstSequence);
        sb.append(delimiter).append("lastTicket=").append(lastTicket);
        sb.append(delimiter).append("nextTradeDate=").append(nextTradeDate);
        
        return sb.toString();
    }

    public static RtfEndOfDay valueOf(LocalDate messageDate, int messageSequence, String rawString) {
        RtfEndOfDay eod = new RtfEndOfDay();

        eod.messageDate = messageDate;
        eod.messageSequenceNumber = messageSequence;
        
        eod.maxSequenceGenerated = extractInt(rawString, 1, 5);
        eod.maxSequenceSent = extractInt(rawString, 6, 10);
        eod.firstSequence = extractInt(rawString, 11, 15);
        eod.lastTicket = extractInt(rawString, 16, 21);
        eod.nextTradeDate = extractDate(rawString, 22, 29);

        return eod;
    }
}
