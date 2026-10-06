import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProbeServer {
    static int statusFor(String method, String path, boolean ready) {
        if (!method.equals("GET")) return 405;
        return switch (path) {
            case "/live", "/" -> 200;
            case "/ready" -> ready ? 200 : 503;
            default -> 404;
        };
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--self-test")) {
            if (statusFor("GET", "/live", false) != 200
                    || statusFor("GET", "/ready", false) != 503
                    || statusFor("GET", "/ready", true) != 200
                    || statusFor("POST", "/ready", true) != 405
                    || statusFor("GET", "/missing", true) != 404) {
                throw new AssertionError("probe status contract");
            }
            System.out.println("Probe status checks completed");
            return;
        }
        AtomicBoolean ready = new AtomicBoolean();
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 16);
        server.createContext("/", exchange -> {
            int status = statusFor(exchange.getRequestMethod(), exchange.getRequestURI().getPath(), ready.get());
            byte[] body = ("guide-probe status=" + status + "\n").getBytes(StandardCharsets.UTF_8);
            try (exchange) {
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(status, body.length);
                exchange.getResponseBody().write(body);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ready.set(false);
            server.stop(1);
        }, "probe-shutdown"));
        server.start();
        ready.set(true);
    }
}