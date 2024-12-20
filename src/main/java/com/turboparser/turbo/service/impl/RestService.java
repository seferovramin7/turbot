package com.turboparser.turbo.service.impl;


import com.turboparser.turbo.configuration.HttpClientConfig;
import com.turboparser.turbo.configuration.RestTemplateConfig;
import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import com.turboparser.turbo.util.ParseHTML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class RestService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ParseHTML parseHTML;

    public List<NotificationDTO> generalRestService(String url) throws IOException, ParseException {
        String result = restTemplate.getForObject(url, String.class);
        try {
            List<NotificationDTO> notificationDTOList = parseHTML.parseHtml(result);
            return notificationDTOList;
        } catch (ParseException e) {
            return null;
        }
    }

    public SpecificVehicleSearchParameter specificRestService(String link) throws IOException, ParseException {
        String result = restTemplate.getForObject(link, String.class);
        try {
            SpecificVehicleSearchParameter specificVehicleSearchParameter = parseHTML.parseSpecificCarHTML(result);
            return specificVehicleSearchParameter;
        } catch (ParseException e) {
            return null;
        }
    }

    public String makeAndModelRestService(String url) throws IOException {
        String result = restTemplate.getForObject(url, String.class);
        parseHTML.parseMakeAndModel(result);
        return result;
    }

}
