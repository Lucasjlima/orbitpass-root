package fiap.com.br.orbitpasscore.config;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PAYMENT_EXCHANGE = "orbitpass.payment.exchange";
    public static final String PAYMENT_DLX = "orbitpass.payment.dlx";

    public static final String REQUEST_QUEUE = "payment.request.queue";
    public static final String RESPONSE_QUEUE = "payment.response.queue";
    public static final String FAILED_QUEUE = "payment.failed.queue";

    public static final String REQUEST_ROUTING_KEY = "payment.request";
    public static final String RESPONSE_ROUTING_KEY = "payment.response";
    public static final String FAILED_ROUTING_KEY = "payment.failed";

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange paymentDeadLetterExchange() {
        return new TopicExchange(PAYMENT_DLX, true, false);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(REQUEST_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", PAYMENT_DLX,
                        "x-dead-letter-routing-key", FAILED_ROUTING_KEY))
                .build();
    }

    @Bean
    public Queue paymentResponseQueue() {
        return QueueBuilder.durable(RESPONSE_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", PAYMENT_DLX,
                        "x-dead-letter-routing-key", FAILED_ROUTING_KEY))
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(FAILED_QUEUE).build();
    }

    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder.bind(paymentRequestQueue())
                .to(paymentExchange())
                .with(REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentResponseBinding() {
        return BindingBuilder.bind(paymentResponseQueue())
                .to(paymentExchange())
                .with(RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(paymentFailedQueue())
                .to(paymentDeadLetterExchange())
                .with(FAILED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jacksonConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jacksonConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonConverter);
        return factory;
    }
}
