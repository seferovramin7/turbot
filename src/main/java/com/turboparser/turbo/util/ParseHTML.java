package com.turboparser.turbo.util;

import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParseHTML {

    @Autowired
    DBactions dBactions;

    NotificationDTO notificationDTO;

    @Value("${cars.from.minutes}")
    private int minutes;


    public SpecificVehicleSearchParameter parseSpecificCarHTML(String rawHTML) throws ParseException {
//        try {
            String carNameString = "";
            Document doc = Jsoup.parse(rawHTML);

            Elements carLot = doc.getElementsByClass("product-statistics");
            String carLotString = carLot.first().html();
            carLotString = carLotString.split(":")[3].trim().replaceAll("</p>", "");
            long carLotLong = Long.parseLong(carLotString);

            Elements carName = doc.getElementsByClass("product-name");
            carNameString += carName.first().html();
            carNameString = carNameString.replaceAll("<span class=\"nobr\">", "").replaceAll("</span>", "");

            String carPriceString = "";
            Elements carPrice = doc.getElementsByClass("product-price");
            carPriceString += carPrice.first().html();
            carPriceString = carPriceString.replaceAll("<span>", "").replaceAll("</span>", "");

            SpecificVehicleSearchParameter specificVehicleSearchParameter = SpecificVehicleSearchParameter.builder()
                    .lotId(carLotLong)
                    .generalInfo(carNameString)
                    .price(carPriceString)
                    .build();
            return specificVehicleSearchParameter;
//        } catch (NullPointerException e) {
//            return null;
//        }
    }


    public List<NotificationDTO> parseHtml(String rawHTML) throws ParseException {

        Document doc = Jsoup.parse(rawHTML);
        try {

            Elements amountHTML = doc.getElementsByClass("products-title-amount");
            String numberofCars = amountHTML.first().html().split("\\s")[0];


            numberofCars = (Integer.parseInt(numberofCars) > 15) ? "15" : numberofCars;

            List<NotificationDTO> notificationDTOList = new ArrayList<>();
            for (int i = 0; i < Integer.valueOf(numberofCars); i++) {
                Elements carLink = doc.getElementsByClass("products-i__link");
                String link = carLink.get(i).attr("href");

                Elements carPrice = doc.getElementsByClass("product-price");
                String carPriceString = carPrice.get(i).html().trim().replaceAll(" ", "").split("<")[0];
                String currencyString = carPrice.get(i).html().trim().replaceAll(" ", "").split(">")[1].split("<")[0];

                Elements carName = doc.getElementsByClass("products-i__name products-i__bottom-text");
                String carNameString = carName.get(i).html();

                Elements carInfo = doc.getElementsByClass("products-i__attributes products-i__bottom-text");
                String carInfoString = carInfo.get(i).html();

                Elements carDate = doc.getElementsByClass("products-i__datetime");
                String carDateString = carDate.get(i).html();

                String lotLink = "https://turbo.az/" + link;
                String carPriceTotal = carPriceString + currencyString;
                carPriceTotal = new StringBuilder(carPriceTotal).insert(2, ".").toString();

                LocalDateTime now = LocalDateTime.now();
                LocalTime publishTime = LocalTime.parse(carDateString.split(" ")[2]);
                String publishDay = carDateString.split(" ")[1];
                Duration duration = Duration.between(publishTime, now);

                if (publishDay.equals("bugün") && duration.toMinutes() <= minutes && duration.toMinutes() > 0) {
                    notificationDTO = NotificationDTO.builder()
                            .name(carNameString)
                            .info(carInfoString)
                            .price(carPriceTotal)
                            .link(lotLink)
                            .build();
                    notificationDTOList.add(notificationDTO);
                }
            }
            return notificationDTOList;
        } catch (NullPointerException e) {
            return null;
        }

    }

    public void parseMakeAndModel(String rawHTML) {
        Document doc = Jsoup.parse(rawHTML);
        Elements make = doc.getElementsByClass("select optional js-search-select-make");
        String allMake = make.first().html();

        Document document = Jsoup.parse(allMake);
        Elements options = document.select("option");

        for (Element element : options) {
            Elements models = doc.getElementsByClass("js-search-select-model");
            String allModels = models.first().html();
            String modelValue = element.attr("value");
            String markaName = element.html();

            if (!modelValue.equals("")) {
                MakeEntity makeEntity = MakeEntity.builder().make(markaName).makeId(Integer.parseInt(modelValue)).build();
                dBactions.updateMakeTable(makeEntity);
            }
            Document modelDoc = Jsoup.parse(allModels);
            Elements modelOptions = modelDoc.select("option");
            for (Element modelElement : modelOptions) {
                if (modelElement.attr("class").equals(modelValue)) {
                    String model = modelElement.html();
                    Integer modelInt = 0;
                    if (!modelValue.equals("")) {
                        try {
                            modelInt = Integer.valueOf(modelElement.attr("value"));
                        } catch (NumberFormatException e) {
                        }
                        ModelEntity modelEntity = ModelEntity.builder().model(model).makeId(Integer.parseInt(modelValue)).modelId(Integer.valueOf(modelInt)).build();
                        dBactions.updateModelTable(modelEntity);
                    }
                }
            }
        }
    }
}
