package com.shipmonk.testingday.core.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.Map;

@lombok.EqualsAndHashCode
@lombok.ToString
@lombok.AllArgsConstructor(access = AccessLevel.PUBLIC)
@lombok.Getter(AccessLevel.PUBLIC)
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate
@lombok.Setter(AccessLevel.PROTECTED) // hibernate
@Entity
public class Rates {
    @Id
    private LocalDate date;

    private long collectedTimestamp;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Double> rates;

}
