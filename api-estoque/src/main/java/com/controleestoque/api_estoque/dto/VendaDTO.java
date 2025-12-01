package com.controleestoque.api_estoque.dto;

import java.util.List;

// DTO usado pelo endpoint de registro de venda
// Responsabilidade: transportar ID do cliente e a lista de itens (produtoId + quantidade)
// O controller recebe esse DTO e delega a validação/registro ao service
public class VendaDTO {
    private Long clienteId;
    private List<ItemVendaDTO> itens;
    
    public VendaDTO() {}
    
    public VendaDTO(Long clienteId, List<ItemVendaDTO> itens) {
        this.clienteId = clienteId;
        this.itens = itens;
    }
    
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public List<ItemVendaDTO> getItens() { return itens; }
    public void setItens(List<ItemVendaDTO> itens) { this.itens = itens; }
}