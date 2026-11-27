package org.example.catalogclient;

import feign.Capability;
import feign.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public final class FilmClientLogging implements Capability {
    private static final Logger LOG = LoggerFactory.getLogger(FilmClient.class);

    @Override
    public Client enrich(Client client) {
        return (request, options) -> {
            String path = URI.create(request.url()).getRawPath();
            long started = System.nanoTime();
            try {
                var response = client.execute(request, options);
                LOG.info("Feign {} {} -> {} ({} ms)", request.httpMethod(), path, response.status(),
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
                return response;
            } catch (IOException failure) {
                LOG.warn("Feign {} {} -> {} ({} ms)", request.httpMethod(), path, failure.getClass().getSimpleName(),
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
                throw failure;
            }
        };
    }
}
