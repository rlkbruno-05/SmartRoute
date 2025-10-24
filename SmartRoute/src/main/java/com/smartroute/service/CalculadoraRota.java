package com.smartroute.service;

import org.springframework.stereotype.Service;

import com.smartroute.model.Corrida;
import com.smartroute.model.Veiculo;

@Service
public class CalculadoraRota {

	private final SmartAICalculator smartAICalculator;

	public CalculadoraRota(SmartAICalculator smartAICalculator) {
		this.smartAICalculator = smartAICalculator;
	}

	public void analisarCorrida(Corrida corrida, Veiculo veiculo) {
		smartAICalculator.analisarCorrida(corrida, veiculo);
	}
}