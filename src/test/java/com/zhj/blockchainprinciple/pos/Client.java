package com.zhj.blockchainprinciple.pos;

import com.zhj.blockchainprinciple.principle.utils.NamedThreadFactory;
import lombok.Cleanup;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhj
 * @date 2018/9/21
 */
public class Client {
    public static void main(String[] args) {

        int clientSize = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(clientSize, new NamedThreadFactory("pos 客户端"));

        for (int i = 0; i < clientSize; i++) {
            executorService.submit(() -> {
                try {
                    client();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println("main over");
        executorService.shutdown();

    }

    public static void client() throws IOException {
        //@Cleanup会吞异常，只在简单的client使用，server可能不方便debug,同时声明位置也不方便使用
        Socket socket = new Socket("127.0.0.1", 8000);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Random rand = new Random(System.currentTimeMillis());


        System.out.println(Thread.currentThread().getName() + ":" + br.readLine());
        bw.write(String.valueOf(rand.nextInt(100000)));
        bw.newLine();
        bw.flush();
        System.out.println(Thread.currentThread().getName() + ":" + br.readLine());
        bw.write(String.valueOf(rand.nextInt(100000)));
        bw.newLine();
        bw.flush();

        System.out.println(Thread.currentThread().getName() + ":收");
        System.out.println(Thread.currentThread().getName() + br.readLine());
        System.out.println(Thread.currentThread().getName() + br.readLine());

        br.close();
        bw.close();
        socket.close();

    }

}
