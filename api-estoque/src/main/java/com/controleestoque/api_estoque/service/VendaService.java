package com.controleestoque.api_estoque.service;

import com.controleestoque.api_estoque.dto.ItemVendaDTO;
import com.controleestoque.api_estoque.dto.VendaDTO;
import com.controleestoque.api_estoque.model.*;
import com.controleestoque.api_estoque.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;

import java.util.*;

@Component
public class VendaService {
    
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final TransactionTemplate transactionTemplate;
    
    public VendaService(VendaRepository vendaRepository,
                        ClienteRepository clienteRepository,
                        ProdutoRepository produtoRepository,
                        EstoqueRepository estoqueRepository,
                        PlatformTransactionManager transactionManager) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Venda registrarVenda(VendaDTO vendaDTO) {
        return transactionTemplate.execute(status -> {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(vendaDTO.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Validar disponibilidade de estoque para os itens
        for (ItemVendaDTO itemDTO : vendaDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDTO.getProdutoId()));
            
            Estoque estoque = estoqueRepository.findByProdutoId(itemDTO.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para o produto: " + itemDTO.getProdutoId()));
            
            if (estoque.getQuantidade() < itemDTO.getQuantidade()) {
                throw new IllegalArgumentException(
                    "Estoque insuficiente para o produto '" + produto.getNome() + 
                    "'. Disponível: " + estoque.getQuantidade() + 
                    ", Solicitado: " + itemDTO.getQuantidade());
            }
        }
        
        // Criar venda
        BigDecimal valorTotal = BigDecimal.ZERO;
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setValorTotal(valorTotal);
        List<ItemVenda> itens = new ArrayList<>();
        
        
        // Processar itens e dar baixa no estoque
        for (ItemVendaDTO itemDTO : vendaDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDTO.getProdutoId()));
            Estoque estoque = estoqueRepository.findByProdutoId(itemDTO.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para o produto: " + itemDTO.getProdutoId()));
            
            // Criar item de venda
            ItemVenda item = new ItemVenda(venda, produto, itemDTO.getQuantidade(), produto.getPreco());
            itens.add(item);
            
            // Calcular valor total
            valorTotal = valorTotal.add(item.getTotal());
            
            // Dar baixa no estoque
            estoque.setQuantidade(estoque.getQuantidade() - itemDTO.getQuantidade());
            estoqueRepository.save(estoque);
        }
        
        venda.setItens(itens);
        venda.setValorTotal(valorTotal);
        
        return vendaRepository.save(venda);
        });
    }
    
    public Venda obterVendaPorId(Long id) {
        return vendaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada"));
    }
    
    public List<Venda> obterVendasPorCliente(Long clienteId) {
        return vendaRepository.findByClienteId(clienteId);
    }
    
    public List<Venda> obterTodasAsVendas() {
        return vendaRepository.findAll();
    }
}