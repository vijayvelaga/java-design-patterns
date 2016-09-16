package com.iluwatar.servicestub.service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BuyAtLimitPriceCommand {
  private StockService service;
  private Portfolio portfolio;
  private Stock stock;
  private int quantity;
  private BigDecimal priceLimit;
  private Executor executor;

  public BuyAtLimitPriceCommand(StockService service, Portfolio portfolio, Stock stock, 
      int quantity, BigDecimal priceLimit,
      Executor executor) {
    this.service = service;
    this.portfolio = portfolio;
    this.stock = stock;
    this.quantity = quantity;
    this.priceLimit = priceLimit;
    this.executor = executor;
  }

  public Future<Void> execute() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    executor.execute(() -> {
      try {
        while (true) {
          StockQuote stockQuote = service.getQuote(stock);
          
          if (stockQuote.getCurrentPrice().compareTo(priceLimit) <= 0) {
            portfolio.add(stock, quantity, stockQuote.getCurrentPrice());
            future.complete(null);
            break;
          } else {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
          }
        }
        
      } catch (Exception e) {
        future.completeExceptionally(e);
      }    
      
    });
    return future;
  }
}
