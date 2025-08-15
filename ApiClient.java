package com.example.excelprocessor.client;

import com.example.excelprocessor.model.api.Api1Request;
import com.example.excelprocessor.model.api.Api1Response;
import com.example.excelprocessor.model.api.Api2Request;
import com.example.excelprocessor.model.api.Api2Response;
import java.util.Optional;

public interface ApiClient {
    Optional<Api1Response> callApi1(Api1Request request);
    Optional<Api2Response> callApi2(Api2Request request);
}