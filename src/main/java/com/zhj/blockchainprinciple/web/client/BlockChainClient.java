package com.zhj.blockchainprinciple.web.client;

import com.zhj.blockchainprinciple.principle.pojo.Block;
import com.zhj.blockchainprinciple.principle.pojo.Node;
import com.zhj.blockchainprinciple.principle.pojo.Transaction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

/**
 * @author zhj
 * @date 2018/8/18
 */
public interface BlockChainClient {

    @GET("chain/getFullChain")
    Call<List<Block>> getFullChain();

    @GET("chain/mine")
    Call<Block> mine();

    @POST("chain/newTransaction")
    Call<Transaction> newTransaction();

    @POST("chain/registerNodes")
    Call<List<Node>> registerNodes();

    @GET("chain/consensus")
    Call<List<Block>> consensus();

}
