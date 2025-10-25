package com.smartroute.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smartroute.model.Veiculo;
import com.smartroute.repository.VeiculoRepository;

@Service
public class VeiculoService {

	private final VeiculoRepository veiculoRepository;

	public VeiculoService(VeiculoRepository veiculoRepository) {
		this.veiculoRepository = veiculoRepository;
	}

	public Veiculo salvar(Veiculo veiculo) {
		return veiculoRepository.save(veiculo);
	}

	public List<Veiculo> listarTodos() {
		return veiculoRepository.findAll();
	}

	public Optional<Veiculo> buscarPorId(Long id) {
		return veiculoRepository.findById(id);
	}

	public Optional<Veiculo> buscarPorPlaca(String placa) {
		return veiculoRepository.findByPlaca(placa);
	}

	public List<Veiculo> buscarPorMotorista(Long motoristaId) {
		return veiculoRepository.findByMotoristaId(motoristaId);
	}

	public void deletar(Long id) {
		veiculoRepository.deleteById(id);
	}

	public boolean existePorPlaca(String placa) {
		return veiculoRepository.findByPlaca(placa).isPresent();
	}
}