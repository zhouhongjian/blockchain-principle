package com.zhj.blockchainprinciple.pos.pojo;

/**
 * @author zhj
 * @date 2018/9/20
 */
public class POSNode {

//    Allow it to enter a token balance (remember for this tutorial, we won’t perform any balance checks since there is no wallet logic)
//    节点的持有的余额

//    Receive a broadcast of the latest blockchain
//    接受最新区块链的广播

//    Receive a broadcast of which validator in the network won the latest block
//    接受证明中获胜节点是谁的广播

//    Add itself to the overall list of validators
//    把自身加入到精选节点列表中

//    Enter block data BPM — remember, this is each validator’s pulse rate
//    制造区块模拟数据

//    Propose a new block
//    创建一个新的区块

    public void test(){

        //1、接受各个节点发送过来的股权证明信息(包含节点地址和持有股权数)
        //2、将接受到的各个节点的股权证明信息放入竞争出块节点列表中
        //  1、2考虑用一个线程池来执行

    }
}
