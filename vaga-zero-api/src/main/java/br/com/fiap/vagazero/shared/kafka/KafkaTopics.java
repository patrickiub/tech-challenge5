package br.com.fiap.vagazero.shared.kafka;

public final class KafkaTopics {

    public static final String AGENDAMENTO_CANCELADO = "agendamento.cancelado";
    public static final String VAGA_LIBERADA = "vaga.liberada";
    public static final String RISCO_AVALIADO = "risco.avaliado";
    public static final String CONVITE_ENVIADO = "convite.enviado";
    public static final String CONVITE_ACEITO = "convite.aceito";
    public static final String CONVITE_EXPIRADO = "convite.expirado";
    public static final String VAGA_PREENCHIDA = "vaga.preenchida";

    private KafkaTopics() {
    }
}
