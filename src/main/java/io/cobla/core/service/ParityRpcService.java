package io.cobla.core.service;

import io.cobla.core.domain.rpc.ApiWalletMonitor;
import io.cobla.core.dto.ResultDto;
import io.cobla.core.dto.rpc.ApiWalletMonitorReqDto;
import io.cobla.core.dto.rpc.RpcReqDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

public interface ParityRpcService {

    /**
     * 이더리움 밸런스 조회
     * @param params
     * @return
     * @throws IOException
     */
    ResultDto getBalance(HashMap<String, String> params) throws IOException;

    /**
     * 이더리움 모니터링 대상 등록
     * @param reqDto
     * @return
     * @throws IOException
     */
    ResultDto addEthMonitorTarget(ApiWalletMonitorReqDto reqDto) throws IOException;

    /**
     * 이더리움 모니터링
     */
    void ethMonitorSendTx() throws UnsupportedEncodingException, IOException;

    /**
     * 이더리움 모니터링 대상조회
     * @return
     */
    List<ApiWalletMonitor> getEthMonitorList();

    /**
     * 이더리움 send 트랜잭션 조회
     * @param params
     * @return
     * @throws IOException
     */
    ResultDto getSendTxCount(HashMap<String, String> params) throws IOException;

    /**
     * 해쉬값에 대한 트랜잭션 조회
     */
    String getTransactionByHash(HashMap<String, String> params) throws IOException;

    String transactionInElastic(String txData) throws IOException;

}
