package com.turboparser.turbo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SpecificVehicleSearchParameter")
public class SpecificVehicleSearchParameter {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(name = "GENERAL_INFO")
    private String generalInfo;
    @Column(name = "OWNER_NAME")
    private String ownerName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "LOT_ID")
    private String lotId;
    @Column(name = "PRICE")
    private String price;
    @ManyToOne
    private com.turboparser.turbo.entity.Chat chat;

    @Override
    public String toString() {
        return
                generalInfo + '\n' +
                        ownerName + '\n' +
                        phone + '\n' +
                        lotId +
                        price;
    }
}
