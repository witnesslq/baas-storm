package com.ai.baas.storm.sequence.util;

import com.ai.baas.storm.sequence.factory.SeqClientFactory;

/**
 * 拓扑里使用时必须初始化SeqDataSourceLoader.initDefault(map);<br>
 * Date: 2016年4月19日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author mayt
 */
public final class SeqUtil {

    private SeqUtil() {

    }

    public static Long getNewId(String seqName) {
        return SeqClientFactory.getSeqClient().nextValue(seqName);
    }

    public static String getNewId(String seqName, int seqLen) {
        Long newId = getNewId(seqName);
        String seqStr = String.valueOf(newId);
        while (seqStr.length() < seqLen) {
            seqStr = "0000000" + seqStr;
        }
        return seqStr.substring(seqStr.length() - seqLen);
    }

}
