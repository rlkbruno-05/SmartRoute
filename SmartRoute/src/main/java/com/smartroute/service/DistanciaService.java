package com.smartroute.service;

import com.smartroute.model.Distancia;
import com.smartroute.repository.DistanciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DistanciaService {

	private final DistanciaRepository distanciaRepository;

	public DistanciaService(DistanciaRepository distanciaRepository) {
		this.distanciaRepository = distanciaRepository;
	}

	public Distancia salvar(Distancia distancia) {
		return distanciaRepository.save(distancia);
	}

	public List<Distancia> listarTodos() {
		return distanciaRepository.findAll();
	}

	public Optional<Distancia> buscarPorId(Long id) {
		return distanciaRepository.findById(id);
	}

	public void deletar(Long id) {
		distanciaRepository.deleteById(id);
	}

	public Optional<Double> buscarDistanciaCalculada(String enderecoInicial, String enderecoFinal) {
		Optional<Distancia> distanciaOpt = distanciaRepository.findByParEnderecos(enderecoInicial, enderecoFinal);

		if (distanciaOpt.isPresent()) {
			Distancia distancia = distanciaOpt.get();
			System.out.println("💾 Distância encontrada no banco: " + distancia.getDistanciaKm() + " km");
			return Optional.of(distancia.getDistanciaKm());
		}

		return Optional.empty();
	}

	public void salvarDistancia(String enderecoInicial, String enderecoFinal, Double distancia, String fonte) {
		Optional<Distancia> existente = distanciaRepository.findByParEnderecos(enderecoInicial, enderecoFinal);

		if (existente.isPresent()) {
			Distancia dist = existente.get();
			dist.setDistanciaKm(distancia);
			dist.setFonte(fonte);
			dist.setDataCalculo(LocalDateTime.now());
			distanciaRepository.save(dist);
			System.out.println("✏️ Distância atualizada no banco: " + distancia + " km");
		} else {
			Distancia novaDistancia = new Distancia(enderecoInicial, enderecoFinal, distancia, fonte);
			distanciaRepository.save(novaDistancia);
			System.out.println("💾 Distância salva no banco: " + distancia + " km");
		}
	}

	public long contarDistanciaSalvas() {
		return distanciaRepository.count();
	}
}