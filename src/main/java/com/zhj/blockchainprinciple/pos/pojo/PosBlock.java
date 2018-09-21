package com.zhj.blockchainprinciple.pos.pojo;

import lombok.Data;

/**
 * @author zhj
 * @date 2018/9/20
 */
@Data
public class PosBlock {
    private long index;
    private long timestamp;
    private long Bpm;
    /**
     * 有必要么
     */
    private String hash;
    private String preHash;
    private String validator;

}
