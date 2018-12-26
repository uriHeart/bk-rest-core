package io.cobla.core.service;

import io.cobla.core.domain.rpc.ApiWalletMonitor;
import io.cobla.core.dto.ResultDto;
import io.cobla.core.dto.rpc.ApiWalletMonitorReqDto;
import io.cobla.core.dto.rpc.EthTxInsDto;
import io.cobla.core.dto.rpc.RpcReqDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

public interface ParityRpcService {

    /**
     * 로컬 Parity 노드에 rcp 명령호출.
     * @param t
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T callParityRpc(RpcReqDto req, Class<T> t) throws IOException;



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


    /**
     * 현재 노드의 마지막 블럭넘버를 조회
     *
     * @return
     * @throws IOException
     */
    String getEtherBlockNumber() throws IOException;


    /**
     * 이더리움 트랜잭션  ELK 입력
     * @param insDto
     */
    void addEthTxToElk(EthTxInsDto insDto) throws IOException;


    /**
     * 이더리움 블록의 트랜잭션 정보를 가져온다.
     * @param blockNumber
     * @return
     * @throws IOException
     */
    List<EthTxInsDto> getEthBlockDataByNumber(String blockNumber) throws IOException;




}
