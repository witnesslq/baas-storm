package com.ai.baas.storm.sequence.service;

public interface ISequenceService {

    Long nextValue(String sequenceName);

    void modifySequence(String sequenceName, long nextVal);

}
