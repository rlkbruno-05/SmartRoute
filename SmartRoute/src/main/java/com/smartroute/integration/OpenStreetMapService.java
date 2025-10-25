package com.smartroute.integration;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OpenStreetMapService {
    private final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private final String OSRM_URL = "https://router.project-osrm.org/route/v1/driving";
    private final RestTemplate restTemplate;

    public OpenStreetMapService() {
        this.restTemplate = createRestTemplateWithTimeout();
    }

    private RestTemplate createRestTemplateWithTimeout() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

    public Double[] geocodificarEndereco(String endereco) {
        try {
            String url = NOMINATIM_URL + "?q=" + endereco.replace(" ", "+") + 
                        "&format=json&limit=1&addressdetails=1";
            System.out.println("   🔍 Geocodificando: " + endereco);

            List<Map<String, Object>> resultados = restTemplate.getForObject(url, List.class);

            if (resultados != null && !resultados.isEmpty()) {
                Map<String, Object> primeiroResultado = resultados.get(0);
                Double lat = Double.parseDouble(primeiroResultado.get("lat").toString());
                Double lon = Double.parseDouble(primeiroResultado.get("lon").toString());
                
                System.out.println("   ✅ Coordenadas encontradas: " + lat + ", " + lon);
                return new Double[] { lat, lon };
            } else {
                System.out.println("   ❌ Nenhum resultado encontrado para: " + endereco);
            }
        } catch (Exception e) {
            System.out.println("   ❌ Erro ao geocodificar '" + endereco + "': " + e.getMessage());
        }
        return null;
    }

    public Double[] geocodificarComFallback(String endereco) {
        // Primeira tentativa: endereço completo
        Double[] coordenadas = geocodificarEndereco(endereco);
        if (coordenadas != null) return coordenadas;
        
        // Segunda tentativa: extrai apenas cidade e bairro
        String enderecoSimplificado = extrairCidadeEBairro(endereco);
        if (!enderecoSimplificado.equals(endereco)) {
            System.out.println("   🔄 Tentando endereço simplificado: " + enderecoSimplificado);
            coordenadas = geocodificarEndereco(enderecoSimplificado);
            if (coordenadas != null) return coordenadas;
        }
        
        // Terceira tentativa: apenas a cidade
        String apenasCidade = extrairApenasCidade(endereco);
        if (!apenasCidade.isEmpty()) {
            System.out.println("   🔄 Tentando apenas cidade: " + apenasCidade);
            return geocodificarEndereco(apenasCidade + ", RS, Brasil");
        }
        
        return null;
    }

    public Double calcularDistanciaRota(String enderecoInicial, String enderecoFinal) {
        try {
            System.out.println("   🗺️ Calculando rota completa OpenStreetMap...");
            
            // Converter endereços em coordenadas
            Double[] coordsInicio = geocodificarComFallback(enderecoInicial);
            Double[] coordsFim = geocodificarComFallback(enderecoFinal);

            if (coordsInicio != null && coordsFim != null) {
                System.out.println("   📍 Coordenadas para rota:");
                System.out.println("      Início: " + coordsInicio[0] + ", " + coordsInicio[1]);
                System.out.println("      Fim: " + coordsFim[0] + ", " + coordsFim[1]);
                
                // Calcular rota entre coordenadas
                return calcularRotaOSRM(coordsInicio, coordsFim);
            } else {
                System.out.println("   ❌ Não foi possível geocodificar os endereços para cálculo de rota");
            }
        } catch (Exception e) {
            System.out.println("   ❌ Erro ao calcular rota OSM: " + e.getMessage());
        }
        return null;
    }

    private Double calcularRotaOSRM(Double[] inicio, Double[] fim) {
        try {
            // Formato: lon,lat (OSRM usa longitude primeiro)
            String url = String.format("%s/%f,%f;%f,%f?overview=false", 
                OSRM_URL, inicio[1], inicio[0], fim[1], fim[0]);

            System.out.println("   🚗 Calculando rota OSRM...");
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if ("Ok".equals(response.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    Double distanciaMetros = (Double) routes.get(0).get("distance");
                    Double distanciaKm = distanciaMetros / 1000.0;
                    System.out.println("   ✅ Distância OSRM: " + distanciaKm + " km");
                    return distanciaKm;
                }
            } else {
                System.out.println("   ❌ OSRM retornou código: " + response.get("code"));
            }
        } catch (Exception e) {
            System.out.println("   ❌ Erro OSRM: " + e.getMessage());
        }
        return null;
    }

    private String extrairCidadeEBairro(String endereco) {
        String enderecoLower = endereco.toLowerCase();
        
        // Extrai cidade
        String cidade = "";
        if (enderecoLower.contains("sapucaia") || enderecoLower.contains("sapucaia do sul")) cidade = "Sapucaia do Sul";
        else if (enderecoLower.contains("esteio")) cidade = "Esteio";
        else if (enderecoLower.contains("canoas")) cidade = "Canoas";
        else if (enderecoLower.contains("porto alegre") || enderecoLower.contains("poa")) cidade = "Porto Alegre";
        else if (enderecoLower.contains("gravataí") || enderecoLower.contains("gravatai")) cidade = "Gravataí";
        else if (enderecoLower.contains("cachoeirinha")) cidade = "Cachoeirinha";
        else if (enderecoLower.contains("alvorada")) cidade = "Alvorada";
        else if (enderecoLower.contains("viamão") || enderecoLower.contains("viamao")) cidade = "Viamão";
        else if (enderecoLower.contains("guaíba") || enderecoLower.contains("guaiba")) cidade = "Guaíba";
        
        if (cidade.isEmpty()) return endereco;
        
        // Extrai bairro
        String bairro = extrairBairro(endereco);
        if (!bairro.equals("Centro")) {
            return bairro + ", " + cidade + ", RS, Brasil";
        }
        
        return cidade + ", RS, Brasil";
    }

    private String extrairApenasCidade(String endereco) {
        String enderecoLower = endereco.toLowerCase();
        
        if (enderecoLower.contains("sapucaia") || enderecoLower.contains("sapucaia do sul")) return "Sapucaia do Sul";
        if (enderecoLower.contains("esteio")) return "Esteio";
        if (enderecoLower.contains("canoas")) return "Canoas";
        if (enderecoLower.contains("porto alegre") || enderecoLower.contains("poa")) return "Porto Alegre";
        if (enderecoLower.contains("gravataí") || enderecoLower.contains("gravatai")) return "Gravataí";
        if (enderecoLower.contains("cachoeirinha")) return "Cachoeirinha";
        if (enderecoLower.contains("alvorada")) return "Alvorada";
        if (enderecoLower.contains("viamão") || enderecoLower.contains("viamao")) return "Viamão";
        if (enderecoLower.contains("guaíba") || enderecoLower.contains("guaiba")) return "Guaíba";
        
        return "";
    }

    private String extrairBairro(String endereco) {
        String enderecoLower = endereco.toLowerCase();
        String[] palavras = enderecoLower.split(" ");
        
        for (int i = 0; i < palavras.length; i++) {
            if (palavras[i].equals("bairro") && i + 1 < palavras.length) {
                return capitalize(palavras[i + 1]);
            }
            if (palavras[i].equals("jardim") && i + 1 < palavras.length) {
                return "Jardim " + capitalize(palavras[i + 1]);
            }
            if (palavras[i].equals("vila") && i + 1 < palavras.length) {
                return "Vila " + capitalize(palavras[i + 1]);
            }
            if (palavras[i].equals("ipiranga") || palavras[i].equals("ipirang")) {
                return "Ipiranga";
            }
            if (palavras[i].equals("planalto")) {
                return "Planalto";
            }
            if (palavras[i].equals("centro")) {
                return "Centro";
            }
        }
        
        return "Centro";
    }

    private String capitalize(String palavra) {
        if (palavra == null || palavra.isEmpty()) return palavra;
        return palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
    }
}