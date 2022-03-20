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
@Table(name = "SpecificVehicleSearchParameter", schema = "public")
public class SpecificVehicleSearchParameter {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(name = "GENERAL_INFO")
    private String generalInfo;
    @Column(name = "LOT_ID")
    private Long lotId;
    @Column(name = "PRICE")
    private String price;

    @ManyToOne
    @JoinColumn(name = "CHAT_ID")
    private Chat chat;

    @Override
    public String toString() {
        return
                generalInfo + '\n' +
                        lotId + '\n' +
                        price;
    }
}
