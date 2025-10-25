package com.smartroute.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "distancias")
public class Distancia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String origem;
	private String destino;
	private Double distanciaKm;
	private Double tempoEstimadoMinutos;

	private String fonte;

	@Column(name = "data_calculo")
	private LocalDateTime dataCalculo;

	public Distancia() {
	}

	public Distancia(String origem, String destino, Double distanciaKm, Double tempoEstimadoMinutos) {
		this.origem = origem;
		this.destino = destino;
		this.distanciaKm = distanciaKm;
		this.tempoEstimadoMinutos = tempoEstimadoMinutos;
	}

	public Distancia(String origem, String destino, Double distanciaKm, String fonte) {
		this.origem = origem;
		this.destino = destino;
		this.distanciaKm = distanciaKm;
		this.fonte = fonte;
		this.dataCalculo = LocalDateTime.now();
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Double getDistanciaKm() {
		return distanciaKm;
	}

	public void setDistanciaKm(Double distanciaKm) {
		this.distanciaKm = distanciaKm;
	}

	public Double getTempoEstimadoMinutos() {
		return tempoEstimadoMinutos;
	}

	public void setTempoEstimadoMinutos(Double tempoEstimadoMinutos) {
		this.tempoEstimadoMinutos = tempoEstimadoMinutos;
	}

	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public LocalDateTime getDataCalculo() {
		return dataCalculo;
	}

	public void setDataCalculo(LocalDateTime dataCalculo) {
		this.dataCalculo = dataCalculo;
	}
}