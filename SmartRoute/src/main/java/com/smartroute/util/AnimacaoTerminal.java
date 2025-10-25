package com.smartroute.util;

public class AnimacaoTerminal {

    public void mostrarBarraCarregamento(String mensagem, int duracaoSegundos) {
        System.out.print("\n🔄 " + mensagem + " [");
        
        int totalBarras = 20;
        int intervalo = (duracaoSegundos * 1000) / totalBarras;
        
        for (int i = 0; i < totalBarras; i++) {
            try {
                Thread.sleep(intervalo);
                System.out.print("█");
                System.out.flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("] ✅ Concluído!\n");
    }

    public void mostrarBarraCarregamentoDinamica(String mensagem) {
        System.out.print("\n🎯 " + mensagem + " ");
        
        String[] spinners = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        long startTime = System.currentTimeMillis();
        
        try {
            int i = 0;
            while (System.currentTimeMillis() - startTime < 3000) {
                System.out.print("\r🎯 " + mensagem + " " + spinners[i % spinners.length]);
                System.out.flush();
                Thread.sleep(100);
                i++;
            }
            System.out.println("\r🎯 " + mensagem + " ✅ Concluído!     ");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Cronometro iniciarCronometro() {
        return new Cronometro();
    }

    public static class Cronometro {
        private long inicio;
        private long fim;

        public Cronometro() {
            this.inicio = System.currentTimeMillis();
        }

        public void parar() {
            this.fim = System.currentTimeMillis();
        }

        public long getTempoDecorrido() {
            return (fim > inicio) ? (fim - inicio) : (System.currentTimeMillis() - inicio);
        }

        public String getTempoFormatado() {
            long tempoMs = getTempoDecorrido();
            if (tempoMs < 1000) {
                return tempoMs + "ms";
            } else {
                return String.format("%.2fs", tempoMs / 1000.0);
            }
        }
    }

    public void mostrarProgresso(int progresso, int total, String mensagem) {
        int percentual = (int) ((progresso / (double) total) * 100);
        int barras = (int) ((progresso / (double) total) * 20);
        
        StringBuilder sb = new StringBuilder();
        sb.append("\r").append(mensagem).append(" [");
        
        for (int i = 0; i < 20; i++) {
            if (i < barras) {
                sb.append("█");
            } else {
                sb.append("░");
            }
        }
        
        sb.append("] ").append(percentual).append("%");
        System.out.print(sb.toString());
        System.out.flush();
        
        if (progresso == total) {
            System.out.println(" ✅");
        }
    }
}