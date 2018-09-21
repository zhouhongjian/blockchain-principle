package com.zhj.blockchainprinciple.principle.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * @author zhj
 * @date 2018/8/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    @URL
    String address;
}
