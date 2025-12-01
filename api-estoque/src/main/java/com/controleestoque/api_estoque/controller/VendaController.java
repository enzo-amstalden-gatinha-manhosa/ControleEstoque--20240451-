package com.controleestoque.api_estoque.controller;

import com.controleestoque.api_estoque.dto.VendaDTO;
import com.controleestoque.api_estoque.model.Venda;
import com.controleestoque.api_estoque.service.VendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaController {
    
    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }
    
    // Endpoint de registro de venda
    // Responsabilidade:
    //  - receber VendaDTO (clienteId + itens)
    //  - retornar sucesso ou erro (estoque insuficiente, cliente/produto não encontrado)
    @PostMapping
    public ResponseEntity<?> registrarVenda(@RequestBody VendaDTO vendaDTO) {
        try {
            Venda venda = vendaService.registrarVenda(vendaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(venda);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Venda>> obterTodasAsVendas() {
        return ResponseEntity.ok(vendaService.obterTodasAsVendas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obterVendaPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vendaService.obterVendaPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venda>> obterVendasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(vendaService.obterVendasPorCliente(clienteId));
    }
}