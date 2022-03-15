package com.turboparser.turbo.service;


import com.turboparser.turbo.configuration.HttpClientConfig;
import com.turboparser.turbo.configuration.RestTemplateConfig;
import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.entity.SpecificVehicle;
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

    public SpecificVehicle specificRestService(String url, Long lotId) throws IOException, ParseException {
        String result = restTemplate.getForObject(url, String.class);
        try {
            SpecificVehicle notificationDTOList = parseHTML.parseSpecificCarHTML(result, lotId);
            return notificationDTOList;
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
