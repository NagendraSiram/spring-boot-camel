package com.nagendra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagendra.domain.Event;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jacksonxml.ListJacksonXMLDataFormat;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.LoggingLevel.DEBUG;
import static org.apache.camel.LoggingLevel.ERROR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nagendra on 17/04/2018.
 */
@Component
public class InputRoute extends RouteBuilder {


    @Autowired
    private  ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(InputRoute.class);

    public static final String ROUTE_ID = "INPUT_ROUTE";

    private String username;
    private String password;

    @Override
    public void configure() throws Exception {

//        ListJacksonXMLDataFormat format = new ListJacksonXMLDataFormat();
//        format.useList();

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat(objectMapper, Event.class);


        from("{{application.input.endpoint}}")
                .routeId(ROUTE_ID)
                .noMessageHistory()
                .autoStartup(true)
                .log(DEBUG, logger, "Message headers - [${header}]")
                .log(DEBUG, logger, "Message headers - ${body}")
                .unmarshal(jacksonDataFormat)
                .setHeader("Authorization", simple("Basic " + Base64.encodeBase64String((username + ":" + password).getBytes())))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .doTry()
                .to("{{application.output.endpoint}}")
                .doCatch(HttpOperationFailedException.class)
                .process(exchange -> {
                    HttpOperationFailedException exception = (HttpOperationFailedException) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    logger.error("Http call failed - response body is " + exception.getResponseBody());
                    logger.error("Http call failed - response headers are " + exception.getResponseHeaders());
                    throw exception;
                })
                .end();

    }

}
