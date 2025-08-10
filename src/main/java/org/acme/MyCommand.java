package org.acme;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import picocli.CommandLine.Command;

@Command(name = "job", mixinStandardHelpOptions = true)
@ApplicationScoped
public class MyCommand implements QuarkusApplication {
	private static final Logger LOG = LoggerFactory.getLogger(MyCommand.class);

	@Inject
	DataSource dataSource;

	@Inject
	@RestClient
	MyApiClient apiClient;

	@Override
	@Transactional
	public int run(String... args) {
		try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {

			// Criar tabela e inserir dado
			st.execute("CREATE TABLE pessoas(id INT PRIMARY KEY, nome VARCHAR(100))");
			st.execute("INSERT INTO pessoas VALUES (1, 'João')");

			// Ler dados
			ResultSet rs = st.executeQuery("SELECT * FROM pessoas");
			while (rs.next()) {
				var name = rs.getString("nome");
				LOG.info("Pessoa do banco: {}", name);
			}

			// Chamada HTTP
			String result = apiClient.getPost(1);
			LOG.info("Resposta da API: {}", result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
