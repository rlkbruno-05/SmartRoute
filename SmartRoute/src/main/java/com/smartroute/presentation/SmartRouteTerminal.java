package com.smartroute.presentation;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.smartroute.model.Corrida;
import com.smartroute.model.Motorista;
import com.smartroute.model.TipoCombustivel;
import com.smartroute.model.Veiculo;
import com.smartroute.service.CalculadoraRota;
import com.smartroute.service.CorridaService;
import com.smartroute.service.MotoristaService;
import com.smartroute.service.VeiculoService;

@Component
public class SmartRouteTerminal {

	private final CalculadoraRota calculadoraRota;

	private final Scanner scanner;
	private final MotoristaService motoristaService;
	private final VeiculoService veiculoService;
	private final CorridaService corridaService;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

	public SmartRouteTerminal(MotoristaService motoristaService, VeiculoService veiculoService,
			CorridaService corridaService, CalculadoraRota calculadoraRota) {
		this.scanner = new Scanner(System.in);
		this.motoristaService = motoristaService;
		this.veiculoService = veiculoService;
		this.corridaService = corridaService;
		this.calculadoraRota = calculadoraRota;
	}

	public void iniciar() {
		System.out.println("\n🚗 SMARTROUTE - Sistema de Análise de Corridas 🚗");

		while (true) {
			exibirMenuPrincipal();
			int opcao = lerOpcao();

			switch (opcao) {
			case 1 -> cadastrarMotorista();
			case 2 -> listarMotoristas();
			case 3 -> excluirMotorista();
			case 4 -> cadastrarVeiculo();
			case 5 -> listarVeiculos();
			case 6 -> excluirVeiculo();
			case 7 -> analisarCorrida();
			case 8 -> listarCorridas();
			case 9 -> excluirCorrida();
			case 10 -> listarCorridasRecomendadas();
			case 0 -> {
				System.out.println("Saindo do sistema...");
				return;
			}
			default -> System.out.println("❌ Opção inválida!");
			}
		}
	}

	private void exibirMenuPrincipal() {
		System.out.println("\n=== MENU PRINCIPAL ===");
		System.out.println("1. 📝 Cadastrar Motorista");
		System.out.println("2. 👥 Listar Motoristas");
		System.out.println("3. 🗑️ Excluir Motorista");
		System.out.println("4. 🚙 Cadastrar Veículo");
		System.out.println("5. 🚗 Listar Veículos");
		System.out.println("6. 🗑️ Excluir Veículo");
		System.out.println("7. 🧮 Analisar Corrida");
		System.out.println("8. 📊 Listar Corridas");
		System.out.println("9. 🗑️ Excluir Corrida");
		System.out.println("10. ✅ Corridas Recomendadas");
		System.out.println("0. 🚪 Sair");
		System.out.print("Escolha uma opção: ");
	}

	private int lerOpcao() {
		try {
			return scanner.nextInt();
		} catch (Exception e) {
			scanner.nextLine();
			System.out.println("❌ Digite apenas números!");
			return -1;
		} finally {
			scanner.nextLine();
		}
	}

	private void cadastrarMotorista() {
		System.out.println("\n--- CADASTRO DE MOTORISTA ---");

		System.out.print("Nome: ");
		String nome = scanner.nextLine().trim();
		if (nome.isEmpty()) {
			System.out.println("❌ Nome não pode estar vazio!");
			return;
		}

		System.out.print("CPF (apenas números): ");
		String cpf = scanner.nextLine().trim().replaceAll("[^0-9]", "");
		if (cpf.length() != 11) {
			System.out.println("❌ CPF deve ter 11 dígitos!");
			return;
		}

		if (motoristaService.existePorCpf(cpf)) {
			System.out.println("❌ CPF já cadastrado!");
			return;
		}

		System.out.print("Email: ");
		String email = scanner.nextLine().trim().toLowerCase();
		if (!validarEmail(email)) {
			System.out.println("❌ Email inválido!");
			return;
		}

		if (motoristaService.existePorEmail(email)) {
			System.out.println("❌ Email já cadastrado!");
			return;
		}

		System.out.print("Idade: ");
		Integer idade = lerInteiro();
		if (idade == null || idade < 18 || idade > 100) {
			System.out.println("❌ Você deve ser maior de 18 anos!");
			return;
		}

		System.out.print("Telefone (qualquer formato com 11 dígitos): ");
		String telefoneInput = scanner.nextLine().trim();
		String telefoneFormatado = validarEFormatarTelefone(telefoneInput);
		if (telefoneFormatado == null) {
			return;
		}

		try {
			Motorista motorista = new Motorista(nome, cpf, email, idade, telefoneFormatado);
			Motorista salvo = motoristaService.salvar(motorista);
			System.out.println("✅ Motorista cadastrado com ID: " + salvo.getId());
			System.out.println("📞 Telefone cadastrado: " + telefoneFormatado);
		} catch (Exception e) {
			System.out.println("❌ Erro ao cadastrar motorista: " + e.getMessage());
		}
	}

	private void listarMotoristas() {
		System.out.println("\n--- MOTORISTAS CADASTRADOS ---");
		List<Motorista> motoristas = motoristaService.listarTodos();

		if (motoristas.isEmpty()) {
			System.out.println("Nenhum motorista cadastrado.");
			return;
		}

		for (Motorista m : motoristas) {
			System.out.printf("ID: %d | %s | %s | %s\n", m.getId(), m.getNome(), m.getEmail(), m.getTelefone());
		}
	}

	private void excluirMotorista() {
		System.out.println("\n--- EXCLUIR MOTORISTA ---");
		listarMotoristas();

		System.out.print("ID do motorista a excluir: ");
		Long id = lerLong();
		if (id == null)
			return;

		Optional<Motorista> motoristaOpt = motoristaService.buscarPorId(id);
		if (motoristaOpt.isEmpty()) {
			System.out.println("❌ Motorista não encontrado!");
			return;
		}

		System.out.print("Confirma exclusão do motorista " + motoristaOpt.get().getNome() + "? (s/n): ");
		String confirmacao = scanner.nextLine().trim().toLowerCase();

		if (confirmacao.equals("s")) {
			try {
				motoristaService.deletar(id);
				System.out.println("✅ Motorista excluído com sucesso!");
			} catch (Exception e) {
				System.out.println("❌ Erro ao excluir motorista: " + e.getMessage());
			}
		} else if (confirmacao.equals("n")) {
			System.out.println("❌ Exclusão cancelada.");
		}
	}

	private void cadastrarVeiculo() {
		System.out.println("\n--- CADASTRO DE VEÍCULO ---");

		listarMotoristas();
		System.out.print("ID do Motorista: ");
		Long motoristaId = lerLong();
		if (motoristaId == null)
			return;

		Optional<Motorista> motoristaOpt = motoristaService.buscarPorId(motoristaId);
		if (motoristaOpt.isEmpty()) {
			System.out.println("❌ Motorista não encontrado!");
			return;
		}

		System.out.print("Modelo: ");
		String modelo = scanner.nextLine().trim();
		if (modelo.isEmpty()) {
			System.out.println("❌ Modelo não pode estar vazio!");
			return;
		}

		System.out.print("Marca: ");
		String marca = scanner.nextLine().trim();
		if (marca.isEmpty()) {
			System.out.println("❌ Marca não pode estar vazio!");
			return;
		}

		System.out.print("Ano: ");
		Integer ano = lerInteiro();
		if (ano == null || ano < 1900 || ano > 2030) {
			System.out.println("❌ Ano deve ser entre 1900 e 2030!");
			return;
		}

		System.out.print("Placa: ");
		String placa = scanner.nextLine().trim().toUpperCase();
		if (placa.isEmpty()) {
			System.out.println("❌ Placa não pode estar vazia!");
			return;
		}

		if (veiculoService.existePorPlaca(placa)) {
			System.out.println("❌ Placa já cadastrada!");
			return;
		}

		System.out.println("Tipo de Veículo:");
		System.out.println("1. Gasolina");
		System.out.println("2. Etanol");
		System.out.println("3. Diesel");
		System.out.println("4. Flex");
		System.out.println("5. Elétrico");
		System.out.println("6. Híbrido");
		System.out.print("Escolha: ");
		Integer tipoOpcao = lerInteiro();
		if (tipoOpcao == null || tipoOpcao < 1 || tipoOpcao > 6) {
			System.out.println("❌ Opção inválida!");
			return;
		}

		TipoCombustivel tipoCombustivel = switch (tipoOpcao) {
		case 1 -> TipoCombustivel.GASOLINA;
		case 2 -> TipoCombustivel.ETANOL;
		case 3 -> TipoCombustivel.DIESEL;
		case 4 -> TipoCombustivel.FLEX;
		case 5 -> TipoCombustivel.ELETRICO;
		case 6 -> TipoCombustivel.HIBRIDO;
		default -> TipoCombustivel.FLEX;
		};

		Double consumo = null;
		Double capacidadeBateria = null;

		if (tipoCombustivel == TipoCombustivel.ELETRICO || tipoCombustivel == TipoCombustivel.HIBRIDO) {
			System.out.print("Consumo médio (km/kWh): ");
			consumo = lerDouble();
			if (consumo == null || consumo <= 0) {
				System.out.println("❌ Consumo deve ser maior que zero!");
				return;
			}

			if (tipoCombustivel == TipoCombustivel.ELETRICO) {
				System.out.print("Capacidade da bateria (kWh): ");
				capacidadeBateria = lerDouble();
				if (capacidadeBateria == null || capacidadeBateria <= 0) {
					System.out.println("❌ Capacidade da bateria deve ser maior que zero!");
					return;
				}
			}
		} else {
			System.out.print("Consumo médio (km/l): ");
			consumo = lerDouble();
			if (consumo == null || consumo <= 0) {
				System.out.println("❌ Consumo deve ser maior que zero!");
				return;
			}
		}

		try {
			Veiculo veiculo;
			if (tipoCombustivel == TipoCombustivel.ELETRICO) {
				veiculo = new Veiculo(modelo, marca, ano, placa, consumo, capacidadeBateria);
			} else {
				veiculo = new Veiculo(modelo, marca, ano, placa, tipoCombustivel, consumo);
			}

			veiculo.setMotorista(motoristaOpt.get());

			Veiculo salvo = veiculoService.salvar(veiculo);
			System.out.println("✅ Veículo cadastrado com ID: " + salvo.getId());
		} catch (Exception e) {
			System.out.println("❌ Erro ao cadastrar veículo: " + e.getMessage());
		}
	}

	private void listarVeiculos() {
		System.out.println("\n--- VEÍCULOS CADASTRADOS ---");
		List<Veiculo> veiculos = veiculoService.listarTodos();

		if (veiculos.isEmpty()) {
			System.out.println("Nenhum veículo cadastrado.");
			return;
		}

		for (Veiculo v : veiculos) {
			String tipoIcon = v.isEletrico() ? "🔋" : "⛽";
			String consumoInfo = v.isEletrico() ? String.format("%.1f km/kWh", v.getConsumoMedio())
					: String.format("%.1f km/l", v.getConsumoMedio());

			System.out.printf("%s ID: %d | %s %s | %s | %s | %s\n", tipoIcon, v.getId(), v.getMarca(), v.getModelo(),
					v.getPlaca(), v.getTipoCombustivel(), consumoInfo);
		}
	}

	private void excluirVeiculo() {
		System.out.println("\n--- EXCLUIR VEÍCULO ---");
		listarVeiculos();

		System.out.print("ID do veículo a excluir: ");
		Long id = lerLong();
		if (id == null)
			return;

		Optional<Veiculo> veiculoOpt = veiculoService.buscarPorId(id);
		if (veiculoOpt.isEmpty()) {
			System.out.println("❌ Veículo não encontrado!");
			return;
		}

		System.out.print("Confirma exclusão do veículo " + veiculoOpt.get().getPlaca() + "? (s/n): ");
		String confirmacao = scanner.nextLine().trim().toLowerCase();

		if (confirmacao.equals("s")) {
			try {
				veiculoService.deletar(id);
				System.out.println("✅ Veículo excluído com sucesso!");
			} catch (Exception e) {
				System.out.println("❌ Erro ao excluir veículo: " + e.getMessage());
			}
		} else {
			System.out.println("❌ Exclusão cancelada.");
		}
	}

	private void analisarCorrida() {
		System.out.println("\n--- ANÁLISE DE CORRIDA ---");

		listarMotoristas();
		System.out.print("ID do Motorista: ");
		Long motoristaId = lerLong();
		if (motoristaId == null)
			return;

		Optional<Motorista> motoristaOpt = motoristaService.buscarPorId(motoristaId);
		if (motoristaOpt.isEmpty()) {
			System.out.println("❌ Motorista não encontrado!");
			return;
		}

		List<Veiculo> veiculos = veiculoService.buscarPorMotorista(motoristaId);
		if (veiculos.isEmpty()) {
			System.out.println("❌ Nenhum veículo cadastrado para este motorista!");
			return;
		}

		System.out.println("Veículos disponíveis:");
		for (int i = 0; i < veiculos.size(); i++) {
			Veiculo v = veiculos.get(i);
			String consumoInfo = v.isEletrico() ? String.format("%.1f km/kWh", v.getConsumoMedio())
					: String.format("%.1f km/l", v.getConsumoMedio());

			System.out.printf("%d. %s %s (%s) - %s\n", i + 1, v.getMarca(), v.getModelo(), v.getTipoCombustivel(),
					consumoInfo);
		}

		System.out.print("Escolha o veículo (número): ");
		Integer veiculoIndex = lerInteiro();
		if (veiculoIndex == null)
			return;

		veiculoIndex = veiculoIndex - 1;

		if (veiculoIndex < 0 || veiculoIndex >= veiculos.size()) {
			System.out.println("❌ Veículo inválido!");
			return;
		}

		Veiculo veiculoSelecionado = veiculos.get(veiculoIndex);

		System.out.print("Endereço Inicial: ");
		String inicio = scanner.nextLine().trim();
		if (inicio.isEmpty()) {
			System.out.println("❌ Endereço inicial não pode estar vazio!");
			return;
		}

		System.out.print("Endereço Final: ");
		String fim = scanner.nextLine().trim();
		if (fim.isEmpty()) {
			System.out.println("❌ Endereço final não pode estar vazio!");
			return;
		}

		System.out.print("Tem parada? (s/n): ");
		String temParada = scanner.nextLine().trim().toLowerCase();

		String parada = null;
		if (temParada.equals("s")) {
			System.out.print("Endereço da Parada: ");
			parada = scanner.nextLine().trim();
			if (parada.isEmpty()) {
				System.out.println("❌ Endereço da parada não pode estar vazio!");
				return;
			}
		}

		System.out.print("Valor da Corrida (R$): ");
		Double valor = lerDouble();
		if (valor == null || valor <= 0) {
			System.out.println("❌ Valor da corrida deve ser maior que zero!");
			return;
		}

		try {
			Corrida corrida = new Corrida(inicio, fim, valor);
			corrida.setEnderecoParada(parada);
			corrida.setMotorista(motoristaOpt.get());

			Corrida corridaAnalisada = corridaService.analisarESalvar(corrida, veiculoSelecionado);
			exibirResultadoAnalise(corridaAnalisada);
		} catch (Exception e) {
			System.out.println("❌ Erro ao analisar corrida: " + e.getMessage());
		}
	}

	private void exibirResultadoAnalise(Corrida corrida) {
		System.out.println("\n--- RESULTADO DA ANÁLISE ---");
		System.out.printf("📍 Distância: %.2f km\n", corrida.getDistanciaKm());
		System.out.printf("💡 Custo do motorista: R$ %.2f\n", corrida.getCustoMotorista());
		System.out.printf("💰 Valor da corrida: R$ %.2f\n", corrida.getValorCorrida());
		System.out.printf("💵 Lucro líquido: R$ %.2f\n", corrida.getLucroLiquido());

		double margem = ((corrida.getValorCorrida() - corrida.getCustoMotorista()) / corrida.getValorCorrida()) * 100;
		System.out.printf("📈 Margem de lucro: %.1f%%\n", margem);

		Veiculo veiculo = corrida.getVeiculoUtilizado();
		if (veiculo.isEletrico()) {
			double kwhConsumidos = corrida.getDistanciaKm() / veiculo.getConsumoMedio();
			System.out.printf("🔋 Energia consumida: %.1f kWh\n", kwhConsumidos);
		} else {
			double litrosConsumidos = corrida.getDistanciaKm() / veiculo.getConsumoMedio();
			System.out.printf("⛽ Combustível consumido: %.1f litros\n", litrosConsumidos);
		}

		System.out.print("🎯 Recomendação: ");
		switch (corrida.getRecomendacao()) {
		case RECOMENDADA -> System.out.println("✅ RECOMENDADA - Boa lucratividade!");
		case NEUTRA -> System.out.println("🟡 NEUTRA - Pode aceitar, mas há opções melhores");
		case NAO_RECOMENDADA -> System.out.println("❌ NÃO RECOMENDADA - Baixa lucratividade");
		}
	}

	private void listarCorridas() {
		System.out.println("\n--- TODAS AS CORRIDAS ---");
		List<Corrida> corridas = corridaService.listarTodos();

		if (corridas.isEmpty()) {
			System.out.println("Nenhuma corrida analisada.");
			return;
		}

		for (Corrida c : corridas) {
			String status = switch (c.getRecomendacao()) {
			case RECOMENDADA -> "✅";
			case NEUTRA -> "🟡";
			case NAO_RECOMENDADA -> "❌";
			};

			System.out.printf("%s ID: %d | %s → %s | R$ %.2f | Lucro: R$ %.2f\n", status, c.getId(),
					c.getEnderecoInicial(), c.getEnderecoFinal(), c.getValorCorrida(), c.getLucroLiquido());
		}
	}

	private void excluirCorrida() {
		System.out.println("\n--- EXCLUIR CORRIDA ---");
		listarCorridas();

		System.out.print("ID da corrida a excluir: ");
		Long id = lerLong();
		if (id == null)
			return;

		Optional<Corrida> corridaOpt = corridaService.buscarPorId(id);
		if (corridaOpt.isEmpty()) {
			System.out.println("❌ Corrida não encontrada!");
			return;
		}

		System.out.print("Confirma exclusão da corrida ID " + id + "? (s/n): ");
		String confirmacao = scanner.nextLine().trim().toLowerCase();

		if (confirmacao.equals("s")) {
			try {
				corridaService.deletar(id);
				System.out.println("✅ Corrida excluída com sucesso!");
			} catch (Exception e) {
				System.out.println("❌ Erro ao excluir corrida: " + e.getMessage());
			}
		} else {
			System.out.println("❌ Exclusão cancelada.");
		}
	}

	private void listarCorridasRecomendadas() {
		System.out.println("\n--- CORRIDAS RECOMENDADAS ---");
		List<Corrida> corridas = corridaService.buscarRecomendadas();

		if (corridas.isEmpty()) {
			System.out.println("Nenhuma corrida recomendada no momento.");
			return;
		}

		for (Corrida c : corridas) {
			System.out.printf("✅ ID: %d | %s → %s | R$ %.2f | Lucro: R$ %.2f | Dist: %.1f km\n", c.getId(),
					c.getEnderecoInicial(), c.getEnderecoFinal(), c.getValorCorrida(), c.getLucroLiquido(),
					c.getDistanciaKm());
		}
	}

	private boolean validarEmail(String email) {
		return EMAIL_PATTERN.matcher(email).matches() && email.length() <= 100;
	}

	private String validarEFormatarTelefone(String telefone) {
		if (telefone == null || telefone.trim().isEmpty()) {
			System.out.println("❌ Telefone não pode estar vazio!");
			return null;
		}

		String apenasNumeros = telefone.replaceAll("[^0-9]", "");

		if (apenasNumeros.length() != 11) {
			System.out.println("❌ Telefone deve ter 11 dígitos!");
			System.out.println("📞 Exemplo: (11) 91234-5678");
			return null;
		}

		if (apenasNumeros.charAt(2) != '9') {
			System.out.println("❌ Telefone deve ser celular (iniciar com 9 após DDD)!");
			return null;
		}

		// (xx) 9xxxx-xxxx
		return String.format("(%s) %s-%s", apenasNumeros.substring(0, 2), apenasNumeros.substring(2, 7),
				apenasNumeros.substring(7));
	}

	private Integer lerInteiro() {
		try {
			int valor = scanner.nextInt();
			scanner.nextLine();
			return valor;
		} catch (Exception e) {
			scanner.nextLine();
			System.out.println("❌ Digite um número válido!");
			return null;
		}
	}

	private Long lerLong() {
		try {
			long valor = scanner.nextLong();
			scanner.nextLine();
			return valor;
		} catch (Exception e) {
			scanner.nextLine();
			System.out.println("❌ Digite um número válido!");
			return null;
		}
	}

	private Double lerDouble() {
		try {
			double valor = scanner.nextDouble();
			scanner.nextLine();
			return valor;
		} catch (Exception e) {
			scanner.nextLine();
			System.out.println("❌ Digite um número válido!");
			return null;
		}
	}

	public CalculadoraRota getCalculadoraRota() {
		return calculadoraRota;
	}
}