# BlockChain-principle
description:blockchain-principle experimental implement for java

java 版本的区块链原理基本实现

###### 主要简单实现了区块链的一些基本功能
* 交易的生成
* 区块的生成
* 挖矿过程（pow）
* 分叉冲突的解决
* pos用tcp实现在pos包中

###### Todo
1. 持久化
2. 验证交易  公私钥
3. 并发调用
4. 查询索引
5. 其他共识算法
6. pos实现，后期考虑合并到principle包，且支持共识算法的切换

###### 使用测试说明
**principle**包使用的是**pow**共识机制，这个包主要用来简单实现区块链基本功能原理。

使用**spring boot**启动后，通过http的访问方式可以实现区块链生成的大致流程:
1. post /chain/newTransaction产生区块链
2. get /chain/mine  手动进行挖矿（实际应该是有一段循环逻辑不停地进行pow挖矿）
3. get /chain/getFullChain  查看挖矿后的最新区块链


**pos**包使用的是**pos**共识机制，为了方便编写测试，这个包下的区块node结构和principle包
的略有不同。

主要是通过TCP方式，启动一个server，然后让多个client连接，发送client对应的股权信息，
server来进行选择。当前使用的b/s模式可以认为是实际使用的p2p形势的一种简化。

大致流程：
1. PosImpl类启动main方法
2. com.zhj.blockchainprinciple.pos.Client启动main方法，里面的clientSize参数
可以设置同时发送信息的client的数量。
3. 根据PosImpl对应的控制台打印的信息，查看client的连接、执行逻辑、
选出产快client、断开连接的情况
4. 根据Client对应的控制台打印的信息，查看server发送给每个client的选举信息，以及
最新的区块链状况

_ps_ :因为打印的控制信息设计了多个client，所以查看具体某个client的信息的时候，
可以根据线程名进行过滤。