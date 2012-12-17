package org.slc.sli.search.util;

import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;

public class StompMessageConverter extends SimpleMessageConverter {
    
    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message instanceof TextMessage) {
            return extractStringFromMessage((TextMessage) message);
        }
        else if (message instanceof BytesMessage) {
            return extractStringFromByteArrayMessage((BytesMessage) message);
        }
        else if (message instanceof MapMessage) {
            return extractMapFromMessage((MapMessage) message);
        }
        else if (message instanceof ObjectMessage) {
            return extractSerializableFromMessage((ObjectMessage) message);
        }
        else {
            return message;
        }
    }
    
    protected String extractStringFromByteArrayMessage(BytesMessage message) throws JMSException {
        try {
            return new String(super.extractByteArrayFromMessage(message), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException("Unable to convert bytes message to ");
        }
    }
    
}
