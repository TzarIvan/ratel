package malbec.fix;

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;

import malbec.fer.fix.AbstractFixTest;
import malbec.fer.fix.FerFixClientApplication;
import malbec.util.EmailSettings;
import malbec.util.IWaitFor;
import malbec.util.InvalidConfigurationException;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Log;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.Text;

public class FixClientTest extends AbstractFixTest {

    @Test(groups = { "unittest", "unittest-fix" })
    public void testFixStartup() throws InvalidConfigurationException {
        System.out.println("Starting 'testFixStartup' " + new DateTime());
        
        final TestApplication ta = new TestApplication();
        final FixClient fc = new FixClient("Test Session", ta, createInitiatorSession());
        // specify the logger to use (Log4j);
        fc.setLoggerConfig("FixMessage");
      
        fc.start();

        waitForLogon(fc);

        assertTrue(fc.isLoggedOn(), "Session failed to logon");
        waitForValue(new IWaitFor<Boolean>() {
            @Override
            public Boolean waitFor() {
                return ta.loggedOut;
            }
        }, false, MAX_WAIT_TIME);

        assertFalse(ta.loggedOut, "We logged out after logging in");
        waitForLogon(ta);

        assertTrue(ta.loggedOn, "Application did not receive logon notification");

        fc.stop();
        sleep(2000);

        assertFalse(fc.isLoggedOn(), "Logout did not occur on session");
        assertTrue(ta.loggedOut, "Logout did not occur for application");
        assertFalse(fc.isRunning(), "FixClient is running, after stop requested");
        System.out.println("Finished 'testFixStartup'");
    }

    /**
     * Removed this from the tests as it depends on being able to shutdown
     * the external fix connection.  Since we are now using an external 
     * simulator, we cannot do this.
     * 
     * @throws InvalidConfigurationException
     */
    @Test(groups = { "unittest-disabled" })
    public void testServerShutdown() throws InvalidConfigurationException {
        System.out.println("Starting ServerShutdown");
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);

        // ensure we login before we test disconnect
        assertTrue(fc.isLoggedOn(), "Session failed to logon");

        // Stop the server
        acceptor.stop();
        assertTrue(ta.loggedOut, "Did not receive logout from server going down");
        fc.stop();
        assertFalse(fc.isLoggedOn(), "Failed to logout after stop");
    }

    @Test(groups = { "unittest", "unittest-fix" })
    public void testSequenceNumberScrewup() throws InvalidConfigurationException {
        System.out.println("Starting 'testSequenceNumber' " + new DateTime());
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);

        // ensure we login before we test disconnect
        assertTrue(fc.isLoggedOn(), "Session failed to logon");

        // increase the sequence numbers
        fc.requestResend(1);
        fc.stop();
        assertFalse(fc.isLoggedOn(), "Failed to logout after stop");
        // fc.start();
        System.out.println("Starting second instance");
        // Create another instance that tries to connect. This causes the sequence numbers
        // to get messed up.
        final FixClient fc2 = new FixClient("Test Session2");
        TestApplication ta2 = new TestApplication();
        fc2.setConfiguration(ta2, createInitiatorSession());
        fc2.start();

        waitForLogon(fc2);

        // ensure we login before we test disconnect
        assertFalse(fc2.isLoggedOn(), "Duplicate session logged on");
        fc2.stop();
    }

    @Test(groups = { "unittest", "unittest-fix" })
    public void testSequenceNumberChange() throws InvalidConfigurationException {
        System.out.println("Starting 'testSequenceNumberChange' " + new DateTime());
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);

        // ensure we login before we test disconnect
        assertTrue(fc.isLoggedOn(), "Session failed to logon");
        
        int previousTarget = fc.getTargetSequenceNumber();
        fc.setTargetSequenceNumber(100);
        assertEquals(100, fc.getTargetSequenceNumber(), "Failed to set Target sequence number");
        fc.setTargetSequenceNumber(previousTarget);
        
        int previousSender = fc.getSenderSequenceNumber();
        fc.setSenderSequenceNumber(100);
        assertEquals(100, fc.getSenderSequenceNumber(), "Failed to set Target sequence number");
        fc.setSenderSequenceNumber(previousSender);
        
        fc.stop();
    }
    /**
     * This was suppose to test the heartbeat missed, but I cannot figure out how to actually miss the
     * heartbeat.
     * 
     * @throws InvalidConfigurationException
     */
    @Test(groups = { "unittest", "unittest-fix" })
    public void testChangeHeartBeatAfterCreate() throws InvalidConfigurationException {
        System.out.println("Starting 'testChangeHeartBeatAfterCreate' " + new DateTime());
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication() {

            @Override
            public void onCreate(SessionID sessionID) {
                Session mySession = Session.lookupSession(sessionID);
                mySession.setHeartBeatInterval(10);
                super.onCreate(sessionID);
            }
        };
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);

        // ensure we login before we test disconnect
        assertTrue(fc.isLoggedOn(), "Session failed to logon");

        // wait for the heartbeat
        fc.stop();
        assertTrue(ta.loggedOut, "Did not receive logout from server going down");
    }

    @Test(groups = { "unittest", "schedule" })
    public void testFixClientSchedule() throws InvalidConfigurationException {
        System.out.println("Starting 'testFixClientSchedule'");
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorScheduledSession());
        fc.start();

        waitForLogon(fc);

        assertTrue(fc.isLoggedOn(), "Session failed to logon");
        fc.stop();
        System.out.println("Finished 'testFixClientSchedule'");
    }

    @Test(groups = { "unittest", "schedule"})
    public void testFixClientWeeklySchedule() throws InvalidConfigurationException {
        System.out.println("Starting 'testFixClientWeeklySchedule'");
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorClientScheduledSession());
        
        DateTime cst = fc.getClientStartTime();
        DateTime cet = fc.getClientEndTime();
        assertTrue(cst.isBefore(cet), "Client end time before start time");
        assertTrue(cet.isAfter(cst), "Client start time after end time");
        
        assertEquals(cst.getDayOfWeek(), 7, "Not Sunday");
        assertEquals(cet.getDayOfWeek(), 5, "Not Friday");
        System.out.println("Finished 'testFixClientWeeklySchedule'");
    }

    @Test(groups = { "unittest", "schedule" })
    public void testFixClientScheduleWeekend() throws InvalidConfigurationException {
        final FixClient fc = new FixClient("Test Session");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorScheduledSessionWeekend());
        fc.start();

        waitForLogon(fc);

        assertFalse(fc.isLoggedOn(), "Logged on to a weekend only sesson");
        fc.stop();
    }

    @Test(groups = { "unittest", "unittest-fix" })
    public void testSequenceNumberReset() throws InvalidConfigurationException {
        System.out.println("Starting 'testSequenceNumberReset' " + new DateTime());
        final FixClient fc = new FixClient("Test Session");
        // specify the logger to use (Log4j);
        fc.setLoggerConfig("FixMessage");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);

        assertTrue(fc.isLoggedOn(), "Session failed to logon");

        fc.resetSequenceNumbers();
        // assertFalse(fs.isLoggedOn(), "Did not logout after sequence reset");
        // fs.stop();
        waitForLogoff(fc);
        //assertFalse(fc.isLoggedOn(), "Session failed to logoff");
        waitForLogon(fc);

        assertTrue(fc.isLoggedOn(), "Did not re-login after sequence reset");
        fc.stop();
        sleep(3000);
        System.out.println("Finished 'testSequenceNumberReset'");
    }

    @Test(groups = { "external" })
    public void testExternalConnection() throws InvalidConfigurationException {
        final FixClient fc = new FixClient("Test Session");
        // specify the logger to use (Log4j);
        fc.setLoggerConfig("FixMessage");
        TestApplication ta = new TestApplication();
        fc.setConfiguration(ta, createInitiatorSession());
        fc.start();

        waitForLogon(fc);
        sleep(6000);
        assertTrue(fc.isLoggedOn(), "Session failed to logon");

        sleep(6000);
        fc.stop();
    }

    private void waitForLogon(final TestApplication ta) {
        waitForValue(new IWaitFor<Boolean>() {
            @Override
            public Boolean waitFor() {
                return ta.loggedOn;
            }
        }, true, MAX_WAIT_TIME);
    }

    static class TestApplication extends FerFixClientApplication {

        private boolean loggedOn;
        private boolean loggedOut;

        public TestApplication() {
            super(new EmailSettings());
        }
        
        @Override
        public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound,
                IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        // For now, do nothing. Admin message coming in
        }

        @Override
        public void onMessage(quickfix.fix44.Logout message, SessionID sessionID) throws FieldNotFound,
                UnsupportedMessageType, IncorrectTagValue {
            String txt = message.get(new Text()).getValue();

            System.err.println(txt);
        }

        @Override
        public void onMessage(quickfix.fix42.Logout message, SessionID sessionID) throws FieldNotFound,
        UnsupportedMessageType, IncorrectTagValue {
            String txt = message.get(new Text()).getValue();

            System.err.println(txt);
        }
        
        @Override
        public void onCreate(SessionID sessionID) {
            Session mySession = Session.lookupSession(sessionID);
            stateListener = new TestStateListener(sessionID);
            mySession.addStateListener(stateListener);
        }

        @Override
        public void onLogon(SessionID sessionID) {
            // We don't want duplicate code. Delegate to the listener
            stateListener.onLogon(sessionID);
            loggedOn = true;
        }

        @Override
        public void onLogout(SessionID sessionID) {
            stateListener.onLogout(sessionID);
            loggedOut = true;
        }

        @Override
        public void toAdmin(Message msg, SessionID sessionID) {
        // For now, do nothing. Admin messages going out
        }

    }

    private static final class TestStateListener extends FixClientStateListener {
        private Map<DateTime, String> eventLog = new HashMap<DateTime, String>();

        private Log log = Session.lookupSession(getSessionID()).getLog();

        public TestStateListener(SessionID sessionID) {
            super(sessionID, new EmailSettings());
        }

        private void printEvents() {
            log.onEvent(eventLog.toString());
        }

        public void onLogon(SessionID sessionID) {
            super.onLogon(sessionID);
            eventLog.put(new DateTime(), Thread.currentThread().getName() + " - logon");
        }

        public void onLogout(SessionID sessionID) {
            super.onLogout(sessionID);
            eventLog.put(new DateTime(), Thread.currentThread().getName() + " - logout");
            printEvents();
        }
    }
}
