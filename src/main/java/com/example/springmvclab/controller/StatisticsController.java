package com.example.springmvclab.controller;

import com.example.springmvclab.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {
    private final ProductService productService;

    public StatisticsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        model.addAttribute("title", "Statistik Produk");
        model.addAttribute("totalProducts", productService.findAll().size());
        model.addAttribute("categoryStats", productService.getProductCountByCategory());
        model.addAttribute("cheapest", productService.getCheapestProduct());
        model.addAttribute("mostExpensive", productService.getMostExpensiveProduct());
        model.addAttribute("averagePrice", productService.getAveragePrice());
        model.addAttribute("lowStockCount", productService.getLowStockCount());
        return "statistics";
    }
}