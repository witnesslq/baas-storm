package com.ai.baas.storm.sequence.client;

public interface ISeqClient {

    Long nextValue(String sequenceName);

}
