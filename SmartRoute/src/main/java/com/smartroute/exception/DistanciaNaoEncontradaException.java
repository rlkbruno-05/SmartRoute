package com.smartroute.exception;

public class DistanciaNaoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DistanciaNaoEncontradaException(String enderecoInicial, String enderecoFinal, long distanciasNoBanco) {
        super(criarMensagem(enderecoInicial, enderecoFinal, distanciasNoBanco));
    }

    private static String criarMensagem(String enderecoInicial, String enderecoFinal, long distanciasNoBanco) {

        StringBuilder mensagem = new StringBuilder();

        mensagem.append("\n❌ IMPOSSÍVEL CALCULAR DISTÂNCIA\n");
        mensagem.append("================================\n\n");

        mensagem.append("📍 ROTA SOLICITADA:\n");
        mensagem.append("   De: ").append(enderecoInicial).append("\n");
        mensagem.append("   Para: ").append(enderecoFinal).append("\n\n");

        mensagem.append("🔧 TENTATIVAS REALIZADAS:\n");
        mensagem.append("   1. 🎯 Geocodificação OpenStreetMap + Cálculo Preciso: ❌ Falhou\n");
        mensagem.append("   2. 💾 Banco de Dados: ");
        if (distanciasNoBanco > 0) {
            mensagem.append("✅ Disponível (").append(distanciasNoBanco).append(" distâncias)\n");
            mensagem.append("      Mas NENHUMA para esta rota específica\n");
        } else {
            mensagem.append("⛔ Vazio (sem distâncias calculadas)\n");
        }
        mensagem.append("   3. 🗺️ Cálculo de rota OpenStreetMap: ❌ Falhou\n");

        mensagem.append("\n🎯 CAUSA DO PROBLEMA:\n");
        mensagem.append("   • OpenStreetMap não conseguiu geocodificar os endereços\n");
        mensagem.append("   • Banco não tem esta rota armazenada\n");
        mensagem.append("   • OpenStreetMap não conseguiu calcular a rota\n");

        mensagem.append("\n💡 SOLUÇÕES SUGERIDAS:\n");
        mensagem.append("   1. Verifique se os endereços estão corretos\n");
        mensagem.append("   2. Tente endereços mais específicos\n");
        mensagem.append("   3. Use cidades da região metropolitana\n");
        mensagem.append("   4. Tente formatos como: 'Rua Nome, Bairro, Cidade'\n");

        mensagem.append("\n📊 CONTEXTO DO SISTEMA:\n");
        mensagem.append("   • Distâncias no banco: ").append(distanciasNoBanco).append("\n");
        mensagem.append("   • Fontes disponíveis: OpenStreetMap Geocodificação, Banco de Dados, OpenStreetMap Rotas\n");

        return mensagem.toString();
    }
}