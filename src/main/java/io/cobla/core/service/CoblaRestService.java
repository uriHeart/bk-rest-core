package io.cobla.core.service;

import io.cobla.core.dto.ApiWalletTransactionReqDto;
import io.cobla.core.dto.EtherScanDto;
import io.cobla.core.dto.ResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;

public interface CoblaRestService {
    String buildEtherScanAccountUri(ApiWalletTransactionReqDto param,String apiKey);

    ArrayList<ApiWalletTransactionReqDto> addWalletTransaction(EtherScanDto etherTxData);

    ResultDto addBlackWallet(MultipartFile file) throws Exception;

    ResultDto collectTransaction(ApiWalletTransactionReqDto param,String inputApiKey);

    //ArrayList<ApiWalletTransactionReqDto> collectTransaction(ArrayList<ApiWalletTransactionReqDto> param,HashMap<String,String> runKey);
}
