package com.turboparser.turbo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="MakeEntity", schema = "TURBODB")
public class MakeEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String make;

    private int makeId;

    @Override
    public String toString() {
        return "MakeEntity{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", makeId=" + makeId +
                '}';
    }
}
