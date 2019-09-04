package com.flolin.playground.verticles.apidataprovider.cryptocompare;

import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.Map;

public class CryptoCompareApiResponse extends JsonObject
{
   /**
    * The actual requested resource rate.
    */
   private BigDecimal rate;

   public CryptoCompareApiResponse(final Map<String, Object> aMap)
   {
      super(aMap);
   }

   public CryptoCompareApiResponse(final JsonObject aJsonObject)
   {
      super(aJsonObject.getMap());
   }

   public BigDecimal getRate()
   {
      return new BigDecimal(Double.toString(getDouble("EUR", 0.0d)));
   }

   @Override
   public String toString()
   {
      return "CryptoCompareApiResponse{" + "rate=" + getRate() + '}';
   }
}
