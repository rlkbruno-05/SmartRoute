package com.smartroute.service;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.smartroute.model.Corrida;
import com.smartroute.model.Veiculo;
import com.smartroute.repository.CorridaRepository;


@Service
public class CorridaService {

	private final CorridaRepository corridaRepository;
	private final CalculadoraRota calculadoraRota;

	public CorridaService(CorridaRepository corridaRepository, @Lazy CalculadoraRota calculadoraRota) {
		this.corridaRepository = corridaRepository;
		this.calculadoraRota = calculadoraRota;
	}

	public Corrida analisarESalvar(Corrida corrida, Veiculo veiculo) {
		calculadoraRota.analisarCorrida(corrida, veiculo);
		return corridaRepository.save(corrida);
	}

	public List<Corrida> listarTodos() {
		return corridaRepository.findAll();
	}

	public Optional<Corrida> buscarPorId(Long id) {
		return corridaRepository.findById(id);
	}

	public List<Corrida> buscarPorMotorista(Long motoristaId) {
		return corridaRepository.findByMotoristaId(motoristaId);
	}

	public void deletar(Long id) {
		corridaRepository.deleteById(id);
	}

	public List<Corrida> buscarRecomendadas() {
		return corridaRepository.findByRecomendacao(com.smartroute.model.Recomendacao.RECOMENDADA);
	}
}