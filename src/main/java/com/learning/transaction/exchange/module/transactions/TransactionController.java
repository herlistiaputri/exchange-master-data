package com.learning.transaction.exchange.module.transactions;

import com.learning.transaction.masterdata.module.base.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController  {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<BaseResponse<Object>> create(@RequestBody TransactionRequest request){
        service.create(request);
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, null));
    }

    @GetMapping("/page")
    public ResponseEntity<BaseResponse<Page<TransactionResponse>>> getPage(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                     @RequestParam(defaultValue = "asc") String sortDir,
                                                                     @RequestParam(required = false) String search) {
        // Fetch the page of Currency entities
        Page<Transaction> transactions = service.getPage(page, size, sortBy, sortDir, search);
        // Convert the Currency entities to CurrencyResponse DTOs
        List<TransactionResponse> transactionResponses = TransactionMapper.mapResponses(transactions.getContent());
        // Return the ResponseEntity with the page of CurrencyResponse DTOs
        return ResponseEntity.ok(new BaseResponse<>("Success", 200, new PageImpl<>(transactionResponses, transactions.getPageable(), transactions.getTotalPages())));
    }

    @GetMapping("/generate-receipt/{id}")
    @ResponseBody
    public void generateReceipt(HttpServletResponse response, @PathVariable String id) {
// Generate PDF
        byte[] pdfBytes = service.generateReceipt(id);

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=receipt.pdf");
        try {
            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error writing PDF to output stream", e);
        }
    }
}
