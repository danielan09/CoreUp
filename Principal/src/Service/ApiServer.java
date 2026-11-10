package Service;

import Repository.ConnexionFactory;
import Repository.ExercicioJdbcRepositorio;
import Repository.UsuarioJdbcRepositorio;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Exercicio;
import model.ExercicioCardio;
import model.ExercicioForca;
import model.TreinoPredefinido;
import model.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiServer {
    private static final int PORT = 8080;
    private final UsuarioJdbcRepositorio usuarioRepo = new UsuarioJdbcRepositorio();
    private final ExercicioJdbcRepositorio exercicioRepo = new ExercicioJdbcRepositorio();
    private final Path frontendRoot = Paths.get(System.getProperty("user.dir"), "frontend").toAbsolutePath().normalize();

    public void iniciar() throws IOException, java.sql.SQLException {
        ConnexionFactory.criarTabelas();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor CoreUp iniciado em http://localhost:8080");
    }

    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equals(method) && "/api/usuarios".equals(path)) {
                    listarUsuarios(exchange);
                    return;
                }

                if ("GET".equals(method) && "/api/treinos/predefinidos".equals(path)) {
                    listarTreinosPredefinidos(exchange);
                    return;
                }

                if ("POST".equals(method) && "/api/usuarios".equals(path)) {
                    criarUsuario(exchange);
                    return;
                }

                if ("DELETE".equals(method) && path.matches("^/api/usuarios/\\d+$")) {
                    removerUsuario(exchange);
                    return;
                }

                if ("GET".equals(method) && path.matches("^/api/usuarios/\\d+/exercicios$")) {
                    listarExerciciosDoUsuario(exchange);
                    return;
                }

                if ("POST".equals(method) && path.matches("^/api/usuarios/\\d+/exercicios$")) {
                    adicionarExercicio(exchange);
                    return;
                }

                if ("DELETE".equals(method) && path.matches("^/api/exercicios/\\d+$")) {
                    removerExercicio(exchange);
                    return;
                }

                sendJson(exchange, 404, Map.of("message", "Endpoint não encontrado."));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, Map.of("message", e.getMessage()));
            } catch (Exception e) {
                sendJson(exchange, 500, Map.of("message", "Erro interno do servidor: " + e.getMessage()));
            }
        }

        private void listarUsuarios(HttpExchange exchange) throws IOException {
            List<Usuario> usuarios = usuarioRepo.listarTodos();
            List<Map<String, Object>> payload = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                payload.add(usuarioToMap(usuario));
            }
            sendJson(exchange, 200, payload);
        }

        private void listarTreinosPredefinidos(HttpExchange exchange) throws IOException {
            List<Map<String, Object>> payload = new ArrayList<>();
            for (TreinoPredefinido treino : TreinoPredefinido.listarPlanos()) {
                Map<String, Object> plano = new HashMap<>();
                plano.put("nome", treino.getNome());
                plano.put("exercicios", treino.getExercicios().stream()
                        .map(ApiServer::exercicioToMap)
                        .toList());
                payload.add(plano);
            }
            sendJson(exchange, 200, payload);
        }

        private void criarUsuario(HttpExchange exchange) throws IOException {
            Map<String, String> body = parseJsonBody(exchange);
            String nome = require(body, "nome");
            int idade = Integer.parseInt(require(body, "idade"));
            double peso = Double.parseDouble(require(body, "peso"));
            double altura = Double.parseDouble(require(body, "altura"));
            String objetivo = require(body, "objetivo");

            Usuario usuario = new Usuario(0, nome, idade, peso, altura, objetivo);
            usuarioRepo.salvar(usuario);
            sendJson(exchange, 201, usuarioToMap(usuario));
        }

        private void removerUsuario(HttpExchange exchange) throws IOException {
            int usuarioId = parseIdFromPath(exchange.getRequestURI().getPath(), "/api/usuarios/");
            if (!usuarioRepo.remover(usuarioId)) {
                sendJson(exchange, 404, Map.of("message", "Aluno não encontrado."));
                return;
            }
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
        }

        private void listarExerciciosDoUsuario(HttpExchange exchange) throws IOException {
            int usuarioId = parseIdFromPath(exchange.getRequestURI().getPath(), "/api/usuarios/");
            List<Exercicio> exercicios = exercicioRepo.listarPorUsuario(usuarioId);
            List<Map<String, Object>> payload = new ArrayList<>();
            for (Exercicio exercicio : exercicios) {
                payload.add(exercicioToMap(exercicio));
            }
            sendJson(exchange, 200, payload);
        }

        private void adicionarExercicio(HttpExchange exchange) throws IOException {
            int usuarioId = parseIdFromPath(exchange.getRequestURI().getPath(), "/api/usuarios/");
            Map<String, String> body = parseJsonBody(exchange);
            String tipo = require(body, "tipo").toUpperCase();
            String nome = require(body, "nome");

            Exercicio exercicio;
            if ("FORCA".equals(tipo)) {
                String grupoMuscular = require(body, "grupoMuscular");
                int series = Integer.parseInt(require(body, "series"));
                int repeticoes = Integer.parseInt(require(body, "repeticoes"));
                exercicio = new ExercicioForca(nome, grupoMuscular, series, repeticoes);
            } else if ("CARDIO".equals(tipo)) {
                int duracaoMinutos = Integer.parseInt(require(body, "duracaoMinutos"));
                exercicio = new ExercicioCardio(nome, duracaoMinutos);
            } else {
                throw new IllegalArgumentException("Tipo de exercício inválido. Use FORCA ou CARDIO.");
            }

            exercicioRepo.salvar(exercicio, usuarioId);
            sendJson(exchange, 201, exercicioToMap(exercicio));
        }

        private void removerExercicio(HttpExchange exchange) throws IOException {
            int exercicioId = parseIdFromPath(exchange.getRequestURI().getPath(), "/api/exercicios/");
            exercicioRepo.remover(exercicioId);
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path)) {
                path = "/index.html";
            }

            Path file = Paths.get(System.getProperty("user.dir"), "frontend").resolve(path.substring(1)).normalize();
            if (!file.startsWith(Paths.get(System.getProperty("user.dir"), "frontend").toAbsolutePath().normalize())) {
                sendText(exchange, 403, "Acesso negado.");
                return;
            }

            if (Files.exists(file) && Files.isRegularFile(file)) {
                byte[] bytes = Files.readAllBytes(file);
                String contentType = contentTypeFor(file);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return;
            }

            sendText(exchange, 404, "Arquivo não encontrado.");
        }
    }

    private static void sendText(HttpExchange exchange, int status, String text) throws IOException {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, payload.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private static void sendJson(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] response = serializeJson(payload).getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private static String serializeJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return quoteJson((String) value);
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) sb.append(',');
                first = false;
                sb.append(quoteJson(String.valueOf(entry.getKey()))).append(':').append(serializeJson(entry.getValue()));
            }
            sb.append('}');
            return sb.toString();
        }
        if (value instanceof Iterable) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) sb.append(',');
                first = false;
                sb.append(serializeJson(item));
            }
            sb.append(']');
            return sb.toString();
        }
        if (value instanceof Character) return quoteJson(value.toString());
        return quoteJson(value.toString());
    }

    private static String quoteJson(String value) {
        return '"' + value
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\"", "\\\"")
                + '"';
    }

    private static Map<String, String> parseJsonBody(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8).trim();
        if (body.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();
        Matcher matcher = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*(\\\"((?:\\\\.|[^\\\\\"])*)\\\"|\\d+(?:\\.\\d+)?|true|false|null)").matcher(body);
        while (matcher.find()) {
            String key = matcher.group(1);
            String rawValue = matcher.group(2);
            String value = rawValue;
            if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                value = rawValue.substring(1, rawValue.length() - 1)
                        .replace("\\\"", "\"")
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\t", "\t");
            }
            result.put(key, value);
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Corpo JSON inválido.");
        }

        return result;
    }

    private static String require(Map<String, String> values, String field) {
        String value = values.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatório ausente: " + field);
        }
        return value;
    }

    private static int parseIdFromPath(String path, String prefix) {
        if (!path.startsWith(prefix)) {
            throw new IllegalArgumentException("ID inválido.");
        }

        String suffix = path.substring(prefix.length());
        String[] parts = suffix.split("/");
        if (parts.length == 0 || parts[0].isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido.");
        }
    }

    private static Map<String, Object> usuarioToMap(Usuario usuario) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", usuario.getId());
        map.put("nome", usuario.getNome());
        map.put("idade", usuario.getIdade());
        map.put("peso", usuario.getPeso());
        map.put("altura", usuario.getAltura());
        map.put("objetivo", usuario.getObjetivo());
        return map;
    }

    private static Map<String, Object> exercicioToMap(Exercicio exercicio) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", exercicio.getId());
        map.put("tipo", exercicio instanceof ExercicioForca ? "FORCA" : "CARDIO");
        map.put("nome", exercicio.getNome());
        if (exercicio instanceof ExercicioForca forca) {
            map.put("grupoMuscular", forca.getGrupoMuscular());
            map.put("series", forca.getSeries());
            map.put("repeticoes", forca.getRepeticoes());
        }
        if (exercicio instanceof ExercicioCardio cardio) {
            map.put("duracaoMinutos", cardio.getDuracaoMinutos());
        }
        return map;
    }

    private static String contentTypeFor(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".html")) return "text/html; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (name.endsWith(".json")) return "application/json; charset=utf-8";
        if (name.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
