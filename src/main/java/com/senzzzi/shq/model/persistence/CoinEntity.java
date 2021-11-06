package com.senzzzi.shq.model.persistence;

import com.senzzzi.shq.model.CoinValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "coins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "coin_value")
    @Enumerated(EnumType.STRING)
    private CoinValue value;

    @Column(name = "count")
    private Integer count;

}
