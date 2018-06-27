package io.cobla.core.service;

import io.cobla.core.dto.ApiWalletTransactionReqDto;
import io.cobla.core.dto.EtherScanDto;
import io.cobla.core.dto.ResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface CoblaRestService {
    String buildEtherScanAccountUri(ApiWalletTransactionReqDto param);

    String addWalletTransaction(EtherScanDto etherTxData);

    ResultDto addBlackWallet(MultipartFile file) throws Exception;
}
