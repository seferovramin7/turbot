package com.turboparser.turbo.service;


import com.turboparser.turbo.configuration.HttpClientConfig;
import com.turboparser.turbo.configuration.RestTemplateConfig;
import com.turboparser.turbo.util.ParseHTML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;

@Service
@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class RestService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ParseHTML parseHTML;

    public String generalRestService(String url) throws IOException, ParseException {
        String result = restTemplate.getForObject(url, String.class);
        String s = parseHTML.parseHtml(result);
        return s;
    }

    public String makeAndModelRestService(String url) throws IOException {
        String result = restTemplate.getForObject(url, String.class);
        parseHTML.parseMakeAndModel(result);
        return result;
    }

}
