package com.turboparser.turbo.util;

import com.turboparser.turbo.model.CarType;
import org.springframework.stereotype.Component;

@Component
public class URLcreator {

    public String createUrl(CarType carType) {

        String make = carType.make;
        String model = carType.model;
        String price_from = carType.price_from;
        String price_to = carType.price_to;
        String year_from = carType.year_from;
        String year_to = carType.year_to;
        String engine_volume_from = carType.engine_volume_from;
        String engine_volume_to = carType.engine_volume_to;
        String mileage_from = carType.mileage_from;
        String mileage_to = carType.mileage_to;
        String fuel_type = carType.fuel_type;
        String transmission = carType.transmission;


        return "https://turbo.az/autos?utf8=%E2%9C%93&q[make][]=&q[make][]=" +
                make +
                "&q[region][]=&q[model][]=&q[model][]=" +
                model +
                "&q[fuel_type][]=&q[fuel_type][]=" +
                fuel_type +
                "&q[category][]=&q[gear][]=&q[color][]=&q[transmission][]=&q[transmission][]=" +
                "1" +
                "&q[transmission][]=2&q[transmission][]=3&q[transmission][]=" +
                transmission +
                "&q[mileage_from]=" +
                mileage_from +
                "&q[mileage_to]=" +
                mileage_to +
                "&q[year_from]=" +
                year_from +
                "&q[year_to]=" +
                year_to +
                "&q[price_from]=" +
                price_from +
                "&q[price_to]=" +
                price_to +
                "&q[currency]=azn&q[engine_volume_from]=" +
                engine_volume_from +
                "&q[engine_volume_to]=" +
                engine_volume_to +
                "&q[loan]=0&q[barter]=0&q[extras][]=&q[sort]=created_at&button=";
    }

}
