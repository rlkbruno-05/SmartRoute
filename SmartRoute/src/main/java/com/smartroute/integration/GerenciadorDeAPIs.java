package com.smartroute.integration;

import com.smartroute.service.DistanciaService;
import com.smartroute.service.CalculadoraGeografica;
import com.smartroute.exception.DistanciaNaoEncontradaException;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class GerenciadorDeAPIs {

    private final OpenStreetMapService mapsService;
    private final DistanciaService distanciaService;
    private final CalculadoraGeografica calculadoraGeografica;

    public GerenciadorDeAPIs(OpenStreetMapService mapsService, 
                           DistanciaService distanciaService,
                           CalculadoraGeografica calculadoraGeografica) {
        this.mapsService = mapsService;
        this.distanciaService = distanciaService;
        this.calculadoraGeografica = calculadoraGeografica;
    }

    public Double calcularDistancia(String enderecoInicial, String enderecoFinal) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚗 SMARTROUTE - CALCULANDO DISTÂNCIA");
        System.out.println("=".repeat(60));
        System.out.println("📍 De: " + enderecoInicial);
        System.out.println("🎯 Para: " + enderecoFinal);
        System.out.println("=".repeat(60));

        // 1️⃣ Geocodificação OpenStreetMap + Haversine (MAIS PRECISO)
        try {
            System.out.println("\n🎯 [1/3] Iniciando geocodificação de alta precisão...");
            
            Double distancia = calcularDistanciaGeocodificada(enderecoInicial, enderecoFinal);
            if (distancia != null && distancia > 0) {
                System.out.println("✅ Distância calculada com precisão: " + String.format("%.2f", distancia) + " km");
                
                distanciaService.salvarDistancia(enderecoInicial, enderecoFinal, distancia, "OpenStreetMap Geocodificado");
                return distancia;
            }
        } catch (Exception e) {
            System.out.println("❌ Geocodificação falhou: " + e.getMessage());
        }

        // 2️⃣ Banco de dados
        try {
            System.out.println("\n💾 [2/3] Consultando banco de dados...");
            
            Optional<Double> distanciaOpt = distanciaService.buscarDistanciaCalculada(enderecoInicial, enderecoFinal);
            if (distanciaOpt.isPresent()) {
                Double distancia = distanciaOpt.get();
                System.out.println("✅ Banco de dados encontrou: " + String.format("%.2f", distancia) + " km");
                return distancia;
            } else {
                System.out.println("❌ Nenhuma distância encontrada no banco");
            }
        } catch (Exception e) {
            System.out.println("❌ Erro no banco de dados: " + e.getMessage());
        }

        // 3️⃣ OpenStreetMap cálculo de rota completo
        try {
            System.out.println("\n🗺️ [3/3] Calculando rota completa...");
            
            Double distancia = mapsService.calcularDistanciaRota(enderecoInicial, enderecoFinal);
            if (distancia != null && distancia > 0) {
                System.out.println("✅ OpenStreetMap calculou rota: " + String.format("%.2f", distancia) + " km");
                
                distanciaService.salvarDistancia(enderecoInicial, enderecoFinal, distancia, "OpenStreetMap Rota");
                return distancia;
            } else {
                System.out.println("❌ OpenStreetMap retornou distância inválida");
            }
        } catch (Exception e) {
            System.out.println("❌ Cálculo de rota OpenStreetMap falhou: " + e.getMessage());
        }

        // ❌ SE TUDO FALHAR
        System.out.println("\n💔 Todas as tentativas falharam");
        throw new DistanciaNaoEncontradaException(enderecoInicial, enderecoFinal, distanciaService.contarDistanciaSalvas());
    }

    private Double calcularDistanciaGeocodificada(String end1, String end2) {
        try {
            System.out.println("   📍 Obtendo coordenadas do OpenStreetMap...");
            
            Double[] coord1 = mapsService.geocodificarComFallback(end1);
            Double[] coord2 = mapsService.geocodificarComFallback(end2);
            
            if (coord1 == null || coord2 == null) {
                System.out.println("   ❌ Não foi possível obter coordenadas para um ou ambos os endereços");
                return null;
            }
            
            System.out.println("   🎯 Coordenadas encontradas:");
            System.out.println("      Início: [" + coord1[0] + ", " + coord1[1] + "]");
            System.out.println("      Fim:    [" + coord2[0] + ", " + coord2[1] + "]");
            
            double distanciaLinhaReta = calculadoraGeografica.calcularDistanciaHaversine(
                coord1[0], coord1[1], coord2[0], coord2[1]
            );
            
            System.out.println("   📐 Distância em linha reta: " + String.format("%.2f", distanciaLinhaReta) + " km");
            
            double distanciaReal = calculadoraGeografica.calcularDistanciaReal(
                coord1[0], coord1[1], coord2[0], coord2[1], end1, end2
            );
            
            System.out.println("   🛣️  Distância real estimada: " + String.format("%.2f", distanciaReal) + " km");
            
            return distanciaReal;
            
        } catch (Exception e) {
            System.out.println("   ❌ Erro no cálculo geocodificado: " + e.getMessage());
            return null;
        }
    }

    public String getStatusAPIs() {
        return String.format("🗺️ Geocodificação: ✅ | 💾 Banco: %d distâncias | 🗺️ Rota OSM: ✅", 
                distanciaService.contarDistanciaSalvas());
    }
}