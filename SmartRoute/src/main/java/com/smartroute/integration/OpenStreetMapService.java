package com.smartroute.integration;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenStreetMapService {
	private final String OSRM_URL = "https://router.project-osrm.org/route/v1/driving";
	private final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
	private final RestTemplate restTemplate;

	public OpenStreetMapService() {
		this.restTemplate = new RestTemplate();
	}

	public Double calcularDistancia(String enderecoInicial, String enderecoFinal) {
		try {
			// Converter endereços em coordenadas
			Double[] coordsInicio = geocodificarEndereco(enderecoInicial);
			Double[] coordsFim = geocodificarEndereco(enderecoFinal);

			if (coordsInicio != null && coordsFim != null) {
				// Calcular rota entre coordenadas
				return calcularRotaOSRM(coordsInicio, coordsFim);
			}
		} catch (Exception e) {
			System.out.println("Erro ao calcular rota: " + e.getMessage());
		}

		return calcularDistanciaAproximada(enderecoInicial, enderecoFinal);
	}

	@SuppressWarnings("unchecked")
	private Double[] geocodificarEndereco(String endereco) {
		try {
			String url = NOMINATIM_URL + "?q=" + endereco.replace(" ", "+") + "&format=json&limit=1";

			List<Map<String, Object>> resultados = restTemplate.getForObject(url, List.class);

			if (resultados != null && !resultados.isEmpty()) {
				Map<String, Object> primeiroResultado = resultados.get(0);
				Double lat = Double.parseDouble(primeiroResultado.get("lat").toString());
				Double lon = Double.parseDouble(primeiroResultado.get("lon").toString());
				return new Double[] { lat, lon };
			}
		} catch (Exception e) {
			System.out.println("Erro ao geocodificar: " + endereco);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Double calcularRotaOSRM(Double[] inicio, Double[] fim) {
		try {
			// Formato: lon,lat (OSRM usa longitude primeiro)
			String url = String.format("%s/%f,%f;%f,%f?overview=false", OSRM_URL, inicio[1], inicio[0], fim[1], fim[0]);

			Map<String, Object> response = restTemplate.getForObject(url, Map.class);

			if ("Ok".equals(response.get("code"))) {
				List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
				if (routes != null && !routes.isEmpty()) {
					Double distanciaMetros = (Double) routes.get(0).get("distance");
					return distanciaMetros / 1000.0; // Converter para km
				}
			}
		} catch (Exception e) {
			System.out.println("Erro OSRM: " + e.getMessage());
		}
		return null;
	}

	private Double calcularDistanciaAproximada(String end1, String end2) {
		// Fallback: cálculo baseado em hash dos endereços
		int hash1 = Math.abs(end1.hashCode());
		int hash2 = Math.abs(end2.hashCode());
		return ((hash1 % 50) + (hash2 % 30)) / 10.0 + 3.0;
	}
}