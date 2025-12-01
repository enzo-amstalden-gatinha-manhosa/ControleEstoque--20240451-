package com.controleestoque.api_estoque.service;

import com.controleestoque.api_estoque.model.Cliente;
import com.controleestoque.api_estoque.repository.ClienteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteService {
    
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    public Cliente criarCliente(Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return clienteRepository.save(cliente);
    }
    
    public Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }
    
    public List<Cliente> obterTodosOsClientes() {
        return clienteRepository.findAll();
    }
    
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente cliente = obterClientePorId(id);
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        return clienteRepository.save(cliente);
    }
    
    public void deletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}