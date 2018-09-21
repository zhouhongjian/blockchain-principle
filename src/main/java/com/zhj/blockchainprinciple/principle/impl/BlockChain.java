package com.zhj.blockchainprinciple.principle.impl;

import com.zhj.blockchainprinciple.principle.pojo.Block;
import com.zhj.blockchainprinciple.principle.pojo.Node;
import com.zhj.blockchainprinciple.principle.pojo.Transaction;
import com.zhj.blockchainprinciple.principle.utils.BChainUtils;
import com.zhj.blockchainprinciple.web.client.BlockChainClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author zhj
 * @date 2018/8/17
 */
@Service
@Getter
@Setter
public class BlockChain {
    private List<Block> blockChain = new LinkedList<>();
    private List<Transaction> currentTransactions = new LinkedList<>();
    private Set<Node> nodes = new HashSet<>();

    /**
     * 生成新的交易
     */
    public long newTransaction(String sender, String recipient, Double amount) {
        Transaction newTrans = new Transaction(sender, recipient, amount);
        currentTransactions.add(newTrans);
        return blockChain.size() + 1;
    }

    /**
     * 生成新的区块
     */
    public Block newBlock(String proof) {
        String preHash = BChainUtils.hashBlock(getLastBlock());
        Block newBlock = new Block(blockChain.size() + 1, System.currentTimeMillis(),
                currentTransactions, proof, preHash);
        currentTransactions = new LinkedList<>();
        blockChain.add(newBlock);
        return newBlock;
    }

    /**
     * 生成创世区块
     */
    public Block newFirstBlock(String proof, String preHash) {
        Block firstBlock = new Block(blockChain.size() + 1, System.currentTimeMillis(),
                currentTransactions, proof, preHash);
        currentTransactions = new LinkedList<>();
        blockChain.add(firstBlock);
        return firstBlock;
    }

    public Block getLastBlock() {
        if (CollectionUtils.isEmpty(blockChain)) {
            return null;
        } else {
            return blockChain.get(blockChain.size() - 1);
        }
    }

    public int proofOfWork(int lastProof) {
        int proof = 0;
        while (!validProof(lastProof, proof)) {
            proof++;
        }
        return proof;
    }


    /**
     * // TODO: 2018/8/18 添加一个复杂度参数，用来设置工作量证明的难度 (摘要的前多少位为0)
     */
    private boolean validProof(int lastProof, int proof) {
        String seed = lastProof + "" + proof;
        String digest = BChainUtils.getSHA256StrJava(seed);
        System.out.println(digest);
        return "0000".equals(digest.substring(0, 4));
    }

    public void registerNode(String addr) {
        nodes.add(new Node(addr));
    }


    /**
     * 获取其它节点的区块链进行对比，
     * 其它节点区块链长，则对长的区块链进行校验，然后代替原有区块链
     */
    public void resolveConflicts() throws IOException {
        List<Block> maxLengthChain = getBlockChain();

        for (Node node : getNodes()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(node.getAddress())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BlockChainClient chainClient = retrofit.create(BlockChainClient.class);
            Response<List<Block>> response = chainClient.getFullChain().execute();
            List<Block> otherChain = response.body();
            if (!CollectionUtils.isEmpty(otherChain) &&
                    otherChain.size() > maxLengthChain.size() && validChain(otherChain)) {
                maxLengthChain = otherChain;
            }
        }
        setBlockChain(maxLengthChain);
    }

    /**
     * 验证条件：
     * 1、前区块的hash值和后区块的preHash一致
     * 2、前区块的proof和后区块的proof作为工作量证明入参，能直接得到指定复杂度的hash值(有待调整)
     */
    public boolean validChain(List<Block> blockChains) {
        if (CollectionUtils.isEmpty(blockChains)) {
            return true;
        }
        Iterator<Block> blockIterator = blockChains.iterator();
        Block lastBlock=null;
        if (blockIterator.hasNext()){
            lastBlock = blockIterator.next();
        }
        while (blockIterator.hasNext()) {
            Block curBlock = blockIterator.next();
            if (!curBlock.getPreHash().equals(BChainUtils.hashBlock(lastBlock))) {
                return false;
            }
            if (!validProof(Integer.parseInt(lastBlock.getProof()),Integer.parseInt(curBlock.getProof()))) {
                return false;
            }
            lastBlock = curBlock;
        }
        return true;
    }

    public  Block mine() {

        //交易发起者为0表示挖出来的币，接受者为矿工的地址，数量暂定为50 // TODO: 2018/8/18 数量递减
        Transaction reward = new Transaction("0", "xxxxx", 50.0);
        getCurrentTransactions().add(reward);

        if (CollectionUtils.isEmpty(getBlockChain())) {
            return newFirstBlock("1000", "this is first block");
        } else {
            Block last = getLastBlock();
            int proof = proofOfWork(Integer.parseInt(last.getProof()));
            return newBlock(Integer.toString(proof));
        }
    }

}
