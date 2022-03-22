package com.turboparser.turbo.controller;

import com.turboparser.turbo.util.DBactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AliveController {

    @Autowired
    DBactions dBactions;

    @Autowired
    RestTemplate restTemplate;

    @Value("${heroku.app.url}")
    private String url;

    @GetMapping(value = "/alive")
    public String getAlive() {
        return "alive";
    }

    @Scheduled(fixedRateString = "${heroku.keep.alive.rate}")
    private void restTest() {
        String result = restTemplate.getForObject(url, String.class);
        if (!result.equals("alive")){
            String telegramResult = "https://api.telegram.org/bot5026223660:AAEq6dd8V2AwOKTvYhbTkhilHJE59ZaZ8cc/sendMessage?chat_id=508914176&text=";
            telegramResult += result;
            restTemplate.getForObject(telegramResult, String.class);
        }
        System.out.println(result);
    }

}
