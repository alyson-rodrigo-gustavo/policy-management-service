package br.com.alysongustavo.policymanagementservice.infra.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PolicyTypeCacheWarmup implements ApplicationRunner {

    private final PolicyTypeCacheService cacheService;

    @Override
    public void run(ApplicationArguments args) {
        cacheService.getAll();
        cacheService.getAllByName();
    }
}
