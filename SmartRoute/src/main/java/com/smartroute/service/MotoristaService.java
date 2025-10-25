package com.smartroute.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smartroute.model.Motorista;
import com.smartroute.repository.MotoristaRepository;

@Service
public class MotoristaService {

	private final MotoristaRepository motoristaRepository;

	public MotoristaService(MotoristaRepository motoristaRepository) {
		this.motoristaRepository = motoristaRepository;
	}

	public Motorista salvar(Motorista motorista) {
		return motoristaRepository.save(motorista);
	}

	public List<Motorista> listarTodos() {
		return motoristaRepository.findAll();
	}

	public Optional<Motorista> buscarPorId(Long id) {
		return motoristaRepository.findById(id);
	}

	public Optional<Motorista> buscarPorCpf(String cpf) {
		return motoristaRepository.findByCpf(cpf);
	}

	public Optional<Motorista> buscarPorEmail(String email) {
		return motoristaRepository.findByEmail(email);
	}

	public void deletar(Long id) {
		motoristaRepository.deleteById(id);
	}

	public boolean existePorCpf(String cpf) {
		return motoristaRepository.findByCpf(cpf).isPresent();
	}

	public boolean existePorEmail(String email) {
		return motoristaRepository.findByEmail(email).isPresent();
	}
}