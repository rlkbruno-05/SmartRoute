package com.smartroute.service;

import org.springframework.stereotype.Service;

import com.smartroute.integration.GerenciadorDeAPIs;
import com.smartroute.model.Corrida;
import com.smartroute.model.Recomendacao;
import com.smartroute.model.TipoCombustivel;
import com.smartroute.model.Veiculo;

@Service
public class SmartAICalculator {

	private final GerenciadorDeAPIs gerenciadorAPIs;

	public SmartAICalculator(GerenciadorDeAPIs gerenciadorAPIs) {
		this.gerenciadorAPIs = gerenciadorAPIs;
	}

	public Double calcularDistancia(String enderecoInicial, String enderecoFinal) {
		System.out.println("\n" + "=".repeat(50));
		System.out.println("🧭 CALCULADORA DE ROTAS - SEM CHUTES");
		System.out.println("=".repeat(50));

		Double distancia = gerenciadorAPIs.calcularDistancia(enderecoInicial, enderecoFinal);

		System.out.println("\n📊 STATUS FINAL: " + gerenciadorAPIs.getStatusAPIs());
		System.out.println("✅ Distância final: " + distancia + " km");

		return distancia;
	}

	public Double calcularDistancia(String enderecoInicial, String enderecoParada, String enderecoFinal) {
		System.out.println("\n📍 === ROTA COM PARADA ===");

		Double distancia1 = calcularDistancia(enderecoInicial, enderecoParada);
		Double distancia2 = calcularDistancia(enderecoParada, enderecoFinal);
		Double total = distancia1 + distancia2;

		System.out.println("🛑 Rota com parada - Total: " + total + " km");
		return total;
	}

	public Double calcularCusto(Double distancia, Veiculo veiculo) {
		return switch (veiculo.getTipoCombustivel()) {
		case ELETRICO -> calcularCustoEletricidade(distancia, veiculo);
		case HIBRIDO -> calcularCustoHibrido(distancia, veiculo);
		default -> calcularCustoCombustivel(distancia, veiculo);
		};
	}

	private Double calcularCustoCombustivel(Double distancia, Veiculo veiculo) {
		Double precoCombustivel = obterPrecoCombustivel(veiculo.getTipoCombustivel());
		Double litrosConsumidos = distancia / veiculo.getConsumoMedio();
		Double custo = litrosConsumidos * precoCombustivel;

		System.out.println("⛽ Combustível: " + String.format("%.1f", litrosConsumidos) + "L × R$" + precoCombustivel
				+ " = R$" + String.format("%.2f", custo));
		return custo;
	}

	private Double calcularCustoEletricidade(Double distancia, Veiculo veiculo) {
		Double precoKwh = 0.80;
		Double kwhConsumidos = distancia / veiculo.getConsumoMedio();
		Double custo = kwhConsumidos * precoKwh;

		System.out.println("🔋 Eletricidade: " + String.format("%.1f", kwhConsumidos) + "kWh × R$" + precoKwh + " = R$"
				+ String.format("%.2f", custo));
		return custo;
	}

	private Double calcularCustoHibrido(Double distancia, Veiculo veiculo) {
		Double custoEletrico = calcularCustoEletricidade(distancia * 0.7, veiculo);
		Double custoCombustivel = calcularCustoCombustivel(distancia * 0.3, veiculo);
		Double total = custoEletrico + custoCombustivel;

		System.out.println("⚡ Híbrido: R$" + String.format("%.2f", custoEletrico) + " (elétrico) + R$"
				+ String.format("%.2f", custoCombustivel) + " (combustível) = R$" + String.format("%.2f", total));
		return total;
	}

	private Double obterPrecoCombustivel(TipoCombustivel tipo) {
		switch (tipo) {
		case GASOLINA:
			return 5.80;
		case ETANOL:
			return 4.20;
		case DIESEL:
			return 6.10;
		case FLEX:
			return 4.80;
		default:
			return 5.50;
		}
	}

	public void analisarCorrida(Corrida corrida, Veiculo veiculo) {
		Double distancia;
		if (corrida.getEnderecoParada() != null && !corrida.getEnderecoParada().isEmpty()) {
			distancia = calcularDistancia(corrida.getEnderecoInicial(), corrida.getEnderecoParada(),
					corrida.getEnderecoFinal());
		} else {
			distancia = calcularDistancia(corrida.getEnderecoInicial(), corrida.getEnderecoFinal());
		}

		Double custo = calcularCusto(distancia, veiculo);
		Recomendacao recomendacao = avaliarCorrida(corrida.getValorCorrida(), custo);
		Double lucro = corrida.getValorCorrida() - custo;

		corrida.setDistanciaKm(distancia);
		corrida.setCustoMotorista(custo);
		corrida.setLucroLiquido(lucro);
		corrida.setRecomendacao(recomendacao);
		corrida.setVeiculoUtilizado(veiculo);
	}

	public Recomendacao avaliarCorrida(Double valorCorrida, Double custoMotorista) {
		Double lucro = valorCorrida - custoMotorista;
		if (lucro <= 0)
			return Recomendacao.NAO_RECOMENDADA;

		Double margem = (lucro / valorCorrida) * 100;
		if (margem > 40)
			return Recomendacao.RECOMENDADA;
		else if (margem > 20)
			return Recomendacao.NEUTRA;
		else
			return Recomendacao.NAO_RECOMENDADA;
	}
}