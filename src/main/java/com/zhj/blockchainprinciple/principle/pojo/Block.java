package com.zhj.blockchainprinciple.principle.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhj
 * @date 2018/8/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    private long index;

    private long timestamp;

    private List<Transaction> transactions;

    private String proof;

    private String preHash;
}
