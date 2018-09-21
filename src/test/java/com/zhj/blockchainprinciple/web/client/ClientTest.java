package com.zhj.blockchainprinciple.web.client;

import com.alibaba.fastjson.JSON;
import com.zhj.blockchainprinciple.BaseLocalTest;
import com.zhj.blockchainprinciple.principle.pojo.Block;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author zhj
 * @date 2018/8/18
 */
public class ClientTest extends BaseLocalTest {
    @Test
    public void test1() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient())
                .build();
        BlockChainClient chainClient = retrofit.create(BlockChainClient.class);


        Response<List<Block>> response = chainClient.getFullChain().execute();
        System.out.println(response.toString());
        System.out.println(JSON.toJSONString(response));
        System.out.println(JSON.toJSONString(response.body()));
        System.out.println(JSON.toJSONString(response.body().toString()));
    }
}
