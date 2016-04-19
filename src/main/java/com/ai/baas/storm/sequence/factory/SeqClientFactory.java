package com.ai.baas.storm.sequence.factory;

import com.ai.baas.storm.sequence.client.ISeqClient;
import com.ai.baas.storm.sequence.client.impl.SeqClientImpl;

public final class SeqClientFactory {

    private SeqClientFactory() {

    }

    private static ISeqClient sequenceClient;

    public static ISeqClient getSeqClient() {
        if (sequenceClient == null) {
                sequenceClient = new SeqClientImpl();
        }
        return sequenceClient;
    }
}
