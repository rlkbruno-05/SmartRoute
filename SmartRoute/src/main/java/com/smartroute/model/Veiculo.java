package com.smartroute.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "veiculo")
public class Veiculo extends BaseEntity {

	@Column(nullable = false)
	private String modelo;

	@Column(nullable = false)
	private String marca;

	private Integer ano;

	@Column(unique = true, nullable = false)
	private String placa;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_combustivel", nullable = false)
	private TipoCombustivel tipoCombustivel;

	@Column(name = "consumo_medio")
	private Double consumoMedio;

	@Column(name = "capacidade_bateria")
	private Double capacidadeBateria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "motorista_id")
	private Motorista motorista;

	public Veiculo() {
	}

	public Veiculo(String modelo, String marca, Integer ano, String placa, TipoCombustivel tipoCombustivel,
			Double consumoMedio) {
		this.modelo = modelo;
		this.marca = marca;
		this.ano = ano;
		this.placa = placa;
		this.tipoCombustivel = tipoCombustivel;
		this.consumoMedio = consumoMedio;
	}

	public Veiculo(String modelo, String marca, Integer ano, String placa, Double consumoMedio,
			Double capacidadeBateria) {
		this.modelo = modelo;
		this.marca = marca;
		this.ano = ano;
		this.placa = placa;
		this.tipoCombustivel = TipoCombustivel.ELETRICO;
		this.consumoMedio = consumoMedio;
		this.capacidadeBateria = capacidadeBateria;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public TipoCombustivel getTipoCombustivel() {
		return tipoCombustivel;
	}

	public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
		this.tipoCombustivel = tipoCombustivel;
	}

	public Double getConsumoMedio() {
		return consumoMedio;
	}

	public void setConsumoMedio(Double consumoMedio) {
		this.consumoMedio = consumoMedio;
	}

	public Double getCapacidadeBateria() {
		return capacidadeBateria;
	}

	public void setCapacidadeBateria(Double capacidadeBateria) {
		this.capacidadeBateria = capacidadeBateria;
	}

	public Motorista getMotorista() {
		return motorista;
	}

	public void setMotorista(Motorista motorista) {
		this.motorista = motorista;
	}

	public boolean isEletrico() {
		return tipoCombustivel == TipoCombustivel.ELETRICO || tipoCombustivel == TipoCombustivel.HIBRIDO;
	}
}