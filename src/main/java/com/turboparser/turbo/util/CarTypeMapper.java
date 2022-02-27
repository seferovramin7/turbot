package com.turboparser.turbo.util;

import com.turboparser.turbo.model.CarType;
import org.springframework.stereotype.Component;

@Component
public class CarTypeMapper {

    public CarType buildCar(String make, String model,
                            String price_from, String price_to,
                            String year_from, String year_to,
                            String engine_volume_from, String engine_volume_to,
                            String mileage_from, String mileage_to,
                            String fuel_type, String transmission ) {
        CarType car = CarType.builder()
                .make(make)
                .model(model)
                .price_from(price_from)
                .price_to(price_to)
                .year_from(year_from)
                .year_to(year_to)
                .engine_volume_from(engine_volume_from)
                .engine_volume_to(engine_volume_to)
                .mileage_from(mileage_from)
                .mileage_to(mileage_to)
                .fuel_type(fuel_type)
                .transmission(transmission)
                .build();
        return car;
    }
}
