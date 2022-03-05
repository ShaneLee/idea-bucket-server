package ee.shanel.ideabucket.service;

import ee.shanel.ideabucket.model.SubscriptionSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultPaymentsService implements PaymentsService
{
    @Override
    public Mono<Boolean> process(final SubscriptionSubmission submission)
    {
        return Mono.just(submission)
                .doOnNext(val -> LOG.info("Processing payment {}", submission))
                .map(res -> Boolean.TRUE)
                .defaultIfEmpty(Boolean.FALSE);
    }
}
