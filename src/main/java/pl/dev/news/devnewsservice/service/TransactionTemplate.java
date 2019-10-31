package pl.dev.news.devnewsservice.service;

public interface TransactionTemplate {
    void afterCommit(Runnable action);
}
