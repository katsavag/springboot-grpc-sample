package com.katsadourose.grpc_sample.users_service;

import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.service.GrpcService;

@Configuration
public class GrpcConfig {
    @GrpcService
    @Bean
    public BindableService reflectionService() {
        return ProtoReflectionService.newInstance();
    }

}

