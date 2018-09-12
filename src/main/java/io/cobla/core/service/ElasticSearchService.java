package io.cobla.core.service;

import io.cobla.core.dto.rpc.EthTxInsDto;
import io.cobla.core.dto.rpc.RpcReqDto;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public interface ElasticSearchService {


    /**
     *  엘라스틱 데이터 조회
     * @param uri
     * @param t
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T elasticHttpGet(String uri, Class<T> t) throws IOException;

    String  elasticHttpPost(String uri,Object param) throws IOException;

    String makeElasticUri(String dest,String query);

    String getMaxEthBlockNumber() throws IOException;

    String addEthTxBulk(List<EthTxInsDto> insDtoList) throws IOException;

}
