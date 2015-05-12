package activemq.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import activemq.IJmsObserver;
import activemq.JmsException;

public abstract class AbstractTopicMonitor<O extends IJmsObserver<M>, M extends Message> extends
        AbstractJmsMonitor<O, M> {

    protected AbstractTopicMonitor() {
        super();
    }
    
    public AbstractTopicMonitor(String brokerUrl) {
        super(brokerUrl);
    }

    protected Topic createTempTopic() {
        try {
            return connectionTask.getSession().createTemporaryTopic();
        } catch (JMSException e) {
            getLogger().error("Cannot create temporary topic", e);
            throw new JmsException("Cannot create temporary topic", e);
        }
    }

    protected Topic createTopic(String topicName) {
        try {
            return connectionTask.getSession().createTopic(topicName);
        } catch (JMSException e) {
            getLogger().error("Cannot create temporary topic", e);
            throw new JmsException("Cannot create temporary topic", e);
        }
    }

    public Topic publish(String topicName, String message) throws JMSException {
        Session localSession = connectionTask.getSession(); 
        Topic topic = localSession.createTopic(topicName);
        MessageProducer mp = localSession.createProducer(topic);
        TextMessage textMsg = localSession.createTextMessage(message);
        mp.send(textMsg);
        mp.close();
        return topic;
    }

    public Topic publishWithNewSession(String topicName, String message) throws JMSException {
        Session newSession = connectionTask.getNewSession();
        Topic topic = newSession.createTopic(topicName);
        MessageProducer mp = newSession.createProducer(topic);
        TextMessage textMsg = newSession.createTextMessage(message);
        mp.send(textMsg);
        mp.close();
        newSession.close();
        return topic;
    }

    public Topic publishWithNewConnection(String brokerUrl, String topicName, String message) throws JMSException {
        ConnectionTask connectionTask = new ConnectionTask(brokerUrl);
        connectionTask.connect();
        
        Session newSession = connectionTask.getNewSession();
        Topic topic = newSession.createTopic(topicName);
        MessageProducer mp = newSession.createProducer(topic);
        TextMessage textMsg = newSession.createTextMessage(message);
        mp.send(textMsg);
        mp.close();
        newSession.close();
        connectionTask.stop();
        return topic;
    }

}