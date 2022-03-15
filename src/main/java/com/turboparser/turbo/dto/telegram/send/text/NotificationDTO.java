package com.turboparser.turbo.dto.telegram.send.text;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotificationDTO {


    public String name;
    public String info;
    public String price;
    public String link;

    public NotificationDTO() {
    }

    public NotificationDTO(String name, String info, String price, String link) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.link = link;
    }

    @Override
    public String toString() {
        return name + "\n" +
                info + "\n" +
                price + "\n" +
                link;
    }
}
