package io.cobla.core.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.cobla.core.dto.rpc.EthTxInsDto;
import io.cobla.core.service.ElasticSearchService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {


    @Value("${block.tracer.elk.host}")
    private String elkHost;

    @Value("${block.tracer.elk.port}")
    private String elkPort;


    @Override
    public <T> T elasticHttpGet(String uri, Class<T> t) throws IOException {

            HttpGet request = new HttpGet(uri);
            request.addHeader("Content-type", "application/json");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(request);
            String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");
            T result = new Gson().fromJson(rpcResult, t);
        return result;
    }

    @Override
    public String elasticHttpPost(String uri,Object param) throws IOException {

            HttpPost request = new HttpPost(uri);
            StringEntity rpcParams =new StringEntity(new Gson().toJson(param));

            request.addHeader("Content-type", "application/json");
            request.setEntity(rpcParams);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(request);
            String result = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");

        return result;
    }

    @Override
    public String makeElasticUri(String dest, String query) {

            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            sb.append(elkHost);
            sb.append(":");
            sb.append(elkPort);
            sb.append("/");
            sb.append(dest);
            sb.append("/");
            sb.append(query);

        return sb.toString();
    }

    @Override
    public String getMaxEthBlockNumber() throws IOException {

        JsonObject jo_param = new JsonObject();
        jo_param.addProperty("size",0);

        JsonObject jo_field = new JsonObject();
        jo_field.addProperty("field","blockNumber");

        JsonObject jo_max = new JsonObject();
        jo_max.add("max",jo_field);

        JsonObject jo_blockNumber = new JsonObject();
        jo_blockNumber.add("max_blockNumber",jo_max);

        jo_param.add("aggs", jo_blockNumber  );


        String reqUri = this.makeElasticUri("check/block","_search?");
        String elkResult = this.elasticHttpPost(reqUri,jo_param);

        JsonObject gsString = new Gson().fromJson(elkResult,JsonObject .class);

        Integer blockNumber = gsString.get("aggregations").getAsJsonObject().get("max_blockNumber").getAsJsonObject().get("value").getAsInt();

        return blockNumber.toString();
    }

    @Override
    public String addEthTxBulk(List<EthTxInsDto> insDtoList) throws IOException {
        Settings settings = Settings.builder()
                .put("cluster.name", "ethereum").build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.0.29"), 9300));

        BulkRequestBuilder builder = client.prepareBulk();

        for(EthTxInsDto insDto : insDtoList){
            builder.add(client.prepareIndex("eth","transaction",insDto.getHash())
                    .setSource(new Gson().toJson(insDto),XContentType.JSON)
            );
        }

        BulkResponse response = builder.get();

        return response.status().toString();
    }
}
