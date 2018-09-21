package com.zhj.blockchainprinciple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zhj
 * @date 2018/6/30
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合
@SpringBootTest(classes = BlockchainPrincipleApplication.class) // 加载配置
public class BaseLocalTest {
    protected void printResult(Object o) {
        System.out.println("-------------------------");
        System.out.println(JSON.toJSONString(o, SerializerFeature.PrettyFormat));
        System.out.println("-------------------------");
        System.out.println("");
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }
}
