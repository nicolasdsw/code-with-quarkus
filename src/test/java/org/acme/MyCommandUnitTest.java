package org.acme;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class MyCommandUnitTest {

    private MyCommand command;

    private DataSource dataSource;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private MyApiClient apiClient;

    @BeforeEach
    void setup() throws Exception {
        // Mocks
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        apiClient = mock(MyApiClient.class);

        // Comportamento do mock:
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(true);
        when(apiClient.getPost(1)).thenReturn("{\"id\":1,\"title\":\"Teste mockado\"}");

        // Quando executar a query SELECT, retorna o mock ResultSet
        when(statement.executeQuery(ArgumentMatchers.anyString())).thenReturn(resultSet);

        // Configura o ResultSet para simular 1 registro com nome "João"
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("nome")).thenReturn("João");

        // Instancia comando e injeta mock
        command = new MyCommand();
        command.dataSource = dataSource;
        command.apiClient = apiClient;
    }

    @Test
    void testRun() throws Exception {
        command.run();

        // Verificações básicas que os métodos foram chamados
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).execute("CREATE TABLE pessoas(id INT PRIMARY KEY, nome VARCHAR(100))");
        verify(statement, times(1)).execute("INSERT INTO pessoas VALUES (1, 'João')");
        verify(statement, times(1)).executeQuery("SELECT * FROM pessoas");

        // Verifica que resultSet.next() foi chamado ao menos uma vez
        verify(resultSet, atLeastOnce()).next();
        verify(apiClient, times(1)).getPost(1);

        // Você pode adicionar mais asserts aqui se quiser
    }
}
