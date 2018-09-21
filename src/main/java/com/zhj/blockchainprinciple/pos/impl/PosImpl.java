package com.zhj.blockchainprinciple.pos.impl;

import com.alibaba.fastjson.JSON;
import com.zhj.blockchainprinciple.pos.pojo.PosBlock;
import com.zhj.blockchainprinciple.principle.utils.BChainUtils;
import com.zhj.blockchainprinciple.principle.utils.NamedThreadFactory;
import lombok.Cleanup;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhj
 * @date 2018/9/20
 */
public class PosImpl {

    private List<PosBlock> blockChain = new LinkedList<>();


    private List<PosBlock> tempBlock = new LinkedList<>();
    private ReentrantLock tempBlockLock = new ReentrantLock();
    /**
     * 队列，用来存储验证节点生成的block  candidateBlocks
     */
    private LinkedBlockingQueue<PosBlock> candidateBlocks = new LinkedBlockingQueue<>();
    /**
     * 队列，用来实现通知所有节点，那个validator进行出块 announcements
     */
    private LinkedBlockingQueue<String> announcements = new LinkedBlockingQueue<>();

    private Map<String, Long> validators = new HashMap<>();

    /**
     * @param bpm 可以认为是地址为address的节点准备放入区块的交易内容
     */
    public PosBlock generateBlock(PosBlock oldBlock, int bpm, String address) {
        PosBlock newBlock = new PosBlock();
        newBlock.setIndex(oldBlock.getIndex() + 1);
        newBlock.setTimestamp(System.currentTimeMillis());
        newBlock.setBpm(bpm);
        newBlock.setValidator(address);
        newBlock.setPreHash(oldBlock.getHash());
        newBlock.setHash(BChainUtils.hashPosBlock(newBlock));
        return newBlock;
    }

    public boolean validBlock(PosBlock oldBlock, PosBlock newBlock) {

        if ((newBlock.getIndex() - oldBlock.getIndex()) != 1) {
            return false;
        }

        if (!oldBlock.getHash().equals(newBlock.getPreHash())) {
            return false;
        }
        if (!BChainUtils.hashPosBlock(newBlock).equals(newBlock.getHash())) {
            return false;
        }
        return true;
    }


    public void handleConn(Socket socket, String threadId) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("handleConn 启动一个线程" + threadId + "，用来告知连接节点 谁是最终的获胜节点");

//        executorService.submit(() -> {
//            Thread.currentThread().setName("获胜通知线程" + threadId);
//
//            try {
//                while (true) {
//                    Thread.sleep(3000);
//                    String win = announcements.poll(100, TimeUnit.MILLISECONDS);
//                    if (!StringUtils.isEmpty(win)) {
//                        bw.write("获胜的节点是:" + JSON.toJSONString(win));
//                        bw.newLine();
//                        bw.flush();
//                    }
//                }
//            } catch (IOException | InterruptedException e) {
//                if (e.getMessage().contains("Socket closed")){
//                    System.out.println("客户端已经关闭");
//                }else {
//                    e.printStackTrace();
//                }
//            }finally {
//                try {
//                    br.close();
//                    bw.close();
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        System.out.println("等待连接节点输入balance，然后以当前时间的hash作为key，balance作为values，放入validators中");
        bw.write("请输入balance：");
        bw.newLine();
        bw.flush();

        String balanceStr = br.readLine();
        long balance = Integer.parseInt(balanceStr);
        String address = socket.toString();
        System.out.println("address:" + address);
        validators.put(address, balance);

        System.out.println("等待链接节点输入bpm，然后生成新的block，放入candidateBlocks作为备选block");
        bw.write("请输入BPM");
        bw.newLine();
        bw.flush();
        String bpmStr = br.readLine();
        int bpm = Integer.parseInt(bpmStr);

        PosBlock newBlock = generateBlock(getLast(), bpm, address);
        if (validBlock(getLast(), newBlock)) {
            candidateBlocks.offer(newBlock);
        }
// 实验需要，可以不需要单独启动线程来实时通知，
// 可以直接在添加节点后就立马执行一个通知所有节点逻辑更合适，或者提供一个接口让其他节点来调用。
// 只有在比较数量比较大的情况下，立马执行通知逻辑是延迟比较大，会不合理。这个时候也可以考虑使用专业的队列来处理
        System.out.println("启动一个线程，用来告知连接节点 宣布最新block chain的状态");
        executorService.submit(() -> {
            Thread.currentThread().setName("最新区块链通知线程" + threadId);

            try {
                while (true) {
                    Thread.sleep(1200);
                    String win = announcements.poll(100, TimeUnit.MILLISECONDS);
                    if (!StringUtils.isEmpty(win)) {
                        bw.write("获胜的节点是:" + JSON.toJSONString(win));
                        bw.newLine();
                    }
                    bw.write("最新的区块链状态：" + JSON.toJSONString(blockChain));
                    bw.newLine();
                    // TODO: 2018/9/22 连接量小的时候加上bw.flush容易看到结果。量大的时候会造成写错误
                    bw.flush();
                }
            } catch (IOException | InterruptedException e) {
                if (e.getMessage().contains("socket write error") || e.getMessage().contains("close")) {
                    System.out.println("客户端:" + socket + "主动关闭");
//                    e.printStackTrace();
                } else {
                    e.printStackTrace();
                }
            } finally {
                try {
                    br.close();
                    bw.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void pickWinner() throws InterruptedException {
        Thread.sleep(1000);
        List<PosBlock> temp = tempBlock;

        List<String> lotteryPool = new ArrayList<>();
        try {


            tempBlockLock.lock();
            if (temp.size() > 0) {
                System.out.println("竞争人数为:" + temp.size());
                OUT:
                for (PosBlock block : temp) {
                    for (String node : lotteryPool) {
                        if (node.equals(block.getValidator())) {
                            continue OUT;
                        }
                    }
                    Map<String, Long> setValidators = validators;
                    Long balance = setValidators.get(block.getValidator());
                    for (int i = 0; i < balance; i++) {
                        lotteryPool.add(block.getValidator());
                    }
                }
            }
            if (CollectionUtils.isEmpty(lotteryPool)) {
                System.out.println("抽奖池中没有数据");
                return;
            }
            Random rand = new Random(System.currentTimeMillis());
            String lotteryWin = lotteryPool.get(rand.nextInt(lotteryPool.size()));
            System.out.println("抽奖池中奖client是：" + lotteryWin);

            for (PosBlock block : temp) {
                if (block.getValidator().equals(lotteryWin)) {
                    blockChain.add(block);

                    for (int i = 0; i < validators.size(); i++) {
                        announcements.offer(lotteryWin);
                    }
                }
            }

            tempBlock = new LinkedList<>();
        } finally {
            tempBlockLock.unlock();
        }
    }


    private PosBlock getLast() {
        return blockChain.get(blockChain.size() - 1);

    }


    private void main() throws IOException {
        //初始化区块链,创建创世区块链
        PosBlock init = new PosBlock();
        init.setIndex(1);
        init.setTimestamp(System.currentTimeMillis());
        init.setPreHash("");
        init.setValidator("");
        init.setBpm(0);
        init.setHash(BChainUtils.hashPosBlock(init));
        blockChain.add(init);
        System.out.println("区块链初始化完成");


        System.out.println("启动一个线程，用来从阻塞队列candidateBlocks 中获取block，放置到temp block中");
        pollBlockEx.submit(() -> {
            while (true) {
                try {
                    tempBlockLock.lock();
                    PosBlock block = candidateBlocks.poll(100, TimeUnit.MILLISECONDS);
                    if (block != null) {
                        tempBlock.add(block);
                    }
                } catch (InterruptedException e) {
                    System.out.println("暂时没有新的block加入到temp");
                    e.printStackTrace();
                } finally {
                    tempBlockLock.unlock();
                }
            }
        });


        System.out.println("//启动一个线程，用来选取出块节点");
        winEx.submit(() -> {
            try {
                while (true) {
                    pickWinner();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        ServerSocket serverSocket = new ServerSocket(8000);
        //处理连接后的事件
        System.out.println("开始：处理连接后的事件");
        while (true) {
            Socket socket = serverSocket.accept();
            String threadId = BChainUtils.getSHA256StrJava(socket.toString()).substring(4);
            executorService.submit(() -> {
                try {
                    System.out.println("收到一个连接:" + threadId + "，开始handleConn");
                    handleConn(socket, threadId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private ExecutorService pollBlockEx = Executors.newSingleThreadExecutor(new NamedThreadFactory("queue中获取block"));
    private ExecutorService winEx = Executors.newSingleThreadExecutor(new NamedThreadFactory("抽奖"));
    private ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory("socket线程池"));


    public static void main(String[] args) throws IOException {
//        System.out.println("start");
//        ServerSocket serverSocket = new ServerSocket(8000);
//        Socket socket = serverSocket.accept();
//        System.out.println("accept");
//        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//
//        bw.write("哈哈");
//        bw.flush();
////        System.out.println(br.readLine());
//
//        bw.close();
//        br.close();
//        socket.close();

        new PosImpl().main();

    }

}
