package com.zhj.blockchainprinciple.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhj.blockchainprinciple.BaseLocalTest;
import com.zhj.blockchainprinciple.principle.impl.BlockChain;
import com.zhj.blockchainprinciple.principle.pojo.Block;
import com.zhj.blockchainprinciple.principle.pojo.Transaction;
import com.zhj.blockchainprinciple.principle.utils.BChainUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhj
 * @date 2018/8/18
 */
public class BlockChainTest extends BaseLocalTest {

    @Resource
    private BlockChain blockChain;


    @Test
    public void testValidProof() {
        int res = blockChain.proofOfWork(999);
        System.out.println(res);
    }

    @Test
    public void testBlock() {
        blockChain.mine();
        blockChain.mine();
        blockChain.mine();
        blockChain.mine();
        blockChain.mine();
        System.out.println(JSON.toJSON(blockChain.getBlockChain()));
        System.out.println(blockChain.validChain(blockChain.getBlockChain()));
    }



    @Test
    public void test2(){
        Transaction transaction = new Transaction("0","xxxxx",50.0);
        List<Transaction> list = Lists.newLinkedList();
        list.add(transaction);
        Block block = new Block(1L,1537277198796L, list,"1000","this is first block");

        System.out.println(BChainUtils.hashBlock(block));
    }
}
