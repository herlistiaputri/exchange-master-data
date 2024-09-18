package com.learning.transaction.masterdata.module.currency.controller;

import com.learning.transaction.masterdata.module.currency.dto.CurrencyResponse;
import com.learning.transaction.masterdata.module.currency.util.CurrencyMapper;
import com.learning.transaction.masterdata.module.base.BaseResponse;
import com.learning.transaction.masterdata.module.currency.dto.CurrencyRequest;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import com.learning.transaction.masterdata.module.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService service;
    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Object>> create(@RequestBody CurrencyRequest request) {
        service.create(request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CurrencyResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, CurrencyMapper.responseMapper(service.getById(id))));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CurrencyResponse>>> getAll() {
        return ResponseEntity.ok((new BaseResponse<>("Success", 200, CurrencyMapper.responsesMapper(service.getList()))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Object>> update(@PathVariable String id, @RequestBody CurrencyRequest request) {
        service.update(id,request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/page")
    public ResponseEntity<BaseResponse<Page<CurrencyResponse>>> getPage(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "currencyName") String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortDir,
                                                @RequestParam(required = false) String search) {
        // Fetch the page of Currency entities
        Page<Currency> currencyPage = service.getPage(page, size, sortBy, sortDir, search);
        // Convert the Currency entities to CurrencyResponse DTOs
        List<CurrencyResponse> currencyResponses = CurrencyMapper.responsesMapper(currencyPage.getContent());
        // Return the ResponseEntity with the page of CurrencyResponse DTOs
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, new PageImpl<>(currencyResponses, PageRequest.of(page, size), currencyPage.getTotalElements())));
    }

}
