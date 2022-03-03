package com.turboparser.turbo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarType {
    public String make;
    public String model;
    public String price_from;
    public String price_to;
    public String year_from;
    public String year_to;
    public String engine_volume_from;
    public String engine_volume_to;
    public String mileage_from;
    public String mileage_to;
    public String fuel_type;
    public String transmission;
}
