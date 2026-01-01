package test;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {

    private final String name;
    private final String topic1;
    private final String topic2;
    private final String outputTopic;
    private final BinaryOperator<Double> op;

    private Double val1 = null;
    private Double val2 = null;

    public BinOpAgent(String name,
                      String topicName1,
                      String topicName2,
                      String outputTopicName,
                      BinaryOperator<Double> op) {

        this.name = name;
        this.topic1 = topicName1;
        this.topic2 = topicName2;
        this.outputTopic = outputTopicName;
        this.op = op;

        Topic t1 = TopicManagerSingleton.get().getTopic(topic1);
        Topic t2 = TopicManagerSingleton.get().getTopic(topic2);
        Topic out = TopicManagerSingleton.get().getTopic(outputTopic);

        t1.subscribe(this);
        t2.subscribe(this);

        out.addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        val1 = 0.0;
        val2 = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (Double.isNaN(msg.asDouble))
            return;

        double value = (Double) msg.asDouble;

        if (topic.equals(topic1)) {
            val1 = value;
        } else if (topic.equals(topic2)) {
            val2 = value;
        }

        if (val1 != null && val2 != null) {
            double result = op.apply(val1, val2);
            TopicManagerSingleton
                    .get()
                    .getTopic(outputTopic)
                    .publish(new Message(result));
        }
    }

    @Override
    public void close() {
        // nothing required
    }
}
