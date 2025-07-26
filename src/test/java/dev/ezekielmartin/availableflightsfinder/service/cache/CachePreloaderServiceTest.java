package dev.ezekielmartin.availableflightsfinder.service.cache;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class CachePreloaderServiceTest {
    @MockitoSpyBean
    CachePreloaderService cachePreloaderService;

    @Test
    void cachePreloadIsInvoked() {
        Mockito.verify(cachePreloaderService, Mockito.times(1)).run(any());
    }
}
