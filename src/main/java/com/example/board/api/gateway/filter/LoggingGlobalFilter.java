package com.example.board.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var startTime = System.currentTimeMillis();
        var id = exchange.getRequest().getId();
        var method = exchange.getRequest().getMethod().name();
        var path = exchange.getRequest().getPath().value();

        log.info("[Request] - ID: [{}], Method: [{}], Path: [{}]", id, method, path);

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    var endTime = System.currentTimeMillis();
                    var duration = endTime - startTime;
                    var status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().toString()
                            : "UNKNOWN";
                    log.info("[Response] - ID: [{}], Path: [{}], Status: [{}], Time: [{}ms], Signal: [{}]",
                            id, path, status, duration, signalType);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
