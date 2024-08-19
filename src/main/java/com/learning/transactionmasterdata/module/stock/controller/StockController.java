package com.learning.transactionmasterdata.module.stock.controller;

import com.learning.transactionmasterdata.module.base.BaseResponse;
import com.learning.transactionmasterdata.module.currency.dto.CurrencyResponse;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.currency.util.CurrencyMapper;
import com.learning.transactionmasterdata.module.stock.dto.StockRequest;
import com.learning.transactionmasterdata.module.stock.dto.StockResponse;
import com.learning.transactionmasterdata.module.stock.model.Stock;
import com.learning.transactionmasterdata.module.stock.service.StockService;
import com.learning.transactionmasterdata.module.stock.util.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping
    public ResponseEntity<BaseResponse<Object>> create(@RequestBody StockRequest request){
        service.create(request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StockResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, StockMapper.mapResponse(service.getById(id))));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<StockResponse>>> getAll(){
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, StockMapper.mapResponses(service.getList())));
    }

    @PutMapping("/update-master/{id}")
    public ResponseEntity<BaseResponse<Object>> updateMaster(@PathVariable String id, @RequestBody StockRequest request){
        service.masterStockUpdate(id, request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @PutMapping("/set-editable/{id}")
    public ResponseEntity<BaseResponse<Object>> setEditable(@PathVariable String id){
        service.setEditable(id);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/page")
    public ResponseEntity<BaseResponse<Page<StockResponse>>> getPage(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "currencyName") String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortDir,
                                                @RequestParam(required = false) String search) {
        // Fetch the page of Currency entities
        Page<Stock> stockPage = service.getPage(page, size, sortBy, sortDir, search);
        // Convert the Currency entities to CurrencyResponse DTOs
        List<StockResponse> stockResponses = StockMapper.mapResponses(stockPage.getContent());
        // Return the ResponseEntity with the page of CurrencyResponse DTOs
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, new PageImpl<>(stockResponses, PageRequest.of(page, size), stockPage.getTotalElements())));
    }


}
