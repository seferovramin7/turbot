package com.turboparser.turbo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParseHTML {

    @Autowired
    DBactions dBactions;

    public String parseHtml(String rawHTML) {

        Document doc = Jsoup.parse(rawHTML);
        Elements amountHTML = doc.getElementsByClass("products-title-amount");
        String numberofCars = amountHTML.first().html().split("\\s")[0];

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

            System.out.println(carNameString);
            System.out.println(carInfoString);
            System.out.println(carDateString);
            System.out.println(carPriceTotal);
            System.out.println(lotLink);


            dBactions.insertOrIgnoreDB(lotLink, carPriceTotal);
        }
        return numberofCars;
    }

    public void parseMakeAndModel(String rawHTML) {
        Document doc = Jsoup.parse(rawHTML);
        Elements make = doc.getElementsByClass("input select optional q_make");
        String allMake = make.first().html();

        Document document = Jsoup.parse(allMake);
        Elements options = document.select("select > option");

        for (Element element : options) {
            String modelValue = element.attr("value");
            Elements models = doc.getElementsByClass("input string optional q_model");
            String allModels = models.first().html();

            Document modelDoc = Jsoup.parse(allModels);
            Elements modelOptions = modelDoc.select("select > option");
            List<String> modelList = new ArrayList<>();
            for (Element modelElement : modelOptions) {
                if (modelElement.attr("class").equals(modelValue)) {
                    String model = modelElement.html();
                    modelList.add(model);
                }
            }
            System.out.println(modelList);
        }
    }
}
