package com.smartroute.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "motorista")
public class Motorista extends BaseEntity {

	@Column(nullable = false)
	private String nome;

	@Column(unique = true, nullable = false)
	private String cpf;

	@Column(unique = true, nullable = false)
	private String email;

	private Integer idade;
	private String telefone;

	@OneToMany(mappedBy = "motorista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Veiculo> veiculos = new ArrayList<>();

	@OneToMany(mappedBy = "motorista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Corrida> corridas = new ArrayList<>();

	public Motorista() {
	}

	public Motorista(String nome, String cpf, String email, Integer idade, String telefone) {
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.idade = idade;
		this.telefone = telefone;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public List<Veiculo> getVeiculos() {
		return veiculos;
	}

	public void setVeiculos(List<Veiculo> veiculos) {
		this.veiculos = veiculos;
	}

	public List<Corrida> getCorridas() {
		return corridas;
	}

	public void setCorridas(List<Corrida> corridas) {
		this.corridas = corridas;
	}
}