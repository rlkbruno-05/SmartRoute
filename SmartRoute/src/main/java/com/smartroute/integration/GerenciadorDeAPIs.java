package com.smartroute.integration;

import com.smartroute.service.DistanciaService;
import com.smartroute.service.GeminiService;
import com.smartroute.exception.DistanciaNaoEncontradaException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GerenciadorDeAPIs {

	private final AtomicInteger contadorRequisicoes = new AtomicInteger(0);
	private LocalDateTime ultimoReset = LocalDateTime.now();

	private final GeminiService geminiService;
	private final OpenStreetMapService mapsService;
	private final DistanciaService distanciaService;

	public GerenciadorDeAPIs(GeminiService geminiService, OpenStreetMapService mapsService,
			DistanciaService distanciaService) {
		this.geminiService = geminiService;
		this.mapsService = mapsService;
		this.distanciaService = distanciaService;
	}

	public Double calcularDistancia(String enderecoInicial, String enderecoFinal) {
		


		// 2. 🗺️ Tentar OpenStreetMap (SECUNDÁRIA)
		try {
			System.out.println("2️⃣ 🗺️ Tentando OpenStreetMap...");
			Double distancia = mapsService.calcularDistancia(enderecoInicial, enderecoFinal);
			if (distancia != null && distancia > 0) {
				System.out.println("✅ OpenStreetMap calculou: " + distancia + " km");
				return distancia;
			} else {
				System.out.println("❌ OpenStreetMap retornou distância inválida");
			}
		} catch (Exception e) {
			System.out.println("❌ OpenStreetMap falhou: " + e.getMessage());
		}

		// 3. 💾 Tentar banco de dados (TERCIÁRIA)
		try {
			System.out.println("3️⃣ 💾 Tentando banco de dados...");
			Optional<Double> distanciaOpt = distanciaService.buscarDistanciaCalculada(enderecoInicial, enderecoFinal);
			if (distanciaOpt.isPresent()) {
				Double distancia = distanciaOpt.get();
				System.out.println("✅ Banco de dados encontrou: " + distancia + " km");
				return distancia;
			} else {
				System.out.println("❌ Nenhuma distância encontrada no banco");
			}
		} catch (Exception e) {
			System.out.println("❌ Erro no banco de dados: " + e.getMessage());
		}

		// 4. ❌ EXCEÇÃO - TODAS AS OPÇÕES FALHARAM
		throw new DistanciaNaoEncontradaException(enderecoInicial, enderecoFinal, contadorRequisicoes.get() < 100,
				contadorRequisicoes.get(), distanciaService.contarDistanciaSalvas());
	}

	private void verificarEResetarContador() {
		LocalDateTime agora = LocalDateTime.now();
		if (agora.getMinute() != ultimoReset.getMinute()) {
			int antigo = contadorRequisicoes.get();
			contadorRequisicoes.set(0);
			ultimoReset = agora;
			System.out.println("🔄 Contador da Hugging Face resetado: " + antigo + " → 0");
		}
	}

	public int getRequisicoesRestantes() {
		return 100 - contadorRequisicoes.get();
	}

	public String getStatusAPIs() {
		return String.format("🤖 Hugging Face: %s (%d/100 req) | 🗺️ OSM: ✅ | 💾 Banco: %d distâncias",
				contadorRequisicoes.get() < 100 ? "✅" : "⛔", contadorRequisicoes.get(),
				distanciaService.contarDistanciaSalvas());
	}
}
