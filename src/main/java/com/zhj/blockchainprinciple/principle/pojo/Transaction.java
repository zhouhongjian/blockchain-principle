package com.zhj.blockchainprinciple.principle.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhj
 * @date 2018/8/17
 * <p>
 * 交易pojo对象
 * // TODO: 2018/8/18  实际的一笔交易中会涉及到多个支付、接收对象。暂时简化处理。
 * // TODO: 2018/8/18  交易验证涉及私钥的签名
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @NotEmpty
    @NotNull
    private String sender;

    @NotEmpty
    @NotNull
    private String recipient;

    private Double amount;

}
