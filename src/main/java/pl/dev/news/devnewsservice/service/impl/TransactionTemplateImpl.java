package pl.dev.news.devnewsservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.dev.news.devnewsservice.service.TransactionTemplate;

@Service
public class TransactionTemplateImpl implements TransactionTemplate {

    @Override
    public void afterCommit(final Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            public void afterCommit() {
                action.run();
            }
        });
    }

}
