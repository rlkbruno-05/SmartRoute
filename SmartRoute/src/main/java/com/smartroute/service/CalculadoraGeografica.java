package com.smartroute.service;

import org.springframework.stereotype.Service;

@Service
public class CalculadoraGeografica {

    private static final double RAIO_TERRA_KM = 6371.0;
    
    	//Fórmula de Haversine 
    
    public double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        	
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RAIO_TERRA_KM * c;
    }

    public double calcularDistanciaReal(double lat1, double lon1, double lat2, double lon2, String end1, String end2) {
    	
        double linhaReta = calcularDistanciaHaversine(lat1, lon1, lat2, lon2);
        
        double fatorAjuste = calcularFatorAjuste(end1, end2, linhaReta);
        
        return linhaReta * fatorAjuste;
    }

    private double calcularFatorAjuste(String end1, String end2, double distanciaLinhaReta) {
        if (distanciaLinhaReta < 2) {
            return 1.3; // rotas muito curtas têm mais desvios
        } else if (distanciaLinhaReta < 10) {
            return 1.25; // rotas urbanas médias
        } else {
            return 1.2; // rotas mais longas tendem a ser mais diretas
        }
    }
}