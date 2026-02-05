package com.bena.api.module.admin.controller;

import com.bena.api.common.constant.IraqGovernorates;
import com.bena.api.common.dto.ApiResponse;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller لجلب بيانات المحافظات والمدن
 */
@RestController
@RequestMapping("/v1/public/governorates")
public class GovernoratesController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<GovernorateResponse>>> getAllGovernorates() {
        List<GovernorateResponse> governorates = new ArrayList<>();
        
        for (String govName : IraqGovernorates.getAllGovernorates()) {
            GovernorateResponse gov = new GovernorateResponse();
            gov.setName(govName);
            gov.setCities(IraqGovernorates.getCitiesByGovernorate(govName));
            governorates.add(gov);
        }
        
        return ResponseEntity.ok(ApiResponse.success(governorates));
    }

    @GetMapping("/{governorate}/cities")
    public ResponseEntity<ApiResponse<List<String>>> getCitiesByGovernorate(@PathVariable String governorate) {
        List<String> cities = IraqGovernorates.getCitiesByGovernorate(governorate);
        if (cities.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("المحافظة غير موجودة"));
        }
        return ResponseEntity.ok(ApiResponse.success(cities));
    }
}

@Data
class GovernorateResponse {
    private String name;
    private List<String> cities;
}
