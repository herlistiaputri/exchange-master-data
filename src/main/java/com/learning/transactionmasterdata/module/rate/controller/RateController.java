package com.learning.transactionmasterdata.module.rate.controller;

import com.learning.transactionmasterdata.module.base.BaseResponse;
import com.learning.transactionmasterdata.module.rate.dto.RateRequest;
import com.learning.transactionmasterdata.module.rate.dto.RateResponse;
import com.learning.transactionmasterdata.module.rate.dto.RateRoundingRequest;
import com.learning.transactionmasterdata.module.rate.dto.TransactionRateRequest;
import com.learning.transactionmasterdata.module.rate.model.Rate;
import com.learning.transactionmasterdata.module.rate.service.RateService;
import com.learning.transactionmasterdata.module.rate.util.RateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rate")
@RequiredArgsConstructor
public class RateController {

    private final RateService service;

    @PostMapping
    public ResponseEntity<BaseResponse<Object>> create(@RequestBody RateRequest request){
        service.create(request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @PutMapping("/rate-rounding/{id}")
    public ResponseEntity<BaseResponse<Object>> updateRounding(@PathVariable String id, @RequestBody RateRoundingRequest request){
        service.updateRateRounding(id, request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @PutMapping("/transaction-rate/{id}")
    public ResponseEntity<BaseResponse<Object>> updateTransactionRate(@PathVariable String id, @RequestBody TransactionRateRequest request){
        service.updateTransactionRate(id, request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RateResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, RateUtil.mapResponse(service.getById(id))));
    }

    @GetMapping("/all")
    private ResponseEntity<BaseResponse<List<RateResponse>>> getAll(){
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, RateUtil.mapResponses(service.getAll())));
    }

    @GetMapping("/page")
    public ResponseEntity<BaseResponse<Page<RateResponse>>> getPage(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "createdAt") String sortBy,
                                                       @RequestParam(defaultValue = "asc") String sortDir,
                                                       @RequestParam(required = false) String search) {
        // Fetch the page of Currency entities
        Page<Rate> ratePage = service.getPage(page, size, sortBy, sortDir, search);
        // Convert the Currency entities to CurrencyResponse DTOs
        List<RateResponse> rateResponses = RateUtil.mapResponses(ratePage.getContent());
        // Return the ResponseEntity with the page of CurrencyResponse DTOs
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, new PageImpl<>(rateResponses, PageRequest.of(page, size), ratePage.getTotalElements())));
    }


}
