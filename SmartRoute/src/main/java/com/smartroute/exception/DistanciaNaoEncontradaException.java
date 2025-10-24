package com.smartroute.exception;

public class DistanciaNaoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DistanciaNaoEncontradaException(String enderecoInicial, String enderecoFinal, boolean geminiDisponivel,
			int requisicoesGemini, long distanciasNoBanco) {
		super(criarMensagem(enderecoInicial, enderecoFinal, geminiDisponivel, requisicoesGemini, distanciasNoBanco));
	}

	private static String criarMensagem(String enderecoInicial, String enderecoFinal, boolean geminiDisponivel,
			int requisicoesGemini, long distanciasNoBanco) {

		StringBuilder mensagem = new StringBuilder();

		mensagem.append("\n❌ IMPOSSÍVEL CALCULAR DISTÂNCIA\n");
		mensagem.append("================================\n\n");

		mensagem.append("📍 ROTA SOLICITADA:\n");
		mensagem.append("   De: ").append(enderecoInicial).append("\n");
		mensagem.append("   Para: ").append(enderecoFinal).append("\n\n");

		mensagem.append("🔧 STATUS DAS FONTES:\n");

		mensagem.append("   1. 🤖 Gemini AI: ");
		if (geminiDisponivel && requisicoesGemini < 60) {
			mensagem.append("✅ Disponível (").append(requisicoesGemini).append("/60 req)\n");
		} else {
			mensagem.append("⛔ Indisponível (").append(requisicoesGemini).append("/60 req)\n");
		}

		mensagem.append("   2. 🗺️ OpenStreetMap: ❌ Falhou nesta requisição\n");

		mensagem.append("   3. 💾 Banco de Dados: ");
		if (distanciasNoBanco > 0) {
			mensagem.append("✅ Disponível (").append(distanciasNoBanco).append(" distâncias)\n");
			mensagem.append("      Mas NENHUMA para esta rota específica\n");
		} else {
			mensagem.append("⛔ Vazio (sem distâncias calculadas)\n");
		}

		mensagem.append("\n🎯 CAUSA DO PROBLEMA:\n");
		mensagem.append("   • Gemini AI não conseguiu calcular esta rota\n");
		mensagem.append("   • OpenStreetMap também falhou\n");
		mensagem.append("   • Banco não tem esta rota armazenada\n");
		mensagem.append("   • Sistema configurado para NÃO chutar distâncias\n");

		mensagem.append("\n💡 SOLUÇÕES SUGERIDAS:\n");
		mensagem.append("   1. Verifique se os endereços estão corretos\n");
		mensagem.append("   2. Tente endereços mais específicos\n");
		mensagem.append("   3. Aguarde 1 minuto para reset do Gemini\n");
		mensagem.append("   4. Tente rotas dentro de cidades maiores\n");

		mensagem.append("\n📊 CONTEXTO DO SISTEMA:\n");
		mensagem.append("   • Distâncias no banco: ").append(distanciasNoBanco).append("\n");
		mensagem.append("   • Requisições Gemini: ").append(requisicoesGemini).append("/60\n");
		mensagem.append("   • Gemini disponível: ").append(geminiDisponivel ? "Sim" : "Não").append("\n");

		return mensagem.toString();
	}
}