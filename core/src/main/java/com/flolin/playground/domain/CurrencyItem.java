package com.flolin.playground.domain;

import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Domain object to hold the currency relevant information.
 */
@SuppressWarnings("JavaDoc")
public class CurrencyItem extends JsonObject
{
   private String name = null;

   private LocalDateTime lastUpdated = null;

   private BigDecimal rateInEur = null;

   public CurrencyItem(final String aName, final BigDecimal aRateInEur, final LocalDateTime aLastUpdated)
   {
      name = aName;
      rateInEur = aRateInEur;
      lastUpdated = aLastUpdated;
   }

   public CurrencyItem(final Map<String, Object> aMap)
   {
      super(aMap);
   }

   public CurrencyItem(final String aJsonString)
   {
      super(aJsonString);
   }

   public String getName()
   {
      return name;
   }

   public LocalDateTime getLastUpdated()
   {
      return lastUpdated;
   }

   public BigDecimal getRateInEur()
   {
      return rateInEur;
   }

   public void setName(final String aName)
   {
      name = aName;
   }

   @Override
   public String toString()
   {
      return "CurrencyItem{" + "name='" + name + '\'' + ", lastUpdated=" + lastUpdated + ", rateInEur=" + rateInEur + '}';
   }

   @Override
   public JsonObject copy()
   {
      //noinspection ReturnOfThis
      return this;
   }
}
