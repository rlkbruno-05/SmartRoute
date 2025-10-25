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
@Table(name = "corrida")
public class Corrida extends BaseEntity {

	@Column(name = "endereco_inicial", nullable = false)
	private String enderecoInicial;

	@Column(name = "endereco_final", nullable = false)
	private String enderecoFinal;

	@Column(name = "endereco_parada")
	private String enderecoParada;

	@Column(name = "distancia_km")
	private Double distanciaKm;

	@Column(name = "valor_corrida", nullable = false)
	private Double valorCorrida;

	@Column(name = "custo_motorista")
	private Double custoMotorista;

	@Column(name = "lucro_liquido")
	private Double lucroLiquido;

	@Enumerated(EnumType.STRING)
	private Recomendacao recomendacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "motorista_id")
	private Motorista motorista;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "veiculo_id")
	private Veiculo veiculoUtilizado;

	public Corrida() {
	}

	public Corrida(String enderecoInicial, String enderecoFinal, Double valorCorrida) {
		this.enderecoInicial = enderecoInicial;
		this.enderecoFinal = enderecoFinal;
		this.valorCorrida = valorCorrida;
	}

	public String getEnderecoInicial() {
		return enderecoInicial;
	}

	public void setEnderecoInicial(String enderecoInicial) {
		this.enderecoInicial = enderecoInicial;
	}

	public String getEnderecoFinal() {
		return enderecoFinal;
	}

	public void setEnderecoFinal(String enderecoFinal) {
		this.enderecoFinal = enderecoFinal;
	}

	public String getEnderecoParada() {
		return enderecoParada;
	}

	public void setEnderecoParada(String enderecoParada) {
		this.enderecoParada = enderecoParada;
	}

	public Double getDistanciaKm() {
		return distanciaKm;
	}

	public void setDistanciaKm(Double distanciaKm) {
		this.distanciaKm = distanciaKm;
	}

	public Double getValorCorrida() {
		return valorCorrida;
	}

	public void setValorCorrida(Double valorCorrida) {
		this.valorCorrida = valorCorrida;
	}

	public Double getCustoMotorista() {
		return custoMotorista;
	}

	public void setCustoMotorista(Double custoMotorista) {
		this.custoMotorista = custoMotorista;
	}

	public Double getLucroLiquido() {
		return lucroLiquido;
	}

	public void setLucroLiquido(Double lucroLiquido) {
		this.lucroLiquido = lucroLiquido;
	}

	public Recomendacao getRecomendacao() {
		return recomendacao;
	}

	public void setRecomendacao(Recomendacao recomendacao) {
		this.recomendacao = recomendacao;
	}

	public Motorista getMotorista() {
		return motorista;
	}

	public void setMotorista(Motorista motorista) {
		this.motorista = motorista;
	}

	public Veiculo getVeiculoUtilizado() {
		return veiculoUtilizado;
	}

	public void setVeiculoUtilizado(Veiculo veiculoUtilizado) {
		this.veiculoUtilizado = veiculoUtilizado;
	}
}