package com.zhj.blockchainprinciple.web.server;

import com.google.common.collect.Lists;
import com.zhj.blockchainprinciple.principle.impl.BlockChain;
import com.zhj.blockchainprinciple.principle.pojo.Block;
import com.zhj.blockchainprinciple.principle.pojo.Node;
import com.zhj.blockchainprinciple.principle.pojo.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author zhj
 * @date 2018/8/18
 */
@Controller
@RequestMapping("chain")
public class BlockChainController {

    @Resource
    private BlockChain blockChain;

    /**
     * 挖矿（工作量证明）
     * 添加一笔挖矿奖励交易
     * 打包进区块
     */
    @RequestMapping("mine")
    @ResponseBody
    public synchronized Block mine() {
        return blockChain.mine();
    }

    @RequestMapping(value = "newTransaction", method = RequestMethod.POST)
    @ResponseBody
    public Transaction newTransaction(@RequestBody Transaction newTrans, HttpServletResponse response) throws IOException {
        //校验
        if (StringUtils.isEmpty(newTrans.getSender()) || StringUtils.isEmpty(newTrans.getRecipient()) ||
                newTrans.getAmount() <= 0) {
            response.sendError(400, "it is param error");
            return newTrans;
        }
        //添加
        blockChain.newTransaction(newTrans.getSender(), newTrans.getRecipient(), newTrans.getAmount());
        return newTrans;
    }

    @RequestMapping("getFullChain")
    @ResponseBody
    public List<Block> getFullChain() {
        return blockChain.getBlockChain();
    }

    @RequestMapping("hello")
    @ResponseBody
    public List<Block> hello(HttpServletResponse response) throws IOException {
        return blockChain.getBlockChain();
    }

    @RequestMapping(value = "registerNodes", method = RequestMethod.POST)
    @ResponseBody
    public List<Node> registerNodes(@RequestBody @Valid Node node, BindingResult result,
                                    HttpServletResponse response) throws IOException {
        //校验参数
        if (result.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : result.getAllErrors()) {
                builder.append(error.getDefaultMessage()).append(";");
            }
            response.sendError(400, builder.toString());
            return Lists.newArrayList(blockChain.getNodes());
        }
        blockChain.registerNode(node.getAddress());
        return Lists.newArrayList(blockChain.getNodes());
    }

    @RequestMapping("consensus")
    @ResponseBody
    public List<Block> consensus() throws IOException {
        blockChain.resolveConflicts();
        return blockChain.getBlockChain();
    }
}
