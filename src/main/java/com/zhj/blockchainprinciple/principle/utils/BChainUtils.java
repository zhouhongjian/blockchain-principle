package com.zhj.blockchainprinciple.principle.utils;

import com.alibaba.fastjson.JSON;
import com.zhj.blockchainprinciple.pos.pojo.PosBlock;
import com.zhj.blockchainprinciple.principle.pojo.Block;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhj
 * @date 2018/8/18
 */
public class BChainUtils {
    /**
     * 可以考虑使用hashCode()
     * 但是考虑可能多平台使用，hash摘要暂且通过json来作为sha256的入参
     */
    public static String hashBlock(Block block) {
        String jsonStr = JSON.toJSONString(block);
        return getSHA256StrJava(jsonStr);
    }

    /**
     * pos共识下block的hash算法
     */
    public static String hashPosBlock(PosBlock block) {
        String blockStr = String.valueOf(block.getIndex()) +
                block.getValidator() +
                block.getPreHash() +
                block.getBpm() +
                block.getTimestamp();
        return getSHA256StrJava(blockStr);
    }

    /**
     * 　　* 利用java原生的摘要实现SHA256加密
     * 　　* @param str 加密后的报文
     * 　　* @return
     */
    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 　　* 将byte转为16进制
     * 　　* @param bytes
     * 　　* @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        Block block = new Block(1, 1, null, "ssss", "qqqq");
        System.out.println(hashBlock(block));
        System.out.println(getSHA256StrJava(hashBlock(block)));
        System.out.println(getSHA256StrJava(hashBlock(block)).length());
    }
}
