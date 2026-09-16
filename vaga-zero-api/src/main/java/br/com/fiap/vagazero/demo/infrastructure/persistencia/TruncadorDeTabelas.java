package br.com.fiap.vagazero.demo.infrastructure.persistencia;

import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Limpa o banco para o /demo/reset descobrindo as tabelas na hora, em vez de
 * uma lista fixa mantida a mao em cada modulo. Uma tabela nova criada por
 * uma migration futura entra automaticamente no truncamento, sem exigir
 * alteracao de codigo aqui.
 *
 * TRUNCATE ... CASCADE dispensa se preocupar com ordem de FK entre as
 * tabelas truncadas (Postgres resolve as dependencias sozinho) e, por pedir
 * um lock exclusivo de tabela antes de agir, tambem serializa o reset contra
 * qualquer scheduler (job de risco, reenvio de aviso) que esteja lendo e
 * gravando nessas tabelas no mesmo instante - a transacao do scheduler
 * termina primeiro (e falha sozinha, sem afetar o reset) ou o reset trunca
 * primeiro e o scheduler encontra as linhas ja limpas.
 */
@Component
public class TruncadorDeTabelas {

    private final JdbcTemplate jdbcTemplate;

    public TruncadorDeTabelas(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void truncarTudoExceto(Set<String> tabelasPreservadas) {
        List<String> tabelas = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables "
                        + "WHERE table_schema = current_schema() AND table_type = 'BASE TABLE'",
                String.class);

        List<String> alvo = tabelas.stream()
                .filter(tabela -> !tabelasPreservadas.contains(tabela))
                .toList();

        if (alvo.isEmpty()) {
            return;
        }

        jdbcTemplate.execute("TRUNCATE TABLE " + String.join(", ", alvo) + " RESTART IDENTITY CASCADE");
    }
}
