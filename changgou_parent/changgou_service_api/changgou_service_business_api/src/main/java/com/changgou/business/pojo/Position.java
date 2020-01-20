package com.changgou.business.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by zps on 2020/1/20 11:46
 */
@Data
@Table(name = "tb_position")
@NoArgsConstructor
@AllArgsConstructor
public class Position implements Serializable {

    @Id
    private Integer id;
    @Column(name = "name")
    private String name;
}
